package advanced.algorithms.programming.tailoredsocialnetwork.entity;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.id.LikeId;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.id.ShareId;
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
@Table(name = "share")
@IdClass(ShareId.class)
public class Share {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Id
    @ManyToOne
    @JoinColumn(name = "shared_post_id")
    private Post sharedPost;
    @Id
    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;
}