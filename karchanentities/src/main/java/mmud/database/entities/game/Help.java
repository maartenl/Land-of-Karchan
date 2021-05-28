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
package mmud.database.entities.game;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import mmud.database.OutputFormatter;

/**
 * @author maartenl
 */
@Entity
@Table(name = "mm_help")
@NamedQueries(
  {
    @NamedQuery(name = "Help.findAll", query = "SELECT h FROM Help h"),
    @NamedQuery(name = "Help.findByCommand", query = "SELECT h FROM Help h WHERE h.command = :command"),
    @NamedQuery(name = "Help.findBySynopsis", query = "SELECT h FROM Help h WHERE h.synopsis = :synopsis"),
    @NamedQuery(name = "Help.findBySeealso", query = "SELECT h FROM Help h WHERE h.seealso = :seealso"),
    @NamedQuery(name = "Help.findByExample1", query = "SELECT h FROM Help h WHERE h.example1 = :example1"),
    @NamedQuery(name = "Help.findByExample1a", query = "SELECT h FROM Help h WHERE h.example1a = :example1a"),
    @NamedQuery(name = "Help.findByExample1b", query = "SELECT h FROM Help h WHERE h.example1b = :example1b"),
    @NamedQuery(name = "Help.findByExample2", query = "SELECT h FROM Help h WHERE h.example2 = :example2"),
    @NamedQuery(name = "Help.findByExample2a", query = "SELECT h FROM Help h WHERE h.example2a = :example2a"),
    @NamedQuery(name = "Help.findByExample2b", query = "SELECT h FROM Help h WHERE h.example2b = :example2b"),
    @NamedQuery(name = "Help.findByExample2c", query = "SELECT h FROM Help h WHERE h.example2c = :example2c"),
    @NamedQuery(name = "Help.countAll", query = "SELECT count(h) FROM Help h")
  })
public class Help implements Serializable, DisplayInterface
{

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 20)
  @Column(name = "command")
  private String command;
  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 1, max = 65535)
  @Column(name = "contents")
  private String contents;
  @Size(max = 120)
  @Column(name = "synopsis")
  private String synopsis;
  @Size(max = 120)
  @Column(name = "seealso")
  private String seealso;
  @Size(max = 120)
  @Column(name = "example1")
  private String example1;
  @Size(max = 120)
  @Column(name = "example1a")
  private String example1a;
  @Size(max = 120)
  @Column(name = "example1b")
  private String example1b;
  @Size(max = 120)
  @Column(name = "example2")
  private String example2;
  @Size(max = 120)
  @Column(name = "example2a")
  private String example2a;
  @Size(max = 120)
  @Column(name = "example2b")
  private String example2b;
  @Size(max = 120)
  @Column(name = "example2c")
  private String example2c;

  public Help()
  {
  }

  public Help(String command)
  {
    this.command = command;
  }

  public Help(String command, String contents)
  {
    this.command = command;
    this.contents = contents;
  }

  /**
   * The command for which help is requested and found.
   *
   * @return String containing the command.
   */
  public String getCommand()
  {
    return command;
  }

  public void setCommand(String command)
  {
    this.command = command;
  }

  /**
   * The explanation, description, whatever-you-may-call-it.
   *
   * @return 'the help' as a String
   */
  public String getContents()
  {
    return contents;
  }

  public void setContents(String contents)
  {
    this.contents = contents;
  }

  /**
   * The way the command syntax works.
   *
   * @return String, for example "swoon [to &lt;person&gt;]".
   */
  public String getSynopsis()
  {
    return synopsis;
  }

  public void setSynopsis(String synopsis)
  {
    this.synopsis = synopsis;
  }

  /**
   * Reference to other commands.
   *
   * @return references to other commands.
   */
  public String getSeealso()
  {
    return seealso;
  }

  public void setSeealso(String seealso)
  {
    this.seealso = seealso;
  }

  /**
   * An example of the way the command is executed.
   *
   * @return For example "unwield pick from bothhands".
   */
  public String getExample1()
  {
    return example1;
  }

  public void setExample1(String example1)
  {
    this.example1 = example1;
  }

  /**
   * Output of the example visible to you.
   *
   * @return For example "You open an old, iron chest.".
   */
  public String getExample1a()
  {
    return example1a;
  }

  public void setExample1a(String example1a)
  {
    this.example1a = example1a;
  }

  /**
   * Output of the example visible to others.
   *
   * @return For example "Hotblack locks an old, iron chest with an old iron key."
   */
  public String getExample1b()
  {
    return example1b;
  }

  public void setExample1b(String example1b)
  {
    this.example1b = example1b;
  }

  /**
   * An example of the way the command is executed to someone else.
   *
   * @return For example "tell to marvin Hey!".
   */
  public String getExample2()
  {
    return example2;
  }

  public void setExample2(String example2)
  {
    this.example2 = example2;
  }

  /**
   * What the players sees.
   *
   * @return For example "<b>You tell Marvin</b> : Hey!".
   */
  public String getExample2a()
  {
    return example2a;
  }

  public void setExample2a(String example2a)
  {
    this.example2a = example2a;
  }

  /**
   * What the target notices.
   *
   * @return For example "<b>Hotblack tells you</b> : Hey!".
   */
  public String getExample2b()
  {
    return example2b;
  }

  public void setExample2b(String example2b)
  {
    this.example2b = example2b;
  }

  /**
   * What a third party sees.
   *
   * @return For example "Hotblack says [to Marvin]: Hey!".
   */
  public String getExample2c()
  {
    return example2c;
  }

  public void setExample2c(String example2c)
  {
    this.example2c = example2c;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (command != null ? command.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Help))
    {
      return false;
    }
    Help other = (Help) object;
    if ((this.command == null && other.command != null) || (this.command != null && !this.command.equals(other.command)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String getMainTitle()
  {
    return getCCommand();
  }

  @Override
  public String getImage()
  {
    return null;
  }

  @Override
  public String getBody()
  {
    String result = " <dl><dt><b>NAME</b></dt>"
      + "<dd><b>" + getCCommand() + "</b> - formatted output</dd><p/>"
      + "<dt><b>SYNOPSIS</b></dt>" + "<dd>" + getCSynopsis() + "</dd><p/>"
      + "<dt><b>DESCRIPTION</b></dt>" + "<dd>" + getCDescription() + "</dd><p/>"
      + "<dt><b>EXAMPLES</b></dt><dd>";
    result += getExample1Description();
    result += getExample2Description();
    result += "</dd><dt><b>SEE ALSO</b></dt><dd>" + getSeealso() + "<p/></dd></dl>";
    return result;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.Help[ command=" + command + " ]";
  }

  private String getExample2Description()
  {
    if ((example2 == null))
    {
      return "";
    }
    StringBuilder result = new StringBuilder();
    if ((example2 != null) && !example2.trim().equals(""))
    {
      result.append("\"").append(example2).append("\"<p/>");
    }
    if ((example2a != null) && !example2a.trim().equals(""))
    {
      result.append("You: <tt>").append(example2a).append("</tt><br/>");
    }
    if ((example2b != null) && !example2b.trim().equals(""))
    {
      result.append("Marvin: <tt>").append(example2b).append("</tt><br/>");
    }
    if ((example2c != null) && !example2c.trim().equals(""))
    {
      result.append("Anybody: <tt>").append(example2c).append("</tt><p/>");
    }
    return result.toString();
  }

  private String getExample1Description()
  {
    if (example1 == null)
    {
      return "";
    }
    StringBuilder result = new StringBuilder();
    if ((example1 != null) && !example1.trim().equals(""))
    {
      result.append("\"").append(example1).append("\"<p/>");
    }
    if ((example1a != null) && !example1a.trim().equals(""))
    {
      result.append("You: <tt>").append(example1a).append("</tt><br/>");
    }
    if ((example1b != null) && !example1b.trim().equals(""))
    {
      result.append("Anybody: <tt>").append(example1b).append("</tt><p/>");
    }
    return result.toString();
  }

  private String getCCommand()
  {
    return command.substring(0, 1).toUpperCase()
      + command.substring(1);
  }

  private String getCSynopsis()
  {
    if (getSynopsis() == null)
    {
      return "";
    }
    String command = OutputFormatter.htmlEscaper(getCommand());
    String synopsis = OutputFormatter.htmlEscaper(getSynopsis());
    return synopsis.replaceAll(command, "<b>" + command + "</b>");
  }

  private String getCDescription()
  {
    return getContents().replaceAll(getCommand(),
      "<b>" + getCommand() + "</b>").replaceAll(getCCommand(),
      "<b>" + getCCommand() + "</b>");
  }
}
