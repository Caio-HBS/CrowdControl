package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.exception.ValidationErrorException;
import com.caiohbs.crowdcontrol.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class JwtServiceTest {

    @InjectMocks
    JwtService jwtService;

    String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsInVzZXJJZCI6MSwiaWF0IjoxNzIyODg2MjY3LCJleHAiOjQ4Nzg2NDYyNjd9.SjXa_6dpUJYzW3nn8KYqtiETIxLxsu5_ehyFKZE4VE0";
    String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsInVzZXJJZCI6MSwiaWF0IjoxNzIyODg2MjY3LCJleHAiOjE3MjI4ODYyNjd9.vPz-QNASX49y1nSbE7RoBgQAoFKhJwJiTStD5H4zwv4";

    private final User newUser = new User("John", "Doe", "test@email.com", "789",
            LocalDate.now().minusYears(18), LocalDate.now(), null, List.of(), List.of(), null);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(
                jwtService, "SECRET_KEY", "6a26215b3c7525256179415424737a2e766f493d5f654e716658793863274079"
        );
    }

    @Test
    @DisplayName("Should successfully extract username (e-mail) from token")
    void extractUsername_Success() {
        assertEquals("test@email.com", jwtService.extractUsername(validToken));
    }

    @Test
    @DisplayName("Should successfully extract claim from token")
    void extractClaim_Success() {
        assertEquals("test@email.com", jwtService.extractClaim(validToken, Claims::getSubject));
    }

    @Test
    @DisplayName("Should generate a new token and validate it")
    void generateTokenAndValidate_Success() {
        String generatedToken = jwtService.generateToken(Map.of(), newUser);

        assertNotNull(generatedToken);
        assertTrue(jwtService.isTokenValid(generatedToken, newUser));
    }

    @Test
    @DisplayName("Should fail to validate token because it is expired")
    void isTokenValid_Failed() {
        ValidationErrorException e = assertThrows(
                ValidationErrorException.class, () -> jwtService.isTokenValid(invalidToken, newUser)
        );
        assertThat(Objects.equals(e.getMessage(), "Token expired.")).isTrue();
    }

}