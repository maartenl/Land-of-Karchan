/*
 *  Copyright (C) 2012 maartenl
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
package mmud.testing.tests;

import java.util.*;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.SecurityContext;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.*;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.services.GuildRestService;
import mmud.rest.webentities.PrivateGuild;
import mmud.services.GuildService;
import mmud.services.PlayerAuthenticationService;
import mmud.testing.TestingConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * @author maartenl
 */
public class GuildRestServiceTest {

  // Obtain a suitable LOGGER.
  private static final Logger LOGGER = Logger.getLogger(GuildRestServiceTest.class.getName());

  private User hotblack;
  private User marvin;

  private final PlayerAuthenticationService playerAuthenticationService = new PlayerAuthenticationService() {
    @Override
    public String getPlayerName(SecurityContext context) throws IllegalStateException {
      return "Marvin";
    }

    @Override
    public User authenticate(String name, SecurityContext context) {
      if (name.equals("Marvin")) {
        return marvin;
      } else if (name.equals("Hotblack")) {
        return hotblack;
      }
      return null;
    }
  };

  @BeforeClass
  public void setUpClass() {
  }

  @AfterClass
  public void tearDownClass() {
  }

  @BeforeMethod
  public void setUp() throws MudException {
    Area aArea = TestingConstants.getArea();
    Room aRoom = TestingConstants.getRoom(aArea);
    hotblack = TestingConstants.getHotblack(aRoom);
    marvin = TestingConstants.getMarvin(aRoom);
  }

  @AfterMethod
  public void tearDown() {
  }

  @Test(expectedExceptions= MudWebException.class, expectedExceptionsMessageRegExp = "No guild found for player Marvin")
  public void testGetGuildNoGuild() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    LOGGER.fine("testGetGuild");
    EntityManager entityManager = mock(EntityManager.class);
    marvin.setGuild(null);
    GuildRestService guildRestService = new GuildRestService() {
      @Override
      protected EntityManager getEntityManager() {
        return entityManager;
      }
    };
    guildRestService.setPlayerAuthenticationService(playerAuthenticationService);
    // Unit under test is exercised.
    PrivateGuild result = guildRestService.getGuildInfo("Marvin");
    // Verification code (JUnit/TestNG asserts), if any.
    assertThat(result).isNull();
  }

  @Test
  public void testGetGuild() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    LOGGER.fine("testGetGuild");
    EntityManager entityManager = mock(EntityManager.class);
    final Guild guild = TestingConstants.getGuild(hotblack);
    marvin.setGuild(guild);
    Set<User> members = Set.of(hotblack);
    guild.setMembers(members);
    var guildrank = new Guildrank();
    guildrank.setGuild(guild);
    guildrank.setGuildrankPK(new GuildrankPK(1, guild.getName()));
    guildrank.setTitle("Leader");
    guildrank.setAcceptAccess(true);
    guildrank.setRejectAccess(true);
    guildrank.setLogonmessageAccess(true);
    guildrank.setSettingsAccess(true);
    guild.setGuildranks(Set.of(guildrank));
    GuildRestService guildRestService = new GuildRestService() {
      @Override
      protected EntityManager getEntityManager() {
        return entityManager;
      }
    };
    guildRestService.setPlayerAuthenticationService(playerAuthenticationService);
    guildRestService.setGuildService(new GuildService(){
      @Override
      public List<User> getGuildHopefuls(Guild aGuild) {
        return List.of();
      }
    });
    // Unit under test is exercised.
    PrivateGuild result = guildRestService.getGuildInfo("Marvin");
    // Verification code (JUnit/TestNG asserts), if any.
    assertThat(result).isNotNull();
    assertThat(result.bossname).isEqualTo("Hotblack");
    assertThat(result.guilddescription).isEqualTo("Disaster Area");
    assertThat(result.guildMembers.stream().map(member -> member.name).toList()).isEqualTo(List.of("Hotblack"));
    assertThat(result.guildRanks.stream().map(rank -> rank.title).toList()).isEqualTo(List.of("Leader"));
  }
}
