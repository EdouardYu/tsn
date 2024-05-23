package advanced.algorithms.programming.tailoredsocialnetwork.entity;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.id.ViewId;
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
@Table(name = "view")
@IdClass(ViewId.class)
public class View {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "viewed_at")
    private Instant viewedAt = Instant.now();
}