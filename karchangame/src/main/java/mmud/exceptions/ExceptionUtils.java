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
package mmud.exceptions;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * @author maartenl
 */
public class ExceptionUtils
{

  /**
   * Creates a useful message for your constraint violations.
   *
   * @param e the constraint violation exception
   * @return the string representation
   */
  public static String createMessage(ConstraintViolationException e)
  {
    System.out.println(e.getConstraintViolations().stream().map(x -> x.getPropertyPath().toString() + x.getInvalidValue() + x.getMessage()).collect(Collectors.joining()));
    Set<String> constraintViolations = e.getConstraintViolations().stream()
      .map(ConstraintViolation::getMessage)
      .collect(Collectors.toSet());
    if (constraintViolations.size() == 1)
    {
      return constraintViolations.stream().findFirst().orElse("Unknown constraint violation occurred.");
    }
    return constraintViolations.stream()
      .map(str -> "<p>" + str + "</p>")
      .reduce((x, y) -> x + y).orElse("Unknown constraint violation occurred.");
  }

  public static Optional<String> createMessage(PersistenceException f)
  {
    if (f.getCause() instanceof ConstraintViolationException)
    {
      ConstraintViolationException e = (ConstraintViolationException) f.getCause();
      return Optional.of(createMessage(e));
    }
    return Optional.empty();
  }
}
