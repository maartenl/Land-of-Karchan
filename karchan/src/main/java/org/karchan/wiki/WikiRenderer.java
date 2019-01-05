/*
 * Copyright (C) 2018 maartenl
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
package org.karchan.wiki;

import info.bliki.wiki.model.WikiModel;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maartenl
 */
public class WikiRenderer
{

  public String render(String string)
  {
    WikiModel wikiModel
            = new WikiModel("${image}",
                    "${title}.html")
    {
      @Override
      public String encodeTitleDotUrl(String wikiTitle, boolean firstCharacterAsUpperCase)
      {
        return wikiTitle;
        // super.encodeTitleDotUrl(wikiTitle, firstCharacterAsUpperCase); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public String encodeTitleToUrl(String wikiTitle, boolean firstCharacterAsUpperCase)
      {
        return wikiTitle;
        // super.encodeTitleToUrl(wikiTitle, firstCharacterAsUpperCase); //To change body of generated methods, choose Tools | Templates.
      }
    };
    try
    {
      return wikiModel.render(string);
    } catch (IOException ex)
    {
      Logger.getLogger(WikiRenderer.class.getName()).log(Level.SEVERE, null, ex);
      throw new RuntimeException(ex);
    }
  }

}
