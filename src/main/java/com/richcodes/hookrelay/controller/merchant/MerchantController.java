package com.richcodes.hookrelay.controller.merchant;

import com.richcodes.hookrelay.dto.auth.MerchantRegisterRequest;
import com.richcodes.hookrelay.entities.Merchant;
import com.richcodes.hookrelay.response.MerchantRegisterResponse;
import com.richcodes.hookrelay.services.merchant.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private final MerchantService merchantService;
    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @Operation(summary = "Register a Merchant")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public MerchantRegisterResponse register(@RequestBody MerchantRegisterRequest merchantRegisterRequest) {
        return merchantService.registerMerchant(merchantRegisterRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public List<MerchantRegisterResponse> getMerchants(){
        return  merchantService.getMerchants();
    }
}
