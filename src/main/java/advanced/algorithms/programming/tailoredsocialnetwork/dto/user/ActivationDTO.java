package advanced.algorithms.programming.tailoredsocialnetwork.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class ActivationDTO {
    private String email;
    private String code;

    @JsonCreator
    public ActivationDTO(String email, String code) {
        this.email = email == null ? null : email.toLowerCase();
        this.code = code;
    }
}
