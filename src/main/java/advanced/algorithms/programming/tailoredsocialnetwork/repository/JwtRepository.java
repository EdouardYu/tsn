package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.Jwt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public interface JwtRepository extends JpaRepository<Jwt, Integer> {
    Optional<Jwt> findByValueAndEnabled(String value, boolean enabled);

    @Query("SELECT J FROM Jwt J WHERE J.enabled = :enabled AND J.user.email = :email")
    Optional<Jwt> findUserTokenByValidity(String email, boolean enabled);

    @Query("SELECT J FROM Jwt J WHERE J.user.email = :email")
    Stream<Jwt> findUserTokens(String email);

    void deleteAllByEnabledOrExpiredAtBefore(boolean enabled, Instant instant);
}
