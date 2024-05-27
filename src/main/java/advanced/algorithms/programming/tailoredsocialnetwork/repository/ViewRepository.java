package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.View;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.id.ViewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ViewRepository extends JpaRepository<View, ViewId> {

    @Query("SELECT COUNT(v) FROM View v WHERE v.post.id = :postId")
    int countViewsByPostId(int postId);

    @Query("SELECT v.user.username FROM View v WHERE v.post.id = :postId")
    List<String> findUsernamesByPostId(int postId);

    @Query("SELECT v FROM View v WHERE v.post.id = :postId")
    List<View> findByPostId(int postId);

    @Query("SELECT v FROM View v WHERE v.post.id = :id")
    List<View> findAllByPostId(int id);
}
