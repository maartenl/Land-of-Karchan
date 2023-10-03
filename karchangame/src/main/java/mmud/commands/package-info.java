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
/**
 * <p>
 * Provides the different commands possible for players.</p>
 * <p>
 * @plantuml
 * <!--
 * interface Command
 * abstract class GuildCommand
 * abstract class GuildMasterCommand
 * abstract class NormalCommand
 * abstract class TargetCommand
 * abstract class CommunicationCommand
 * class BowCommand
 * class AskCommand
 * class MeCommand
 * Command <|-- NormalCommand
 * NormalCommand <|-- TargetCommand
 * TargetCommand <|-- CommunicationCommand
 * TargetCommand <|-- BowCommand
 * CommunicationCommand <|-- AskCommand
 * NormalCommand <|-- MeCommand
 * NormalCommand <|-- GuildCommand
 * GuildCommand <|-- GuildMasterCommand
 * -->
 */
package mmud.commands;
