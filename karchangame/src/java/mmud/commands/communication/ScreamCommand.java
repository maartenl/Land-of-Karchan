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
package mmud.commands.communication;

/**
 * Scream to someone "scream Help!". Or just scream in general. The interesting
 * part about this command is, because screaming is louder in general than
 * simple talking, that the scream can be heard in neighboring rooms.
 * @author maartenl
 */
public class ScreamCommand extends ShoutCommand
{

	public ScreamCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public CommType getCommType()
	{
		return CommType.SCREAM;
	}
}
