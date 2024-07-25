package com.caiohbs.crowdcontrol.config;

import com.caiohbs.crowdcontrol.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * Returns the userId so that the @PreAuthorize annotations on endpoints may
     * check credentials on user.
     *
     * @return The ID of the user if they are authenticated or null if they are
     * not.
     */
    public static Long getAuthUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (
                authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")
        ) {
            User userDetails = (User) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        return null;
    }
}