package mmud.rest.webentities.admin;

import mmud.JsonUtils;
import mmud.database.entities.game.Help;

public class AdminManpage
{
  public static final String GET_QUERY = "select json_object(\"command\", command) from mm_help order by command";

  public String command;
  public String contents;
  public String synopsis;
  public String seealso;
  public String example1;
  public String example1a;
  public String example1b;
  public String example2;
  public String example2a;
  public String example2b;
  public String example2c;

  public AdminManpage()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminManpage(Help item)
  {
    this.command = item.getCommand();
    this.contents = item.getContents();
    this.synopsis = item.getSynopsis();
    this.seealso = item.getSeealso();
    this.example1 = item.getExample1();
    this.example1a = item.getExample1a();
    this.example1b = item.getExample1b();
    this.example2 = item.getExample2();
    this.example2a = item.getExample2a();
    this.example2b = item.getExample2b();
    this.example2c = item.getExample2c();
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static AdminManpage fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminManpage.class);
  }


}
