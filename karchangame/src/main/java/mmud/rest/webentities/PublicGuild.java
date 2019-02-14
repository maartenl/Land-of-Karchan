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
package mmud.rest.webentities;


import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Information on a certain guild that is available to the big unwashed public.
 * Used for JSON in REST services.
 * @author maartenl
 */
@XmlRootElement
public class PublicGuild
{
    public String guildurl;
    public String title;
    public String bossname;
    public String guilddescription;
    public LocalDateTime creation;
    public String image;
}
