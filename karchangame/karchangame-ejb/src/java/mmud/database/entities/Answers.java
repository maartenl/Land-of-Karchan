/*
 * Copyright (C) 2011 maartenl
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
package mmud.database.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_answers")
@NamedQueries(
{
    @NamedQuery(name = "Answers.findAll", query = "SELECT a FROM Answers a"),
    @NamedQuery(name = "Answers.findByName", query = "SELECT a FROM Answers a WHERE a.answersPK.name = :name"),
    @NamedQuery(name = "Answers.findByQuestion", query = "SELECT a FROM Answers a WHERE a.answersPK.question = :question")
})
public class Answers implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AnswersPK answersPK;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "answer")
    private String answer;
    @JoinColumn(name = "name", referencedColumnName = "name", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Person person;

    public Answers()
    {
    }

    public Answers(AnswersPK answersPK)
    {
        this.answersPK = answersPK;
    }

    public Answers(AnswersPK answersPK, String answer)
    {
        this.answersPK = answersPK;
        this.answer = answer;
    }

    public Answers(String name, String question)
    {
        this.answersPK = new AnswersPK(name, question);
    }

    public AnswersPK getAnswersPK()
    {
        return answersPK;
    }

    public void setAnswersPK(AnswersPK answersPK)
    {
        this.answersPK = answersPK;
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
        hash += (answersPK != null ? answersPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Answers))
        {
            return false;
        }
        Answers other = (Answers) object;
        if ((this.answersPK == null && other.answersPK != null) || (this.answersPK != null && !this.answersPK.equals(other.answersPK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.Answers[ answersPK=" + answersPK + " ]";
    }

}
