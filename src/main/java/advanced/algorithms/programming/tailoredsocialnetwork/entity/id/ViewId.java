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
public class ViewId implements Serializable {
    private int userId;
    private int postId;
}
