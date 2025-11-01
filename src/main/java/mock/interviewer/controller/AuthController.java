package mock.interviewer.controller;

import mock.interviewer.dto.*;
import mock.interviewer.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.SpinnerUI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegistrationDTO u) {
       // UserResponseDTO res = userService.registerUser(u);
        return ResponseEntity.ok(null);

    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginDTO dto) {
        //UserLoginResponse res = userService.login(dto);

        return ResponseEntity.ok(null); // return res
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassowrd(@AuthenticationPrincipal CustomUserDetails user,
                                               @RequestBody ChangePasswordDTO dto) {
        // use userService to change the password
        return ResponseEntity.ok().build();
    }
}
