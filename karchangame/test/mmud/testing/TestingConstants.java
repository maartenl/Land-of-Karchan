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
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Room;
import mmud.database.enums.Sex;
import mmud.exceptions.MudException;

/**
 * Generates constants for use in testcases.
 * @author maartenl
 */
public class TestingConstants
{

    public static Area getArea()
    {
        Area aArea = new Area();
        aArea.setShortdescription("On board the Starship Heart of Gold");
        return aArea;
    }

    public static Room getRoom(Area aArea)
    {
        Room aRoom = new Room();
        aRoom.setTitle("The bridge");
        aRoom.setArea(aArea);
        return aRoom;
    }

    /**
     * @param aRoom
     * @return
     * @throws MudException
     */
    public static Person getHotblack(Room aRoom) throws MudException
    {
        Person person = new User();
        person.setName("Hotblack");
        // JDK7: number formats, for clarification.
        // 1_000_000 ms = 1_000 sec = 16 min, 40 sec
        person.setLastlogin(new Date((new Date()).getTime() - 1_000_000));
        person.setSleep(Boolean.FALSE);
        person.setTitle("Guitar keyboard player of the rock group Disaster Area");
        person.setRoom(aRoom);
        person.setSex(Sex.MALE);
        person.setRace("undead");
        person.setLok("lok");
        person.setAddress("82-170-94-123.ip.telfort.nl");
        person.setPassword("93ef5f419670b2d0efe0c9461b765725a74c86eb"); // sha1 of "hotblack"

        person.setRealname(null);
        person.setEmail(null);

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
        return person;
    }

    public static Person getMarvin(Room aRoom) throws MudException
    {
        Person person = new User();
        person.setName("Marvin");
        // JDK7: number formats, for clarification.
        // 2_000_000 ms = 2_000 sec = 33 min, 20 sec
        person.setLastlogin(new Date((new Date()).getTime() - 2_000_000));
        person.setRoom(aRoom);
        person.setSex(Sex.MALE);
        person.setSleep(Boolean.TRUE);
        person.setRace("android");
        person.setTitle("The Paranoid Android");
        person.setLok("lok");
        person.setAddress("82-170-94-123.ip.telfort.nl");
        person.setPassword("a4cac82164ef67d9d07d379b5d5d8c4abe1e02ff"); // sha1 of "marvin"
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
}