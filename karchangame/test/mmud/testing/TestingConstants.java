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
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Person;
import mmud.database.entities.game.Room;
import mmud.database.enums.Sex;

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

    public static Person getHotblack(Room aRoom)
    {
        Person person = new Person();
        person.setName("Hotblack");
        // JDK7: number formats, for clarification.
        // 1_000_000 ms = 1_000 sec = 16 min, 40 sec
        person.setLastlogin(new Date((new Date()).getTime() - 1_000_000));
        person.setSleep(Boolean.FALSE);
        person.setTitle("Guitar keyboard player of the rock group Disaster Area");
        person.setRoom(aRoom);
        person.setSex(Sex.MALE);
        person.setRace("undead");
        return person;
    }

    public static Person getMarvin(Room aRoom)
    {
        Person person = new Person();
        person.setName("Marvin");
        // JDK7: number formats, for clarification.
        // 2_000_000 ms = 2_000 sec = 33 min, 20 sec
        person.setLastlogin(new Date((new Date()).getTime() - 2_000_000));
        person.setRoom(aRoom);
        person.setSex(Sex.MALE);
        person.setSleep(Boolean.TRUE);
        person.setRace("android");
        person.setTitle("The Paranoid Android");
        return person;
    }
}