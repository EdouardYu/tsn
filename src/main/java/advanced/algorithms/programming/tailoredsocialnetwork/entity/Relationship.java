package advanced.algorithms.programming.tailoredsocialnetwork.entity;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.id.FollowId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "relationship")
@IdClass(Relationship.class)
public class Relationship {
    @Id
    @ManyToOne
    @JoinColumn(name = "user1_id")
    private User user1;
    @Id
    @ManyToOne
    @JoinColumn(name = "user2_id")
    private User user2;
    @Column(name = "started_at")
    private Instant startedAt;
}