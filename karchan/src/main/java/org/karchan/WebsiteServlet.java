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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("*.html")
public class WebsiteServlet extends HttpServlet
{

  @Inject 
  private Freemarker freemarker;
  
  @Override
  public void init() throws ServletException
  {
    // do nothing for now.
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    // Set response content type
    response.setContentType("text/html");

    // Actual logic goes here.
    PrintWriter out = response.getWriter();
    out.println(request.getRequestURI() + "</br>");
    
    /* Create a data-model */
    Map<String, Object> root = new HashMap<>();
    root.put("user", "Big Joe");

    /* Get the template (uses cache internally) */
    Template temp = freemarker.getConfiguration().getTemplate("test.ftlh");

    try
    {
      /* Merge data-model with template */
      temp.process(root, out);
    } catch (TemplateException ex)
    {
      Logger.getLogger(WebsiteServlet.class.getName()).log(Level.SEVERE, null, ex);
      throw new ServletException(ex);
    }
  }

}
