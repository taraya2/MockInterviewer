package mock.interviewer.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TokenDTO {
    private String token;
    private LocalDateTime issuedAt;
    private LocalDateTime expiration;
    private String tokenType;

}
