package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.exception.ValidationErrorException;
import com.caiohbs.crowdcontrol.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for handling JWT operations such as token generation, validation,
 * and claim extraction.
 */
@Service
public class JwtService {

    @Value("${crowdcontrol.vars.SECRET_KEY}")
    private String SECRET_KEY;

    /**
     * Extracts the username from the JWT token.
     *
     * @param token the JWT token.
     * @return the username extracted from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token using a claims resolver
     * function.
     *
     * @param <T>            the type of the claim.
     * @param token          the JWT token.
     * @param claimsResolver a function to resolve the claim from the token's claims.
     * @return the resolved claim.
     */
    public <T> T extractClaim(
            String token, Function<Claims, T> claimsResolver
    ) {

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);

    }

    /**
     * Generates a JWT token for the specified user without additional claims.
     *
     * @param userDetails the user details for whom the token is to be generated.
     * @return the generated JWT token.
     */
    public String generateToken(User userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Validates the JWT token against the provided user details.
     *
     * @param token       the JWT token to be validated.
     * @param userDetails the user details to be matched against the token.
     * @return {@code true} if the token is valid, {@code false} otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUsername(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }

    /**
     * Checks if the JWT token is expired.
     *
     * @param token the JWT token to be checked.
     * @return {@code true} if the token is expired, {@code false} otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token.
     * @return the expiration date extracted from the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generates a JWT token for the specified user with additional claims.
     *
     * @param extraClaims a map of additional claims to be included in the token.
     * @param userDetails the user details for whom the token is to be generated.
     * @return the generated JWT token.
     */
    public String generateToken(
            Map<String, Object> extraClaims, User userDetails
    ) {

        return Jwts.builder()
                .claims()
                .subject(userDetails.getUsername())
                .add(extraClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 604800000)) // 7 days.
                .and()
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();

    }

    /**
     * Extracts every claim in the JWT token.
     *
     * @param token the JWT token to be parsed.
     * @return the {@link Claims} extracted from the token.
     * @throws ValidationErrorException if the JWT token is invalid or any errors
     *                                  occur during parsing.
     */
    private Claims extractAllClaims(
            String token
    ) throws ValidationErrorException {

        try {
            return Jwts
                    .parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new ValidationErrorException("Token expired.");
        } catch (UnsupportedJwtException e) {
            throw new ValidationErrorException("Token not supported.");
        } catch (MalformedJwtException e) {
            throw new ValidationErrorException("Token malformed.");
        } catch (SignatureException e) {
            throw new ValidationErrorException("Invalid signature.");
        } catch (IllegalArgumentException e) {
            throw new ValidationErrorException("Token invalid.");
        }

    }

    /**
     * Retrieves the singing key used for JWT operations.
     *
     * @return the singing key.
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);
    }

}
