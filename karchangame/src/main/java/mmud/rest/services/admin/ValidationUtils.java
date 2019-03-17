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
package mmud.rest.services.admin;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Response;
import mmud.exceptions.MudWebException;

/**
 *
 * @author maartenl
 */
public class ValidationUtils
{

  private ValidationUtils()
  {
    // defeat instantiation, static utility class.
  }

  public static <T> void checkValidation(String name, T attribute)
  {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<T>> constraintViolations = validator.validate(attribute);
    StringBuffer buffer = null;
    if (!constraintViolations.isEmpty())
    {
      for (ConstraintViolation<T> cv : constraintViolations)
      {
        if (buffer == null)
        {
          buffer = new StringBuffer();
        }
        buffer.append(cv.getRootBeanClass().getSimpleName()).append(".").append(cv.getPropertyPath()).append(" ").append(cv.getMessage());
      }
    }
    if (buffer != null)
    {
      throw new MudWebException(name, buffer.toString(), Response.Status.BAD_REQUEST);
    }
  }

}
