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

import static java.util.Arrays.asList;
import java.util.Collections;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

/**
 * Can retrieve the groups a user has access to.
 * @author maartenl
 */
@ApplicationScoped
public class KarchanAuthorizationStore implements IdentityStore
{

  private static final Logger LOGGER = Logger.getLogger(KarchanAuthorizationStore.class.getName());

  /**
   * 
   */
  public static final String PLAYER_ROLE = "player";

  public static final String DEPUTY_ROLE = "deputy";

  private final Map<String, Set<String>> groupsPerCaller;

  public KarchanAuthorizationStore()
  {
    groupsPerCaller = new HashMap<>();
    groupsPerCaller.put("Karn", new HashSet<>(asList(DEPUTY_ROLE, PLAYER_ROLE)));
    groupsPerCaller.put("Marvin", singleton(PLAYER_ROLE));
  }

  @Override
  public Set<String> getCallerGroups(CredentialValidationResult validationResult)
  {
    LOGGER.info("getCallerGroups");
    Set<String> result = groupsPerCaller.get(validationResult.getCallerPrincipal().getName());
    if (result == null)
    {
      result = emptySet();
    }
    LOGGER.info("getCallerGroups" + result);
    return result;
  }

  @Override
  public Set<ValidationType> validationTypes()
  {
    return Collections.singleton(ValidationType.PROVIDE_GROUPS);
  }

  
}
