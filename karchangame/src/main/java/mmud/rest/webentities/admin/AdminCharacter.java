/*
 *  Copyright (C) 2020 maartenl
 *   *
 *   * This program is free software: you can redistribute it and/or modify
 *   * it under the terms of the GNU General Public License as published by
 *   * the Free Software Foundation, either version 3 of the License, or
 *   * (at your option) any later version.
 *   *
 *   * This program is distributed in the hope that it will be useful,
 *   * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   * GNU General Public License for more details.
 *   *
 *   * You should have received a copy of the GNU General Public License
 *   * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package mmud.rest.webentities.admin;


import java.time.LocalDateTime;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;

/**
 * @author maartenl
 * @see mmud.database.entities.characters.Person
 */
public class AdminCharacter
{
  public static final String GET_QUERY = "select json_object(\"name\", name, \"room\", room, \"race\", race, \"sex\", sex, \"address\", address, \"owner\", owner, \"creation\", creation_date) from mm_usertable order by name";

  public String name;
  public String address;
  public String image;
  public String title;
  public String realname;
  public String email;
  public String race;
  public String sex;
  public String age;
  public String height;
  public String width;
  public String complexion;
  public String eyes;
  public String face;
  public String hair;
  public String beard;
  public String arm;
  public String leg;
  public Integer copper;
  public Long room;
  public String lok;
  public String whimpy;
  public int experience;
  public String fightingwho;
  public Boolean sleep;
  public Integer punishment;
  public Boolean fightable;
  public Integer vitals;
  public Integer fysically;
  public Integer mentally;
  public Integer drinkstats;
  public Integer eatstats;
  public String active;
  public String lastlogin;
  public LocalDateTime birth;
  public String god;
  public String guild;
  public Integer strength;
  public Integer intelligence;
  public Integer dexterity;
  public Integer constitution;
  public Integer wisdom;
  public Integer practises;
  public Integer training;
  public Integer bandage;
  public Integer alignment;
  public Integer manastats;
  public Integer movementstats;
  public Integer maxmana;
  public Integer maxmove;
  public Integer maxvital;
  public String cgiServerSoftware;
  public String cgiServerName;
  public String cgiGatewayInterface;
  public String cgiServerProtocol;
  public String cgiServerPort;
  public String cgiRequestMethod;
  public String cgiPathInfo;
  public String cgiPathTranslated;
  public String cgiScriptName;
  public String cgiRemoteHost;
  public String cgiRemoteAddr;
  public String cgiAuthType;
  public String cgiRemoteUser;
  public String cgiRemoteIdent;
  public String cgiContentType;
  public String cgiAccept;
  public String cgiUserAgent;
  public Integer jumpmana;
  public Integer jumpmove;
  public Integer jumpvital;
  public String owner;
  public LocalDateTime creation;
  public String notes;
  public String currentstate;
  public LocalDateTime lastcommand;
  public String wieldleft;
  public String wieldright;
  public String wieldboth;
  public String wearhead;
  public String wearneck;
  public String weartorso;
  public String weararms;
  public String wearleftwrist;
  public String wearrightwrist;
  public String wearleftfinger;
  public String wearrightfinger;
  public String wearfeet;
  public String wearhands;
  public String wearfloatingnearby;
  public String wearwaist;
  public String wearlegs;
  public String weareyes;
  public String wearears;
  public String wearaboutbody;
  public String guildlevel;
  public String timeout;
  public boolean visible;
  public Integer rrribbits;
  public Integer heehaws;
  public boolean ooc;

  public AdminCharacter()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminCharacter(User item)
  {
    this((Person) item);
    this.address = item.getAddress();
    this.realname = item.getRealname();
    this.email = item.getEmail();
    this.punishment = item.getPunishment();
    this.guild = item.getGuild() == null ? null : item.getGuild().getName();
    this.cgiServerSoftware = item.getCgiServerSoftware();
    this.cgiServerName = item.getCgiServerName();
    this.cgiGatewayInterface = item.getCgiGatewayInterface();
    this.cgiServerProtocol = item.getCgiServerProtocol();
    this.cgiServerPort = item.getCgiServerPort();
    this.cgiRequestMethod = item.getCgiRequestMethod();
    this.cgiPathInfo = item.getCgiPathInfo();
    this.cgiPathTranslated = item.getCgiPathTranslated();
    this.cgiScriptName = item.getCgiScriptName();
    this.cgiRemoteHost = item.getCgiRemoteHost();
    this.cgiRemoteAddr = item.getCgiRemoteAddr();
    this.cgiAuthType = item.getCgiAuthType();
    this.cgiRemoteUser = item.getCgiRemoteUser();
    this.cgiRemoteIdent = item.getCgiRemoteIdent();
    this.cgiContentType = item.getCgiContentType();
    this.cgiAccept = item.getCgiAccept();
    this.cgiUserAgent = item.getCgiUserAgent();
    this.rrribbits = item.getPunishment();
    this.heehaws = item.getPunishment();
    this.ooc = item.getOoc();
  }

  public AdminCharacter(Person item)
  {
    this.name = item.getName();
    this.image = item.getImage();
    this.title = item.getTitle();
    this.race = item.getRace();
    this.sex = item.getSex().toString();
    this.age = item.getAge();
    this.height = item.getHeight();
    this.width = item.getWidth();
    this.complexion = item.getComplexion();
    this.eyes = item.getEyes();
    this.face = item.getFace();
    this.hair = item.getHair();
    this.beard = item.getBeard();
    this.arm = item.getArm();
    this.leg = item.getLeg();
    this.copper = item.getCopper();
    this.room = item.getRoom() == null ? null : item.getRoom().getId();
    this.whimpy = item.getWimpy().toString();
    this.experience = item.getExperience();
    this.fightingwho = item.getFightingwho();
    this.sleep = item.getSleep();
    this.fightable = item.getFightable();
    this.vitals = item.getVitals();
    this.fysically = item.getFysically();
    this.mentally = item.getMentally();
    this.drinkstats = item.getDrinkstats();
    this.eatstats = item.getEatstats();
//    this.   active=item.getA;
//    this.   lastlogin=item.getLa;
    this.birth = item.getBirth();
    this.god = item.getGod().toString();
    this.strength = item.getStrength();
    this.intelligence = item.getIntelligence();
    this.dexterity = item.getDexterity();
    this.constitution = item.getConstitution();
    this.wisdom = item.getWisdom();
    this.practises = item.getPractises();
    this.training = item.getTraining();
    this.bandage = item.getBandage();
    this.alignment = item.getAlignment();
    this.manastats = item.getManastats();
    this.movementstats = item.getMovementstats();
    this.maxmana = item.getMaxmana();
    this.maxmove = item.getMaxmove();
    this.maxvital = item.getMaxvital();
    this.jumpmana = item.getJumpmana();
    this.jumpmove = item.getJumpmove();
    this.jumpvital = item.getJumpvital();
    this.notes = item.getNotes();
//    this.   wieldleft=item.wields(Wielding.WIELD_LEFT).;
//    this.   wieldright;
//    this.   wieldboth;
//    this.   wearhead;
//    this.   wearneck;
//    this.   weartorso;
//    this.   weararms;
//    this.   wearleftwrist;
//    this.   wearrightwrist;
//    this.   wearleftfinger;
//    this.   wearrightfinger;
//    this.   wearfeet;
//    this.   wearhands;
//    this.   wearfloatingnearby;
//    this.   wearwaist;
//    this.   wearlegs;
//    this.   weareyes;
//    this.   wearears;
//    this.   wearaboutbody;
//    this.   guildlevel;
//    this.   timeout;
    this.visible = item.getVisible();

    this.creation = item.getCreation();
    this.owner = item.getOwner() == null ? null : item.getOwner().getName();
  }

  public String toJson()
  {
    return JsonbBuilder.create().toJson(this);
  }

  public static AdminCharacter fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(json, AdminCharacter.class);
  }
}
