package com.bridge.herofincorp.configs;

import com.bridge.herofincorp.service.impls.CustomUserDetailService;
import com.bridge.herofincorp.service.impls.JWTTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JWTTokenService jwtTokenService;
    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        APILogger logger = new APILogger(request.getRequestURI());
        request.setAttribute("logger", logger);
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String custId = null;
        String userType = null;
        if (authHeader!=null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            try {
                custId = jwtTokenService.verifySignature(token).getJWTClaimsSet().getSubject();
                userType = jwtTokenService.verifySignature(token).getJWTClaimsSet().getClaim("userType").toString();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if(custId!=null && SecurityContextHolder.getContext().getAuthentication()==null){

            UserDetails userDetails = customUserDetailService.loadUserByUsername(userType+"&"+custId);

            try {
                if(jwtTokenService.validateToken(token,userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,token,userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        filterChain.doFilter(request,response);
    }
}