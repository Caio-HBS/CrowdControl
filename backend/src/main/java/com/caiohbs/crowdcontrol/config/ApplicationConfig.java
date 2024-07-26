package com.caiohbs.crowdcontrol.config;

import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for setting up application-specific beans. This class
 * configures various beans required for both security and authentication.
 */
@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a {@link SecurityUtils} bean.
     *
     * @return a new instance of {@link SecurityUtils}.
     */
    @Bean
    public SecurityUtils securityUtils() {
        return new SecurityUtils();
    }

    /**
     * Creates a {@link UserDetailsService} bean.
     *
     * @return a {@link UserDetailsService} that loads user details from the
     * {@link UserRepository}.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @Bean
    public UserDetailsService userDetailsService() {

        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

    }

    /**
     * Creates an {@link AuthenticationProvider} bean.
     *
     * @return a configured {@link DaoAuthenticationProvider}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;

    }

    /**
     * Creates an {@link AuthenticationManager} bean.
     *
     * @param config the {@link AuthenticationConfiguration} used to configure
     *               the authentication manager.
     * @return the {@link AuthenticationManager} from the specified configuration.
     * @throws Exception if an error occurs while getting the authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Creates a {@link PasswordEncoder} bean.
     *
     * @return a new instance of {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
