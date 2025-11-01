package mock.interviewer.security;

import lombok.RequiredArgsConstructor;
import mock.interviewer.entity.User;
import mock.interviewer.repository.RefreshTokenRepository;
import mock.interviewer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.security.jwt.refresh-expiration-days}")
    private long refreshTokenExpDays;

    public String login(String email, String passowrd) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, passowrd)
        );
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));
        return jwtTokenProvider.generateToken(user.getEmail(), user.getRole());
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpirationDate(LocalDateTime.now().plusDays(refreshTokenExpDays));
        refreshTokenRepository.save(refreshToken); // save to database
        return refreshToken;
    }

    public String refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(()-> new RuntimeException("Invalid refresh token"));
        if (refreshToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token is expired");
        }
        User user = refreshToken.getUser();
        return jwtTokenProvider.generateToken(user.getEmail(), user.getRole());
    }

}
