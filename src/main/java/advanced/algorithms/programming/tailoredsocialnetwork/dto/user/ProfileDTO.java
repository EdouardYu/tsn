package advanced.algorithms.programming.tailoredsocialnetwork.dto.user;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.Interest;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Gender;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Nationality;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Role;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Visibility;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.List;

@Builder
@Data
public class ProfileDTO {
    private String email;
    private String firstname;
    private String lastname;
    private String username;
    private LocalDate birthday;
    private Gender gender;
    private Nationality nationality;
    private String picture;
    private String bio;
    private Visibility visibility;
    private Instant createdAt;
    private Role role;
    private List<Interest> interests;

}
