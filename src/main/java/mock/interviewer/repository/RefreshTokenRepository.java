package mock.interviewer.repository;

import mock.interviewer.entity.User;
import mock.interviewer.security.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    // delete tokens by using User info
    void deleteByUser(User user);
}
