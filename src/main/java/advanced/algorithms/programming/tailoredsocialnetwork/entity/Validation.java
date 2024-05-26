package advanced.algorithms.programming.tailoredsocialnetwork.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "validation")
public class Validation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;

    @Column(name = "expired_at")
    private Instant expiredAt = Instant.now().plus(10, ChronoUnit.MINUTES);

    @Column(name = "is_enabled")
    private boolean enabled = true;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
