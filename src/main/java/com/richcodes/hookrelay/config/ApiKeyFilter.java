package com.richcodes.hookrelay.config;


import com.richcodes.hookrelay.domain.Merchant;
import com.richcodes.hookrelay.services.merchant.MerchantService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    private final MerchantService merchantService;

    public ApiKeyFilter(MerchantService merchantService,
                                   @Lazy UserDetailsService userDetailsService) {
        this.merchantService = merchantService;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {


//       Skipping API KEY validation for auth endpoint
        String path = request.getRequestURI();
        if (path.startsWith("/api/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String apiKeyHeader = request.getHeader("X-API-KEY");
        final String merchantId = request.getHeader("X-MERCHANT-ID");

        // No auth header - continue (let Spring Security decide if this is allowed)
        if (apiKeyHeader == null || merchantId == null) {
            response.setStatus(401);
            return;
        }

        try {
            Merchant merchant = merchantService.isKeyValid(merchantId.trim(),apiKeyHeader);

            request.setAttribute("merchant", merchant);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            System.out.println("hey " + e.getMessage());
            response.setStatus(401);
        }

    }
}