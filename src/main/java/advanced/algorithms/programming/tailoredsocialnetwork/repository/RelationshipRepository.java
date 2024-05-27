package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.Relationship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RelationshipRepository extends JpaRepository<Relationship, Integer> {
    void deleteByUserIdAndFriendId(int userId, int friendId);

    @Query("SELECT COUNT(R) > 0 FROM Relationship R " +
        "WHERE R.user.id = :userId AND R.friend.id = :friendId")
    boolean isFriend(int userId, int friendId);

    Page<Relationship> findByUserId(int userId, Pageable pageable);

    boolean existsByUserIdAndFriendId(int userId, int friendId);

}


