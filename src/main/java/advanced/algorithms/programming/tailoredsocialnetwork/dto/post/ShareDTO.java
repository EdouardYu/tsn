package advanced.algorithms.programming.tailoredsocialnetwork.dto.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShareDTO {
    private int postId;

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @JsonCreator
    public ShareDTO(int postId, String username) {
        this.postId = postId;
        this.username = username;
    }
}
