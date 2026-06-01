package com.richcodes.hookrelay.repository;

import com.richcodes.hookrelay.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, String> {

    boolean existsByEmail(String email);
    Optional<Merchant> findByMerchantId(UUID merchantId);

}
