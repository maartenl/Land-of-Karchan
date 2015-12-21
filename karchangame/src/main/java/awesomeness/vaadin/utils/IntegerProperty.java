/*
 * Copyright (C) 2015 maartenl
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
package awesomeness.vaadin.utils;

import com.vaadin.data.Property;

/**
 *
 * @author maartenl
 */
public class IntegerProperty implements Property<Integer>
{

    private Integer value;
    private boolean readOnly;

    public IntegerProperty()
    {
        super();
    }

    public IntegerProperty(Integer integer)
    {
        this();
        value = integer;
    }

    @Override
    public Integer getValue()
    {
        return value;
    }

    @Override
    public void setValue(Integer newValue) throws ReadOnlyException
    {
        this.value = newValue;
    }

    @Override
    public Class<? extends Integer> getType()
    {
        return Integer.class;
    }

    @Override
    public boolean isReadOnly()
    {
        return readOnly;
    }

    @Override
    public void setReadOnly(boolean newStatus)
    {
        this.readOnly = newStatus;
    }

}
