/*
 *  Copyright (C) 2013 maartenl
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
package mmud.testing.tests.scripting;

import java.util.HashSet;
import java.util.Set;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.Item;
import mmud.database.enums.Sex;
import mmud.exceptions.MudException;
import mmud.scripting.Items;
import mmud.scripting.ItemsInterface;
import mmud.scripting.Persons;
import mmud.scripting.PersonsInterface;
import mmud.scripting.Rooms;
import mmud.scripting.RoomsInterface;
import mmud.scripting.World;
import mmud.scripting.WorldInterface;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 *
 * @author maartenl
 */
public class RunScriptTest
{

    protected Persons persons;
    protected Rooms rooms;
    protected Items items;
    protected World world;
    private RoomsInterface roomsInterface;
    private ItemsInterface itemsInterface;
    private WorldInterface worldInterface;

    public static class RoomStub extends Room
    {

        private Set<Person> persons = new HashSet<>();

        public void addPerson(Person person)
        {
            persons.add(person);
        }

        public boolean removePerson(Person person)
        {
            return persons.remove(person);
        }

        public void clearPersons()
        {
            persons.clear();
        }

        @Override
        public void sendMessage(Person aPerson, String aMessage) throws MudException
        {
            for (Person myChar : persons)
            {
                myChar.writeMessage(aPerson, aMessage);
            }
        }

        @Override
        public void sendMessage(Person aPerson, Person aSecondPerson,
                String aMessage) throws MudException
        {
            for (Person myChar : persons)
            {
                myChar.writeMessage(aPerson, aSecondPerson, aMessage);
            }
        }

        @Override
        public void sendMessage(String aMessage)
                throws MudException
        {
            for (Person myChar : persons)
            {
                myChar.writeMessage(aMessage);
            }
        }

        @Override
        public void sendMessageExcl(Person aPerson, Person aSecondPerson,
                String aMessage) throws MudException
        {
            for (Person myChar : persons)
            {
                if (myChar != aPerson
                        && myChar != aSecondPerson)
                {
                    myChar.writeMessage(aPerson, aSecondPerson, aMessage);
                }
            }
        }

        @Override
        public Person retrievePerson(String aName)
        {
            for (Person person : persons)
            {
                if ((person.getName().equalsIgnoreCase(aName)))
                {
                    return person;
                }
            }
            return null;
        }
    };
    protected PersonsInterface personsInterface;
    protected Person marvin;
    protected Person hotblack;
    protected RoomStub room;

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception
    {
        room = new RoomStub();

        hotblack = new User();
        hotblack.setName("Hotblack");
        hotblack.setSex(Sex.MALE);
        hotblack.setRoom(room);
        marvin = new User();
        marvin.setName("Marvin");
        marvin.setSex(Sex.MALE);
        marvin.setRoom(room);

        final Room room2 = new RoomStub();
        room2.setId(2);
        room2.setContents("the stimulation of thin air; the intense blueness of the sky; the towering "
                + "thunderheads of summer that ramble and flash and produce sheets of rain with "
                + "a sudden rush of water that soon passes, leaving only a wet arroyo to dry "
                + "within an hour; the quick change of climate, from burning dry heat that "
                + "allows no sweat to wet one's clothing to a shivering cold during the "
                + "rainfall; these are among the attributes of a land that gets into one's "
                + "blood and bones. Yes, this is the <I>Land of Karchan</I>.<P> "
                + "You are standing in the exuberant air.<p>To the south, a small cave beckons you.  To "
                + "the west, you see a sandy yellow path leading near a very big forest. In the east, "
                + "there is an opening in a big mountain. It looks like a big, moist cave. <P>");
        room2.setTitle("Outside The Cave");
        room2.setPicture("/images/gif/cave-ent.gif");

        final Room room3 = new RoomStub();
        room3.setId(3);
        room3.setContents("you are standing in the  exuberant air on a road which leads to "
                + "the north and south. In the north, you can see a little  village; little in "
                + "the meaning that whatever resides there must be equally little. In the east, "
                + "you are approaching a mountain and in the west, a huge forest blocks the "
                + "eye.<p>Right in front of  you, a big board can be seen. All sorts of little "
                + "papers are littered all over it. A sign is  pinned to the board. You could "
                + "try reading the sign.<p> ");
        room3.setTitle("The Road");
        room3.setPicture("/images/gif/road.gif");

        room.setId(1);
        room.setContents("You are in the middle of a cave.  Around you, stone "
                + "walls make it impenetrable.  To the west, you can see a beautiful blue sky.  The "
                + "cave is wet; water is running down the walls.  It is dark and dreary, a complete "
                + "contrast with the sky in the west.<p/><p>Nearby, a small lake can be seen with "
                + "crystal clear water.  A strange glow in the water makes it appear like there is a "
                + "light in there.</p><p>In the cave, a red leather book on an old rusty chain can be found.</p><p>"
                + "  There is also a red button.  The red button looks strangely modern and very out of "
                + "place next to the book and the chain.  Who knows what might happen if you were to "
                + "push the button...</p>");
        room.setTitle("The Cave");
        room.setPicture("/images/gif/cave.gif");
        room.addPerson(hotblack);
        room.addPerson(marvin);
        room.setWest(room2);
        room2.setEast(room);
        room2.setWest(room3);
        room3.setEast(room2);
        personsInterface = new PersonsInterface()
        {

            @Override
            public Person find(String name)
            {
                if ("Hotblack".equalsIgnoreCase(name))
                {
                    return hotblack;
                }
                if ("Marvin".equalsIgnoreCase(name))
                {
                    return marvin;
                }
                return null;
            }
        };
        roomsInterface = new RoomsInterface()
        {

            @Override
            public Room find(Integer id)
            {
                if (id == null)
                {
                    return null;
                }
                if (id == 1)
                {
                    return room;
                }
                if (id == 2)
                {
                    return room2;
                }
                if (id == 3)
                {
                    return room3;
                }
                return null;
            }
        };
        itemsInterface = new ItemsInterface(){

            @Override
            public Item addItem(long itemdefnr, Person person) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Item addItem(long itemdefnr, Room room) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Item addItem(long itemdefnr, Item item) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        worldInterface = new WorldInterface()
        {

            @Override
            public String getAttribute(String name)
            {
                return null;
            }
        };
        persons = new Persons(personsInterface);
        rooms = new Rooms(roomsInterface);
        items = new Items(itemsInterface);
        world = new World(worldInterface);
    }

    @AfterMethod
    public void tearDownMethod() throws Exception
    {
    }
}
