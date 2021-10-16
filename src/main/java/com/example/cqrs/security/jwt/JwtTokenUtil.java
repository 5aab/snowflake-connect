package com.example.cqrs.security.jwt;

import com.example.cqrs.security.jwt.config.JWTConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
public class JwtTokenUtil implements Serializable {
    private static final String CLAIM_KEY_ROLES = "roles";
    private static final String HTTP_AUTH_PREFIX = "Bearer ";
    private static final String WorkGroup = "WorkGroup";
    private static final String WorkGroup_CODE = "WorkGroupCode";
    private static final String CLIENT = "client";
    private static final String CLIENT_NAME = "clientName";
    private static final String USER_TYPE = "userType";
    private static final String CLAIM_KEY_PRODUCTS = "products";

    private static final long serialVersionUID = -3301605591108950415L;

    private final JWTConfiguration jwtConfiguration;


    public Date getExpirationDateFromToken(Claims claims) {
        Date expiration;
        try {
            expiration = claims.getExpiration();
        } catch (Exception e) {
            log.warn("Error parsing token", e);
            expiration = null;
        }
        return expiration;
    }

    public JwtUser parseAndValidate(String tokenHeader) {
        JwtUser user;
        final Claims claims = getClaimsFromToken(tokenHeader);
        String userId = claims.getSubject();
        List<String> roleNames = (List<String>) claims.get(CLAIM_KEY_ROLES);
        if (isTokenExpired(claims)) {
            throw new BadCredentialsException("Token has expired");
        }
        if (claims.containsKey(WorkGroup)) {
            UUID WorkGroupId = UUID.fromString(claims.get(WorkGroup, String.class));
            UUID clientId = UUID.fromString(claims.get(CLIENT, String.class));
            user = new JwtUser(userId, "", authorities(roleNames), WorkGroupId,
                    claims.get(WorkGroup_CODE, String.class), clientId, claims.get(CLIENT_NAME, String.class),
                    claims.get(USER_TYPE, String.class), getProducts(claims));
        } else {
            user = new JwtUser(userId, "", roleNames.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()),
                    claims.get(USER_TYPE, String.class), getProducts(claims));
        }
        return user;
    }

    private List<SimpleGrantedAuthority> authorities(List<String> roleNames) {
        return roleNames.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private Set<String> getProducts(Claims claims) {
        List<String> products = (List<String>) claims.get(CLAIM_KEY_PRODUCTS);
        return products == null ? new HashSet() : products.stream().collect(Collectors.toSet());
    }

    private Claims getClaimsFromToken(String token) {
        String authtoken = token;
        if (token != null && token.startsWith(HTTP_AUTH_PREFIX)) {
            authtoken = token.substring(HTTP_AUTH_PREFIX.length());
        }
        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(jwtConfiguration.getSecret()).build();
            return parser.parseClaimsJws(authtoken).getBody();
        } catch (Exception e) {
            log.error("JWT Token invalid {}", token, e);
            throw new BadCredentialsException("JWT Token invalid");
        }
    }

    private boolean isTokenExpired(Claims claims) {
        final Date expiration = getExpirationDateFromToken(claims);
        return expiration.before(new Date());
    }
}