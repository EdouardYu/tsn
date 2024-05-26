package advanced.algorithms.programming.tailoredsocialnetwork.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

import java.time.Instant;

@Data
public class CommentDTO {
    private int id;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotNull(message = "Comment time cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant commentedAt;

    @JsonCreator
    public CommentDTO(int id, String content, String username, Instant commentedAt) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.commentedAt = commentedAt;
    }
}
