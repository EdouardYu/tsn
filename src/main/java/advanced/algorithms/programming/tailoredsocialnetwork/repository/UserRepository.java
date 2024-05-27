package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT U FROM User U WHERE U.email = :username OR U.username = :username")
    Optional<User> findByEmailOrUsername(String username);


    @Query("WITH cte_friend_scores AS ( " +
            "SELECT u.user_id, u2.user_id AS potential_friend_id, \n" +
            "   (SELECT COUNT(*) " +
            "     FROM friendships f1" +
            "     JOIN friendships f2 ON f1.friend_id = f2.friend_id" +
            "     WHERE f1.user_id = u.user_id\n" +
            "       AND f2.user_id = u2.user_id) * 10 AS mutual_friends_score, -- Calcul du score d'amis en commun (pondéré à 10)\n" +
            "    (SELECT COUNT(*)\n" +
            "     FROM likes l1\n" +
            "     JOIN likes l2 ON l1.post_id = l2.post_id  \n" +
            "     WHERE l1.user_id = u.user_id\n" +
            "       AND l2.user_id = u2.user_id) AS common_liked_posts_score, -- Calcul du score de posts likés en commun\n" +
            "    (SELECT SUM(CASE WHEN u1_interests.interest = ANY(u2_interests.interests) THEN 3 ELSE 0 END)\n" +
            "     FROM (\n" +
            "       SELECT UNNEST(STRING_TO_ARRAY(u1.interests, ',')) AS interest\n" +
            "       FROM users u1\n" +
            "       WHERE u1.user_id = u.user_id\n" +
            "     ) AS u1_interests\n" +
            "     CROSS JOIN (\n" +
            "       SELECT UNNEST(STRING_TO_ARRAY(u2.interests, ',')) AS interest\n" +
            "       FROM users u2\n" +
            "       WHERE u2.user_id = u2.user_id  \n" +
            "     ) AS u2_interests) AS common_interests_score, -- Calcul du score d'intérêts en commun (pondéré à 3)\n" +
            "   FROM User u\n" +
            "  CROSS JOIN users u2\n" +
            "  WHERE u.user_id != u2.user_id -- Exclure l'utilisateur actuel\n" +
            "    AND NOT EXISTS (SELECT 1 FROM friendships WHERE (user_id = u.user_id AND friend_id = u2.user_id) \n" +
            "                                                 OR (user_id = u2.user_id AND friend_id = u.user_id)) -- Exclure les amis existants\n" +
            ")\n" +
            "SELECT potential_friend_id, \n" +
            "  mutual_friends_score + common_liked_posts_score + common_interests_score AS total_score -- Calcul du score total\n" +
            "FROM cte_friend_scores\n" +
            "WHERE total_score > 0 -- Exclure les scores nuls\n" +
            "ORDER BY total_score DESC -- Trier par score décroissant\n" +
            "LIMIT 10; -- Limiter à 10 résultats\n")
    List<User> findNewFriends( Long userId, String userInterests);
}
