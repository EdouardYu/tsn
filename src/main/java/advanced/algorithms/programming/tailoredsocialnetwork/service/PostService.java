package advanced.algorithms.programming.tailoredsocialnetwork.service;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.*;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.*;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Role;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.*;
import advanced.algorithms.programming.tailoredsocialnetwork.service.exception.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostDTO createPost(PostDTO postDTO, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Post post = Post.builder()
                .content(postDTO.getContent())
                .picture(postDTO.getPicture())
                .createdAt(Instant.now())
                .visibility(postDTO.getVisibility())
                .user(user)
                .build();
        Post savedPost = postRepository.save(post);
        return toPostDTO(savedPost);
    }


    public PostDTO getPost(int id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        return toPostDTO(post);
    }


    public void updatePost(int id, PostDTO postDTO, String username) {
        Post post = getPostById(id);
        checkPermission(post.getUser().getId());
        post.setContent(postDTO.getContent());
        post.setPicture(postDTO.getPicture());
        post.setVisibility(postDTO.getVisibility());
        postRepository.save(post);
    }

    public void deletePost(int id, String email) {
        Post post = getPostById(id);
        checkPermission(post.getUser().getId());
        postRepository.delete(post);
    }

    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream().map(this::toPostDTO).collect(Collectors.toList());
    }

    public LikeDTO likePost(int id, String email) {
        Post post = getPostById(id);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        like.setLikedAt(Instant.now());

        post.addLike(like);
        postRepository.save(post);

        return new LikeDTO(post.getId(), user.getUsername(), like.getLikedAt());
    }

    public void unlikePost(int id, String email) {
        Post post = getPostById(id);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Find the like associated with the user and post
        Like like = post.getLikes().stream()
                .filter(l -> l.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Like not found"));

        // Remove the like from the post
        post.getLikes().remove(like);
        postRepository.save(post);
    }

    public CommentDTO commentPost(int id, CommentDTO commentDTO, String email) {
        Post post = getPostById(id);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Post reply = Post.builder()
                .content(commentDTO.getContent())
                .user(user)
                .parent(post)
                .createdAt(Instant.now())
                .build();

        post.addReply(reply);
        postRepository.save(post);

        return new CommentDTO(reply.getId(), reply.getContent(), user.getUsername(), reply.getCreatedAt());
    }

    public void deleteComment(int postId, int commentId, String username) {
        Post post = getPostById(postId);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Find the comment associated with the user and post
        Post comment = post.getReplies().stream()
                .filter(c -> c.getId() == commentId && c.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        // Remove the comment from the post
        post.getReplies().remove(comment);
        postRepository.save(post);
    }

    public ShareDTO sharePost(int id, String email) {
        Post post = getPostById(id);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Share share = new Share();
        share.setUser(user);
        share.setPost(post);

        post.addShare(share);
        postRepository.save(post);

        return new ShareDTO(post.getId(), user.getUsername());
    }


    private User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Post getPostById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));
    }


    private void checkPermission(int userId) {
        User currentUser = getCurrentUser();
        if (currentUser.getId() != userId && !currentUser.getRole().equals(Role.ADMINISTRATOR)) {
            throw new UnauthorizedException("You are not authorized to perform this action");
        }
    }

    private PostDTO toPostDTO(Post post) {
        return new PostDTO(
                post.getId(),
                post.getContent(),
                post.getPicture(),
                post.getCreatedAt(),
                post.getVisibility(),
                post.getUser().getUsername()
        );
    }

    public List<PostDTO> getPostsByUser(String username) {
        User user = userRepository.findByEmailOrUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return postRepository.findByUser(user).stream()
                .map(this::toPostDTO)
                .collect(Collectors.toList());
    }
}