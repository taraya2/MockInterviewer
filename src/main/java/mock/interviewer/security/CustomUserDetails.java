package mock.interviewer.security;

import lombok.Data;
import mock.interviewer.entity.User;
import mock.interviewer.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
@Data
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String email;
    private String passwordHash;
    private String ROLE;
    private String username;

    private  UserRepository userRepository;
    // constructor
    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.ROLE = user.getRole();
        this.username = user.getUsername();
    }

    public Optional<User> toUserEntity() {return userRepository.findByEmail(email);}


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(ROLE + "role"));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }
    public boolean isAccountNonExpired() {
        return true;
    }
    public boolean isAccountNonLocked() {
        return true;
    }
    public boolean isCredentialsNonExpired() { return true;}
    public boolean isEnabled() {return true;}
}

