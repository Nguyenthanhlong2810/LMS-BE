package ntlong.repository;

import ntlong.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    @Query(value = "select vt from VerificationToken vt where vt.token = :token")
    Optional<VerificationToken> findByToken(String token);
}
