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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mmud.Attributes;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Guildrank;
import mmud.database.entities.game.GuildrankPK;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.PrivateGuild;
import mmud.rest.webentities.PrivatePerson;
import mmud.rest.webentities.PrivateRank;

/**
 * Takes care of the guilds. All the REST services in this bean, can be subdivided
 * into two categories:<ul><li>persons who are a member of the guild, can view information of the guild</li>
 * <li>guildmasters (who are also member of the guild) can change/delete/add information to the guild</li></ul>
 * TODO: need to add different access rights.
 *
 * @author maartenl
 */
@Stateless
@LocalBean
@Path("/private/{name}/guild")
public class GuildBean
{

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    @EJB
    private PrivateBean privateBean;

    @EJB
    private PersonBean personBean;

    /**
     * Returns the entity manager of Hibernate/JPA. This is defined in
     * build/web/WEB-INF/classes/META-INF/persistence.xml.
     *
     * @return EntityManager
     */
    protected EntityManager getEntityManager()
    {
        return em;
    }
    private static final Logger itsLog = Logger.getLogger(GuildBean.class.getName());

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
     * returns a list of persons that wish to
     * become a member of a guild.
     *
     * @return list of guild hopefuls.
     * @param aGuild
     * the guild
     * @throws MudException
     * if something goes wrong.
     */
    public List<User> getGuildHopefuls(Guild aGuild)
    {
        Query query = getEntityManager().createNamedQuery("Guild.findGuildHopefuls");
        query.setParameter("guildname", aGuild.getName());
        query.setParameter("attributename", Attributes.GUILDWISH);
        query.setParameter("valuetype", "string");

        return query.getResultList();
    }

    /**
     * Get the guild of the user.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @return the guild
     */
    @GET
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public PrivateGuild getGuild(@PathParam("name") String name, @QueryParam("lok") String lok)
    {
        itsLog.finer("entering getGuild");
        getEntityManager().setProperty("activePersonFilter", 0);
        Guild guild = authenticate(name, lok);
        PrivateGuild privateGuild = new PrivateGuild();
        privateGuild.guildurl = guild.getHomepage();
        privateGuild.title = guild.getTitle();
        privateGuild.image = guild.getImage();
        privateGuild.name = guild.getName();
        privateGuild.logonmessage = guild.getLogonmessage();
        if (guild.getBoss() == null)
        {
            itsLog.log(Level.INFO, "guilds: no boss found for guild {0}", guild.getName());
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
     * @param name the name of the user
     * @param lok the session password
     * @param cinfo the guild object containing the new stuff to update.
     * @return Response.ok if everything is okay.
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation
     * failed. BAD_REQUEST if an unexpected exception crops up.
     */
    @PUT
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response updateGuild(@PathParam("name") String name, @QueryParam("lok") String lok, PrivateGuild cinfo)
    {
        itsLog.finer("entering updateGuild");
        getEntityManager().setProperty("activePersonFilter", 0);
        User person = authenticateGuildMaster(name, lok);
        Guild guild = person.getGuild();
        if (!cinfo.bossname.equals(person.getName()))
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
        guild.setTitle(cinfo.title);
        return Response.ok().build();
    }

    /**
     * Creates a new guild.
     *
     * @param name the name of the user
     * @param lok the session password
     * @param cinfo the guild object containing the new stuff to create.
     * @return Response.ok if everything is okay.
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation
     * failed. BAD_REQUEST if an unexpected exception crops up.
     */
    @POST
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response createGuild(@PathParam("name") String name, @QueryParam("lok") String lok, PrivateGuild cinfo)
    {
        itsLog.finer("entering createGuild");
        getEntityManager().setProperty("activePersonFilter", 0);
        User person = privateBean.authenticate(name, lok);
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
        guild.setDaysguilddeath(10);
        guild.setMaxguilddeath(10);
        guild.setMinguildmembers(20);
        try
        {
            getEntityManager().persist(guild);
        } catch (ConstraintViolationException ex)
        {
            StringBuilder buffer = new StringBuilder("ConstraintViolationException:");
            for (ConstraintViolation<?> violation : ex.getConstraintViolations())
            {
                buffer.append(violation);
            }
            itsLog.warning(buffer.toString());
            throw ex;
        }
        person.setGuild(guild);
        return Response.ok().build();
    }

    /**
     * Deletes the guild.
     *
     * @param name the name of the user
     * @param lok the session password
     * @return Response.ok if everything is okay.
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation
     * failed. BAD_REQUEST if an unexpected exception crops up.
     */
    @DELETE
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response deleteGuild(@PathParam("name") String name, @QueryParam("lok") String lok)
    {
        itsLog.finer("entering deleteGuild");
        getEntityManager().setProperty("activePersonFilter", 0);
        User person = authenticateGuildMaster(name, lok);
        Guild guild = person.getGuild();
        for (User guildmember : guild.getMembers())
        {
            guildmember.setGuild(null);
            guildmember.setGuildrank(null);
        }
        Set<User> emptySet = Collections.emptySet();
        guild.setMembers(emptySet);
        for (Guildrank rank : guild.getGuildrankCollection())
        {
            getEntityManager().remove(rank);
        }
        Set<Guildrank> emptyRanks = Collections.emptySet();
        guild.setGuildrankCollection(emptyRanks);
        getEntityManager().remove(guild);
        return Response.ok().build();
    }

    /**
     * Get the hopefuls of the guild of the user.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @return list of hopefuls
     */
    @GET
    @Path("members")
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public List<PrivatePerson> getMembers(@PathParam("name") String name, @QueryParam("lok") String lok)
    {
        itsLog.finer("entering getGuildMembers");
        getEntityManager().setProperty("activePersonFilter", 0);
        Guild guild = authenticate(name, lok);
        Collection<User> members = guild.getMembers();
        List<PrivatePerson> result = new ArrayList<>();
        for (User person : members)
        {
            PrivatePerson privatePerson = new PrivatePerson();
            privatePerson.name = person.getName();
            if (person.getGuildrank() != null)
            {
                privatePerson.guildrank = person.getGuildrank().getTitle();
            }
            result.add(privatePerson);
        }
        return result;
    }

    /**
     * Get the member of the guild of the user.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param membername the name of the guild member
     * @param name the name of the user
     * @return member
     */
    @GET
    @Path("members/{membername}")
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public PrivatePerson getMember(@PathParam("name") String name,
            @PathParam("membername") String membername,
            @QueryParam("lok") String lok)
    {
        itsLog.finer("entering getMember");
        getEntityManager().setProperty("activePersonFilter", 0);
        Guild guild = authenticate(name, lok);
        User member = guild.getMember(membername);
        if (member == null)
        {
            throw new MudWebException(name, membername + " is either not a user or not a member of this guild.", Response.Status.NOT_FOUND);
        }
        PrivatePerson privatePerson = new PrivatePerson();
        privatePerson.name = member.getName();
        if (member.getGuildrank() != null)
        {
            privatePerson.guildrank = member.getGuildrank().getTitle();
            privatePerson.guildlevel = member.getGuildrank().getGuildrankPK().getGuildlevel();
        }
        return privatePerson;
    }

    /**
     * Removes a member from the guild.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param membername the name of the guild member to remove
     * @param name the name of the user
     * @return member
     */
    @DELETE
    @Path("members/{membername}")
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response deleteMember(@PathParam("name") String name,
            @PathParam("membername") String membername,
            @QueryParam("lok") String lok)
    {
        itsLog.finer("entering deleteMember");
        getEntityManager().setProperty("activePersonFilter", 0);
        User person = authenticateGuildMaster(name, lok);
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
        member.setGuildrank(null);
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
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param member the member to add, really should contain only the name,
     * that's enough
     * @return Response.ok if everything is okay.
     */
    @POST
    @Path("members")
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response createMember(@PathParam("name") String name, @QueryParam("lok") String lok, PrivatePerson member)
    {
        itsLog.finer("entering createMember");
        getEntityManager().setProperty("activePersonFilter", 0);
        User person = authenticateGuildMaster(name, lok);
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
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param membername the name of the guild member, to set the guild rank for
     * @param member the member object that contains the changes
     * @param name the name of the user
     * @return Response.ok() if everything's okay.
     */
    @PUT
    @Path("members/{membername}")
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response updateMember(@PathParam("name") String name,
            @PathParam("membername") String membername,
            @QueryParam("lok") String lok, PrivatePerson member)
    {
        itsLog.finer("entering updateMember");
        getEntityManager().setProperty("activePersonFilter", 0);
        User person = authenticateGuildMaster(name, lok);
        Guild guild = person.getGuild();
        User user = guild.getMember(membername);
        if (user == null)
        {
            throw new MudWebException(name, membername + " is either not a user or not a member of this guild.", Response.Status.NOT_FOUND);
        }
        if (member.guildlevel == null)
        {
            user.setGuildrank(null);
            itsLog.finer("updateMember cleared rank");
            return Response.ok().build();
        }
        Guildrank rank = guild.getRank(member.guildlevel);
        if (rank == null)
        {
            throw new MudWebException(name, "Rank " + member.guildlevel + " does not exist in this guild.", Response.Status.NOT_FOUND);
        }
        user.setGuildrank(rank);
        return Response.ok().build();
    }

    /**
     * Get the hopefuls of the guild of the user.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @return list of hopefuls, i.e. people who wish to join the guild
     */
    @GET
    @Path("hopefuls")
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public List<PrivatePerson> getGuildHopefuls(@PathParam("name") String name, @QueryParam("lok") String lok)
    {
        itsLog.finer("entering getGuildHopefuls");
        getEntityManager().setProperty("activePersonFilter", 0);
        Guild guild = authenticate(name, lok);
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
                privatePerson.guildrank = person.getGuildrank().getTitle();
            }
            result.add(privatePerson);
        }
        return result;
    }

    /**
     * Remove a hopeful of the guild of the user.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param hopefulname the name of the hopeful to reject
     * @return Response.Status.ok if everything's okay.
     */
    @DELETE
    @Path("hopefuls/{hopefulname}")
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response deleteGuildHopeful(@PathParam("name") String name,
            @QueryParam("lok") String lok,
            @PathParam("hopefulname") String hopefulname)
    {
        itsLog.finer("entering getGuildHopefuls");
        getEntityManager().setProperty("activePersonFilter", 0);
        User person = authenticateGuildMaster(name, lok);
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
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @return list of hopefuls
     */
    @GET
    @Path("ranks")
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public List<PrivateRank> getGuildRanks(@PathParam("name") String name, @QueryParam("lok") String lok)
    {
        itsLog.finer("entering getGuildRanks");
        getEntityManager().setProperty("activePersonFilter", 0);
        Guild guild = authenticate(name, lok);
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
            PrivateRank privateRank = new PrivateRank();
            privateRank.title = rank.getTitle();
            privateRank.guildlevel = rank.getGuildrankPK().getGuildlevel();
            privateRank.accept_access = rank.getAcceptAccess();
            privateRank.reject_access = rank.getRejectAccess();
            privateRank.settings_access = rank.getSettingsAccess();
            privateRank.logonmessage_access = rank.getLogonmessageAccess();
            result.add(privateRank);
        }
        return result;
    }

    /**
     * Get the ranks of the guild of the user.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param guildlevel the id of the guild rank
     * @return list of hopefuls
     */
    @GET
    @Path("ranks/{guildlevel}")
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public PrivateRank getGuildRank(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("guildlevel") Integer guildlevel)
    {
        itsLog.finer("entering getGuildRank");
        getEntityManager().setProperty("activePersonFilter", 0);
        User person = authenticateGuildMaster(name, lok);
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
        PrivateRank privateRank = new PrivateRank();
        privateRank.title = rank.getTitle();
        privateRank.guildlevel = rank.getGuildrankPK().getGuildlevel();
        privateRank.accept_access = rank.getAcceptAccess();
        privateRank.reject_access = rank.getRejectAccess();
        privateRank.settings_access = rank.getSettingsAccess();
        privateRank.logonmessage_access = rank.getLogonmessageAccess();
        return privateRank;
    }

    /**
     * Create a rank of the guild of the user.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param rank the rank to add
     * @return Response.ok if everything is okay.
     */
    @POST
    @Path("ranks")
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response createGuildRank(@PathParam("name") String name, @QueryParam("lok") String lok, PrivateRank rank)
    {
        itsLog.finer("entering createGuildRank");
        getEntityManager().setProperty("activePersonFilter", 0);
        User person = authenticateGuildMaster(name, lok);
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
            StringBuilder buffer = new StringBuilder("ConstraintViolationException:");
            for (ConstraintViolation<?> violation : ex.getConstraintViolations())
            {
                buffer.append(violation);
            }
            throw new MudWebException(name, buffer.toString(), ex, Response.Status.BAD_REQUEST);
        }
        return Response.ok().build();
    }

    /**
     * Update a rank of the guild of the user.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param guildlevel the guildlevel of the rank
     * @param rank the rank to update
     * @return Response.ok if everything is okay.
     */
    @PUT
    @Path("ranks/{guildlevel}")
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response updateGuildRank(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("guildlevel") Integer guildlevel, PrivateRank rank)
    {
        itsLog.finer("entering updateGuildRank");
        getEntityManager().setProperty("activePersonFilter", 0);
        User person = authenticateGuildMaster(name, lok);
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
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param guildlevel the guildlevel of the rank
     * @return Response.ok if everything is okay.
     */
    @DELETE
    @Path("ranks/{guildlevel}")
    @Consumes(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response deleteGuildRank(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("guildlevel") Integer guildlevel)
    {
        itsLog.finer("entering deleteGuildRank");
        getEntityManager().setProperty("activePersonFilter", 0);
        User person = authenticateGuildMaster(name, lok);
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

    private Guild authenticate(String name, String lok)
    {
        User person = privateBean.authenticate(name, lok);
        final Guild guild = person.getGuild();
        if (guild == null)
        {
            throw new MudWebException(name,
                    "User is not a member of a guild.",
                    "User is not a member of a guild (" + name + ", " + lok + ")",
                    Response.Status.NOT_FOUND);
        }
        return guild;
    }

    private User authenticateGuildMaster(String name, String lok)
    {
        return privateBean.authenticateGuildMaster(name, lok);
    }

}