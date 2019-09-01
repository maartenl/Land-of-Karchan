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
 * 
 * @returns name of player or "undefined"
 */
function retrieveName()
{
  if (window.console)
    console.log("retrieveName");
  if (typeof (Storage) !== "undefined") {
    localStorage.removeItem("karchanname");
  }
  return Cookies.get('karchanname');
}

function playGame()
{
  var name = retrieveName();
  if (name === undefined) {
    return false;
  }
  $.ajax({
    type: 'POST',
    url: "/karchangame/resources/game/" + name + "/enter", // Which url should be handle the ajax request.
    cache: false,
    success: (function (data) {
      if (window.location !== window.parent.location) {
        window.parent.location.href = '/game/play.html';
      }
      window.location.href = '/game/play.html';
    }),
    complete: (function () {
      if (window.console)
        console.log("complete");
    }),
    dataType: 'json' //define the type of data that is going to get back from the server
  }); // end of ajax
  return false;
}

