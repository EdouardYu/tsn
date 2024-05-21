package advanced.algorithms.programming.tailoredsocialnetwork.service;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.post.PostDTO;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.Post;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.PostRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.service.exception.PostNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@AllArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    public void createPost(PostDTO postDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Post post = Post.builder()
            .content(postDTO.getContent())
            .picture(postDTO.getPicture())
            .createdAt(Instant.now())
            .visibility(postDTO.getVisibility())
            .user(user)
            .build();

        this.postRepository.save(post);
    }

    public Post findById(int id) {
        return this.postRepository.findById(id)
            .orElseThrow(() -> new PostNotFoundException("Post not found"));
    }

    public void updatePost(int id, PostDTO postDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = this.findById(id);

        if(user.getId() != post.getUser().getId())
            throw new AccessDeniedException("Access denied");

        post.setContent(postDTO.getContent());
        post.setPicture(postDTO.getPicture());
        post.setVisibility(postDTO.getVisibility());

        this.postRepository.save(post);
    }

    public void deletePost(int id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = this.findById(id);

        if(user.getId() != post.getUser().getId())
            throw new AccessDeniedException("Access denied");

        this.postRepository.delete(post);
    }
}
