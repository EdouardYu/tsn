package advanced.algorithms.programming.tailoredsocialnetwork.security;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.Jwt;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import advanced.algorithms.programming.tailoredsocialnetwork.repository.JwtRepository;
import advanced.algorithms.programming.tailoredsocialnetwork.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class JwtService {
    private final UserService userService;
    private final JwtRepository jwtRepository;

    private final String BEARER = "bearer";
    @Value("${encryption.key}")
    private String ENCRYPTION_KEY;

    public Map<String, String> generate(String username) {
        User user = this.userService.loadUserByUsername(username);

        this.disableTokens(user);

        long currentTime = System.currentTimeMillis();
        long expirationTime = currentTime + 30 * 60 * 1000; // 30 minutes in milliseconds

        Map<String, String> jwtMap = new HashMap<>(this.generateJwt(user, currentTime, expirationTime));

        Jwt jwt = Jwt.builder()
            .value(jwtMap.get(this.BEARER))
            .expiredAt(Instant.ofEpochMilli(expirationTime))
            .enabled(true)
            .user(user)
            .build();

        this.jwtRepository.save(jwt);

        return jwtMap;
    }

    private void disableTokens(User user) {
        List<Jwt> jwtList = this.jwtRepository.findUserTokens(user.getEmail())
            .peek(jwt -> jwt.setEnabled(false))
            .toList();

        this.jwtRepository.saveAll(jwtList);
    }

    private Map<String, String> generateJwt(User user, long currentTime, long expirationTime) {
        Map<String, String> claims = Map.of(
            "username", user.getUsername(),
            "role", user.getRole().name()
        );

        String bearer = Jwts.builder()
            .issuedAt(new Date(currentTime))
            .expiration(new Date(expirationTime))
            .subject(user.getEmail())
            .claims(claims)
            .signWith(this.getKey())
            .compact();

        return Map.of(this.BEARER, bearer);
    }

    public Jwt findTokenByValue(String token) {
        return this.jwtRepository.findByValueAndEnabled(token, true)
            .orElseThrow(() -> new SignatureException("Invalid token"));
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = this.getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    public String extractEmail(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    private Date getExpirationDateFromToken(String token) {
        return this.getClaim(token, Claims::getExpiration);
    }

    private <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = this.getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey() {
        byte[] decoder = Decoders.BASE64.decode(this.ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

    public void signOut() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Jwt jwt = this.jwtRepository.findUserTokenByValidity(user.getEmail(), true)
            .orElseThrow(() -> new SignatureException("Token is already disabled"));

        jwt.setEnabled(false);
        this.jwtRepository.save(jwt);
    }

    @Scheduled(cron = "@daily")
    public void removeUselessJwt() {
        log.info("Deletion of useless tokens at: {}", Instant.now());
        this.jwtRepository.deleteAllByEnabledOrExpiredAtBefore(false, Instant.now());
    }
}
