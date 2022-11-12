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
package mmud.rest.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mmud.database.Attributes;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Guildrank;
import mmud.database.entities.game.GuildrankPK;
import mmud.exceptions.ExceptionUtils;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.PrivateGuild;
import mmud.rest.webentities.PrivatePerson;
import mmud.rest.webentities.PrivateRank;
import mmud.services.PersonBean;

/**
 * Takes care of the guilds. All the REST services in this bean, can be
 * subdivided into two categories:<ul><li>persons who are a member of the guild,
 * can view information of the guild</li>
 * <li>guildmasters (who are also member of the guild) can change/delete/add
 * information to the guild</li></ul>
 * TODO: need to add different access rights.
 *
 * @author maartenl
 */
@DeclareRoles(
  {
    "player", "guildmaster", "guildmember"
  })
@RolesAllowed("player")
@Path("/private/{name}/guild")
public class GuildRestService
{

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private PrivateRestService privateRestService;

  @Inject
  private PersonBean personBean;

  /**
   * Returns the entity manager of JPA. This is defined in
   * build/web/WEB-INF/classes/META-INF/persistence.xml.
   *
   * @return EntityManager
   */
  protected EntityManager getEntityManager()
  {
    return em;
  }

  private static final Logger LOGGER = Logger.getLogger(GuildRestService.class.getName());

  /**
   * Retrieves the guild by name. Returns null if not found.
   *
   * @param name the name of the guild.
   * @return a guild or null.
   */
  public Guild getGuild(String name)
  {
    return getEntityManager().find(Guild.class, name);

  }

  /**
   * returns a list of persons that wish to become a member of a guild.
   *
   * @param aGuild the guild
   * @return list of guild hopefuls.
   * @throws MudException if something goes wrong.
   */
  public List<User> getGuildHopefuls(Guild aGuild)
  {
    TypedQuery<Person> query = getEntityManager().createNamedQuery("Guild.findGuildHopefuls", Person.class);
    query.setParameter("guildname", aGuild.getName());
    query.setParameter("attributename", Attributes.GUILDWISH);
    query.setParameter("valuetype", "string");

    return query.getResultList().stream()
      .filter(Person::isUser)
      .map(person -> (User) person)
      .collect(Collectors.toList());
  }

  /**
   * Get the guild of the user.
   *
   * @param name the name of the user
   * @return the guild
   */
  @GET
  @RolesAllowed("guildmember")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public PrivateGuild getGuildInfo(@PathParam("name") String name)
  {
    LOGGER.finer("entering getGuild");
    Guild guild = authenticate(name);
    PrivateGuild privateGuild = new PrivateGuild();
    privateGuild.guildurl = guild.getHomepage();
    privateGuild.title = guild.getTitle();
    privateGuild.image = guild.getImage();
    privateGuild.name = guild.getName();
    privateGuild.colour = guild.getColour();
    privateGuild.logonmessage = guild.getLogonmessage();
    if (guild.getBoss() == null)
    {
      LOGGER.log(Level.INFO, "guilds: no boss found for guild {0}", guild.getName());
    } else
    {
      privateGuild.bossname = guild.getBoss().getName();
    }
    privateGuild.guilddescription = guild.getDescription();
    privateGuild.creation = guild.getCreation();
    return privateGuild;
  }

  /**
   * Updates the guild.
   *
   * @param name  the name of the user
   * @param cinfo the guild object containing the new stuff to update.
   * @return Response.ok if everything is okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
   *                                 BAD_REQUEST if an unexpected exception crops up.
   */
  @PUT
  @RolesAllowed("guildmaster")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response updateGuild(@PathParam("name") String name, PrivateGuild cinfo)
  {
    LOGGER.finer("entering updateGuild");
    User person = authenticateGuildMaster(name);
    Guild guild = person.getGuild();
    if (cinfo.bossname == null || !cinfo.bossname.equals(person.getName()))
    {
      User newGuildMaster = personBean.getUser(cinfo.bossname);
      if (newGuildMaster == null)
      {
        throw new MudWebException(name, "New boss " + cinfo.bossname + " not found.", Response.Status.NOT_FOUND);
      }
      guild.setBoss(newGuildMaster);
    }
    guild.setDescription(cinfo.guilddescription);
    guild.setHomepage(cinfo.guildurl);
    guild.setImage(cinfo.image);
    guild.setLogonmessage(cinfo.logonmessage);
    guild.setColour(cinfo.colour);
    guild.setTitle(cinfo.title);
    return Response.ok().build();
  }

  /**
   * Creates a new guild.
   *
   * @param name  the name of the user
   * @param cinfo the guild object containing the new stuff to create.
   * @return Response.ok if everything is okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
   *                                 BAD_REQUEST if an unexpected exception crops up.
   */
  @POST
  @RolesAllowed("player")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response createGuild(@PathParam("name") String name, PrivateGuild cinfo)
  {
    LOGGER.finer("entering createGuild");
    User person = privateRestService.authenticate(name);
    if (person.getGuild() != null)
    {
      throw new MudWebException(name, "You are already a member of a guild and therefore cannot start a guild.", Response.Status.UNAUTHORIZED);
    }
    Guild guild = new Guild();
    guild.setDescription(cinfo.guilddescription);
    guild.setHomepage(cinfo.guildurl);
    guild.setImage(cinfo.image);
    guild.setLogonmessage(cinfo.logonmessage);
    guild.setTitle(cinfo.title);
    guild.setBoss(person);
    guild.setName(cinfo.name);
    guild.setActive(Boolean.TRUE);
    guild.setColour(cinfo.colour);
    guild.setDaysguilddeath(10);
    guild.setMaxguilddeath(10);
    guild.setMinguildmembers(20);
    try
    {
      getEntityManager().persist(guild);
    } catch (ConstraintViolationException ex)
    {
      LOGGER.warning(ExceptionUtils.createMessage(ex));
      throw ex;
    }
    person.setGuild(guild);
    return Response.ok().build();
  }

  /**
   * Deletes the guild.
   *
   * @param name the name of the user
   * @return Response.ok if everything is okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
   *                                 BAD_REQUEST if an unexpected exception crops up.
   */
  @DELETE
  @RolesAllowed("guildmaster")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response deleteGuildRest(@PathParam("name") String name)
  {
    deleteGuild(name);
    return Response.ok().build();
  }

  public void deleteGuild(String name)
  {
    LOGGER.finer("entering deleteGuild");
    User person = authenticateGuildMaster(name);
    Guild guild = person.getGuild();
    for (User guildmember : guild.getMembers())
    {
      guildmember.setGuild(null);
    }
    SortedSet<User> emptySet = new TreeSet<>();
    guild.setMembers(emptySet);
    for (Guildrank rank : guild.getGuildrankCollection())
    {
      getEntityManager().remove(rank);
    }
    SortedSet<Guildrank> emptyRanks = new TreeSet<>();
    guild.setGuildrankCollection(emptyRanks);
    getEntityManager().remove(guild);
  }

  /**
   * Get all members of the guild of the user.
   *
   * @param name the name of the user
   * @return list of guild members
   */
  @GET
  @Path("members")
  @RolesAllowed("guildmember")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public List<PrivatePerson> getMembers(@PathParam("name") String name)
  {
    LOGGER.finer("entering getGuildMembers");
    Guild guild = authenticate(name);
    Collection<User> members = guild.getMembers();
    List<PrivatePerson> result = new ArrayList<>();
    for (User person : members)
    {
      PrivatePerson privatePerson = new PrivatePerson();
      privatePerson.name = person.getName();
      if (person.getGuildrank() != null)
      {
        privatePerson.guildrank = PrivateRank.createRank(person.getGuildrank());
      }
      result.add(privatePerson);
    }
    Collections.sort(result, (PrivatePerson o1, PrivatePerson o2) -> o1.name.compareTo(o2.name));
    return result;
  }

  /**
   * Get the member of the guild of the user.
   *
   * @param membername the name of the guild member
   * @param name       the name of the user
   * @return member
   */
  @GET
  @Path("members/{membername}")
  @RolesAllowed("guildmember")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public PrivatePerson getMember(@PathParam("name") String name,
                                 @PathParam("membername") String membername)
  {
    LOGGER.finer("entering getMember");
    Guild guild = authenticate(name);
    User member = guild.getMember(membername);
    if (member == null)
    {
      throw new MudWebException(name, membername + " is either not a user or not a member of this guild.", Response.Status.NOT_FOUND);
    }
    PrivatePerson privatePerson = new PrivatePerson();
    privatePerson.name = member.getName();
    if (member.getGuildrank() != null)
    {
      privatePerson.guildrank = PrivateRank.createRank(member.getGuildrank());
    }
    return privatePerson;
  }

  /**
   * Removes a member from the guild.
   *
   * @param membername the name of the guild member to remove
   * @param name       the name of the user
   * @return member
   */
  @DELETE
  @Path("members/{membername}")
  @RolesAllowed("guildmaster")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response deleteMember(@PathParam("name") String name,
                               @PathParam("membername") String membername)
  {
    LOGGER.finer("entering deleteMember");
    User person = authenticateGuildMaster(name);
    Guild guild = person.getGuild();
    if (membername != null && membername.equals(person.getName()))
    {
      throw new MudWebException(name, "Cannot remove the guildmaster of guild " + guild.getName(), Response.Status.BAD_REQUEST);
    }
    User member = guild.getMember(membername);
    if (member == null)
    {
      throw new MudWebException(name, membername + " is either not a user or not a member of this guild.", Response.Status.NOT_FOUND);
    }
    member.setGuild(null);
    return Response.ok().build();
  }

  /**
   * Adds a member to the guild of the user. There are some checks made:
   * <ul><li>possible member should exist</li>
   * <li>should be a user, not a bot</li>
   * <li>should not be a member of a guild</li>
   * <li>should have a guildwish corresponding to the name of the guild>/li>
   * </ul>
   *
   * @param name   the name of the user
   * @param member the member to add, really should contain only the name,
   *               that's enough
   * @return Response.ok if everything is okay.
   */
  @POST
  @Path("members")
  @RolesAllowed("guildmaster")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response createMember(@PathParam("name") String name, PrivatePerson member)
  {
    LOGGER.finer("entering createMember");
    User person = authenticateGuildMaster(name);
    Guild guild = person.getGuild();
    String membername = member.name;
    User possibleMember = personBean.getUser(membername);
    if (possibleMember == null)
    {
      throw new MudWebException(name, membername + " is not a user.", Response.Status.NOT_FOUND);
    }
    if (possibleMember.getGuild() != null)
    {
      throw new MudWebException(name, membername + " is already part of a guild.", Response.Status.NOT_FOUND);
    }
    if (!possibleMember.verifyAttribute(Attributes.GUILDWISH, guild.getName()))
    {
      throw new MudWebException(name, membername + " has no appropriate guildwish.", Response.Status.NOT_FOUND);
    }
    possibleMember.setGuild(guild);
    possibleMember.removeAttribute(Attributes.GUILDWISH);
    return Response.ok().build();
  }

  /**
   * Set or deletes the rank of a member of the guild of the user.
   *
   * @param membername the name of the guild member, to set the guild rank for
   * @param member     the member object that contains the changes
   * @param name       the name of the user
   * @return Response.ok() if everything's okay.
   */
  @PUT
  @Path("members/{membername}")
  @RolesAllowed("guildmaster")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response updateMember(@PathParam("name") String name,
                               @PathParam("membername") String membername, PrivatePerson member)
  {
    LOGGER.finer("entering updateMember");
    User person = authenticateGuildMaster(name);
    Guild guild = person.getGuild();
    User user = guild.getMember(membername);
    if (user == null)
    {
      throw new MudWebException(name, membername + " is either not a user or not a member of this guild.", Response.Status.NOT_FOUND);
    }
    if (member.guildrank == null)
    {
      user.setGuildrank(null);
      LOGGER.finer("updateMember cleared rank");
      return Response.ok().build();
    }
    Guildrank rank = guild.getRank(member.guildrank.guildlevel);
    if (rank == null)
    {
      throw new MudWebException(name, "Rank " + member.guildrank.guildlevel + " does not exist in this guild.", Response.Status.NOT_FOUND);
    }
    user.setGuildrank(rank);
    return Response.ok().build();
  }

  /**
   * Get the hopefuls of the guild of the user.
   *
   * @param name the name of the user
   * @return list of hopefuls, i.e. people who wish to join the guild
   */
  @GET
  @Path("hopefuls")
  @RolesAllowed("guildmember")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public List<PrivatePerson> getGuildHopefuls(@PathParam("name") String name)
  {
    LOGGER.finer("entering getGuildHopefuls");
    Guild guild = authenticate(name);
    Collection<User> hopefuls = getGuildHopefuls(guild);
    List<PrivatePerson> result = new ArrayList<>();
    for (User person : hopefuls)
    {
      PrivatePerson privatePerson = new PrivatePerson();
      privatePerson.name = person.getName();
      if (person.getGuild() != null)
      {
        privatePerson.guild = person.getGuild().getTitle();
      }
      if (person.getGuildrank() != null)
      {
        privatePerson.guildrank = PrivateRank.createRank(person.getGuildrank());
      }
      result.add(privatePerson);
    }
    Collections.sort(result, new Comparator<PrivatePerson>()
    {

      @Override
      public int compare(PrivatePerson o1, PrivatePerson o2)
      {
        return o1.name.compareTo(o2.name);
      }
    });
    return result;
  }

  /**
   * Remove a hopeful of the guild of the user.
   *
   * @param name        the name of the user
   * @param hopefulname the name of the hopeful to reject
   * @return Response.Status.ok if everything's okay.
   */
  @DELETE
  @Path("hopefuls/{hopefulname}")
  @RolesAllowed("guildmaster")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response deleteGuildHopeful(@PathParam("name") String name,
                                     @PathParam("hopefulname") String hopefulname)
  {
    LOGGER.finer("entering getGuildHopefuls");
    User person = authenticateGuildMaster(name);
    Guild guild = person.getGuild();
    User possibleMember = personBean.getUser(hopefulname);
    if (possibleMember == null)
    {
      throw new MudWebException(name, hopefulname + " is not a user.", Response.Status.NOT_FOUND);
    }
    possibleMember.removeAttribute(Attributes.GUILDWISH);
    return Response.ok().build();
  }

  /**
   * Get the ranks of the guild of the user.
   *
   * @param name the name of the user
   * @return list of hopefuls
   */
  @GET
  @Path("ranks")
  @RolesAllowed("guildmember")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public List<PrivateRank> getGuildRanks(@PathParam("name") String name)
  {
    LOGGER.finer("entering getGuildRanks");
    Guild guild = authenticate(name);
    Guildrank[] ranks = guild.getGuildrankCollection().toArray(new Guildrank[0]);
    Arrays.sort(ranks, new Comparator<Guildrank>()
    {

      @Override
      public int compare(Guildrank o1, Guildrank o2
      )
      {
        return Integer.valueOf(o1.getGuildrankPK().getGuildlevel()).compareTo(Integer.valueOf(o2.getGuildrankPK().getGuildlevel()));
      }
    });
    List<PrivateRank> result = new ArrayList<>();
    for (Guildrank rank : ranks)
    {
      result.add(PrivateRank.createRank(rank));
    }
    Collections.sort(result, new Comparator<PrivateRank>()
    {

      @Override
      public int compare(PrivateRank o1, PrivateRank o2)
      {
        return o1.guildlevel.compareTo(o2.guildlevel);
      }
    });
    return result;
  }

  /**
   * Get the ranks of the guild of the user.
   *
   * @param name       the name of the user
   * @param guildlevel the id of the guild rank
   * @return list of hopefuls
   */
  @GET
  @Path("ranks/{guildlevel}")
  @RolesAllowed("guildmember")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public PrivateRank getGuildRank(@PathParam("name") String name, @PathParam("guildlevel") Integer guildlevel)
  {
    LOGGER.finer("entering getGuildRank");
    User person = authenticateGuildMaster(name);
    Guild guild = person.getGuild();

    if (guildlevel == null)
    {
      throw new MudWebException(name, "Rank was not received.", "guildlevel was null, never found.", Response.Status.NOT_FOUND);
    }
    Guildrank rank = guild.getRank(guildlevel);
    if (rank == null)
    {
      throw new MudWebException(name, "Rank " + guildlevel + " of guild " + guild.getName() + " not found.", Response.Status.NOT_FOUND);
    }
    return PrivateRank.createRank(rank);
  }

  /**
   * Create a rank of the guild of the user.
   *
   * @param name the name of the user
   * @param rank the rank to add
   * @return Response.ok if everything is okay.
   */
  @POST
  @RolesAllowed("guildmaster")
  @Path("ranks")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response createGuildRank(@PathParam("name") String name, PrivateRank rank)
  {
    LOGGER.finer("entering createGuildRank");
    User person = authenticateGuildMaster(name);
    Guild guild = person.getGuild();
    Guildrank newRank = new Guildrank();
    newRank.setAcceptAccess(rank.accept_access);
    newRank.setGuild(guild);
    newRank.setLogonmessageAccess(rank.logonmessage_access);
    newRank.setRejectAccess(rank.reject_access);
    newRank.setSettingsAccess(rank.settings_access);
    newRank.setTitle(rank.title);
    GuildrankPK pk = new GuildrankPK();
    pk.setGuildlevel(rank.guildlevel);
    pk.setGuildname(guild.getName());
    newRank.setGuildrankPK(pk);
    try
    {
      getEntityManager().persist(newRank);
    } catch (ConstraintViolationException ex)
    {
      throw new MudWebException(name, ExceptionUtils.createMessage(ex), ex, Response.Status.BAD_REQUEST);
    }
    return Response.ok().build();
  }

  /**
   * Update a rank of the guild of the user.
   *
   * @param name       the name of the user
   * @param guildlevel the guildlevel of the rank
   * @param rank       the rank to update
   * @return Response.ok if everything is okay.
   */
  @PUT
  @RolesAllowed("guildmaster")
  @Path("ranks/{guildlevel}")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response updateGuildRank(@PathParam("name") String name, @PathParam("guildlevel") Integer guildlevel, PrivateRank rank)
  {
    LOGGER.finer("entering updateGuildRank");
    User person = authenticateGuildMaster(name);
    Guild guild = person.getGuild();

    if (guildlevel == null)
    {
      throw new MudWebException(name, "Rank was not received.", "guildlevel was null, never found.", Response.Status.NOT_FOUND);
    }
    Guildrank guildrank = guild.getRank(guildlevel);
    if (guildrank == null)
    {
      throw new MudWebException(name, "Rank " + guildlevel + " of guild " + guild.getName() + " not found.", Response.Status.NOT_FOUND);
    }
    guildrank.setAcceptAccess(rank.accept_access);
    guildrank.setLogonmessageAccess(rank.logonmessage_access);
    guildrank.setRejectAccess(rank.reject_access);
    guildrank.setSettingsAccess(rank.settings_access);
    guildrank.setTitle(rank.title);
    return Response.ok().build();
  }

  /**
   * Delete a rank of the guild of the user.
   *
   * @param name       the name of the user
   * @param guildlevel the guildlevel of the rank
   * @return Response.ok if everything is okay.
   */
  @DELETE
  @RolesAllowed("guildmaster")
  @Path("ranks/{guildlevel}")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response deleteGuildRank(@PathParam("name") String name, @PathParam("guildlevel") Integer guildlevel)
  {
    LOGGER.finer("entering deleteGuildRank");
    User person = authenticateGuildMaster(name);
    Guild guild = person.getGuild();

    if (guildlevel == null)
    {
      throw new MudWebException(name, "Rank was not received.", "guildlevel was null, never found.", Response.Status.NOT_FOUND);
    }
    Guildrank guildrank = guild.getRank(guildlevel);
    if (guildrank == null)
    {
      throw new MudWebException(name, "Rank " + guildlevel + " of guild " + guild.getName() + " not found.", Response.Status.NOT_FOUND);
    }
    getEntityManager().remove(guildrank);
    return Response.ok().build();
  }

  private Guild authenticate(String name)
  {
    User person = privateRestService.authenticate(name);
    final Guild guild = person.getGuild();
    if (guild == null)
    {
      throw new MudWebException(name,
        "User is not a member of a guild.",
        "User is not a member of a guild (" + name + ")",
        Response.Status.NOT_FOUND);
    }
    return guild;
  }

  @VisibleForTesting
  protected User authenticateGuildMaster(String name)
  {
    return privateRestService.authenticateGuildMaster(name);
  }

}
