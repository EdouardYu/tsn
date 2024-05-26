package advanced.algorithms.programming.tailoredsocialnetwork.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PasswordModificationDTO {
    @JsonProperty("old_password")
    private String oldPassword;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters long")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!#$%&*+<=>?@^_-]).*$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, " +
            "one number, and one special character (! # $ % & * + - < = > ? @ ^ _)"
    )
    @JsonProperty("new_password")
    private String newPassword;
}
