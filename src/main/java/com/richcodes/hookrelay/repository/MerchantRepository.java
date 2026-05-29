package com.richcodes.hookrelay.repository;

import com.richcodes.hookrelay.entities.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, String> {

    boolean existsByEmail(String email);

}
