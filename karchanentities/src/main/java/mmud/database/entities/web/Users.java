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
package mmud.database.entities.web;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "mmv_users")
@NamedQueries(
{
  @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
  @NamedQuery(name = "Users.findByNameAndPassword", query = "SELECT u FROM Users u WHERE u.name = :name and u.password = :password"),
  @NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password")
})
public class Users implements Serializable
{

  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 20)
  @Column(name = "name")
  private String name;
  
  @Size(max = 128)
  @Column(name = "password")
  private String password;

  public Users()
  {
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }
  
}
