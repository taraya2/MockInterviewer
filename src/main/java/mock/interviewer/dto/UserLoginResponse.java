package mock.interviewer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginResponse {
    // returns this once logged in

    private TokenDTO accessToken;
    private RefreshTokenDTO refreshToken;
    private String username;
    private String email;
}
