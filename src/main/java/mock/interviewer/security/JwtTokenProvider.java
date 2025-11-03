package mock.interviewer.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import mock.interviewer.dto.RefreshTokenDTO;
import mock.interviewer.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.KeyStoreException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long expirationMs; // for access token
    private final long refreshExpirationMs;

    public JwtTokenProvider(@Value("${spring.security.jwt.secret}") String secret,
                            @Value("${spring.security.jwt.expiration-ms}") long ms,
                            @Value("${spring.security.jwt.refresh-expiration-days}") long refreshMs) {
        // .getBytes concerts to byte array then takes byte array and creates a secret
        // key(cryptographic key) used to sign JWTs tokens
        // creates the signiture part of the JWT
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = ms;
        this.refreshExpirationMs = refreshMs;
    }

    public TokenDTO generateToken(String subjectEmail, String role) {
        Date now = new Date();
        // add the expriration time to the current time creation of token
        Date exp = new Date(now.getTime() + expirationMs);

        String token = Jwts.builder()
                .subject(subjectEmail)
                .claim("role", role)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();

        LocalDateTime issuedAtLocal = now.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime expLocal = exp.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return TokenDTO.builder()
                .token(token)
                .issuedAt(issuedAtLocal)
                .expiration(expLocal)
                .tokenType("Bearer")
                .build();
    }

    public RefreshTokenDTO refreshToken(String oldToken) {
        if (!validateToken(oldToken)) {
            throw new IllegalArgumentException("Invalid Token");
        }
        String email = getEmailFromToken(oldToken);
        String role = getRoleFromToken(oldToken);
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshExpirationMs);
        String token  = Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(exp)
                .signWith(key)
                .compact();
        LocalDateTime expLocal = exp.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return RefreshTokenDTO.builder()
                .token(token)
                .expiration(expLocal)
                .build();
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
