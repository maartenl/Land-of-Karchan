/*
 * Copyright (C) 2019 maartenl
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
package mmud.services;

import mmud.Constants;
import mmud.database.InputSanitizer;
import mmud.database.OutputFormatter;
import mmud.database.entities.characters.Person;
import mmud.exceptions.MudException;
import mmud.rest.webentities.Message;
import mmud.services.websocket.ChatLogEndPoint;

import javax.websocket.EncodeException;
import java.io.*;

import static mmud.database.entities.characters.Person.EMPTY_LOG;

/**
 * @author maartenl
 */
public class PersonCommunicationService implements CommunicationService
{

  private Person person;

  private File theLogfile;

  private StringBuffer theLog;

  PersonCommunicationService(Person person)
  {
    if (person == null)
    {
      throw new NullPointerException();
    }
    this.person = person;
  }

  /**
   * Clears the log.
   */
  public void clearLog() throws MudException
  {
    try (RandomAccessFile file = new RandomAccessFile(getLogfile(), "rw");)
    {
      file.setLength(0);
    } catch (FileNotFoundException fileNotFoundException)
    {
      throw new MudException("Log file not found.", fileNotFoundException);
    } catch (IOException e)
    {
      throw new MudException("Error writing to log file.", e);
    }
  }

  /**
   * writes a message to the log file of the character that contains all
   * communication and messages. The sentence will start with a capital.
   *
   * <p>
   * <b>Important!</b> : Use this method only for Environmental communication or
   * personal communication , as it does not check the Ignore Flag. Use the
   * writeMessage(Person aSource, String aMessage) for specific communication
   * between users.</p>
   *
   * @param aMessage the message to be written to the logfile.
   * @see #writeMessage(Person aSource, Person aTarget, String aMessage)
   * @see #writeMessage(Person aSource, String aMessage)
   */
  public void writeMessage(String aMessage) throws MudException
  {

    if (aMessage == null)
    {
      return;
    }
    aMessage = InputSanitizer.security(aMessage);

    int i = 0;
    int _container = 0;
    int foundit = -1;
    while ((i < aMessage.length()) && (foundit == -1))
    {
      if (_container == 0)
      {
        if (aMessage.charAt(i) == '<')
        {
          _container = 1;
        } else if (aMessage.charAt(i) != ' ')
        {
          foundit = i;
        }
      } else if (_container == 1)
      {
        if (aMessage.charAt(i) == '>')
        {
          _container = 0;
        }
      }
      i++;
    }
    if (foundit != -1)
    {
      aMessage = aMessage.substring(0, foundit)
        + Character.toUpperCase(aMessage.charAt(foundit))
        + aMessage.substring(foundit + 1);
    }
    try (FileWriter myFileWriter = new FileWriter(getLogfile(), true))
    {
      ChatLogEndPoint.send(person.getName(), new Message(null, person.getName(), aMessage, "chat"));
      myFileWriter.write(aMessage, 0, aMessage.length());

    } catch (IOException | EncodeException e)
    {
      throw new MudException("error writing message", e);
    }
  }

  /**
   * writes a message to the log file of the character that contains all
   * communication and messages. The message will be <I>interpreted</I> by
   * replacing the following values by the following other values: <TABLE>
   * <TR> <TD><B>REPLACE</B></TD> <TD><B>WITH (if target)</B></TD> <TD><B>WITH
   * (if not target)</B></TD> </TR> <TR> <TD>%TNAME</TD> <TD>you</TD>
   * <TD>name</TD> </TR> <TR> <TD>%TNAMESELF</TD> <TD>yourself</TD>
   * <TD>name</TD> </TR> <TR> <TD>%THISHER</TD> <TD>your</TD> <TD>his/her</TD>
   * </TR> <TR> <TD>%THIMHER</TD> <TD>you</TD> <TD>him/her</TD> </TR> <TR>
   * <TD>%THESHE</TD> <TD>you</TD> <TD>he/she</TD> </TR> <TR> <TD>%TISARE</TD>
   * <TD>are</TD> <TD>is</TD> </TR> <TR> <TD>%THASHAVE</TD> <TD>have</TD>
   * <TD>has</TD> </TR> <TR> <TD>%TYOUPOSS</TD> <TD>your</TD> <TD>name + s</TD>
   * </TR> <TR> <TD></TD> <TD></TD> <TD></TD> </TR> </TABLE>
   *
   * @param aMessage the message to be written to the logfile.
   * @param aSource  the source of the message, the thing originating the
   *                 message.
   * @param aTarget  the target of the message, could be null if there is not
   *                 target for this specific message.
   * @see #writeMessage(String aMessage)
   * @see #writeMessage(Person aSource, String aMessage)
   */
  public void writeMessage(Person aSource, Person aTarget, String aMessage) throws MudException
  {
    if (person.isIgnoring(aSource))
    {
      return;
    }
    String message = aMessage;
    if (aTarget == person)
    {
      message = message.replaceAll("%TNAMESELF", "yourself");
      message = message.replaceAll("%TNAME", "you");
      message = message.replaceAll("%THISHER", "your");
      message = message.replaceAll("%THIMHER", "you");
      message = message.replaceAll("%THESHE", "you");
      message = message.replaceAll("%TISARE", "are");
      message = message.replaceAll("%THASHAVE", "have");
      message = message.replaceAll("%TYOUPOSS", "your");
    } else
    {
      message = message.replaceAll("%TNAMESELF", aTarget.getName());
      message = message.replaceAll("%TNAME", aTarget.getName());
      message = message.replaceAll("%THISHER", (aTarget.getSex().posession()));
      message = message.replaceAll("%THIMHER", (aTarget.getSex().indirect()));
      message = message.replaceAll("%THESHE", (aTarget.getSex().direct()));
      message = message.replaceAll("%TISARE", "is");
      message = message.replaceAll("%THASHAVE", "has");
      message = message.replaceAll("%TYOUPOSS", aTarget.getName() + "s");
    }
    writeMessage(aSource, message);
  }

  /**
   * writes a message to the log file of the character that contains all
   * communication and messages. The message will be <I>interpreted</I> by
   * replacing the following values by the following other values: <TABLE>
   * <TR> <TD><B>REPLACE</B></TD> <TD><B>WITH (if source)</B></TD> <TD><B>WITH
   * (if not source)</B></TD> </TR> <TR> <TD>%SNAME</TD> <TD>you</TD>
   * <TD>name</TD> </TR> <TR> <TD>%SNAMESELF</TD> <TD>yourself</TD>
   * <TD>name</TD> </TR> <TR> <TD>%SHISHER</TD> <TD>your</TD> <TD>his/her</TD>
   * </TR> <TR> <TD>%SHIMHER</TD> <TD>you</TD> <TD>him/her</TD> </TR> <TR>
   * <TD>%SHESHE</TD> <TD>you</TD> <TD>he/she</TD> </TR> <TR> <TD>%SISARE</TD>
   * <TD>are</TD> <TD>is</TD> </TR> <TR> <TD>%SHASHAVE</TD> <TD>have</TD>
   * <TD>has</TD> </TR> <TR> <TD>%SYOUPOSS</TD> <TD>your</TD> <TD>name + s</TD>
   * </TR> <TR> <TD>%VERB1</TD> <TD></TD> <TD>es</TD> </TR> <TR>
   * <TD>%VERB2</TD> <TD></TD> <TD>s</TD> </TR> <TR> <TD></TD> <TD></TD>
   * <TD></TD> </TR> </TABLE>
   *
   * @param aMessage the message to be written to the logfile.
   * @param aSource  the source of the message, the thing originating the
   *                 message.
   * @see #writeMessage(Person aSource, Person aTarget, String aMessage)
   * @see #writeMessage(String aMessage)
   */
  public void writeMessage(Person aSource, String aMessage) throws MudException
  {
    if (person.isIgnoring(aSource))
    {
      return;
    }
    String message = aMessage;
    if (aSource == person)
    {
      message = message.replaceAll("%SNAMESELF", "yourself");
      message = message.replaceAll("%SNAME", "you");
      message = message.replaceAll("%SHISHER", "your");
      message = message.replaceAll("%SHIMHER", "you");
      message = message.replaceAll("%SHESHE", "you");
      message = message.replaceAll("%SISARE", "are");
      message = message.replaceAll("%SHASHAVE", "have");
      message = message.replaceAll("%SYOUPOSS", "your");
      message = message.replaceAll("%VERB1", "");
      message = message.replaceAll("%VERB2", "");
    } else
    {
      message = message.replaceAll("%SNAMESELF", aSource.getName());
      message = message.replaceAll("%SNAME", aSource.getName());
      message = message.replaceAll("%SHISHER", (aSource.getSex().posession()));
      message = message.replaceAll("%SHIMHER", (aSource.getSex().indirect()));
      message = message.replaceAll("%SHESHE", (aSource.getSex().direct()));
      message = message.replaceAll("%SISARE", "is");
      message = message.replaceAll("%SHASHAVE", "has");
      message = message.replaceAll("%SYOUPOSS", aSource.getName() + "s");
      message = message.replaceAll("%VERB1", "es");
      message = message.replaceAll("say%VERB2", "says");
      message = message.replaceAll("y%VERB2", "ies");
      message = message.replaceAll("%VERB2", "s");
    }
    writeMessage(message);
  }

  /**
   * Gets the log file, or creates it if it does not exist.
   *
   * @throws MudException which indicates probably that the log file could not
   *                      be created. Possibly due to either permissions or the directory does not
   *                      exist.
   */
  private File getLogfile()
  {
    if (theLogfile == null)
    {
      theLogfile = new File(Constants.getMudfilepath(), person.getName() + ".log");
    }
    try
    {
      theLogfile.createNewFile();
    } catch (IOException e)
    {
      throw new MudException("Error creating logfile for " + person.getName()
        + " in " + Constants.getMudfilepath(), e);
    }
    return theLogfile;
  }

  private void readLog() throws MudException
  {
    File file = getLogfile();

    try (BufferedReader reader = new BufferedReader(
      new FileReader(file)))
    {
      theLog = new StringBuffer(1000);

      char[] buf = new char[1024];
      int numRead;
      while ((numRead = reader.read(buf)) != -1)
      {
        theLog.append(buf, 0, numRead);
      }
    } catch (FileNotFoundException ex)
    {
      try
      {
        throw new MudException("Logfile of " + person.getName() + " (" + file.getCanonicalPath() + ") not found", ex);
      } catch (IOException ex1)
      {
        throw new MudException("Logfile of " + person.getName(), ex);
        // surrender peacefully with your hands up!
      }
    } catch (IOException ex)
    {
      try
      {
        throw new MudException("Error reading logfile of " + person.getName() + " (" + file.getCanonicalPath() + ")", ex);
      } catch (IOException ex1)
      {
        throw new MudException("Error reading logfile of " + person.getName(), ex);
        // surrender peacefully with your hands up!
      }
    }
  }

  /**
   * Returns the log starting from the offset, or an empty string if the offset
   * is past the length of the log.Can also contain the empty string, if the log
   * happens to be empty.
   *
   * @param offset the offset from whence to read the log. Offset starts with 0
   *               and is inclusive.
   * @return a String, part of the Log.
   * @throws MudException in case of accidents with reading the log, or a
   *                      negative offset.
   */
  public String getLog(Integer offset) throws MudException
  {
    if (offset == null)
    {
      offset = 0;
    }
    if (theLog == null)
    {
      readLog();
    }
    if (offset >= theLog.length())
    {
      return EMPTY_LOG;
    }
    if (offset < 0)
    {
      throw new MudException("Attempting to get log with negative offset.");
    }
    return theLog.substring(offset);
  }

  /**
   * Returns the amount of money that you are carrying.
   *
   * @return String description of the amount of money.
   */
  public String getDescriptionOfMoney()
  {
    return OutputFormatter.getDescriptionOfMoney(person.getCopper());
  }
}
