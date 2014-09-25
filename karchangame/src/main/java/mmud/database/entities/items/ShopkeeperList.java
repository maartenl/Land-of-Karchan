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
package mmud.database.entities.items;

import java.util.logging.Logger;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import mmud.Constants;
import mmud.database.entities.characters.Shopkeeper;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 *
 * @author maartenl
 */
@Entity
@DiscriminatorValue("1")
public class ShopkeeperList extends Item
{

    private static final Logger itsLog = Logger.getLogger(ShopkeeperList.class.getName());

    /**
     * In the database, this column is actually Nullable, as it has no value
     * for superclasses of shopkeeperlist.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "shopkeeper", nullable = false, referencedColumnName = "name")
    @NotNull
    private Shopkeeper shopkeeper;

    @Override
    public ItemCategory getCategory()
    {
        return ItemCategory.SHOPKEEPERLIST;
    }

    /**
     * Not only shows the reading of the item, but also the shopkeeper list.
     *
     * @return
     */
    @Override
    public DisplayInterface getRead()
    {
        itsLog.info("ShopkeeperList getRead");
        final DisplayInterface display = super.getRead();
        return new DisplayInterface()
        {
            @Override
            public String getMainTitle() throws MudException
            {
                return display.getMainTitle();
            }

            @Override
            public String getImage() throws MudException
            {
                return display.getImage();
            }

            @Override
            public String getBody() throws MudException
            {
                String read = display.getBody();
                StringBuilder builder = new StringBuilder();
                Constants.addShopkeeperList(shopkeeper.getItems(), builder);
                read = read.replace("%VIEW", builder.toString());
                return read;
            }
        };
    }

}
