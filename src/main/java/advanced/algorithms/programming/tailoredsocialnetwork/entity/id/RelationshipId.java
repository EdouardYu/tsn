package advanced.algorithms.programming.tailoredsocialnetwork.entity.id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipId implements Serializable {
    private int user1Id;
    private int user2Id;
}