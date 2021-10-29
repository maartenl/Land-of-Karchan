package org.karchan.webentities;

import mmud.database.entities.characters.User;

/**
 * Data record containing admin information
 */
public class PublicAdmin
{

  public String name;
  public String title;

  public PublicAdmin()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public PublicAdmin(User user)
  {
    this.name = user.getName();
    this.title = user.getTitle();
  }

}
