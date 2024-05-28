package advanced.algorithms.programming.tailoredsocialnetwork.dto.user;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Role;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PotentialFriendDTO {
    private int id;
    private String firstname;
    private String lastname;
    private String username;
    private String picture;
    private Role role;
    private Integer totalScore;
    private Integer mutualFriendsCount;
    private Integer commonLikedPostsCount;
    private Integer commonInterestsCount;
}

