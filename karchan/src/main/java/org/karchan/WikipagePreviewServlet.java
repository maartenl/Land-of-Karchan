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
package org.karchan;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.karchan.wiki.WikiRenderer;

@WebServlet("/wiki/preview")
public class WikipagePreviewServlet extends HttpServlet
{

  private final static Logger LOGGER = Logger.getLogger(WikipagePreviewServlet.class.getName());

  @Override
  public void init() throws ServletException
  {
    // do nothing for now.
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    LOGGER.info("Wikipage preview started.");
    // Set response content type
    response.setContentType("text/html;charset=UTF-8");
    String content = request.getReader().lines().collect(Collectors.joining("\n"));
    LOGGER.log(Level.INFO, "Wikipage preview started with content {0}.", content);
    if (content == null)
    {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "No content found.");
      return;
    }
    PrintWriter out = response.getWriter();
    out.print(new WikiRenderer().render(content));
    LOGGER.info("Wikipage preview finished.");
  }

}
