package mock.interviewer.service;

import mock.interviewer.dto.*;
import mock.interviewer.security.CustomUserDetails;

public interface UserService {

    UserLoginResponse registerUser(UserRegistrationDTO dto);
    UserLoginResponse loginUser(UserLoginDTO dto);
    void changePassword(CustomUserDetails userDetails, ChangePasswordDTO dto);
    UserLoginResponse refreshUserToken(String oldToken);

}
