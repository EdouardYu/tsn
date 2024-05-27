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

    @OneToMany(fetch = FetchType.LAZY)
    private List<Post> replies = new ArrayList<>();


    public List<Like> getLikes() {
        if (likes == null)
            likes = new ArrayList<>();
        return likes;
    }

    public void addReply(Post reply) {
        replies.add(reply);
        reply.setParent(this);
    }

    public void addView(View view) {
        if (views == null)
            views = new ArrayList<>();
        views.add(view);
    }

    public void addLike(Like like) {
        if(likes == null)
            likes = new ArrayList<>();
        likes.add(like);
    }

    public void removeLikeByEmail(String email) {
        likes.removeIf(like -> like.getUser().getEmail().equals(email));
    }

    public void addShare(Share share) {
        if (shares == null)
            shares = new ArrayList<>();
        shares.add(share);
    }

    public void removeShareByUsername(String username) {
        shares.removeIf(share -> share.getUser().getEmail().equals(username));
    }

    public boolean isComment() {
        return parent != null;
    }
}
