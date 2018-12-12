package com.example.management.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends  OncePerRequestFilter {

    private static final String TOKEN_USER_NAME = "user_name";
    private static final String TOKEN_TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN_AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_AUTHORITIES_PARAMETER = "authorities";

    //@Value("${security.jwt.secret:as466gf}")
    private String secret = "as466gf";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(TOKEN_AUTHORIZATION_HEADER);
        if(header == null || !header.startsWith(TOKEN_TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        String token = header.replace(TOKEN_TOKEN_PREFIX, StringUtils.EMPTY);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            if(claims.containsKey(TOKEN_USER_NAME)) {
                @SuppressWarnings("unchecked")
                List<String> authorities = (List<String>) claims.get(TOKEN_AUTHORITIES_PARAMETER);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(claims.get(TOKEN_USER_NAME), null,
                                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(request, response);
    }

}