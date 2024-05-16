package advanced.algorithms.programming.tailoredsocialnetwork.service;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.*;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.Validation;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Role;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Visibility;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.UserRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.service.exception.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidationService validationService;

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

    @Override
    public User loadUserByUsername(String username) {
        return this.userRepository.findByEmailOrUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
