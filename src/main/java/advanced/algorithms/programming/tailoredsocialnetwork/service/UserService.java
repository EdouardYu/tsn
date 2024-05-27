package advanced.algorithms.programming.tailoredsocialnetwork.service;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.SearchCriteria;
import advanced.algorithms.programming.tailoredsocialnetwork.dto.mapper.UserMapper;
import advanced.algorithms.programming.tailoredsocialnetwork.dto.user.*;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.*;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.*;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.FollowRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.InterestRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.RelationshipRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.UserRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.specification.UserSpecification;
import advanced.algorithms.programming.tailoredsocialnetwork.service.exception.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final FollowRepository followRepository;
    private final RelationshipRepository relationshipRepository;

    public void signUp(RegistrationDTO userDTO) {
        if(this.userRepository.existsByEmailAndEnabled(userDTO.getEmail(), true))
            throw new AlreadyUsedException("Email already used");

        Optional<User> dbUser = this.userRepository.findByEmail(userDTO.getEmail());
        String encryptedPassword = this.passwordEncoder.encode(userDTO.getPassword());

        User user;
        if(dbUser.isPresent()){
            user = dbUser.get();
            user.setPassword(encryptedPassword);
            user.setFirstname(userDTO.getFirstname());
            user.setLastname(userDTO.getLastname());
            user.setUsername(userDTO.getFirstname() + " " + userDTO.getLastname());
            user.setBirthday(userDTO.getBirthday());
            user.setGender(userDTO.getGender());
            user.setNationality(userDTO.getNationality());
            user.setCreatedAt(Instant.now());
        } else {
            user = User.builder()
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
        }

        this.userRepository.save(user);

        List<Interest> interests = userDTO.getInterests().stream()
            .map(tag -> Interest.builder().interest(tag).user(user).build())
            .toList();

        this.interestRepository.saveAll(interests);

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

        if (!Role.ADMINISTRATOR.equals(user.getRole()) &&
            user.getId() != profile.getId() &&
            !Visibility.PUBLIC.equals(profile.getVisibility()) &&
            !(Visibility.FRIENDS_ONLY.equals(profile.getVisibility()) &&
                this.relationshipRepository.isFriend(user.getId(), profile.getId())))
            return ProfileDTO.builder()
                .firstname(profile.getFirstname())
                .lastname(profile.getLastname())
                .username(profile.getUsername())
                .picture(profile.getPicture())
                .visibility(profile.getVisibility())
                .role(profile.getRole())
                .build();

        return ProfileDTO.builder()
                .email(profile.getEmail())
                .firstname(profile.getFirstname())
                .lastname(profile.getLastname())
                .username(profile.getUsername())
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

    public ProfileDTO modifyProfile(int id, ProfileModificationDTO userDTO) {
        User user = hasPermission(id);

        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setUsername(userDTO.getUsername());
        user.setBirthday(userDTO.getBirthday());
        user.setGender(userDTO.getGender());
        user.setNationality(userDTO.getNationality());
        user.setPicture(userDTO.getPicture());
        user.setBio(userDTO.getBio());
        user.setVisibility(userDTO.getVisibility());

        user = this.userRepository.save(user);

        List<Interest> interests = this.updateInterests(user, userDTO.getInterests());

        return ProfileDTO.builder()
            .email(user.getEmail())
            .firstname(user.getFirstname())
            .lastname(user.getLastname())
            .username(user.getUsername())
            .birthday(user.getBirthday())
            .gender(user.getGender())
            .nationality(user.getNationality())
            .picture(user.getPicture())
            .bio(user.getBio())
            .interests(interests)
            .visibility(user.getVisibility())
            .createdAt(user.getCreatedAt())
            .role(user.getRole())
            .build();
    }

    private List<Interest> updateInterests(User user, List<InterestTag> newInterestsTag) {
        List<Interest> currentInterests = user.getInterests();

        currentInterests.removeIf(interest -> !newInterestsTag.contains(interest.getInterest()));

        List<InterestTag> currentInterestsTag = currentInterests.stream()
            .map(Interest::getInterest)
            .toList();

        newInterestsTag.stream()
            .filter(tag -> !currentInterestsTag.contains(tag))
            .map(tag -> Interest.builder().interest(tag).user(user).build())
            .forEach(currentInterests::add);

        return this.interestRepository.saveAll(currentInterests);
    }

    /*
    public void modifyEmail(int id, EmailModificationDTO userDTO) {
        User user = hasPermission(id);

        if(this.userRepository.existsByEmail(userDTO.getEmail()))
            throw new AlreadyUsedException("Email already used");

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new BadPasswordException("Incorrect password");
        }

        user.setEmail(userDTO.getEmail());
        this.userRepository.save(user);
    }
    */

    public void modifyPassword(int id, PasswordModificationDTO userDTO) {
        User user = hasPermission(id);

        if (!passwordEncoder.matches(userDTO.getOldPassword(), user.getPassword())) {
            throw new BadPasswordException("Incorrect password");
        }

        String newEncryptedPassword = this.passwordEncoder.encode(userDTO.getNewPassword());

        user.setPassword(newEncryptedPassword);
        this.userRepository.save(user);
    }

    //public void deleteProfile(int id) {
    //    User user = this.hasPermission(id);
    //    this.userRepository.deleteById(user.getId());
    //}

    private User hasPermission(int id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User dbUser = this.userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(user.getId() != dbUser.getId() && !Role.ADMINISTRATOR.equals(user.getRole()))
            throw new AccessDeniedException("Access denied");

        return dbUser;
    }

    public boolean followUser(int followerId, int followedId) {
        User user = hasPermission(followerId);

        User followed = this.userRepository.findById(followedId)
            .orElseThrow(() -> new UsernameNotFoundException("The user you tried to follow is not found"));

        if(this.followRepository.existsByFollowerIdAndFollowedId(followerId, followedId))
            throw new AlreadyProcessedException("You already follow this user");

        Instant now = Instant.now();

        Follow follow = Follow.builder()
            .follower(user)
            .followed(followed)
            .followedAt(now)
            .build();

        this.followRepository.save(follow);

        if(this.followRepository.existsByFollowerIdAndFollowedId(followedId, followerId)) {
            List<Relationship> relationships = new ArrayList<>();

            relationships.add(Relationship.builder()
                .user(user)
                .friend(followed)
                .startedAt(now)
                .build());

            relationships.add(Relationship.builder()
                .user(followed)
                .friend(user)
                .startedAt(now)
                .build());

            this.relationshipRepository.saveAll(relationships);
        }

        return true;
    }

    public boolean unfollowUser(int followerId, int followedId) {
        hasPermission(followerId);

        if(!this.userRepository.existsById(followedId))
            throw new UsernameNotFoundException("The user you tried to unfollow is not found");

        if(!this.followRepository.existsByFollowerIdAndFollowedId(followerId, followedId))
            throw new AlreadyProcessedException("You never follow this user");

        if(this.followRepository.existsByFollowerIdAndFollowedId(followedId, followerId)) {
            this.relationshipRepository.deleteByUserIdAndFriendId(followerId, followedId);
            this.relationshipRepository.deleteByUserIdAndFriendId(followedId, followerId);
        }

        this.followRepository.deleteByFollowerIdAndFollowedId(followerId, followedId);

        return false;
    }

    public boolean userFollowed(int followerId, int followedId) {
        hasPermission(followerId);

        if(!this.userRepository.existsById(followedId))
            throw new UsernameNotFoundException("The user you tried to see if you follow him is not found");

        return this.followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }

    public boolean isFriend(int userId, int friendId) {
        hasPermission(userId);

        if(!this.userRepository.existsById(friendId))
            throw new UsernameNotFoundException("Fiend is not found");

        return this.relationshipRepository.existsByUserIdAndFriendId(userId, friendId);
    }

    public List<UserDTO> searchUsers(String term) {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<User> users = this.userRepository.searchUsers(term, pageRequest);
        return users.stream().map(UserMapper::toUserDTO).toList();
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

    public Page<UserDTO> getFriends(int id, Pageable pageable) {
        hasPermission(id);

        return this.relationshipRepository.findByUserId(id, pageable).map(Relationship::getFriend)
            .map(UserMapper::toUserDTO);
    }

    public Map<String, EnumSet<?>> getAllEnumerations() {
        Map<String, EnumSet<?>> enumMap = new HashMap<>();
        enumMap.put("genders", EnumSet.allOf(Gender.class));
        enumMap.put("interests", EnumSet.allOf(InterestTag.class));
        enumMap.put("nationalities", EnumSet.allOf(Nationality.class));
        enumMap.put("visibilities", EnumSet.allOf(Visibility.class));
        return enumMap;
    }

    @Override
    public User loadUserByUsername(String username) {
        return this.userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
