package advanced.algorithms.programming.tailoredsocialnetwork.controller;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.post.PostDTO;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.Post;
import advanced.algorithms.programming.tailoredsocialnetwork.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(path = "posts")
public class PostController {
    private PostService postService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createPost(@Valid @RequestBody PostDTO postDTO) {
        this.postService.createPost(postDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Post getPostById(@PathVariable int id) {
        return this.postService.findById(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updatePost(@PathVariable int id, @Valid @RequestBody PostDTO post) {
        this.postService.updatePost(id, post);
    }

    @DeleteMapping("{id}")
    public void deletePost(@PathVariable int id) {
        this.postService.deletePost(id);
    }
}
