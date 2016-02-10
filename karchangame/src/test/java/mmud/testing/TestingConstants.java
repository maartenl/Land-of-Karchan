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

import java.util.Date;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Room;
import mmud.database.enums.Sex;
import mmud.exceptions.MudException;

/**
 * Generates constants for use in testcases.
 *
 * @author maartenl
 */
public class TestingConstants
{

    public static final String NO_SUCH_METHOD = "No such function ";

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

        person.setLastlogin(new Date((new Date()).getTime() - 1_000_000));
        person.setLok("lok");
        person.setAddress("82-170-94-123.ip.telfort.nl");
        person.setPassword("93ef5f419670b2d0efe0c9461b765725a74c86eb"); // sha1 of "hotblack"
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
        person.clearLog();
        return person;
    }

    public static User getMarvin(Room aRoom) throws MudException
    {
        User person = new User();

        person.setLastlogin(new Date((new Date()).getTime() - 2_000_000));
        person.setLok("lok");
        person.setAddress("82-170-94-123.ip.telfort.nl");
        person.setPassword("a4cac82164ef67d9d07d379b5d5d8c4abe1e02ff"); // sha1 of "marvin"
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
        person.clearLog();
        return person;
    }

    public static Admin getAdmin()
    {
        Admin admin = new Admin();
        admin.setIp("10.0.0.12");
        admin.setEmail("maarten_l@yahoo.com");
        admin.setName("Karn");
        admin.setPasswd("somesecretpasswordthatnobodycanguessinanmillionyears");
        admin.setValiduntil(new Date((new Date()).getTime() + 100_000_000));
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

        person.setLastlogin(new Date((new Date()).getTime() - 2_000_000));
        person.setLok("lok");
        person.setAddress("82-170-94-123.ip.telfort.nl");
        person.setPassword("a4cac82164ef67d9d07d379b5d5d8c4abe1e02ff"); // sha1 of "marvin"
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
        person.clearLog();
        return person;
    }

}
