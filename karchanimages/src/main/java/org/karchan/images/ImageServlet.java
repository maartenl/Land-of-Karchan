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

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mmud.database.entities.web.Image;

/**
 * Provides images stored by players in the database.
 * @author maartenl
 */
@WebServlet("/*")
public class ImageServlet extends HttpServlet
{

  private static final Logger LOGGER = Logger.getLogger(ImageServlet.class.getName());

  @Inject
  private ImageService imageService;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    final String url = request.getRequestURI();
    LOGGER.entering(ImageServlet.class.getName(), "doGet " + url);
    ServletContext context = request.getServletContext();


    if (!url.startsWith("/images/player"))
    {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      LOGGER.log(Level.SEVERE, "Bad url :{0}, Not Found", url);
      return;
    }

    ImageData imageData = new ImageData(url);
    LOGGER.log(Level.FINEST, "Imagedata : {0}.", imageData);

    String mime = context.getMimeType(imageData.getImageUrl());
    if (mime == null)
    {
      LOGGER.log(Level.SEVERE, "Mime of {0} could not be parsed.", imageData.getImageUrl());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }

    Optional<Image> optionalImage = imageService.getImage(imageData.getImageUrl(), imageData.getPlayerName());

    if (optionalImage.isEmpty())
    {
      LOGGER.log(Level.SEVERE, "Image {0} of {1} not found.", new Object[]
        {
          imageData.getImageUrl(), imageData.getPlayerName()
        });
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    response.addHeader("Cache-Control", "private, max-age=31536000");

    Image image = optionalImage.get();

    // Set response content type
    response.setContentType(image.getMimeType());

    response.setContentLength(image.getContent().length);
    ServletOutputStream outputStream = response.getOutputStream();

    outputStream.write(image.getContent());
  }

}
