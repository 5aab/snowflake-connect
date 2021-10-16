package com.example.cqrs.security.jwt;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class JwtUser extends User {
    private static final long serialVersionUID = 1L;

    private UUID workGroupId;
    private String workGroupCode;
    private UUID clientId;
    private String clientName;
    private String userType;
    private Set<String> products = new HashSet<>();

    public JwtUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String userType, Set<String> products) {
        this(username, password, authorities, null, null, null, null, userType, products);
    }

    public JwtUser(String username, String password, Collection<? extends GrantedAuthority> authorities, UUID workGroupId,
                   String workGroupCode, UUID clientId, String clientName, String userType, Set<String> products) {
        super(username, password, authorities);
        this.workGroupId = workGroupId;
        this.workGroupCode = workGroupCode;
        this.clientId = clientId;
        this.clientName = clientName;
        this.userType = userType;
        this.products.addAll(products);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }
}
