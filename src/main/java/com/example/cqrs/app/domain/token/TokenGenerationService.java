package com.example.cqrs.app.domain.token;

import com.example.cqrs.security.jwt.config.JWTConfiguration;
import com.google.common.collect.Lists;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TokenGenerationService {
    private final JWTConfiguration jwtConfiguration;
    private static final String HTTP_AUTH_PREFIX = "Bearer ";
    private static final String WORKGROUP = "workgroup";
    private static final String WORKGROUP_CODE = "workgroupCode";
    private static final String CLIENT = "client";
    private static final String CLIENT_NAME = "clientName";
    private static final String CLAIM_KEY_ROLES = "roles";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String CLAIM_KEY_USER_TYPE = "userType";
    private static final String CLAIM_KEY_PRODUCTS = "products";

    public String generateToken() {
        List<String> auths = Lists.newArrayList("ADMIN");
        List<String> products = Lists.newArrayList("INSTITUTIONAL");
        byte[] keyInBytes = Decoders.BASE64.decode(jwtConfiguration.getSecret());
        SecretKey secretKey = Keys.hmacShaKeyFor(keyInBytes);
        JwtBuilder jwtBuilder = Jwts.builder().setId(UUID.randomUUID().toString())
                .setSubject("IshMahajan")
                .claim(CLAIM_KEY_ROLES, auths)
                .claim(CLAIM_KEY_PRODUCTS, products)
                .claim(CLAIM_KEY_CREATED, new Date())
                .claim(CLAIM_KEY_USER_TYPE, "HUMAN")
                .setExpiration(generateExpirationDate())
                .signWith(secretKey);
        return HTTP_AUTH_PREFIX + jwtBuilder.compact();
    }

    private Date generateExpirationDate() {
        return Date.from(ZonedDateTime.now().plusMinutes(jwtConfiguration.getTokenExpiryInMinutes().toMinutes()).toInstant());
    }
}
