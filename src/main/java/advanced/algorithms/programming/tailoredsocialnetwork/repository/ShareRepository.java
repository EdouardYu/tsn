package advanced.algorithms.programming.tailoredsocialnetwork.repository;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.Share;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.id.ShareId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShareRepository extends JpaRepository<Share, ShareId> {

    @Query("SELECT COUNT(s) FROM Share s WHERE s.sharedPost.id = :postId")
    int countSharesByPostId(int postId);

    @Query("SELECT s.user.username FROM Share s WHERE s.sharedPost.id = :postId")
    List<String> findUsernamesByPostId(int postId);

    @Query("SELECT s FROM Share s WHERE s.sharedPost.id = :postId")
    List<Share> findByPostId(int postId);

    @Query("SELECT s FROM Share s WHERE s.sharedPost.id = :id")
    List<Share> findAllByPostId(int id);

    @Query("SELECT s FROM Share s WHERE s.user.username = :username")
    List<Share> findAllByUsername(String username);

    @Query("SELECT s FROM Share s WHERE s.user.username = :username AND s.sharedPost.id = :postId")
    List<Share> findAllByUsernameAndPostId(String username, int postId);


}
