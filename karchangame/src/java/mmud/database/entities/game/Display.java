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
package mmud.database.entities.game;

/**
 * TODO: feels wrong, need a better solution.
 *
 * @author maartenl
 */
public class Display implements DisplayInterface
{

    private String title;
    private String image;
    private String body;

    public Display()
    {
    }

    public Display(String title, String image, String body)
    {
        this.title = title;
        this.image = image;
        this.body = body;
    }

    @Override
    public String getMainTitle()
    {
        return title;
    }

    public void setMainTitle(String title)
    {
        this.title = title;
    }

    @Override
    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    @Override
    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

}
