package com.springSecurity.spring.security.jwt;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.logging.Logger;

@Component
public class JwtUtils {

    private  static  final Logger logger = Logger.getLogger(JwtUtils.class.getName());


    @Value("${spring.app.jwtSecret}")
    private  String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private  int jwtExpirationMs;

    public  String getJwtFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        logger.info("Authorization Header:"+ bearerToken);

        if(bearerToken!=null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public  String generateJwtToken(UserDetails userDetails){
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime()+jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public String getUserNameFromToken(String token){
        return Jwts.parser()
                        .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public  boolean validateJwtToken(String authToken){
        try {
            System.out.println("Validating JWT token");
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return  true;

        } catch (MalformedJwtException e){
            System.out.println( ("Invalid JWT token: {}"+e.getMessage()));
        } catch (ExpiredJwtException e){
//
            System.out.println("JWT token is expired: "+ e.getMessage());
        } catch (UnsupportedJwtException e){
            System.out.println("JWT token is unsupported: "+ e.getMessage());
        } catch (IllegalArgumentException e){
            System.out.println("JWT claims string is empty: "+ e.getMessage());
        } catch (Exception e){
            System.out.println("JWT token is invalid: "+ e.getMessage());
        }

        return false;
    }


}
