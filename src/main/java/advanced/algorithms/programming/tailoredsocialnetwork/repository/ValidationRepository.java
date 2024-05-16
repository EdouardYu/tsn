package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public interface ValidationRepository extends JpaRepository<Validation, Integer> {
    @Query("SELECT V FROM Validation V WHERE V.user.email = :email AND V.code = :code")
    Optional<Validation> findUserValidationCode(String email, String code);

    @Query("SELECT V FROM Validation V WHERE V.user.email = :email")
    Stream<Validation> findUserValidationCodes(String email);

    void deleteAllByEnabledOrExpiredAtBefore(boolean enabled, Instant instant);
}
