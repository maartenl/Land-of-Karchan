/*
 * Copyright (C) 2014 maartenl
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
package mmud.rest.webentities;


import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.XmlRootElement;
import mmud.database.entities.game.Method;

/**
 *
 * @author maartenl
 */
@XmlRootElement
public class DeputyScript
{

    public LocalDateTime creation;
    public String name;
    public String owner;
    public String src;

    public DeputyScript()
    {
        // empty constructor for JAXB
    }

    public DeputyScript(Method method)
    {
        this.creation = method.getCreation();
        this.name = method.getName();
        this.owner = (method.getOwner() == null ? null : method.getOwner().getName());
        this.src = method.getSrc();
    }

}
