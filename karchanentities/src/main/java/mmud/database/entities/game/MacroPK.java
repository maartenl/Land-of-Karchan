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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Embeddable
public class MacroPK implements Serializable
{
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "macroname")
    private String macroname;

    public MacroPK()
    {
    }

    public MacroPK(String name, String macroname)
    {
        this.name = name;
        this.macroname = macroname;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMacroname()
    {
        return macroname;
    }

    public void setMacroname(String macroname)
    {
        this.macroname = macroname;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        hash += (macroname != null ? macroname.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MacroPK))
        {
            return false;
        }
        MacroPK other = (MacroPK) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name)))
        {
            return false;
        }
        if ((this.macroname == null && other.macroname != null) || (this.macroname != null && !this.macroname.equals(other.macroname)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.MacroPK[ name=" + name + ", macroname=" + macroname + " ]";
    }

}
