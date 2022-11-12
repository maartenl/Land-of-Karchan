/*
 * Copyright (C) 2014 maartenl
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
package mmud.testing.tests;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import mmud.database.entities.characters.Person;
import mmud.services.CommunicationService;

/**
 * @author maartenl
 */
public class MudTest
{

  /**
   * Sets the field 'fieldname' in object 'object' to the value 'value'. Where 'object' is an object
   * of class 'targetClass'.
   *
   * @param <T>
   * @param targetClass
   * @param fieldName
   * @param object
   * @param value
   */
  public <T> void setField(Class<T> targetClass, String fieldName, Object object, Object value) {
    try {
      Field field = targetClass.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(object, value);
    }
    catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
      Logger.getLogger(MudTest.class.getName()).log(Level.SEVERE, null, ex);
      throw new RuntimeException(ex);
    }
  }

  public String getLog(Person person) {
    return CommunicationService.getCommunicationService(person).getLog(0);
  }

}
