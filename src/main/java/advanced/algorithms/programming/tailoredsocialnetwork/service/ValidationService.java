package advanced.algorithms.programming.tailoredsocialnetwork.service;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.Validation;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.ValidationRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.service.exception.ValidationCodeException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class ValidationService {
    private final ValidationRepository validationRepository;
    private final NotificationService notificationService;

    public void register(User user) {
        this.disableValidationCodes(user);
        Validation validation = generateValidationCode(user);
        this.notificationService.sendActivationCodeEmail(validation);
    }

    public void resetPassword(User user) {
        this.disableValidationCodes(user);
        Validation validation = generateValidationCode(user);
        this.notificationService.sendPasswordResetEmail(validation);
    }

    private void disableValidationCodes(User user) {
        List<Validation> validationCodeList = this.validationRepository.findUserValidationCodes(user.getEmail())
            .peek(validationCode -> validationCode.setEnabled(false))
            .toList();

        this.validationRepository.saveAll(validationCodeList);
    }

    private Validation generateValidationCode(User user) {
        Random random = new Random();
        int randomInteger = random.nextInt(1_000_000);
        String code = String.format("%06d", randomInteger);

        Validation validation = Validation.builder()
            .code(code)
            .expiredAt(Instant.now().plus(10, ChronoUnit.MINUTES))
            .enabled(true)
            .user(user)
            .build();

        return this.validationRepository.save(validation);
    }

    public Validation findUserActivationCode(String email, String activationCode) {
        return this.validationRepository.findUserValidationCode(email, activationCode)
            .orElseThrow(() -> new ValidationCodeException("Invalid activation code"));
    }

    public Validation findUserPasswordResetCode(String email, String passwordResetCode) {
        return this.validationRepository.findUserValidationCode(email, passwordResetCode)
            .orElseThrow(() -> new ValidationCodeException("Invalid password reset code"));
    }

    @Scheduled(cron = "@daily")
    public void removeUselessValidationCodes() {
        log.info("Deletion of expired validation codes at: {}", Instant.now());
        this.validationRepository.deleteAllByEnabledOrExpiredAtBefore(
            false,
            Instant.now().minus(1, ChronoUnit.DAYS) // 1 day after validation code expires
        );
    }
}
