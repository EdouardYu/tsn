package advanced.algorithms.programming.tailoredsocialnetwork.dto.user;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Role;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDTO {
    private String firstname;
    private String lastname;
    private String username;
    private String picture;
    private Role role;
}
