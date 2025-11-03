package mock.interviewer.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RefreshTokenDTO {

    private String token;
    private LocalDateTime expiration;

}
