package com.richcodes.hookrelay.utils.merchant;

import com.richcodes.hookrelay.entities.Merchant;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FindAuthenticatedUserImpl
        implements FindAuthenticatedUser {

    @Override
    public Merchant findAuthenticatedUser() {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                        RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new RuntimeException("No active request");
        }

        Merchant merchant =
                (Merchant) attributes
                        .getRequest()
                        .getAttribute("merchant");

        if (merchant == null) {
            throw new RuntimeException("Merchant not authenticated");
        }

        return merchant;
    }
}