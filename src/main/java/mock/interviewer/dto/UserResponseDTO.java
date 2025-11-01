package mock.interviewer.dto;

import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String role;
    private String createdAt;


}
