package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import org.springframework.data.domain.Page;
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

    @Query(value = """
        WITH cte_friend_scores AS (
                SELECT
                    u.id AS user_id,
                    u2.id AS potential_friend_id,
                    (SELECT COUNT(*)
                     FROM "relationship" f1
                              JOIN "relationship" f2 ON f1.friend_id = f2.friend_id
                     WHERE f1.user_id = u.id
                       AND f2.user_id = u2.id) AS mutual_friends_count,
                    (SELECT COUNT(*)
                     FROM "like" l1
                              JOIN "like" l2 ON l1.post_id = l2.post_id
                     WHERE l1.user_id = u.id
                       AND l2.user_id = u2.id) AS common_liked_posts_count,
                    (SELECT COUNT(*)
                     FROM "interest" i1
                              JOIN "interest" i2 ON i1.interest = i2.interest
                     WHERE i1.user_id = u.id
                       AND i2.user_id = u2.id) AS common_interests_count
                FROM "user" u
                         CROSS JOIN "user" u2
                WHERE u.id != u2.id
                  AND u.id = :userId
                  AND NOT EXISTS (
                    SELECT 1
                    FROM "relationship" r
                    WHERE (r.user_id = u.id AND r.friend_id = u2.id)
                       OR (r.user_id = u2.id AND r.friend_id = u.id)
                )
            )
            SELECT
                u2.id,
                u2.firstname,
                u2.lastname,
                u2.username,
                u2.picture,
                u2.role,
                (mutual_friends_count * 10 + common_liked_posts_count + common_interests_count * 3) AS total_score,
                mutual_friends_count,
                common_liked_posts_count,
                common_interests_count
            FROM cte_friend_scores
            JOIN "user" u2 ON u2.id = potential_friend_id
            WHERE (mutual_friends_count * 10 + common_liked_posts_count + common_interests_count * 3) > 0
            ORDER BY total_score DESC
        """,
        countQuery = """
            WITH cte_friend_scores AS (
                    SELECT
                        u.id AS user_id,
                        u2.id AS potential_friend_id,
                        (SELECT COUNT(*)
                         FROM "relationship" f1
                                  JOIN "relationship" f2 ON f1.friend_id = f2.friend_id
                         WHERE f1.user_id = u.id
                           AND f2.user_id = u2.id) AS mutual_friends_count,
                        (SELECT COUNT(*)
                         FROM "like" l1
                                  JOIN "like" l2 ON l1.post_id = l2.post_id
                         WHERE l1.user_id = u.id
                           AND l2.user_id = u2.id) AS common_liked_posts_count,
                        (SELECT COUNT(*)
                         FROM "interest" i1
                                  JOIN "interest" i2 ON i1.interest = i2.interest
                         WHERE i1.user_id = u.id
                           AND i2.user_id = u2.id) AS common_interests_count
                    FROM "user" u
                             CROSS JOIN "user" u2
                    WHERE u.id != u2.id
                      AND u.id = :userId
                      AND NOT EXISTS (
                        SELECT 1
                        FROM "relationship" r
                        WHERE (r.user_id = u.id AND r.friend_id = u2.id)
                           OR (r.user_id = u2.id AND r.friend_id = u.id)
                    )
                )
                SELECT COUNT(*)
                FROM cte_friend_scores
                JOIN "user" u2 ON u2.id = potential_friend_id
                WHERE (mutual_friends_count * 10 + common_liked_posts_count + common_interests_count * 3) > 0
        """,
        nativeQuery = true)
    Page<Object> findNewFriends(int userId, Pageable pageable);

}

