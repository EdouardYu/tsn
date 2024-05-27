package advanced.algorithms.programming.tailoredsocialnetwork.dto.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class ViewDTO {
    private int postId;

    @NotBlank(message = "Username cannot be empty")
    private String email;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant viewedAt;

    @JsonCreator
    public ViewDTO(int postId, String email, Instant viewedAt) {
        this.postId = postId;
        this.email = email;
        this.viewedAt = viewedAt;
    }
}
