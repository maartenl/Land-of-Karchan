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

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author maartenl
 */
@Stateless
@LocalBean
public class Freemarker implements TemplateLoader
{

//  @Inject
//  private EntityManagerFactory entityManagerFactory;
  
  private Configuration configuration;

  public Configuration getConfiguration()
  {
    return configuration;
  }

  @PostConstruct
  private void init()
  {
    // Create your Configuration instance, and specify if up to what FreeMarker
    // version (here 2.3.27) do you want to apply the fixes that are not 100%
    // backward-compatible. See the Configuration JavaDoc for details.
    configuration = new Configuration(Configuration.VERSION_2_3_27);

    // Specify the source where the template files come from. 
    configuration.setTemplateLoader(this);
    
    // Set the preferred charset template files are stored in. UTF-8 is
    // a good choice in most applications:
    configuration.setDefaultEncoding("UTF-8");

    // Sets how errors will appear.
    // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
    configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

    // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
    configuration.setLogTemplateExceptions(false);

    // Wrap unchecked exceptions thrown during template processing into TemplateException-s.
    configuration.setWrapUncheckedExceptions(true);
  }

  private Map<String, HtmlTemplate> map = new HashMap<>();
  
  @Override
  public Object findTemplateSource(String name) throws IOException
  {
    HtmlTemplate template = map.get(name);
    if (template == null) {
      template = new HtmlTemplate(name);
      template.setContents("Maarten van Leunen ${user} " + name);
      map.put(name, template);
    }
    return template;
  }

  @Override
  public long getLastModified(Object templateSource)
  {
    HtmlTemplate template = (HtmlTemplate) templateSource;
    return template.getLastModified();
  }

  @Override
  public Reader getReader(Object templateSource, String encoding) throws IOException
  {
    HtmlTemplate template = (HtmlTemplate) templateSource;
    return template.getReader();
  }

  @Override
  public void closeTemplateSource(Object arg0) throws IOException
  {
    // do nothing for now.
  }

  @Override
  public String toString()
  {
    return "Freemarker(database)";
  }
  
  
}
