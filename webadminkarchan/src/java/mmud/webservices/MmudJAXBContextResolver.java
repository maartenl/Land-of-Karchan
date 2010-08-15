/*-----------------------------------------------------------------------
svninfo: $Id: charactersheets.php 1078 2006-01-15 09:25:36Z maartenl $
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Maarten van Leunen
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-----------------------------------------------------------------------*/

package mmud.webservices;

import com.sun.jersey.api.json.JSONJAXBContext;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import mmud.webservices.webentities.LogonMessage;
import mmud.webservices.webentities.Result;

/**
 *
 * @author maartenl
 */
@Provider
public class MmudJAXBContextResolver implements ContextResolver<JAXBContext>
{
       private Logger itsLog = Logger.getLogger("mmudrest");

       private JAXBContext context;
       private Class[] types = {Result.class, LogonMessage.class, Character.class};

       public MmudJAXBContextResolver() throws Exception 
       {
           itsLog.entering(this.getClass().getName(), "MmudJAXBContextResolver");
           Map props = new HashMap<String, Object>();
           props.put(JSONJAXBContext.JSON_NOTATION, JSONJAXBContext.JSONNotation.MAPPED);
           props.put(JSONJAXBContext.JSON_ROOT_UNWRAPPING, Boolean.TRUE);
           //props.put(JSONJAXBContext.JSON_ARRAYS, new HashSet<String>(1){{add("jobs");}});
           //props.put(JSONJAXBContext.JSON_NON_STRINGS, new HashSet<String>(1){{add("pages"); add("tonerRemaining");}});

           this.context = new JSONJAXBContext(types, props);

           // the new way of doing,... once I get this damn jersey updated.
           //this.context = new JSONJAXBContext(
           //        JSONConfiguration.natural().build(),
           //        types);
           itsLog.exiting(this.getClass().getName(), "MmudJAXBContextResolver");

       }

       @Override
       public JAXBContext getContext(Class<?> objectType)
       {
           itsLog.entering(this.getClass().getName(), "getContext");

           for (Class myObjectType : types)
           {
               if (myObjectType.equals(objectType))
               {
                   itsLog.exiting(this.getClass().getName(), "getContext context");
                   return context;
               }
           }
           itsLog.exiting(this.getClass().getName(), "getContext null");
           return null;
           // return (types.contains(objectType)) ? context : null;
       }

}
