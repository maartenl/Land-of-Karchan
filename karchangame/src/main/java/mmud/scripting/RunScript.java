/*
 *  Copyright (C) 2012 maartenl
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
package mmud.scripting;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import javax.script.*;

import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.scripting.entities.Room;
import mmud.services.LogService;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

/**
 * Can run javascript source code. The methods call specific javascript
 * functions in the source code.
 *
 * @author maartenl
 */
public class RunScript
{

  private static final Logger LOGGER = Logger.getLogger(RunScript.class.getName());

  private final Persons persons;

  private final Rooms rooms;

  private final Items items;

  private final World world;

  private final LogService logService;

  public RunScript(Persons persons, Rooms rooms, Items items, World world, LogService logService)
  {
    if (persons == null)
    {
      throw new NullPointerException("persons was null.");
    }
    if (rooms == null)
    {
      throw new NullPointerException("rooms was null.");
    }
    if (world == null)
    {
      throw new NullPointerException("world was null.");
    }
    this.persons = persons;
    this.rooms = rooms;
    this.world = world;
    this.items = items;
    this.logService = logService;
  }

  /**
   * Runs a specific function called "function command(person, command)".
   * <p>
   * Basically it calls a specific deputy defined "command".
   *
   * @param person     the person issuing forth the command.
   * @param command    the command, a string.
   * @param sourceCode the source code, javascript, a string.
   * @return false if failed, true if successful
   * @throws ScriptException       if an error occurred in the javascript
   * @throws NoSuchMethodException if the function cannot be found,
   */
  public DisplayInterface run(mmud.database.entities.characters.Person person, String command, String sourceCode)
  {
    LOGGER.entering(this.getClass().getName(), "run(" + person.getName() + ", " + command + ")");

    final Object result = initialiseScriptEngine(sourceCode, "command", this::makeDisplayInterface, new mmud.scripting.entities.Person(person), command);

    return (DisplayInterface) result;
  }

  private DisplayInterface makeDisplayInterface(Value result)
  {
    if (result != null && result.hasMembers())
    {
      String title = getValue(result, "title", "getTitle");
      String image = getValue(result, "image", "getImage");
      String body = getValue(result, "body", "getBody");
      return new DisplayInterface()
      {
        @Override
        public String getMainTitle() throws MudException
        {
          return title;
        }

        @Override
        public String getImage() throws MudException
        {
          return image;
        }

        @Override
        public String getBody() throws MudException
        {
          return body;
        }
      };
    }
    return null;
  }

  private String getValue(Value result, String propertyname, String methodname)
  {
    if (result.hasMember(propertyname))
    {
      return result.getMember(propertyname).asString();
    }
    if (result.hasMember(methodname))
    {
      return result.getMember(methodname).execute().asString();
    }
    return null;
  }

  /**
   * Runs a specific function called "function event(person)".
   * <p>
   * Basically it calls a specific deputy defined "event".
   *
   * @param person     the event needs to be executed with this person as the focus.
   * @param sourceCode the source code, javascript, a string.
   * @return false if failed, true if successful
   * @throws ScriptException                             if an error occurred in the javascript
   * @throws NoSuchMethodException                       if the function cannot be found,
   * @throws java.lang.IllegalAccessException
   * @throws java.lang.InstantiationException
   * @throws java.lang.reflect.InvocationTargetException
   */
  public boolean run(mmud.database.entities.characters.Person person, String sourceCode)
  {
    LOGGER.entering(this.getClass().getName(), "run(" + person.getName() + ")");

    Object result = initialiseScriptEngine(sourceCode, "event", Value::asBoolean, new mmud.scripting.entities.Person(person));

    return (boolean) result;
  }

  /**
   * Runs a specific function called "function event(room)".
   * <p>
   * Basically it calls a specific deputy defined "event".
   *
   * @param room       the event needs to be executed with this room as the focus.
   * @param sourceCode the source code, javascript, a string.
   * @return false if failed, true if successful
   * @throws ScriptException                             if an error occurred in the javascript
   * @throws NoSuchMethodException                       if the function cannot be found,
   * @throws java.lang.IllegalAccessException
   * @throws java.lang.InstantiationException
   * @throws java.lang.reflect.InvocationTargetException
   */
  public boolean run(mmud.database.entities.game.Room room, String sourceCode)
  {
    LOGGER.entering(this.getClass().getName(), "run(" + room.getId() + ")");

    Object inv = initialiseScriptEngine(sourceCode, "event", Value::asBoolean, new Room(room));

    return (boolean) inv;
  }

  /**
   * Runs a specific function called "function event()". This is a generic
   * event without a focus.
   *
   * @param sourceCode the source code, javascript, a string.
   * @return false if failed, true if successful
   * @throws ScriptException                             if an error occurred in the javascript
   * @throws NoSuchMethodException                       if the function cannot be found,
   * @throws java.lang.IllegalAccessException
   * @throws java.lang.InstantiationException
   * @throws java.lang.reflect.InvocationTargetException
   */
  public boolean run(String sourceCode)
  {
    LOGGER.entering(this.getClass().getName(), "run()");

    Object inv = initialiseScriptEngine(sourceCode, "event", Value::asBoolean);

    return (boolean) inv;
  }

  private Object initialiseScriptEngine(String sourceCode, String functionName, Function<Value, Object> function, Object... object)
  {
    LOGGER.entering(this.getClass().getName(), "initialiseScriptEngine");
    Object result = null;
    try (Context context = createContext())
    {
      // expose persons and rooms object as variable to script
      context.getBindings("js").putMember("persons", persons);
      context.getBindings("js").putMember("rooms", rooms);
      context.getBindings("js").putMember("items", items);
      context.getBindings("js").putMember("world", world);
      context.eval("js", sourceCode);
      Value value = context.eval("js", functionName);
      // returnValue should be the result of the execution of the JavaScript function.
      Value returnValue = object != null ? value.execute(object) : value.execute();
      result = function.apply(returnValue);
      context.getBindings("js").removeMember("persons");
      context.getBindings("js").removeMember("rooms");
      context.getBindings("js").removeMember("items");
      context.getBindings("js").removeMember("world");
    }

    return result;
  }

  private Context createContext()
  {
    return Context.newBuilder("js")
      .allowHostAccess(HostAccess.ALL)
      //allows access to all Java classes
      .allowHostClassLookup(className -> true)
      .build();
  }
}
