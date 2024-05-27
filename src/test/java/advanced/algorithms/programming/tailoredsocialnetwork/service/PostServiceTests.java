package advanced.algorithms.programming.tailoredsocialnetwork.service;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.post.*;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.*;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.PostRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.UserRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.service.exception.NotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    private static User user;

    @BeforeAll
    static void setUp(@Autowired UserRepository userRepository) {
        user = new User();
        user.setUsername("Testing");
        user.setEmail("test5@example.com");
        user.setPassword("password");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setBirthday(LocalDate.EPOCH);
        user.setGender(Gender.UNSPECIFIED);
        user.setRole(Role.USER);
        user.setVisibility(Visibility.FRIENDS_ONLY);
        user.setEnabled(true);
        user.setCreatedAt(Instant.now());
        userRepository.save(user);
    }

    @Test
    void createPost_WithValidInput_ShouldCreatePost() {
        PostDTO postDTO = new PostDTO("Test content", "test.jpg", Instant.now(), Visibility.PUBLIC, user.getUsername());

        PostDTO createdPost = postService.createPost(postDTO, user.getEmail());

        assertNotNull(createdPost);
        assertEquals(postDTO.getContent(), createdPost.getContent());
        assertEquals(postDTO.getPicture(), createdPost.getPicture());
        assertEquals(postDTO.getVisibility(), createdPost.getVisibility());
        assertEquals(user.getUsername(), createdPost.getUsername());
    }

    @Test
    void getPost_WithValidId_ShouldReturnPost() {
        PostDTO postDTO = new PostDTO("Test content", "test.jpg", Instant.now(), Visibility.PUBLIC, user.getUsername());
        PostDTO createdPost = postService.createPost(postDTO, user.getEmail());

        PostDTO retrievedPost = postService.getPost(createdPost.getId());

        assertNotNull(retrievedPost);
        assertEquals(createdPost.getId(), retrievedPost.getId());
        assertEquals(createdPost.getContent(), retrievedPost.getContent());
        assertEquals(createdPost.getPicture(), retrievedPost.getPicture());
    }

    @Test
    void updatePost_WithValidInput_ShouldUpdatePost() {
        PostDTO postDTO = new PostDTO("Test content", "test.jpg", Instant.now(), Visibility.PUBLIC, user.getUsername());
        PostDTO createdPost = postService.createPost(postDTO, user.getEmail());

        PostDTO updateDTO = new PostDTO("Updated content", "updated.jpg", Instant.now(), Visibility.PRIVATE, user.getUsername());
        postService.updatePost(createdPost.getId(), updateDTO, user.getEmail());

        PostDTO updatedPost = postService.getPost(createdPost.getId());

        assertNotNull(updatedPost);
        assertEquals(updateDTO.getContent(), updatedPost.getContent());
        assertEquals(updateDTO.getPicture(), updatedPost.getPicture());
        assertEquals(updateDTO.getVisibility(), updatedPost.getVisibility());
    }

    @Test
    void deletePost_WithValidId_ShouldDeletePost() {
        PostDTO postDTO = new PostDTO("Test content", "test.jpg", Instant.now(), Visibility.PUBLIC, user.getUsername());
        PostDTO createdPost = postService.createPost(postDTO, user.getEmail());

        postService.deletePost(createdPost.getId(), user.getEmail());

        assertThrows(NotFoundException.class, () -> postService.getPost(createdPost.getId()));
    }

    @Test
    void likePost_WithValidInput_ShouldLikePost() {
        PostDTO postDTO = new PostDTO("Test content", "test.jpg", Instant.now(), Visibility.PUBLIC, user.getUsername());
        PostDTO createdPost = postService.createPost(postDTO, user.getEmail());

        LikeDTO likeDTO = postService.likePost(createdPost.getId(), user.getEmail());

        assertNotNull(likeDTO);
        assertEquals(createdPost.getId(), likeDTO.getPostId());
        assertEquals(user.getEmail(), likeDTO.getEmail());
    }

    @Test
    void unlikePost_WithValidInput_ShouldUnlikePost() {
        PostDTO postDTO = new PostDTO("Test content", "test.jpg", Instant.now(), Visibility.PUBLIC, user.getUsername());
        PostDTO createdPost = postService.createPost(postDTO, user.getEmail());

        postService.likePost(createdPost.getId(), user.getEmail());
        postService.unlikePost(createdPost.getId(), user.getEmail());

        PostDTO updatedPost = postService.getPost(createdPost.getId());

        assertTrue(updatedPost.getLikedBy().isEmpty());
    }

    @Test
    void commentPost_WithValidInput_ShouldCommentPost() {
        PostDTO postDTO = new PostDTO("Test content", "test.jpg", Instant.now(), Visibility.PUBLIC, user.getUsername());
        PostDTO createdPost = postService.createPost(postDTO, user.getEmail());

        CommentDTO commentDTO = new CommentDTO(0, "Test comment", user.getUsername(), Instant.now());
        CommentDTO createdComment = postService.commentPost(createdPost.getId(), commentDTO, user.getEmail());

        assertNotNull(createdComment);
        assertEquals(commentDTO.getContent(), createdComment.getContent());
        assertEquals(user.getUsername(), createdComment.getUsername());
    }

    @Test
    void sharePost_WithValidInput_ShouldSharePost() {
        PostDTO postDTO = new PostDTO("Test content", "test.jpg", Instant.now(), Visibility.PUBLIC, user.getUsername());
        PostDTO createdPost = postService.createPost(postDTO, user.getEmail());

        ShareDTO shareDTO = postService.sharePost(createdPost.getId(), user.getEmail());

        assertNotNull(shareDTO);
        assertEquals(createdPost.getId(), shareDTO.getPostId());
        assertEquals(user.getUsername(), shareDTO.getUsername());
    }

    @Test
    void getPostsByUser_WithValidInput_ShouldReturnUserPosts() {
        PostDTO postDTO1 = new PostDTO("Test content", "test.jpg", Instant.now(), Visibility.PUBLIC, user.getUsername());
        PostDTO postDTO2 = new PostDTO("Second post", "second.jpg", Instant.now(), Visibility.PUBLIC, user.getUsername());

        postService.createPost(postDTO1, user.getEmail());
        postService.createPost(postDTO2, user.getEmail());

        List<PostDTO> userPosts = postService.getPostsByUser(user.getEmail());

        assertEquals(2, userPosts.size());
    }
}
