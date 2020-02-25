package com.reminder.security;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
class TokenAuthenticationService {
  static final long EXPIRATIONTIME = 864_000_000; // 10 days
  static final String SECRET = "ThisIsASecret";
  static final String TOKEN_PREFIX = "Bearer";
  static final String HEADER_STRING = "Authorization";
  private static Logger logger = Logger.getLogger(JWTLoginFilter.class);

  static void addAuthentication(HttpServletResponse res, String username) {
		logger.info("addAuthentication(res=" + res + ", username=" + username + ") - start - entering addAuthentication");
    String JWT = Jwts.builder()
        .setSubject(username)
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
        .signWith(SignatureAlgorithm.HS512, SECRET)
        .compact();
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
//    res.addHeader("Access-Control-Allow-Origin", "*");
    res.addHeader("access-control-expose-headers", "Authorization");
    res.addHeader("Access-Control-Allow-Headers","Content-Type, Authorization, Access-Control-Request-Method, Access-Control-Request-Headers");
		logger.info("addAuthentication(res=" + res + ", username=" + username + ") - end - exit addAuthentication");
  }

/*static Authentication getAuthentication(HttpServletRequest request) {
    String token = request.getHeader(HEADER_STRING);
    if (token != null) {
      // parse the token.
      String user = Jwts.parser()
          .setSigningKey(SECRET)
          .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
          .getBody()
          .getSubject();

      return user != null ?
          new UsernamePasswordAuthenticationToken(user, null, emptyList()) :
          null;
    }
    return null;
  }*/
}