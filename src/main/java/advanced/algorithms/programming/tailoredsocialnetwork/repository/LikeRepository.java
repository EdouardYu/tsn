package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.Like;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.id.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, LikeId> {

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId")
    int countLikesByPostId(int postId);

    @Query("SELECT l.user.username FROM Like l WHERE l.post.id = :postId")
    List<String> findUsernamesByPostId(int postId);
}
