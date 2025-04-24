package com.springSecurity.spring.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import static java.lang.System.out;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {


    @Autowired
    private  JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        out.println("AuthTokenFilter called URI: "+request.getRequestURI());
        try{
        String jwt = parseJwt(request);

        if(jwt!=null && jwtUtils.validateJwtToken(jwt)){
            String username = jwtUtils.getUserNameFromToken(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
            out.println("ROLES from JWT: "+userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }else{
            out.println("JWT is null or invalid");
        }
        }catch(Exception e){
            out.println("Cannot set user authetnicatino"+e.getMessage());
        }
        //* continue with the next filter in the chain */
        filterChain.doFilter(request,response);
    }
    private String parseJwt(HttpServletRequest request){
        String jwt = jwtUtils.getJwtFromHeader(request);
        out.println("JWT: "+jwt);
        return  jwt;
    }
}
