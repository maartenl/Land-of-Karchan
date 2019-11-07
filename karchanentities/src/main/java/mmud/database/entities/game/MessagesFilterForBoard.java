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
package mmud.database.entities.game;

import java.time.LocalDateTime;

import mmud.database.entities.DateTimeUtilities;
import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.OneToManyMapping;

/**
 * This class is customizing the collection of Messages on a Board to only
 * contain messages of the last week.
 *
 * @author maartenl
 */
public class MessagesFilterForBoard implements DescriptorCustomizer
{

  @Override
  public void customize(ClassDescriptor descriptor) throws Exception
  {
    OneToManyMapping mapping = (OneToManyMapping) descriptor
            .getMappingForAttributeName("messages");

    ExpressionBuilder eb = new ExpressionBuilder(mapping
            .getReferenceClass());
    Expression fkExp = eb.getField("mm_boardmessages.boardid").equal(eb.getParameter("mm_boards.id"));
    Expression activeExp = eb.get("removed").equal(0);
//    TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
//    LocalDate lastSunday = LocalDate.now().with(fieldUS, 1);

    Expression recent = eb.get("posttime").greaterThan(DateTimeUtilities.getLastSunday(LocalDateTime.now()));
    mapping.setSelectionCriteria(fkExp.and(activeExp).and(recent));
  }

}
