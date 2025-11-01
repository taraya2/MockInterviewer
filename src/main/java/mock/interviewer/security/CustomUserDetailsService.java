package mock.interviewer.security;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import mock.interviewer.entity.User;
import mock.interviewer.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override       //paramater is email but called username by spring security
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found: " + email));
        return new CustomUserDetails(user);
        ///// since UserDetails is a interface and CustomUserDetials implements it we can return CustomUserDetails obj
        /// (polymorphism)
    }
}
