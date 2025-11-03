package mock.interviewer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class UserRegistrationDTO {

    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;


}
