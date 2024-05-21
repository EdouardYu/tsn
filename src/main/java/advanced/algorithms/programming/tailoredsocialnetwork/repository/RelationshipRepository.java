package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RelationshipRepository extends JpaRepository<Relationship, Integer> {
    @Query(
        "SELECT COUNT(r) > 0 FROM Relationship r " +
        "WHERE (r.user1.id = :userId1 AND r.user2.id = :userId2)" +
            "OR (r.user1.id = :userId2 AND r.user2.id = :userId1)"
    )
    boolean isFriend(int userId1, int userId2);}
