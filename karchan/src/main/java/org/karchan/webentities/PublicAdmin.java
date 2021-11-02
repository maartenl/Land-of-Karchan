package org.karchan.webentities;

import mmud.database.entities.characters.User;

/**
 * Data record containing admin information
 */
public class PublicAdmin
{

  private final String name;
  private final String title;

  public PublicAdmin(User user)
  {
    this.name = user.getName();
    this.title = user.getTitle();
  }

  public String getName()
  {
    return name;
  }

  public String getTitle()
  {
    return title;
  }
}
