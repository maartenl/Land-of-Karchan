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
package mmud.rest.webentities.admin;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import mmud.database.entities.game.Attribute;

/**
 * See {@link mmud.database.entities.game.Attribute}
 *
 * @author maartenl
 */
public class AdminAttribute
{

  public Long id;
  public String name;
  public String value;
  public String valueType;
  public AdminMudType objecttype;
  public String objectid;

  public AdminAttribute()
  {
    // default constructor required for reconstruction from json.
  }

  public AdminAttribute(Attribute attribute, AdminMudType objecttype)
  {
    name = attribute.getName();
    valueType = attribute.getValueType();
    value = attribute.getValue();
    id = attribute.getAttributeId();
    this.objecttype = objecttype;
  }

  public AdminAttribute(Attribute attribute, AdminMudType objecttype, String objectid)
  {
    this(attribute, objecttype);
    this.objectid = objectid;
  }

  public String toJson()
  {
    return JsonbBuilder.create().toJson(this);
  }

  public static AdminAttribute fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(json, AdminAttribute.class);
  }
}
