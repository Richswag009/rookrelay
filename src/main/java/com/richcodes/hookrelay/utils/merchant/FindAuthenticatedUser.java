package com.richcodes.hookrelay.utils.merchant;

import com.richcodes.hookrelay.entities.Merchant;
import jakarta.servlet.http.HttpServletRequest;

public interface FindAuthenticatedUser {
    Merchant findAuthenticatedUser();
}
