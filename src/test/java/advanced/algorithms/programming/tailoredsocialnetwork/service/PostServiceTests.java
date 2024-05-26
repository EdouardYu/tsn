package advanced.algorithms.programming.tailoredsocialnetwork.service;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.*;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Visibility;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.*;
import advanced.algorithms.programming.tailoredsocialnetwork.service.exception.*;
import advanced.algorithms.programming.tailoredsocialnetwork.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostServiceTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createPost_WithValidInput_ShouldCreatePost() {
        // Create a user
        User user = new User();
        user.setUsername("Testing");
        user.setEmail("test@example.com");
        user.setPassword("password");
        userRepository.save(user);

        // Create a post DTO
        PostDTO postDTO = new PostDTO(0, "Test content", "test.jpg", Instant.now(), Visibility.PUBLIC, user.getUsername());

        // Create a post service instance
        PostService postService = new PostService(postRepository, userRepository);

        // Create a post
        PostDTO createdPostDTO = postService.createPost(postDTO, user.getUsername());

        // Retrieve the post from the repository
        Post createdPost = postRepository.findById(createdPostDTO.getId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve created post from the repository"));

        // Assert that the created post matches the input data
        assertEquals(postDTO.getContent(), createdPost.getContent());
        assertEquals(postDTO.getPicture(), createdPost.getPicture());
        assertEquals(postDTO.getVisibility(), createdPost.getVisibility());
        assertEquals(user.getUsername(), createdPost.getUser().getUsername());
    }

    // Add more test methods for other functionalities of the PostService
}
