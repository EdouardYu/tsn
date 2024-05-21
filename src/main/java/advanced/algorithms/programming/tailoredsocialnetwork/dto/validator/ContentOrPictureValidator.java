package advanced.algorithms.programming.tailoredsocialnetwork.dto.validator;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.post.PostDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContentOrPictureValidator implements ConstraintValidator<ContentOrPictureRequired, PostDTO> {
    @Override
    public boolean isValid(PostDTO postDTO, ConstraintValidatorContext constraintValidatorContext) {
        boolean isContentNotEmpty = postDTO.getContent() != null && !postDTO.getContent().trim().isEmpty();
        boolean isPictureNotEmpty = postDTO.getPicture() != null && !postDTO.getPicture().trim().isEmpty();

        return isContentNotEmpty || isPictureNotEmpty;

    }
}
