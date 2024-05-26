package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.Post;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("SELECT p FROM Post p WHERE p.user = :user")
    List<Post> findByUser(User user);

    @Query("SELECT p FROM Post p WHERE p.visibility = :visibility")
    List<Post> findByVisibility(Visibility visibility);

    @Query("SELECT p FROM Post p WHERE p.parent.id = :parentId")
    List<Post> findByParentId(int parentId);

    @Query("SELECT p FROM Post p JOIN p.likes l WHERE l.user = :user")
    List<Post> findLikedPostsByUser(User user);
}
