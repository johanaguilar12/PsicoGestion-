package mx.uady.psicogestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface JpaPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {

    @Query(value = "SELECT * FROM password_reset_token WHERE token_hash = :tokenHash FOR UPDATE", nativeQuery = true)
    Optional<PasswordResetTokenEntity> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);

    Optional<PasswordResetTokenEntity> findFirstByIdSecretariaOrderByCreatedAtDesc(Integer idSecretaria);

    @Modifying
    @Transactional
    void deleteByIdSecretaria(Integer idSecretaria);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetTokenEntity t WHERE t.expiresAt < :now")
    void deleteExpired(@Param("now") LocalDateTime now);
}
