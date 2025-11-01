package mock.interviewer.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.KeyStoreException;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long expirationMs;

    public JwtTokenProvider(@Value("$spring.security.jwt.secret") String secret,
                            @Value("spring.security.jwt.expiration-ms") long ms) {
        // .getBytes concerts to byte array then takes byte array and creates a secret
        // key(cryptographic key) used to sign JWTs tokens
        // creates the signiture part of the JWT
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = ms;
    }

    public String generateToken(String subjectEmail, String role) {
        Date now = new Date();
        // add the expriration time to the current time creation of token
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(subjectEmail)
                .claim("role", role)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // was not  valid
        }
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getPayload().getSubject();
    }

    public String getRoleFromToken(String token) {
        Object role = parseClaims(token).getPayload().get("role");
        return role == null ? null : role.toString();
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) key) // change to setSigningKey(key) if theres error
                .build()
                .parseSignedClaims(token);

    }

}
