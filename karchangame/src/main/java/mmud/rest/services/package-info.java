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
 * <img src="doc-files/package-info_restservices.png"/></p>
 * @startuml doc-files/package-info_restservices.png
 * title Sequence diagram of the current software setup
 * participant Firefox as Firefox << Internet Browser >>
 * participant Apache as Apache << Webserver >>
 * participant Drupal as Drupal << CMS >>
 * participant Glassfish as Glassfish << Application Server >>
 * participant MySQL as MySQL << Database >>
 * Firefox -> Apache: Web Request
 * Apache -> Drupal : Php
 * Drupal -> MySQL : Database calls
 * MySQL -> Drupal : Database response
 * Drupal -> Apache : html, css and js
 * Apache -> Firefox : HTTP response
 * Firefox -> Firefox : Run Javascript
 * Firefox -> Apache : HTTP Rest call
 * Apache -> Glassfish : Proxy
 * Glassfish -> MySQL : Database calls
 * MySQL -> Glassfish : Database response
 * Glassfish -> Apache : HTTP with JSON response
 * Apache -> Firefox : ReverseProxy
 * Firefox -> Firefox : Run Javascript, create display
 * @enduml
 */
package mmud.rest.services;
