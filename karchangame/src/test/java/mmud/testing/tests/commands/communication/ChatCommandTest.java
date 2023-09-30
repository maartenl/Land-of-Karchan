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
package mmud.testing.tests.commands.communication;

import jakarta.annotation.Nonnull;
import mmud.Constants;
import mmud.commands.CommandFactory;
import mmud.commands.CommandRunner;
import mmud.commands.NormalCommand;
import mmud.commands.communication.*;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Chatline;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.exceptions.ChatlineAlreadyExistsException;
import mmud.exceptions.ChatlineNotFoundException;
import mmud.services.ChatService;
import mmud.services.CommunicationService;
import mmud.services.PersonService;
import mmud.testing.TestingConstants;
import mmud.testing.tests.LogServiceStub;
import mmud.testing.tests.MudTest;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author maartenl
 */
public class ChatCommandTest extends MudTest
{
  private Map<String, Chatline> chatlines = new HashMap<>();

  private Administrator karn;
  private User marvin;

  private LogServiceStub logService;

  private CommandRunner commandRunner = new CommandRunner();

  private PersonService personService;

  private ChatService chatService;

  public ChatCommandTest()
  {
  }

  /**
   * Tries to chat to an unknown chatline.
   */
  @Test
  public void testUnknownChatline()
  {
    ChatCommand chatCommand = new ChatCommand("chat (\\w)+ .+");
    chatCommand.setCallback(commandRunner);
    assertThat(chatCommand.getRegExpr()).isEqualTo("chat (\\w)+ .+");
    DisplayInterface display = chatCommand.run("chat help Help me!", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(log).isEqualTo("Unknown chatline.<br />\r\n");
  }

  /**
   * Creates a chatline and tries successfully to chat to it.
   */
  @Test
  public void testChatline()
  {
    commandRunner.setServices(personService, null, null, null, null, null, null, chatService);
    CreateChatCommand createChatCommand = new CreateChatCommand("createchat (\\w)+ (\\w)+ (\\w)+");
    createChatCommand.setCallback(commandRunner);
    assertThat(createChatCommand.getRegExpr()).isEqualTo("createchat (\\w)+ (\\w)+ (\\w)+");
    DisplayInterface display = createChatCommand.run("createchat help none blue", marvin);

    ChatCommand chatCommand = new ChatCommand("chat (\\w)+ .+");
    chatCommand.setCallback(commandRunner);
    assertThat(chatCommand.getRegExpr()).isEqualTo("chat (\\w)+ .+");
    display = chatCommand.run("chat help Help me!", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(log).startsWith("""
        Chatline created.<br />\r
        <span class="chat-blue">[help]<b>Marvin</b>: Help me!</span><br />\r
        """);
  }


  /**
   * Edit a chatline and tries successfully to chat to it.
   */
  @Test
  public void testEditChatline()
  {
    commandRunner.setServices(personService, null, null, null, null, null, null, chatService);
    List<NormalCommand> commands = CommandFactory.getCommand("createchat help none blue");
    NormalCommand createChatCommand = commands.get(0);
    createChatCommand.setCallback(commandRunner);
    assertThat(createChatCommand.getRegExpr()).isEqualTo("createchat (\\w)+ (\\w)+ (\\w)+");
    DisplayInterface display = createChatCommand.run("createchat help none blue", marvin);

    EditChatCommand editChatCommand = new EditChatCommand("editchat (\\w)+ (\\w)+ (\\w)+");
    editChatCommand.setCallback(commandRunner);
    assertThat(editChatCommand.getRegExpr()).isEqualTo("editchat (\\w)+ (\\w)+ (\\w)+");
    editChatCommand.run("editchat help none red", marvin);
    Chatline help = chatlines.get("help");
    assertThat(help).isNotNull();
    try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions())
    {
      softly.assertThat(help.getChatname()).isEqualTo("help");
      softly.assertThat(help.getAttributename()).isNull();
      softly.assertThat(help.getColour()).isEqualTo("red");
      softly.assertThat(help.getOwner()).isNotNull();
      softly.assertThat(help.getOwner() != null ? help.getOwner().getName() : null).isEqualTo("Marvin");
      softly.assertThat(help.getId()).isZero();
    }

    ChatCommand chatCommand = new ChatCommand("chat (\\w)+ .+");
    chatCommand.setCallback(commandRunner);
    assertThat(chatCommand.getRegExpr()).isEqualTo("chat (\\w)+ .+");
    display = chatCommand.run("chat help Help me!", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(log).startsWith("""
        Chatline created.<br />\r
        Chatline edited.<br />\r
        <span class="chat-red">[help]<b>Marvin</b>: Help me!</span><br />\r
        """);
  }

  /**
   * Removes a chatline.
   */
  @Test
  public void testRemoveChatline()
  {
    commandRunner.setServices(personService, null, null, null, null, null, null, chatService);
    CreateChatCommand createChatCommand = new CreateChatCommand("createchat (\\w)+ (\\w)+ (\\w)+");
    createChatCommand.setCallback(commandRunner);
    assertThat(createChatCommand.getRegExpr()).isEqualTo("createchat (\\w)+ (\\w)+ (\\w)+");
    DisplayInterface display = createChatCommand.run("createchat help none blue", marvin);

    assertThat(chatlines.get("help")).isNotNull();

    DeleteChatCommand deleteChatCommand = new DeleteChatCommand("deletechat (\\w)+");
    deleteChatCommand.setCallback(commandRunner);
    assertThat(deleteChatCommand.getRegExpr()).isEqualTo("deletechat (\\w)+");
    display = deleteChatCommand.run("deletechat help", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(log).startsWith("""
        Chatline created.<br />\r
        You removed the chatline.<br />\r
        """);
    assertThat(chatlines.get("help")).isNull();
  }

  /**
   * Creates a chatline twice and fail.
   */
  @Test
  public void testCreateChatlineTwice()
  {
    commandRunner.setServices(personService, null, null, null, null, null, null, chatService);
    CreateChatCommand createChatCommand = new CreateChatCommand("createchat (\\w)+ (\\w)+ (\\w)+");
    createChatCommand.setCallback(commandRunner);
    assertThat(createChatCommand.getRegExpr()).isEqualTo("createchat (\\w)+ (\\w)+ (\\w)+");
    DisplayInterface display = createChatCommand.run("createchat help none blue", marvin);

    Chatline chatline = new Chatline();
    chatline.setChatname("help");
    chatline.setAttributename(null);
    chatline.setColour("blue");

    chatlines.put("help", chatline);
    display = createChatCommand.run("createchat help none blue", marvin);

    String log = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(log).startsWith("""
        Chatline created.<br />\r
        Chatline already exists.<br />\r
        """);
  }

  /**
   * Creates a chatline and tries successfully to chat to it, then leave.
   */
  @Test
  public void testLeaveChatline()
  {
    commandRunner.setServices(personService, null, null, null, null, null, null, chatService);
    CreateChatCommand createChatCommand = new CreateChatCommand("createchat (\\w)+ (\\w)+ (\\w)+");
    createChatCommand.setCallback(commandRunner);
    assertThat(createChatCommand.getRegExpr()).isEqualTo("createchat (\\w)+ (\\w)+ (\\w)+");
    DisplayInterface display = createChatCommand.run("createchat help none blue", marvin);

    ChatCommand chatCommand = new ChatCommand("chat (\\w)+ .+");
    chatCommand.setCallback(commandRunner);
    assertThat(chatCommand.getRegExpr()).isEqualTo("chat (\\w)+ .+");
    display = chatCommand.run("chat help Help me!", marvin);

    LeaveChatCommand leaveChatCommand = new LeaveChatCommand("leavechat (\\w)+");
    leaveChatCommand.setCallback(commandRunner);
    assertThat(leaveChatCommand.getRegExpr()).isEqualTo("leavechat (\\w)+");
    display = leaveChatCommand.run("leavechat help", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(log).startsWith("""
        Chatline created.<br />\r
        <span class="chat-blue">[help]<b>Marvin</b>: Help me!</span><br />\r
        You left chatline.<br />\r
        """);
  }

  /**
   * Try to leave a chatline you've not joined.
   */
  @Test
  public void testLeaveUnjoinedChatline()
  {
    commandRunner.setServices(personService, null, null, null, null, null, null, chatService);
    LeaveChatCommand leaveChatCommand = new LeaveChatCommand("leavechat (\\w)+");
    leaveChatCommand.setCallback(commandRunner);
    assertThat(leaveChatCommand.getRegExpr()).isEqualTo("leavechat (\\w)+");
    DisplayInterface display = leaveChatCommand.run("leavechat help", marvin);

    String log = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(log).startsWith("Chatline not found.<br />");
  }

  /**
   * Tries to join an unknown chatline.
   */
  @Test
  public void testJoinUnknownChatline()
  {
    JoinChatCommand joinChatCommand = new JoinChatCommand("joinchat (\\w)+");
    joinChatCommand.setCallback(commandRunner);
    assertThat(joinChatCommand.getRegExpr()).isEqualTo("joinchat (\\w)+");
    DisplayInterface display = joinChatCommand.run("joinchat help", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(log).isEqualTo("Chatline not found.<br />\r\n");
  }

  /**
   * Creates a chatline and somebody else tries to join it and chat on it.
   */
  @Test
  public void testJoinChatline()
  {
    commandRunner.setServices(personService, null, null, null, null, null, null, chatService);
    CreateChatCommand createChatCommand = new CreateChatCommand("createchat (\\w)+ (\\w)+ (\\w)+");
    createChatCommand.setCallback(commandRunner);
    assertThat(createChatCommand.getRegExpr()).isEqualTo("createchat (\\w)+ (\\w)+ (\\w)+");
    DisplayInterface display = createChatCommand.run("createchat help none blue", marvin);
    Chatline chatline = new Chatline();
    chatline.setChatname("help");
    chatline.setAttributename(null);
    chatline.setColour("blue");

    chatlines.put("help", chatline);
    JoinChatCommand joinChatCommand = new JoinChatCommand("joinchat (\\w)+");
    joinChatCommand.setCallback(commandRunner);
    assertThat(joinChatCommand.getRegExpr()).isEqualTo("joinchat (\\w)+");
    display = joinChatCommand.run("joinchat help", karn);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(log).isEqualTo("You joined a chatline.<br />\r\n");

    ChatCommand chatCommand = new ChatCommand("chat (\\w)+ .+");
    chatCommand.setCallback(commandRunner);
    assertThat(chatCommand.getRegExpr()).isEqualTo("chat (\\w)+ .+");
    display = chatCommand.run("chat help Help me!", karn);
    assertThat(display).isNotNull();
    log = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(log).startsWith("""
        Chatline created.<br />\r
        <span class="chat-blue">[help]<b>Karn</b>: Help me!</span><br />\r
        """);
    log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(log).startsWith("""
        You joined a chatline.<br />\r
        <span class="chat-blue">[help]<b>Karn</b>: Help me!</span><br />\r
        """);
  }


  /**
   * Creates a chatline and somebody else tries to join it and chat on it.
   */
  @Test
  public void testJoinChatlineNotAllowed()
  {
    commandRunner.setServices(personService, null, null, null, null, null, null, chatService);
    CreateChatCommand createChatCommand = new CreateChatCommand("createchat (\\w)+ (\\w)+ (\\w)+");
    createChatCommand.setCallback(commandRunner);
    assertThat(createChatCommand.getRegExpr()).isEqualTo("createchat (\\w)+ (\\w)+ (\\w)+");
    DisplayInterface display = createChatCommand.run("createchat help access-restricted blue", marvin);
    Chatline chatline = new Chatline();
    chatline.setChatname("help");
    chatline.setAttributename("access-restricted");
    chatline.setColour("blue");

    chatlines.put("help", chatline);
    JoinChatCommand joinChatCommand = new JoinChatCommand("joinchat (\\w)+");
    joinChatCommand.setCallback(commandRunner);
    assertThat(joinChatCommand.getRegExpr()).isEqualTo("joinchat (\\w)+");
    display = joinChatCommand.run("joinchat help", karn);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(log).isEqualTo("Chatline not allowed.<br />\r\n");
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
    chatlines = new HashMap<>();
    logService = new LogServiceStub();
    chatService = new ChatService(logService)
    {
      @Override
      public Chatline createChatline(String chatlinename, String attribute, String colour, User aUser)
      {
        if (chatlines.containsKey(chatlinename))
        {
          throw new ChatlineAlreadyExistsException(chatlinename);
        }
        Chatline chatline = new Chatline();
        chatline.setChatname(chatlinename);
        chatline.setAttributename(attribute);
        chatline.setColour(colour);
        chatline.setOwner(aUser);
        chatlines.put(chatlinename, chatline);
        return chatline;
      }

      @Override
      public Optional<Chatline> getChatline(@Nonnull String chatlinename)
      {
        return Optional.ofNullable(chatlines.get(chatlinename));
      }

      @Override
      public void deleteChatLine(User aUser, @Nonnull String chatlinename)
      {
        chatlines.remove(chatlinename,
            getChatline(chatlinename).orElseThrow(() -> new ChatlineNotFoundException(chatlinename)));
      }
    };
    personService = new PersonService(logService)
    {

      @Override
      public User getActiveUser(String name)
      {
        if (name.equalsIgnoreCase("Marvin"))
        {
          return marvin;
        }
        if (name.equalsIgnoreCase("Karn"))
        {
          return karn;
        }
        return null;
      }

      @Override
      public List<User> getActivePlayers()
      {
        return Arrays.asList(karn, marvin);
      }

    };

    karn = TestingConstants.getKarn();
    final Room room = TestingConstants.getRoom(TestingConstants.getArea());
    karn.setRoom(room);
    marvin = TestingConstants.getMarvin(room);
    CommunicationService.getCommunicationService(karn).clearLog();
    CommunicationService.getCommunicationService(marvin).clearLog();
    room.addPerson(karn);
    room.addPerson(marvin);
    File file = new File(Constants.getMudfilepath() + File.separator + "Karn.log");
    PrintWriter writer = new PrintWriter(file);
    writer.close();
    file = new File(Constants.getMudfilepath() + File.separator + "Marvin.log");
    writer = new PrintWriter(file);
    writer.close();
  }

}
