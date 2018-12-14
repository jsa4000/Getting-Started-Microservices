package com.example.management.config.security;

import com.example.management.config.bean.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("unchecked")
public class JwtUserControllerFilter implements   Filter  {

    private static final String TOKEN_USER_NAME = "user_name";
    private static final String TOKEN_TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN_AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_RESOURCES_PARAMETER = "resources";
    private static final String TOKEN_AUTHORITIES_PARAMETER = "authorities";
    private static final String TOKEN_ADMIN_AUTHORITY_ROLE = "ROLE_ADMIN";

    private SecurityProperties securityProperties;

    public JwtUserControllerFilter(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public  void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String header = request.getHeader(TOKEN_AUTHORIZATION_HEADER);
        if(header == null || !header.startsWith(TOKEN_TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        Claims claims = Jwts.parser()
                .setSigningKey(securityProperties.getSymmetricKey().getBytes())
                .parseClaimsJws(header.replace(TOKEN_TOKEN_PREFIX, StringUtils.EMPTY))
                .getBody();

        List<String> authorities = (List<String>) claims.get(TOKEN_AUTHORITIES_PARAMETER);
        if (!authorities.stream().anyMatch(x -> x.equals(TOKEN_ADMIN_AUTHORITY_ROLE))) {


            List<String> resources = (List<String>) claims.get(TOKEN_RESOURCES_PARAMETER);
            if (!resources.stream().anyMatch(x -> x.equals(request.getRequestURI()))) {
                throw new AuthorizationServiceException(String.format("User %s not authorized for resource %s",
                        claims.get(TOKEN_USER_NAME), request.getRequestURI()));
            }
        }
        chain.doFilter(request, response);
    }

}