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
 * Provides the Enterprise Java Beans used for the REST services.</p>
 * <p>
 * @plantuml
 * <!--
 * title Sequence diagram of the current software setup
 * participant Firefox as Firefox << Internet Browser >>
 * participant "Payara Microprofile" as Payara << Application Server >>
 * participant MariaDB as MariaDB << Database >>
 * Firefox -> Payara: Web Request
 * Payara -> MariaDB : Database calls
 * MariaDB -> Payara : Database response
 * Payara -> Firefox : html, css and js
 * Payara -> Firefox : HTTP response
 * Firefox -> Firefox : Run Javascript
 * Firefox -> Payara : HTTP Rest call
 * Payara -> MariaDB : Database calls
 * MariaDB -> Payara : Database response
 * Payara -> Firefox : HTTP with JSON response
 * Firefox -> Firefox : Run Javascript, create display
 * -->
 */
package mmud.rest.services;
