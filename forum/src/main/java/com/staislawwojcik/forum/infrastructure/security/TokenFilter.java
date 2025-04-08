package com.staislawwojcik.forum.infrastructure.security;

import com.staislawwojcik.forum.infrastructure.database.user.UserRepository;
import com.staislawwojcik.forum.infrastructure.database.user.UserSession;
import com.staislawwojcik.forum.infrastructure.database.user.UserSessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class TokenFilter extends OncePerRequestFilter {

    private final UserDetailsServiceJPA userDetailsService;
    private final UserSessionRepository userSessionRepository;

    public TokenFilter(UserDetailsServiceJPA userDetailsService, UserSessionRepository userSessionRepository) {
        this.userDetailsService = userDetailsService;
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("token");
        if(token != null){
            UserSession userSession = userSessionRepository.findById(token).orElse(null);
            System.out.println("User session" + userSession);
            if(userSession != null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(userSession.getLoggedUser().getLogin());
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }

}
