package com.caiohbs.crowdcontrol.config;

import com.caiohbs.crowdcontrol.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public SecurityUtils() {
    }

    public static Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            User userDetails = (User) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        return null;
    }
}