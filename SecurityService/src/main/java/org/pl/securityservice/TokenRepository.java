package org.pl.securityservice;

import org.pl.securityservice.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<TokenEntity, UUID> {
    Optional<TokenEntity> findByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
}

