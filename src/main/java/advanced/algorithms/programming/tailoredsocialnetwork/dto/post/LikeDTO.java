package advanced.algorithms.programming.tailoredsocialnetwork.dto.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class LikeDTO {
    private int postId;

    @NotBlank(message = "Username cannot be empty")
    private String email;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant likedAt;

    @JsonCreator
    public LikeDTO(int postId, String email, Instant likedAt) {
        this.postId = postId;
        this.email = email;
        this.likedAt = likedAt;
    }
}
