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
package mmud;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import mmud.rest.webentities.PublicPerson;

/**
 *
 * @author maartenl
 */
@Provider
public class MyJAXBContextResolver implements ContextResolver<JAXBContext>
{

    private JAXBContext context;
    private Class[] types =
    {
        PublicPerson.class
    };

    public MyJAXBContextResolver() throws Exception
    {
        this.context =
                new JSONJAXBContext(
                JSONConfiguration.mapped().arrays("publicPerson").build(), types);
    }

    @Override
    public JAXBContext getContext(Class<?> objectType)
    {
        for (Class type : types)
        {
            if (type == objectType)
            {
                return context;
            }
        }
        return null;
    }
}
