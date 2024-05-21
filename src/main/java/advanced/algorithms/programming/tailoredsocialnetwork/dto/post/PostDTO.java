package advanced.algorithms.programming.tailoredsocialnetwork.dto.post;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.validator.ContentOrPictureRequired;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Visibility;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@ContentOrPictureRequired
@AllArgsConstructor
@Data
public class PostDTO {
    private String content;
    private String picture;
    @NotNull(message = "Visibility cannot be null")
    private Visibility visibility;
}
