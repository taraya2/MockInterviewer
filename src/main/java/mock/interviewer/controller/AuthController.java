package mock.interviewer.controller;

import lombok.AllArgsConstructor;
import mock.interviewer.dto.*;
import mock.interviewer.security.CustomUserDetails;
import mock.interviewer.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<UserLoginResponse> register(@RequestBody UserRegistrationDTO u) {
        return ResponseEntity.ok(userService.registerUser(u));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginDTO dto) {
        return ResponseEntity.ok(userService.loginUser(dto)); // return res
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal CustomUserDetails user,
                                               @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(user, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<UserLoginResponse> refreshToken(@AuthenticationPrincipal CustomUserDetails user,
                                                          @RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(userService.refreshUserToken(request.getRefreshToken()));
    }

}
