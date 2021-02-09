package mmud.rest.webentities.admin;

import java.time.LocalDateTime;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Area;

/**
 * Data record containing admin information
 */
public class AdminAdmin
{

  public String name;
  public String title;
  public LocalDateTime creation;
  public String email;
  public LocalDateTime validuntil;

  public AdminAdmin()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminAdmin(User user, Admin admin)
  {
    this.name = user.getName();
    this.title = user.getTitle();
    this.email = admin.getEmail();
    this.validuntil = admin.getValiduntil();
    this.creation = admin.getCreated();
  }

  public AdminAdmin(User user)
  {
    this.name = user.getName();
    this.title = user.getTitle();
  }

  public String toJson()
  {
    return JsonbBuilder.create().toJson(this);
  }

  public static AdminAdmin fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(json, AdminAdmin.class);
  }


}
