package advanced.algorithms.programming.tailoredsocialnetwork.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterestDTO {
    @NotBlank(message = "Interest cannot be empty")
    private String interest;
}
