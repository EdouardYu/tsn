package advanced.algorithms.programming.tailoredsocialnetwork.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class AuthenticationDTO {
    private String email;
    private String password;

    @JsonCreator
    public AuthenticationDTO(String email, String password) {
        this.email = email == null ? null : email.toLowerCase();
        this.password = password;
    }
}
