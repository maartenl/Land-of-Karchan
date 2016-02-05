/*
 * Copyright (C) 2016 maartenl
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
package awesomeness.vaadin;

import awesomeness.vaadin.utils.HelpEditor;
import com.vaadin.ui.VerticalLayout;
import java.util.logging.Logger;
import mmud.database.entities.game.Admin;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
public class Explanations extends VerticalLayout
{

    private static final Logger logger = Logger.getLogger(Explanations.class.getName());

    private final LogBean logBean;
    private final Admin currentUser;

    public Explanations(final Admin currentUser, final LogBean logBean)
    {
        this.logBean = logBean;
        this.currentUser = currentUser;
    }

    public void init()
    {
        buildView();
    }

    protected void buildView()
    {
        HelpEditor helpEditor = new HelpEditor(this, currentUser, logBean);
        helpEditor.buildView();
    }

}
