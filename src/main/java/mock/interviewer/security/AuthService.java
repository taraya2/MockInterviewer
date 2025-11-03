package mock.interviewer.security;

import lombok.RequiredArgsConstructor;
import mock.interviewer.dto.RefreshTokenDTO;
import mock.interviewer.dto.TokenDTO;
import mock.interviewer.dto.UserLoginResponse;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${spring.security.jwt.refresh-expiration-days}")
    private long refreshTokenExpDays;

    public UserLoginResponse login(String email, String passoword) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, passoword)
        );
        // userDetails is now authenticated and can be trusted
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        TokenDTO accessToken = jwtTokenProvider.generateToken(userDetails.getEmail(), userDetails.getROLE());

        // Fetch user from database and create refresh token
        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        RefreshTokenDTO refreshToken = createAndSaveRefreshToken(user);

        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .build();
    }

    public RefreshTokenDTO createAndSaveRefreshToken(User user) {
        // Delete any existing refresh token for this user
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        // Create new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        LocalDateTime expiration = LocalDateTime.now().plusDays(refreshTokenExpDays);
        refreshToken.setExpirationDate(expiration);
        refreshTokenRepository.save(refreshToken); // save to database

        return RefreshTokenDTO.builder()
                .token(refreshToken.getToken())
                .expiration(expiration)
                .build();
    }

    public UserLoginResponse refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (refreshToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token is expired");
        }

        User user = refreshToken.getUser();
        // Generate new access token
        TokenDTO accessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());

        // Create new refresh token and save to database
        RefreshTokenDTO newRefreshToken = createAndSaveRefreshToken(user);

        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

}
