package mock.interviewer.service;


import lombok.AllArgsConstructor;
import mock.interviewer.dto.*;
import mock.interviewer.entity.User;
import mock.interviewer.repository.UserRepository;
import mock.interviewer.security.AuthService;
import mock.interviewer.security.CustomUserDetails;
import mock.interviewer.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UsersServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwt;


    public UserLoginResponse registerUser(UserRegistrationDTO dto) {
        // verify if email exists
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }
        // create user and save to database
        User u =  User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .passwordHash(encoder.encode(dto.getPassword()))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(u); // save user to database

        // Generate access token and refresh token for the newly registered user
        TokenDTO accessToken = jwt.generateToken(u.getEmail(), u.getRole());
        RefreshTokenDTO refreshToken = authService.createAndSaveRefreshToken(u);

        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(u.getUsername())
                .email(u.getEmail())
                .build();
    }


    public UserLoginResponse loginUser(UserLoginDTO dto) {
        return authService.login(dto.getEmail(), dto.getPassword());
    }


    public void changePassword(CustomUserDetails userDetails, ChangePasswordDTO dto) {
        // user is already verified and authentication principal is injected into userDetails

        // grab user from database
        User u = userRepository.findByEmail(userDetails.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("User not found when changing password"));

        // verify old password is correct
        if (!encoder.matches(dto.getOldPassword(), userDetails.getPassword())) {
            throw new IllegalArgumentException("Password does not match");
        }

        u.setPasswordHash(encoder.encode(dto.getNewPassword()));
        userRepository.save(u); // save the updated user info to database
    }

    public UserLoginResponse refreshUserToken(String oldToken) {
        return authService.refreshAccessToken(oldToken);
    }

}
