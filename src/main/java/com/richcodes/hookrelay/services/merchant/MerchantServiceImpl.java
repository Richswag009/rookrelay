package com.richcodes.hookrelay.services.merchant;

import com.richcodes.hookrelay.dto.auth.MerchantRegisterRequest;
import com.richcodes.hookrelay.domain.Merchant;
import com.richcodes.hookrelay.repository.MerchantRepository;
import com.richcodes.hookrelay.response.MerchantRegisterResponse;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MerchantServiceImpl implements MerchantService {
    private final MerchantRepository merchantRepository;
    public MerchantServiceImpl(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return  merchantRepository.existsByEmail(email);
    }

    @Override
    public MerchantRegisterResponse registerMerchant(MerchantRegisterRequest request) {

       boolean emailExist = existsByEmail(request.email());
        if(emailExist){
            throw new IllegalArgumentException("Email already exists");
        }

        String plainApiKey = "hk_live_" + UUID.randomUUID().toString().replace("-", "");

        UUID merchantId = UUID.randomUUID();

        String apiKeyHash = BCrypt.hashpw(plainApiKey, BCrypt.gensalt());

        Merchant merchant = new Merchant();
        merchant.setEmail(request.email());
        merchant.setApiKeyHash(apiKeyHash);
        merchant.setName(request.name());
        merchant.setPhone(request.phone());
        merchant.setMerchantId(merchantId);

        Merchant savedMerchant = merchantRepository.save(merchant);

        return new MerchantRegisterResponse(
                savedMerchant.getId(),
                savedMerchant.getName(),
                savedMerchant.getEmail(),
                plainApiKey,
                savedMerchant.getMerchantId(),
                savedMerchant.getCreatedAt()
        );
    }

    @Override
    public List<MerchantRegisterResponse> getMerchantsByEmail(String email) {
        return List.of();
    }

    @Override
    public List<MerchantRegisterResponse> getMerchants() {
        List<Merchant> merchants= merchantRepository.findAll();

        return  merchants.stream()
                .map(this::convertMerchantResponse)
                .toList();
    }

    public Merchant isKeyValid(String merchantId, String apiKeyHeader) {

        UUID merchantUuid = UUID.fromString(merchantId.trim());

        Merchant merchant = merchantRepository
                .findByMerchantId(merchantUuid)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));
        boolean valid = BCrypt.checkpw(
                apiKeyHeader,
                merchant.getApiKeyHash()
        );

        if (!valid) {
            throw new IllegalArgumentException("Invalid API key");
        }

        return merchant;
    }

    private MerchantRegisterResponse convertMerchantResponse(Merchant merchant){
        return new MerchantRegisterResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getEmail(),
                "",
                merchant.getMerchantId(),
                merchant.getCreatedAt()
        );
    }
}


