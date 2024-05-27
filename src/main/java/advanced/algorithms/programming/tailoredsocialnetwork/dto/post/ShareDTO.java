package advanced.algorithms.programming.tailoredsocialnetwork.dto.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class ShareDTO {
    private int sharedPostId;
    private int postId;
    private String username;

    @JsonCreator
    public ShareDTO(int sharedPostId, String username, int postId) {
        this.sharedPostId = sharedPostId;
        this.username = username;
        this.postId = postId;
    }
}
