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
package awesomeness.vaadin.editor;

import awesomeness.vaadin.UserInterface;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmud.database.entities.game.Admin;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
public abstract class Buttons extends HorizontalLayout
{

  private static final Logger LOGGER = Logger.getLogger(Buttons.class.getName());

  private boolean busyCreatingNewItem;

  private final LogBean logBean;

  private final Button commit;

  private final Button discard;

  private final Button delete;

  private final Button disown;

  private boolean deleteEnabled = true;

  protected boolean enableCreate()
  {
    return true;
  }

  /**
   * Persist changes to an existing record to the database.
   *
   * @return
   */
  protected abstract Object save();

  /**
   * Persists a new record to the database.
   *
   * @return
   */
  protected abstract Object create();

  /**
   * Creates a new record (without writing it to the database yet).
   */
  protected abstract void instantiate();

  /**
   * Discards any changes.
   */
  protected abstract void discard();

  /**
   * Deletes a record from the database.
   *
   * @return
   */
  protected abstract Object delete();

  /**
   * Remove ownership of a record.
   *
   * @return
   */
  protected abstract String disown();

  /**
   * Does nothing with the create button.
   *
   * @param enable
   */
  public void setButtonsEnabled(boolean enable)
  {
    commit.setEnabled(enable);
    discard.setEnabled(enable);
    delete.setEnabled(deleteEnabled == true && enable);
    disown.setEnabled(enable);
  }

  /**
   *
   * @param currentUser current administrator
   * @param logBean the LOGGER
   * @param itemname the name of the items to be edited, for example "Item definition". Used for logging.
   * @param permitDeletes indicates that deleteing of information is permitted.
   */
  public Buttons(final Admin currentUser, final LogBean logBean, final String itemname, boolean permitDeletes, UserInterface userInterface)
  {
    this.deleteEnabled = permitDeletes;
    HorizontalLayout buttonsLayout = this;
    this.logBean = logBean;
    final String itemnamelowercase = itemname.toLowerCase();

    commit = new Button("Save", new Button.ClickListener()
    {
      @Override
      public void buttonClick(Button.ClickEvent event)
      {
        if (busyCreatingNewItem == true)
        {
          logBean.writeDeputyLog(currentUser, "New " + itemnamelowercase + " '" + create() + "' created.");
        } else
        {
          logBean.writeDeputyLog(currentUser, itemname + " '" + save() + "' updated.");
        }
        busyCreatingNewItem = false;
      }

    });
    buttonsLayout.addComponent(commit);

    discard = new Button("Cancel", new Button.ClickListener()
    {
      @Override
      public void buttonClick(Button.ClickEvent event)
      {
        LOGGER.log(Level.FINEST, "discard clicked.");
        discard();
      }
    });
    buttonsLayout.addComponent(discard);

    if (enableCreate())
    {
      Button create;
      create = new Button("Create", new Button.ClickListener()
      {

        @Override
        public void buttonClick(Button.ClickEvent event)
        {
          busyCreatingNewItem = true;
          discard();
          instantiate();
          setButtonsEnabled(true);
        }
      });
      buttonsLayout.addComponent(create);
    }

    delete = new Button("Delete", new Button.ClickListener()
    {

      @Override
      public void buttonClick(Button.ClickEvent event)
      {
        userInterface.confirm("Please Confirm:", "Are you really sure?",
                "Yes", "No", () ->
        {
          logBean.writeDeputyLog(currentUser, itemname + " '" + delete() + "' deleted.");
        });
      }
    });
    delete.setEnabled(deleteEnabled);
    buttonsLayout.addComponent(delete);

    disown = new Button("Disown", new Button.ClickListener()
    {
      @Override
      public void buttonClick(Button.ClickEvent event)
      {
        LOGGER.log(Level.FINEST, "disown clicked.");
        logBean.writeDeputyLog(currentUser, itemname + " '" + disown() + "' deleted.");
      }
    });
    buttonsLayout.addComponent(disown);

  }

}
