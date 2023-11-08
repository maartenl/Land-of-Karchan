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
package org.karchan.menus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mmud.constants.Adverbs;
import mmud.constants.Emotions;

import java.util.List;
import java.util.Map;

public class CommandReferenceMenu extends Menu
{

  CommandReferenceMenu(String name, String url)
  {
    super(name, url);
  }

  @Override
  public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]>
    parameters)
  {
    TypedQuery<String> query = entityManager.createNamedQuery("Help.findAll", String.class);
    List<String> list = query.getResultList();
    root.put("help", list);
    root.put("adverbs", Adverbs.getAdverbs());
    root.put("emotions", Emotions.getEmotions());
    root.put("targetEmotions", Emotions.getTargetEmotions());
  }
}
