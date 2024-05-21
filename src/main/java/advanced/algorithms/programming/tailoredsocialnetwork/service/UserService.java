package advanced.algorithms.programming.tailoredsocialnetwork.service;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.SearchCriteria;
import advanced.algorithms.programming.tailoredsocialnetwork.dto.mapper.UserMapper;
import advanced.algorithms.programming.tailoredsocialnetwork.dto.user.*;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.Follow;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.Relationship;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.Validation;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Role;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Visibility;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.FollowRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.RelationshipRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.UserRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.specification.UserSpecification;
import advanced.algorithms.programming.tailoredsocialnetwork.service.exception.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final FollowRepository followRepository;
    private final RelationshipRepository relationshipRepository;

    public void signUp(RegistrationDTO userDTO) {
        this.userRepository.findByEmail(userDTO.getEmail())
            .orElseThrow(() -> new AlreadyUsedException("Email already used"));

        String encryptedPassword = this.passwordEncoder.encode(userDTO.getPassword());

        User user = User.builder()
            .email(userDTO.getEmail())
            .password(encryptedPassword)
            .firstname(userDTO.getFirstname())
            .lastname(userDTO.getLastname())
            .username(userDTO.getFirstname() + " " + userDTO.getLastname())
            .birthday(userDTO.getBirthday())
            .gender(userDTO.getGender())
            .nationality(userDTO.getNationality())
            .picture("https://upload.wikimedia.org/wikipedia/commons/4/4b/User-Pict-Profil.svg")
            .bio("Hey! I use Tailored Social Network")
            .visibility(Visibility.FRIENDS_ONLY)
            .createdAt(Instant.now())
            .enabled(false)
            .role(Role.USER)
            .build();

        this.userRepository.save(user);

        this.validationService.register(user);
    }

    public void activate(ActivationDTO activationDTO) {
        Validation validation = this.validationService.findUserActivationCode(
            activationDTO.getEmail(),
            activationDTO.getCode()
        );

        if(Instant.now().isAfter(validation.getExpiredAt()))
            throw new ValidationCodeException("Expired activation code");

        if(!validation.isEnabled())
            throw new ValidationCodeException("Disabled activation code");

        User user = validation.getUser();
        if(user.isEnabled())
            throw new AlreadyProcessedException("User already enabled");

        user.setEnabled(true);
        this.userRepository.save(user);
    }

    public void newActivationCode(EmailDTO userDTO) {
        User user = this.userRepository.findByEmail(userDTO.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(user.isEnabled())
            throw new AlreadyProcessedException("User already enabled");

        this.validationService.register(user);
    }

    public void resetPassword(EmailDTO userDTO) {
        User user = this.userRepository.findByEmail(userDTO.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(!user.isEnabled())
            throw new NotYetEnabledException("User not yet enabled");

        this.validationService.resetPassword(user);
    }

    public void newPassword(PasswordResetDTO passwordResetDTO) {
        Validation validation = validationService.findUserPasswordResetCode(
            passwordResetDTO.getEmail(),
            passwordResetDTO.getCode()
        );

        User user = validation.getUser();
        if(!user.isEnabled())
            throw new NotYetEnabledException("User not yet enabled");

        if(Instant.now().isAfter(validation.getExpiredAt()))
            throw new ValidationCodeException("Expired password reset code");

        if(!validation.isEnabled())
            throw new ValidationCodeException("Disabled password reset code");

        String encryptedPassword = this.passwordEncoder.encode(passwordResetDTO.getPassword());
        user.setPassword(encryptedPassword);
        this.userRepository.save(user);
    }

    public ProfileDTO getProfile(int id) {
        User profile = this.userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getRole().equals(Role.ADMINISTRATOR) &&
            !Visibility.PUBLIC.equals(profile.getVisibility()) &&
            !(Visibility.FRIENDS_ONLY.equals(profile.getVisibility())
                && relationshipRepository.isFriend(profile.getId(), user.getId())) &&
            !(Visibility.PRIVATE.equals(profile.getVisibility())
                && profile.getId() == user.getId()))
            throw new AccessDeniedException("Access denied");

        return ProfileDTO.builder()
                .email(profile.getEmail())
                .firstname(profile.getFirstname())
                .lastname(profile.getLastname())
                .birthday(profile.getBirthday())
                .gender(profile.getGender())
                .nationality(profile.getNationality())
                .picture(profile.getPicture())
                .bio(profile.getBio())
                .interests(profile.getInterests())
                .visibility(profile.getVisibility())
                .createdAt(profile.getCreatedAt())
                .role(profile.getRole())
                .build();
    }

    public void modifyProfile(int id, ProfileModificationDTO userDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User dbUser = this.userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(user.getId() != dbUser.getId() || !user.getRole().equals(Role.ADMINISTRATOR))
            throw new AccessDeniedException("Access denied");

        this.userRepository.findByEmail(userDTO.getEmail())
            .orElseThrow(() -> new AlreadyUsedException("Email already used"));

        String encryptedPassword = this.passwordEncoder.encode(userDTO.getPassword());

        dbUser.setEmail(userDTO.getEmail());
        dbUser.setPassword(encryptedPassword);
        dbUser.setFirstname(userDTO.getFirstname());
        dbUser.setLastname(userDTO.getLastname());
        dbUser.setUsername(userDTO.getUsername());
        dbUser.setBirthday(userDTO.getBirthday());
        dbUser.setGender(userDTO.getGender());
        dbUser.setNationality(userDTO.getNationality());
        dbUser.setPicture(userDTO.getPicture());
        dbUser.setBio(userDTO.getBio());
        dbUser.setVisibility(userDTO.getVisibility());
        dbUser.setInterests(userDTO.getInterests());

        this.userRepository.save(dbUser);
    }

    public void deleteProfile(int id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User dbUser = this.userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(user.getId() != dbUser.getId() || !user.getRole().equals(Role.ADMINISTRATOR))
            throw new AccessDeniedException("Access denied");

        this.userRepository.deleteById(user.getId());
    }

    public void followUser(int followerId, int followedId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User dbUser = this.userRepository.findById(followedId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(user.getId() != dbUser.getId())
            throw new AccessDeniedException("Access denied");

        User followed = this.userRepository.findById(followedId)
            .orElseThrow(() -> new UsernameNotFoundException("The user you tried to follow is not found"));

        if(this.followRepository.existsByFollowerIdAndFollowedId(followerId, followedId))
            throw new AlreadyProcessedException("You already follow this user");

        Instant now = Instant.now();

        Follow follow = Follow.builder()
            .follower(dbUser)
            .followed(followed)
            .followedAt(now)
            .build();

        this.followRepository.save(follow);

        if(this.followRepository.existsByFollowerIdAndFollowedId(followedId, followerId)) {
            Relationship relationship = Relationship.builder()
                .user1(dbUser)
                .user2(followed)
                .startedAt(now)
                .build();

            this.relationshipRepository.save(relationship);
        }
    }

    public Page<UserDTO> getRecommendedProfiles(List<SearchCriteria> criteria, Pageable pageable) {

        UserSpecification specs = criteria.stream()
            .map(UserSpecification::new)
            .reduce((spec1, spec2) -> (UserSpecification) spec1.and(spec2)).orElse(null);

        Page<User> users;
        if (specs != null)
            users = this.userRepository.findAll(specs, pageable);
        else
            users = this.userRepository.findAll(pageable);

        return users.map(UserMapper::toUserDTO);
    }

    @Override
    public User loadUserByUsername(String username) {
        return this.userRepository.findByEmailOrUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
