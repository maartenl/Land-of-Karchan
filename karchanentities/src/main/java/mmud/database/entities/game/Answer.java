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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mmud.database.entities.characters.Person;

import java.io.Serializable;

/**
 * @author maartenl
 */
@Entity
@Table(name = "mm_answers")
@NamedQuery(name = "Answer.findAll", query = "SELECT a FROM Answer a")
@NamedQuery(name = "Answer.findByName", query = "SELECT a FROM Answer a WHERE a.answerPK.name = :name")
@NamedQuery(name = "Answer.deleteAnswers", query = "DELETE FROM Answer a WHERE a.person = :person")
@NamedQuery(name = "Answer.findByQuestion", query = "SELECT a FROM Answer a WHERE a.answerPK.question = :question")
public class Answer implements Serializable
{
  private static final long serialVersionUID = 1L;
  @EmbeddedId
  protected AnswerPK answerPK;
  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 1, max = 65535)
  @Column(name = "answer")
  private String answer;
  @JoinColumn(name = "name", referencedColumnName = "name", insertable = false, updatable = false)
  @ManyToOne(optional = false)
  private Person person;

  public Answer()
  {
  }

  public Answer(AnswerPK answerPK)
  {
    this.answerPK = answerPK;
  }

  public Answer(AnswerPK answerPK, String answer)
  {
    this.answerPK = answerPK;
    this.answer = answer;
  }

  public Answer(String name, String question)
  {
    this.answerPK = new AnswerPK(name, question);
  }

  public AnswerPK getAnswerPK()
  {
    return answerPK;
  }

  public void setAnswerPK(AnswerPK answerPK)
  {
    this.answerPK = answerPK;
  }

  public String getAnswer()
  {
    return answer;
  }

  public void setAnswer(String answer)
  {
    this.answer = answer;
  }

  public Person getPerson()
  {
    return person;
  }

  public void setPerson(Person person)
  {
    this.person = person;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (answerPK != null ? answerPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Answer))
    {
      return false;
    }
    Answer other = (Answer) object;
    if ((this.answerPK == null && other.answerPK != null) || (this.answerPK != null && !this.answerPK.equals(other.answerPK)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.Answer[ answerPK=" + answerPK + " ]";
  }

}
