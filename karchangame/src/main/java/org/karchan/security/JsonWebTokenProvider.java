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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

    return Jwts.builder()
            .setSubject(username)
            .claim(AUTHORITIES_KEY, authorities.stream().collect(joining(",")))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .setExpiration(Date.from(Instant.now().plus(TOKEN_VALIDITY_HOURS, ChronoUnit.HOURS)))
            .compact();
  }

  public JsonWebTokenCredential getCredential(String token)
  {
    Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();

    Set<String> authorities
            = Arrays.asList(claims.get(AUTHORITIES_KEY).toString().split(","))
                    .stream()
                    .collect(Collectors.toSet());

    return new JsonWebTokenCredential(claims.getSubject(), authorities);
  }

  public boolean validateToken(String authToken)
  {
    try
    {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e)
    {
      LOGGER.log(Level.INFO, "Invalid JWT signature: {0}", e.getMessage());
      return false;
    }
  }

}
