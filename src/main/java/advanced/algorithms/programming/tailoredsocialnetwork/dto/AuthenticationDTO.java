package advanced.algorithms.programming.tailoredsocialnetwork.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class AuthenticationDTO {
    private String username;
    private String password;

    @JsonCreator
    public AuthenticationDTO(String username, String password) {
        this.username = username == null ? null : username.contains("@") ?
            username.toLowerCase() : username;
        this.password = password;
    }
}
