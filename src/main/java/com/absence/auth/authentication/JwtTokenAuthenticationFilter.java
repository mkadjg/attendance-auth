package com.absence.auth.authentication;

import com.absence.auth.exceptions.AuthorizationException;
import com.absence.auth.exceptions.SkipFilterException;
import com.absence.auth.factories.ResponseFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;

    public JwtTokenAuthenticationFilter(
            JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable FilterChain chain)
            throws ServletException, IOException {

        try {
            assert request != null;
            String path = request.getServletPath();
            verifyApiPath(path);

            Claims claims = getClaims(request, response);

            String username = getUsername(claims);
            verifyIsForgotPassword(username, path);
            List<String> authorities = getAuthorities(claims);

            setAuth(username, authorities);
        } catch (SkipFilterException ce) {
            // 
        } catch (AuthorizationException ae) {
            assert response != null;
            ResponseFactory.sendUnAuthorizeErrorResponse(response);
            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
        assert chain != null;
        chain.doFilter(request, response);
    }

    private Claims getClaims(HttpServletRequest request, HttpServletResponse response) throws SkipFilterException {
        String authorizationHeader = request.getHeader(jwtConfig.getHeader());
        if(authorizationHeader == null || !authorizationHeader.startsWith(jwtConfig.getPrefix())) {
            throw new SkipFilterException();
        }

        String token = authorizationHeader.replace(jwtConfig.getPrefix(), "");
        Claims claims = Jwts.parser()
                            .setSigningKey(jwtConfig.getSecret().getBytes())
                            .parseClaimsJws(token)
                            .getBody();
        response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);

        return claims;
    }

    private String getUsername(Claims claims) throws AuthorizationException {
        String username = claims.getSubject();
        if (username == null) {
            throw new AuthorizationException();
        }

        return username;
    }

    private List<String> getAuthorities(Claims claims) throws AuthorizationException {
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");
        if (authorities.isEmpty()) {
            throw new AuthorizationException();
        }

        return authorities;
    }



    private void verifyApiPath(String path) throws SkipFilterException{
        if(path.equals("/")) {
            throw new SkipFilterException();
        }
    }

    private void verifyIsForgotPassword(String username, String path) throws SkipFilterException {
        if (path.contains("forgot-password-change-password")) {
            setAuth(username, new ArrayList<>());
            throw new SkipFilterException();
        }
    }

    private void setAuth(String username, List<String> authorities) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
