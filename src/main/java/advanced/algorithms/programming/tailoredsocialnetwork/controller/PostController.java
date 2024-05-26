package advanced.algorithms.programming.tailoredsocialnetwork.controller;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.*;
import advanced.algorithms.programming.tailoredsocialnetwork.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "application/json")
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostDTO postDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.createPost(postDTO, userDetails.getUsername()));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable int id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/like")
    public ResponseEntity<LikeDTO> likePost(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.likePost(id, userDetails.getUsername()));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/comment")
    public ResponseEntity<CommentDTO> commentPost(@PathVariable int id, @Valid @RequestBody CommentDTO commentDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.commentPost(id, commentDTO, userDetails.getUsername()));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/share")
    public ResponseEntity<ShareDTO> sharePost(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.sharePost(id, userDetails.getUsername()));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @DeleteMapping("/{id}/unlike")
    public ResponseEntity<Void> unlikePost(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        postService.unlikePost(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable int postId, @PathVariable int commentId, @AuthenticationPrincipal UserDetails userDetails) {
        postService.deleteComment(postId, commentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/shares/{shareId}")
    public ResponseEntity<Void> deleteShare(@PathVariable int postId, @PathVariable int shareId, @AuthenticationPrincipal UserDetails userDetails) {
        postService.deleteShare(postId, shareId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
