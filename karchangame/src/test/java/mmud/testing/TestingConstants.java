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
package mmud.testing;

import java.time.LocalDate;
import java.time.LocalDateTime;

import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Room;
import mmud.database.enums.Sex;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

/**
 * Generates constants for use in testcases.
 *
 * @author maartenl
 */
public class TestingConstants
{

  public static Area getArea()
  {
    Area area = new Area();
    area.setArea("Main");
    area.setShortdescription("Land of Karchan");
    return area;
  }

  public static Area getSpecialArea()
  {
    Area area = new Area();
    area.setArea("Starship");
    area.setShortdescription("On board the Starship Heart of Gold");
    return area;
  }

  public static Room getRoom(Area aArea)
  {
    Room aRoom = new Room();
    aRoom.setTitle("The bridge");
    aRoom.setArea(aArea);
    aRoom.setContents("You are standing on a small bridge.");
    return aRoom;
  }

  /**
   * @param aRoom
   * @return
   * @throws MudException
   */
  public static User getHotblack(Room aRoom) throws MudException
  {
    User person = new User();
    person.setLastlogin(LocalDateTime.now().plusSeconds(- 1_000L));
    person.setAddress("82-170-94-123.ip.telfort.nl");
    person.setNewpassword("secret"); // sha1 of "marvin"
    person.setRealname(null);
    person.setEmail(null);

    person.setName("Hotblack");
    // JDK7: number formats, for clarification.
    // 1_000_000 ms = 1_000 sec = 16 min, 40 sec
    person.setSleep(Boolean.FALSE);
    person.setTitle("Guitar keyboard player of the rock group Disaster Area");
    person.setRoom(aRoom);
    person.setSex(Sex.MALE);
    person.setRace("undead");

    person.setRace("undead");
    person.setSex(Sex.MALE);
    person.setAge("young");
    person.setHeight("tall");
    person.setWidth("slender");
    person.setComplexion("swarthy");
    person.setEyes("black-eyed");
    person.setFace("long-faced");
    person.setHair("black-haired");
    person.setBeard("none");
    person.setArm("none");
    person.setLeg("none");
    person.setState("Rocking out!");
    CommunicationService.getCommunicationService(person).clearLog();
    return person;
  }

  public static User getMarvin(Room aRoom) throws MudException
  {
    User person = new User();

    person.setLastlogin(LocalDateTime.now().plusSeconds(- 2_000L));
    person.setAddress("82-170-94-123.ip.telfort.nl");
    person.setNewpassword("secret"); // sha1 of "marvin"
    person.setRealname(null);
    person.setEmail(null);

    person.setName("Marvin");
    // JDK7: number formats, for clarification.
    // 2_000_000 ms = 2_000 sec = 33 min, 20 sec
    person.setRoom(aRoom);
    person.setSex(Sex.MALE);
    person.setSleep(Boolean.FALSE);
    person.setRace("android");
    person.setTitle("The Paranoid Android");
    person.setSex(Sex.MALE);
    person.setAge("young");
    person.setHeight("tall");
    person.setWidth("slender");
    person.setComplexion("swarthy");
    person.setEyes("black-eyed");
    person.setFace("long-faced");
    person.setHair("black-haired");
    person.setBeard("none");
    person.setArm("none");
    person.setLeg("none");
    person.setState("Life, don't talk to me about life.");
    CommunicationService.getCommunicationService(person).clearLog();
    return person;
  }

  public static Admin getAdmin()
  {
    Admin admin = new Admin();
    admin.setIp("10.0.0.12");
    admin.setEmail("maarten_l@yahoo.com");
    admin.setName("Karn");
    admin.setValiduntil(LocalDate.now().plusMonths(12));
    return admin;
  }

  public static Guild getGuild()
  {
    Guild guild = new Guild();
    guild.setDescription("Disaster Area");
    guild.setHomepage("http://www.disasterarea.com");
    return guild;
  }

  public static Administrator getKarn()
  {
    Administrator person = new Administrator();

    person.setLastlogin(LocalDateTime.now().plusSeconds(- 2_000L));
    person.setAddress("82-170-94-123.ip.telfort.nl");
    person.setNewpassword("secret"); // sha1 of "marvin"
    person.setRealname(null);
    person.setEmail(null);

    person.setName("Karn");
    // JDK7: number formats, for clarification.
    // 2_000_000 ms = 2_000 sec = 33 min, 20 sec
    person.setSex(Sex.MALE);
    person.setSleep(Boolean.FALSE);
    person.setRace("human");
    person.setTitle("Ruler of Karchan, Keeper of the Key to the Room of Lost Souls");
    person.setSex(Sex.MALE);
    person.setAge("young");
    person.setHeight("tall");
    person.setWidth("slender");
    person.setComplexion("swarthy");
    person.setEyes("black-eyed");
    person.setFace("long-faced");
    person.setHair("black-haired");
    person.setBeard("none");
    person.setArm("none");
    person.setLeg("none");
    person.setState("Amazing!");
    CommunicationService.getCommunicationService(person).clearLog();
    return person;
  }

}
