package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndEnabled(String email, Boolean enabled);

    Optional<User> findByEmail(String email);
}
