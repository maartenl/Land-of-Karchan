package mmud.services;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;
import javax.script.ScriptException;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.core.Response;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.game.Event;
import mmud.database.entities.game.Method;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.Item;
import mmud.exceptions.MudWebException;
import mmud.rest.services.EventsRestService;
import mmud.scripting.Items;
import mmud.scripting.ItemsInterface;
import mmud.scripting.Persons;
import mmud.scripting.Rooms;
import mmud.scripting.RoomsInterface;
import mmud.scripting.RunScript;
import mmud.scripting.World;
import mmud.scripting.WorldInterface;

public class EventsService
{
  private static final Logger LOGGER = Logger.getLogger(EventsService.class.getName());

  @Inject
  private LogService logService;

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private PersonService personService;

  @Inject
  private RoomsService roomsService;

  @Inject
  private ItemService itemService;

  @Inject
  private AttributeService attributeService;

  /**
   * Returns the entity manager of JPA. This is defined in
   * build/web/WEB-INF/classes/META-INF/persistence.xml.
   *
   * @return EntityManager
   */
  protected EntityManager getEntityManager()
  {
    return em;
  }

  /**
   * Runs a single event, right now. Used by administrators for testing.
   *
   * @param aUser   the administrator running the event
   * @param eventid an event id, a number.
   */
  public void runSingleEvent(Administrator aUser, Integer eventid)
  {
    LOGGER.entering(this.getClass().getName(), "runSingleEvent");
    if (eventid == null)
    {
      throw new MudWebException(null, "Event id was empty.", Response.Status.BAD_REQUEST);
    }
    Event event = getEntityManager().find(Event.class, eventid);
    if (event == null)
    {
      throw new MudWebException(null, "Event was not found.", Response.Status.NOT_FOUND);
    }
    Persons persons = new Persons(personService);
    Rooms rooms = new Rooms(new RoomsInterface()
    {
      @Override
      public Room find(Long id)
      {
        return roomsService.find(id);
      }
    });
    Items items = new Items(new ItemsInterface()
    {
      @Override
      public Item createItem(long itemdefnr)
      {
        return itemService.createItem(itemdefnr);
      }
    });
    World world = new World(new WorldInterface()
    {
      @Override
      public String getAttribute(String name)
      {
        return attributeService.getAttribute(name);
      }
    });
    RunScript runScript = new RunScript(persons, rooms, items, world, logService);
    Method method = event.getMethod();
    try
    {
      if (event.getRoom() != null)
      {
        runScript.run(event.getRoom(), method.getSrc());
      } else if (event.getPerson() != null)
      {
        runScript.run(event.getPerson(), method.getSrc());
      } else
      {
        runScript.run(method.getSrc());
      }
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException | ScriptException |
             NoSuchMethodException ex)
    {
      // Error occurred: turn this event off!
      // TODO: that's for debugging,...
      // event.setCallable(Boolean.FALSE);
      // log it but keep going with the next event.
      logService.writeLogException(ex);
      LOGGER.throwing(EventsRestService.class.getName(), "events()", ex);
      throw new MudWebException(aUser == null ? null : aUser.getName(), ex.getMessage(), ex, Response.Status.BAD_REQUEST);
    }
  }
}
