package com.example.cqrs.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Objects;

public class JsonWebTokenAuthentication extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 5558094452725338211l;
    private JwtUser principal;
    private String jsonWebToken;

    public JsonWebTokenAuthentication(JwtUser principal, String jsonWebToken) {
        super(principal.getAuthorities());
        this.principal = principal;
        this.jsonWebToken = jsonWebToken;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public String getJsonWebToken() {
        return jsonWebToken;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        JsonWebTokenAuthentication that = (JsonWebTokenAuthentication) o;
        return Objects.equals(principal, that.principal) &&
                Objects.equals(jsonWebToken, that.jsonWebToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), principal, jsonWebToken);
    }

}
