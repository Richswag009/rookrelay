package com.richcodes.hookrelay.services.merchant;

import com.richcodes.hookrelay.dto.auth.MerchantRegisterRequest;
import com.richcodes.hookrelay.entities.Merchant;
import com.richcodes.hookrelay.response.MerchantRegisterResponse;

import java.util.List;

public interface MerchantService {

    boolean existsByEmail(String email);

    MerchantRegisterResponse registerMerchant(MerchantRegisterRequest merchantRegisterRequest);

    List<MerchantRegisterResponse> getMerchantsByEmail(String email);

    List<MerchantRegisterResponse> getMerchants();

    Merchant isKeyValid(String merchantId, String apiKeyHeader);


}
