package advanced.algorithms.programming.tailoredsocialnetwork.entity;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.id.RelationshipId;
import com.fasterxml.jackson.annotation.JsonValue;
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
@IdClass(RelationshipId.class)
public class Relationship {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Id
    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;
    @Column(name = "started_at")
    private Instant startedAt;

    @JsonValue
    public User getFriend() {
        return this.friend;
    }
}