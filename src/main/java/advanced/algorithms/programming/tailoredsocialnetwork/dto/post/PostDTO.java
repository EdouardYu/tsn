package advanced.algorithms.programming.tailoredsocialnetwork.dto.post;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.List;

@Data
public class PostDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private String picture;

    @NotNull(message = "Creation time cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;

    @NotNull(message = "Visibility cannot be null")
    private Visibility visibility;

    @NotBlank(message = "Username cannot be empty")
    private String username;

    private long likeCount;
    private List<String> likedBy;

    @JsonCreator
    public PostDTO(String content, String picture, Instant createdAt, Visibility visibility, String username) {
        this.content = content;
        this.picture = picture;
        this.createdAt = createdAt;
        this.visibility = visibility;
        this.username = username;
    }

    @JsonCreator
    public PostDTO(int id, String content, String picture, Instant createdAt, Visibility visibility, String username, long likeCount, List<String> likedBy) {
        this.id = id;
        this.content = content;
        this.picture = picture;
        this.createdAt = createdAt;
        this.visibility = visibility;
        this.username = username;
        this.likeCount = likeCount;
        this.likedBy = likedBy;
    }

}
