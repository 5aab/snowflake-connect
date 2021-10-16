package com.example.cqrs.security.header;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthorizationRequestHeaderFilter extends RequestHeaderAuthenticationFilter {
    private static final String JANUS_USER = "janus_user";
    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN = "token";
    private static final String DOWNLOAD_URL = "/download";

    public AuthorizationRequestHeaderFilter(AuthenticationManager authenticationManager) {
        super.setCredentialsRequestHeader(JANUS_USER);
        super.setPrincipalRequestHeader(AUTHORIZATION);
        super.setExceptionIfHeaderMissing(false);
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (isDownloadRequest(httpRequest)) {
            AuthorizationHeaderWrapper wrapper = createAuthorizationHeader(httpRequest);
            super.doFilter(wrapper, response, chain);
        } else {
            super.doFilter(request, response, chain);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException, ServletException {
        log.info("preAuthenticatedPrincipal = " + this.getPreAuthenticatedPrincipal(request) + ", trying to authenticate");
        log.info("Authentication success: " + authResult);
        super.successfulAuthentication(request, response, authResult);
    }

    private AuthorizationHeaderWrapper createAuthorizationHeader(HttpServletRequest httpRequest) {
        AuthorizationHeaderWrapper wrapper = new AuthorizationHeaderWrapper(httpRequest);
        wrapper.addHeader(AUTHORIZATION, httpRequest.getParameter(TOKEN));
        return wrapper;
    }

    private boolean isDownloadRequest(HttpServletRequest request) {
        return StringUtils.isNotBlank(request.getHeader(JANUS_USER))
                && StringUtils.isBlank(request.getHeader(AUTHORIZATION))
                && request.getRequestURI().contains(DOWNLOAD_URL)
                && StringUtils.isNotBlank(request.getParameter(TOKEN));
    }

}

