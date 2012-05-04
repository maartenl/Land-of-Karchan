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
import mmud.database.entities.characters.Person;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_macro", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "Macro.findAll", query = "SELECT m FROM Macro m"),
    @NamedQuery(name = "Macro.findByName", query = "SELECT m FROM Macro m WHERE m.macroPK.name = :name"),
    @NamedQuery(name = "Macro.findByMacroname", query = "SELECT m FROM Macro m WHERE m.macroPK.macroname = :macroname")
})
public class Macro implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MacroPK macroPK;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "contents")
    private String contents;
    @JoinColumn(name = "name", referencedColumnName = "name", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Person person;

    public Macro()
    {
    }

    public Macro(MacroPK macroPK)
    {
        this.macroPK = macroPK;
    }

    public Macro(MacroPK macroPK, String contents)
    {
        this.macroPK = macroPK;
        this.contents = contents;
    }

    public Macro(String name, String macroname)
    {
        this.macroPK = new MacroPK(name, macroname);
    }

    public MacroPK getMacroPK()
    {
        return macroPK;
    }

    public void setMacroPK(MacroPK macroPK)
    {
        this.macroPK = macroPK;
    }

    public String getContents()
    {
        return contents;
    }

    public void setContents(String contents)
    {
        this.contents = contents;
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
        hash += (macroPK != null ? macroPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Macro))
        {
            return false;
        }
        Macro other = (Macro) object;
        if ((this.macroPK == null && other.macroPK != null) || (this.macroPK != null && !this.macroPK.equals(other.macroPK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.Macro[ macroPK=" + macroPK + " ]";
    }

}
