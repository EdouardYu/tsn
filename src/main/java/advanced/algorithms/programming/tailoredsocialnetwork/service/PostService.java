package advanced.algorithms.programming.tailoredsocialnetwork.service;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.post.CommentDTO;
import advanced.algorithms.programming.tailoredsocialnetwork.dto.post.LikeDTO;
import advanced.algorithms.programming.tailoredsocialnetwork.dto.post.PostDTO;
import advanced.algorithms.programming.tailoredsocialnetwork.dto.post.ShareDTO;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.*;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Role;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.id.LikeId;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.*;
import advanced.algorithms.programming.tailoredsocialnetwork.service.exception.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final LikeRepository likeRepository;

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
        long likeCount = likeRepository.countLikesByPostId(savedPost.getId());
        List<String> likedBy = likeRepository.findUsernamesByPostId(savedPost.getId());

        return new PostDTO(savedPost.getId(), savedPost.getContent(), savedPost.getPicture(), savedPost.getCreatedAt(), savedPost.getVisibility(), savedPost.getUser().getUsername(), likeCount, likedBy);
    }

    public void updatePost(int id, PostDTO postDTO, String email) {
        Post post = getPostById(id);
        checkPermission(post.getUser().getEmail(), email);

        post.setContent(postDTO.getContent());
        post.setPicture(postDTO.getPicture());
        post.setVisibility(postDTO.getVisibility());
        postRepository.save(post);
    }

    public void deletePost(int id, String email) {
        Post post = getPostById(id);
        checkPermission(post.getUser().getEmail(), email);
        postRepository.delete(post);
    }

    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream().map(this::toPostDTO).collect(Collectors.toList());
    }

    public LikeDTO likePost(int id, String email) {
        Post post = getPostById(id);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Like like = Like.builder()
                .post(post)
                .user(user)
                .build();

        likeRepository.save(like);

        return new LikeDTO(post.getId(), user.getEmail(), like.getLikedAt());
    }

    public void unlikePost(int id, String email) {
        Post post = getPostById(id);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Like like = likeRepository.findById(new LikeId(user, post))
                .orElseThrow(() -> new NotFoundException("Like not found"));

        likeRepository.delete(like);
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

    public void deleteComment(int postId, int commentId, String email) {
        Post post = getPostById(postId);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Post comment = post.getReplies().stream()
                .filter(c -> c.getId() == commentId && c.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Comment not found"));

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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public PostDTO getPost(int id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        long likeCount = likeRepository.countLikesByPostId(post.getId());
        List<String> likedBy = likeRepository.findUsernamesByPostId(post.getId());

        return new PostDTO(id, post.getContent(), post.getPicture(), post.getCreatedAt(), post.getVisibility(), post.getUser().getUsername(), likeCount, likedBy);
    }

    private Post getPostById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));
    }

    private void checkPermission(String postOwnerEmail, String currentEmail) {
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!postOwnerEmail.equals(currentEmail) && !currentUser.getRole().equals(Role.ADMINISTRATOR)) {
            throw new UnauthorizedException("You are not authorized to perform this action");
        }
    }

    private PostDTO toPostDTO(Post post) {
        return new PostDTO(
                post.getContent(),
                post.getPicture(),
                post.getCreatedAt(),
                post.getVisibility(),
                post.getUser().getUsername()
        );
    }

    public List<PostDTO> getPostsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return postRepository.findByUser(user).stream()
                .map(this::toPostDTO)
                .collect(Collectors.toList());
    }
}
