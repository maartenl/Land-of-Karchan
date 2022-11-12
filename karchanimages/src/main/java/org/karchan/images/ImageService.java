/*
 * Copyright (C) 2019 maartenl
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
package org.karchan.images;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import mmud.database.entities.web.Image;

/**
 * @author maartenl
 */
public class ImageService
{

  @PersistenceContext
  private EntityManager entityManager;

  public Optional<Image> getImage(String imageUrl, String playerName)
  {
    TypedQuery<Image> queryImage = entityManager.createNamedQuery("Image.find", Image.class);
    queryImage.setParameter("url", imageUrl);
    queryImage.setParameter("owner", playerName);
    List<Image> resultList = queryImage.getResultList();

    if (resultList.isEmpty())
    {
      return Optional.empty();
    }

    Image image = resultList.get(0);
    image.addHit();
    image.lastAccessed();
    return Optional.of(image);
  }

}
