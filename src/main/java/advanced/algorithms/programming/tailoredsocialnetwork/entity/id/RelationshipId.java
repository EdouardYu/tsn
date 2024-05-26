package advanced.algorithms.programming.tailoredsocialnetwork.entity.id;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipId implements Serializable {
    private User user;
    private User friend;
}