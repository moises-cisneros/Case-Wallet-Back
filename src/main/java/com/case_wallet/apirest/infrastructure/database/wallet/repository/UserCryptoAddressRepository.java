package com.case_wallet.apirest.infrastructure.database.wallet.repository;

import com.case_wallet.apirest.domain.wallet.model.UserCryptoAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para las direcciones de depósito de criptomonedas
 */
@Repository
public interface UserCryptoAddressRepository extends JpaRepository<UserCryptoAddress, Long> {
    
    /**
     * Busca una dirección por usuario y moneda
     */
    Optional<UserCryptoAddress> findByUserIdAndCurrency(Long userId, String currency);
    
    /**
     * Busca todas las direcciones activas de un usuario
     */
    @Query("SELECT uca FROM UserCryptoAddress uca WHERE uca.userId = :userId AND uca.isActive = true")
    List<UserCryptoAddress> findActiveAddressesByUserId(@Param("userId") Long userId);
    
    /**
     * Busca por dirección (útil para el listener de depósitos)
     */
    Optional<UserCryptoAddress> findByAddressAndIsActive(String address, Boolean isActive);
    
    /**
     * Busca todas las direcciones de una moneda específica
     */
    List<UserCryptoAddress> findByCurrencyAndIsActive(String currency, Boolean isActive);
}
