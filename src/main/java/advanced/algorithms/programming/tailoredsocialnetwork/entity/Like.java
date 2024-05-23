package advanced.algorithms.programming.tailoredsocialnetwork.entity;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.id.LikeId;
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
@Table(name = "\"like\"")
@IdClass(LikeId.class)
public class Like {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "liked_at")
    private Instant likedAt = Instant.now();
}

