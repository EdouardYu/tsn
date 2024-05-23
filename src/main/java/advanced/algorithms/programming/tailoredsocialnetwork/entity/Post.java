package advanced.algorithms.programming.tailoredsocialnetwork.entity;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;

    private String picture;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.FRIENDS_ONLY;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Post parent;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Post> comments = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private List<View> views = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private List<Share> shares = new ArrayList<>();
}

