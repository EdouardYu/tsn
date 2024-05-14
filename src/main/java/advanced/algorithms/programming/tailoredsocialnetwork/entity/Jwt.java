package advanced.algorithms.programming.tailoredsocialnetwork.entity;

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
@Table(name = "jwt")
public class Jwt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String value;
    @Column(name = "expired_at")
    private Instant expiredAt;
    private boolean enabled = true;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
