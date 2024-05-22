package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Integer> {
    boolean existsByFollowerIdAndFollowedId(int followerId, int followedId);

    void deleteByFollowerIdAndFollowedId(int followerId, int followedId);
}
