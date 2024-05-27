package advanced.algorithms.programming.tailoredsocialnetwork.dto.post;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.List;

@Data
public class CommentDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private String picture;

    @NotNull(message = "Creation time cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant commentedAt;

    @NotNull(message = "Visibility cannot be null")
    private Visibility visibility;

    @NotBlank(message = "Username cannot be empty")
    private String email;

    private long likeCount;
    private List<String> likedBy;

    private int parentId;

    @JsonCreator
    public CommentDTO(@JsonProperty("id") int id,
                      @JsonProperty("content") String content,
                      @JsonProperty("picture") String picture,
                      @JsonProperty("commentedAt") Instant commentedAt,
                      @JsonProperty("visibility") Visibility visibility,
                      @JsonProperty("username") String email,
                      @JsonProperty("parentId") int parentId) {
        this.id = id;
        this.content = content;
        this.picture = picture;
        this.commentedAt = commentedAt;
        this.visibility = visibility;
        this.email = email;
        this.parentId = parentId;
    }

    @JsonCreator
    public CommentDTO(@JsonProperty("content") String content,
                      @JsonProperty("picture") String picture,
                      @JsonProperty("commentedAt") Instant commentedAt,
                      @JsonProperty("visibility") Visibility visibility,
                      @JsonProperty("username") String email,
                      @JsonProperty("parentId") int parentId) {
        this.content = content;
        this.picture = picture;
        this.commentedAt = commentedAt;
        this.visibility = visibility;
        this.email = email;
        this.parentId = parentId;
    }
}
