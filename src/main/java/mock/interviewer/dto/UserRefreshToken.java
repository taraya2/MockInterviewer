package mock.interviewer.dto;

import lombok.Data;

@Data
public class UserRefreshToken {
    // used when token is about to expire and need to refresh and get new token
    private String token;
}
