/*
 * Copyright (C) 2019 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.karchan.security;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author maartenl
 */
@ApplicationScoped
public class JsonWebTokenProvider
{

  private static final Logger LOGGER = Logger.getLogger(JsonWebTokenProvider.class.getName());

  private static final String AUTHORITIES_KEY = "auth";

  /**
   * For example : "my-secret-jwt-key".
   */
  @Inject
  @ConfigProperty(name = "karchan.jwt.secret.key")
  private String secretKey;

  /**
   * Validity of token in the REMEMBER_ME cookie is 24 hours.
   */
  private final long TOKEN_VALIDITY_HOURS = 24;

  public String createToken(String username, Set<String> authorities)
  {
    long now = Instant.now().toEpochMilli();

    byte[] keyBytes = getSecretKey();
    Key key = Keys.hmacShaKeyFor(keyBytes);

    return Jwts.builder()
      .setSubject(username)
      .claim(AUTHORITIES_KEY, String.join(",", authorities))
      .signWith(key, SignatureAlgorithm.HS512)
      .setExpiration(Date.from(Instant.now().plus(TOKEN_VALIDITY_HOURS, ChronoUnit.HOURS)))
      .compact();
  }

  public JsonWebTokenCredential getCredential(String token)
  {
    byte[] keyBytes = getSecretKey();

    Claims claims = Jwts.parserBuilder().setSigningKey(keyBytes).build()
      .parseClaimsJws(token)
      .getBody();

    Set<String> authorities
      = new HashSet<>(Arrays.asList(claims.get(AUTHORITIES_KEY).toString().split(",")));

    return new JsonWebTokenCredential(claims.getSubject(), authorities);
  }

  public boolean validateToken(String authToken)
  {
    try
    {
      byte[] keyBytes = getSecretKey();
      Jwts.parserBuilder().setSigningKey(keyBytes).build().parseClaimsJws(authToken);
      return true;
    } catch (JwtException e)
    {
      LOGGER.log(Level.SEVERE, "Invalid JWT signature", e);
      return false;
    }
  }

  private byte[] getSecretKey()
  {
    return Base64.getUrlDecoder().decode(secretKey);
  }

}
