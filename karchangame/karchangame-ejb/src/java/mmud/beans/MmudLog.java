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
package mmud.beans;

/**
 *
 * @author maartenl
 */
public class MmudLog
{

    private String log;
    private int offset;


    /**
     * Get the value of log
     *
     * @return the value of log
     */
    public String getLog()
    {
        return log;
    }


    /**
     * Set the value of log
     *
     * @param log new value of log
     */
    public void setLog(String log)
    {
        this.log = log;
    }


    /**
     * Get the value of offset
     *
     * @return the value of offset
     */
    public int getOffset()
    {
        return offset;
    }


    /**
     * Set the value of offset
     *
     * @param offset new value of offset
     */
    public void setOffset(int offset)
    {
        this.offset = offset;
    }

}
