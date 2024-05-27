package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    //boolean existsByEmail(String email);

    boolean existsByEmailAndEnabled(String email, Boolean enabled);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
        "WHERE (LOWER(u.username) LIKE LOWER(CONCAT(:term, '%')) " +
        "OR LOWER(u.firstname) LIKE LOWER(CONCAT(:term, '%')) " +
        "OR LOWER(u.lastname) LIKE LOWER(CONCAT(:term, '%')) " +
        "OR LOWER(CONCAT(u.firstname, ' ', u.lastname)) LIKE LOWER(CONCAT(:term, '%')))")
    List<User> searchUsers(String term, Pageable pageable);
}
