package advanced.algorithms.programming.tailoredsocialnetwork.dto.user;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Gender;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.InterestTag;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Nationality;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProfileModificationDTO {
    @NotBlank(message = "Firstname cannot be empty")
    @Size(max = 64, message = "Firstname must be at most 64 characters long")
    @Pattern(regexp = "^[\\p{L} '-]+$", message = "Firstname can only contain letters, spaces, hyphens, and apostrophes")
    private String firstname;

    @NotBlank(message = "Lastname cannot be empty")
    @Size(max = 64, message = "Lastname must be at most 64 characters long")
    @Pattern(regexp = "^[\\p{L} '-]+$", message = "Lastname can only contain letters, spaces, hyphens, and apostrophes")
    private String lastname;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 129, message = "Username must be between 3 and 128 characters long")
    @Pattern(regexp = "^[\\p{L} '-]+$", message = "Username can only contain letters, spaces, hyphens, and apostrophes")
    private String username;

    @NotNull(message = "Birthday cannot be null")
    @Past(message = "Birthday cannot be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @NotNull(message = "Gender cannot be null")
    private Gender gender;

    @NotNull(message = "Nationality cannot be null")
    private Nationality nationality;

    @NotBlank(message = "Picture cannot be empty")
    private String picture;

    private String bio;

    @NotNull(message = "Visibility cannot be null")
    private Visibility visibility;

    @Valid
    private List<InterestTag> interests;

    @JsonCreator
    public ProfileModificationDTO(
        String firstname,
        String lastname,
        String username,
        LocalDate birthday,
        Gender gender,
        Nationality nationality,
        String picture,
        String bio,
        Visibility visibility,
        List<InterestTag> interests
    ) {
        this.firstname = firstname == null ? null : firstname.trim();
        this.lastname = lastname == null ? null : lastname.trim();
        this.username = username == null ? null : username.trim();
        this.birthday = birthday;
        this.gender = gender;
        this.nationality = nationality;
        this.picture = picture;
        this.bio = bio;
        this.visibility = visibility;
        this.interests = interests;
    }
}
