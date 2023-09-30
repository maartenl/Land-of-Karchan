package mmud.services;

import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Chatline;
import mmud.database.enums.God;
import mmud.exceptions.ChatlineAlreadyExistsException;
import mmud.exceptions.ChatlineNotFoundException;
import mmud.exceptions.ChatlineOperationNotAllowedException;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ChatService
{
  private static final Logger LOGGER = Logger.getLogger(ChatService.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager entityManager;

  private LogService logService;

  @Inject
  public ChatService(LogService logService)
  {
    this.logService = logService;
  }

  /**
   * Delete the chatline including all the users making use of this chatline.
   * Some notes:
   * 1. you are either a deputy or the owner of the chatline
   *
   * @param chatlinename name of the chatline
   * @param aUser        the user issueing the request for deleting the chatline
   */
  public void deleteChatLine(User aUser, @Nonnull String chatlinename)
  {
    Chatline chatline = getChatline(chatlinename).orElseThrow(() -> new ChatlineNotFoundException("chatlinename"));
    if (hasNoRights(aUser, chatline))
    {
      throw new ChatlineOperationNotAllowedException("Cannot delete chatline " + chatlinename + ". You are neither " +
          "administrator nor owner.");
    }
    logService.writeLog(aUser, " removed chatline " + chatlinename + ".");
    LOGGER.info(MessageFormat.format("{0} removed chatline {1}.", aUser.getName(), chatlinename));
    entityManager.remove(chatline);
  }

  private boolean hasNoRights(User aUser, Chatline chatline)
  {
    LOGGER.fine(MessageFormat.format("hasRights: {0} has rights to chatline {1} ({2}{3}).", aUser.getName(),
        chatline.getChatname(), aUser.getGod(), chatline.getOwner() != null ? chatline.getOwner().getName() : null));
    return !(aUser.getGod() == God.GOD || aUser.equals(chatline.getOwner()));
  }

  public Optional<Chatline> getChatline(@Nonnull String chatlinename)
  {
    return entityManager.createNamedQuery("Chatline.findByName", Chatline.class)
        .setParameter("name", chatlinename)
        .getResultList()
        .stream().findFirst();
  }

  public Chatline createChatline(String chatlinename, String attribute, String colour, User aUser)
  {
    if (getChatline(chatlinename).isPresent())
    {
      throw new ChatlineAlreadyExistsException(chatlinename);
    }
    Chatline chatline = new Chatline();
    chatline.setChatname(chatlinename);
    chatline.setAttributename(attribute);
    chatline.setColour(colour);
    chatline.setOwner(aUser);
    entityManager.persist(chatline);
    logService.writeLog(aUser, " created chatline " + chatlinename + ".");
    LOGGER.info(MessageFormat.format("{0} created chatline {1}.", aUser.getName(), chatlinename));
    return chatline;
  }

  public void editChatline(String chatlinename, String attribute, String colour, User aUser, User newOwner)
  {
    Chatline chatline = getChatline(chatlinename).orElseThrow(() -> new ChatlineNotFoundException("chatlinename"));
    if (hasNoRights(aUser, chatline))
    {
      throw new ChatlineOperationNotAllowedException("Cannot edit chatline " + chatlinename + ". You are neither " +
          "administrator nor owner.");
    }
    chatline.setChatname(chatlinename);
    chatline.setAttributename(attribute);
    chatline.setColour(colour);
    chatline.setOwner(newOwner);
    logService.writeLog(aUser, " edited chatline " + chatlinename + ".");
    LOGGER.info(MessageFormat.format("{0} edited chatline {1}.", aUser.getName(), chatlinename));
  }

  public List<Chatline> getChatlines()
  {
    return entityManager.createNamedQuery("Chatline.all", Chatline.class).getResultList();
  }

}
