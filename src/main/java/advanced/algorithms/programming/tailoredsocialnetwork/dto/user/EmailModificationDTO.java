package advanced.algorithms.programming.tailoredsocialnetwork.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EmailModificationDTO {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    private String password;

    @JsonCreator
    public EmailModificationDTO(
        String email,
        String password
    ) {
        this.email = email == null ? null : email.toLowerCase();
        this.password = password;

    }
}
