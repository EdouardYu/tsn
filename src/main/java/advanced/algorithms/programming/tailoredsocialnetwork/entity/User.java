package advanced.algorithms.programming.tailoredsocialnetwork.entity;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Gender;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Nationality;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Role;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "\"user\"")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;

    private String password;

    private String firstname;

    private String lastname;

    private String username;

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.UNSPECIFIED;

    @Enumerated(EnumType.STRING)
    private Nationality nationality = Nationality.UNSPECIFIED;

    private String picture;

    private String bio;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Interest> interests = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Relationship> friends = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Like> likedPosts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.FRIENDS_ONLY;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "is_enabled")
    private boolean enabled = false;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + this.role.name())
        );
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() { // est-ce que le compte a expiré
        return this.enabled;
    }

    @Override
    public boolean isAccountNonLocked() { // est-ce que le compte est bloqué
        return this.enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() { // est-ce que les informations d'identification ont expiré
        return this.enabled;
    }

    @Override
    public boolean isEnabled() { // est-ce que le compte est actif
        return this.enabled;
    }
}
