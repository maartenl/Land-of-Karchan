/*
 * Copyright (C) 2015 maartenl
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

import mmud.database.entities.characters.Person;
import mmud.services.LogService;

/**
 * @author maartenl
 */
public class LogServiceStub extends LogService
{

    public LogServiceStub()
    {
    }

    private final StringBuffer buffer = new StringBuffer();

    @Override
    public void writeLog(Person person, String message)
    {
        buffer.append(person.getName()).append(":").append(message).append("\n");
    }

    @Override
    public void writeCommandLog(Person person, String command)
    {
        buffer.append(person.getName()).append(":").append(command).append("\n");
    }

    public String getLog()
    {
        return buffer.toString();
    }

}
