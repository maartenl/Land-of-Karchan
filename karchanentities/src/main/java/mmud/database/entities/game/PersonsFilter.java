/*
 * Copyright (C) 2017 maartenl
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
package mmud.database.entities.game;

import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.OneToManyMapping;

/**
 * This class is customizing the collection of {@link mmud.database.entities.characters.Person}s
 * in an entity to only contain
 * active persons. In other words, it is a kind of filter.
 * This is typically EclipseLink and is not provided by JPA.
 *
 * @see <a href="https://wiki.eclipse.org/EclipseLink/Examples/JPA/MappingSelectionCriteria">Mapping Selection Criteria</a>
 * @author maartenl
 */
public abstract class PersonsFilter implements DescriptorCustomizer
{

  /**
   * @return the name of the field in the entity that is the collection of Persons.
   */
  protected abstract String getCollectionName();

  /**
   *
   * @return the name of the field in the {@link Person} Entity referring to
   * the Entity that has the {@link Customizer} annotation.
   */
  protected abstract String getFieldName();

  /**
   *
   * @return the identifier of the Entity that has the {@link Customizer} annotation.
   */
  protected abstract String getIdentifier();

  /**
   * Adds the "active person" check to specific collections, as a filter.
   *
   * @param descriptor the class descriptor
   * @throws Exception in case things go wrong.
   */
  @Override
  public void customize(ClassDescriptor descriptor) throws Exception
  {
    OneToManyMapping mapping = (OneToManyMapping) descriptor
            .getMappingForAttributeName(getCollectionName());

    ExpressionBuilder eb = new ExpressionBuilder(mapping
            .getReferenceClass());
    Expression fkExp = eb.getField(getFieldName()).equal(eb.getParameter(getIdentifier()));
    Expression activeExp = eb.get("active").equal(1);

    mapping.setSelectionCriteria(fkExp.and(activeExp));
  }

}
