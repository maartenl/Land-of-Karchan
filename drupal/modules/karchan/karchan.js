// $Id:$

/**
 * JavaScript behaviors for the front-end display of karchan.
 * @see http://drupal.org/node/756722
 */

(function ($) {

  Drupal.behaviors.karchan = {
    attach: function(context, settings) {
      // $('.example', context).click(function () {
      // $(this).next('ul').toggle('show');
      // alert("Karn is working on it, yes...");
      $('#karchan_news').each(function() {
        if (window.console) console.log("#karchan_news");
        $.ajax({
          type: 'GET',
          url: "/resources/public/news", // Which url should be handle the ajax request.
          success: (function(data) {updateNews(data); }),
          error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
          complete: (function() { if (window.console) console.log("complete"); }),        
          dataType: 'json', //define the type of data that is going to get back from the server
          data: 'js=1' //Pass a key/value pair
        }); // end of ajax
      
      var updateNews = function(data) {
        if (window.console) console.log("updateNews");
        // The data parameter is a JSON object.
        var formatted_html = "";
        for(i=0; i<data.length; i++) 
        { 
           formatted_html += "<hr/><p>" + data[i].posttime + "</p><p>" +
           data[i].message + "</p><p><i>" + data[i].name +
           "</i></p>"; 
        }
        $('#karchan_news').html(formatted_html); // data.products);
      } // updateNews
      }); // karchan_news

      $('#karchan_fortunes').each(function() {
        if (window.console) console.log("#karchan_fortunes");
        $.ajax({
          type: 'GET',
          url: "/resources/public/fortunes", // Which url should be handle the ajax request.
          success: (function(data) {updateFortunes(data); }),
          error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
          complete: (function() { if (window.console) console.log("complete"); }),        
          dataType: 'json', //define the type of data that is going to get back from the server
          data: 'js=1' //Pass a key/value pair
        }); // end of ajax
      
      var updateFortunes = function(data) {
        if (window.console) console.log("updateFortunes");
        // The data parameter is a JSON object.
        var formatted_html = "<table><tr><td><b>Position</b></td><td><b>Name</b></td><td><b>Money</b></td></tr>";
        for(i=0; i<data.length; i++) 
        { 
           formatted_html += "<tr><td>" + (i+1) + "</td><td>" + data[i].name + "</td><td>" +
           data[i].gold + " gold, " + data[i].silver + " silver, " + data[i].copper + " copper</td></tr>"; 
        }
        $('#karchan_fortunes').html(formatted_html); // data.products);
      } // updateFortunes
      }); // karchan_fortunes

      $('#karchan_guilds').each(function() {
        if (window.console) console.log("#karchan_guilds");
        $.ajax({
          type: 'GET',
          url: "/resources/public/guilds", // Which url should be handle the ajax request.
          success: (function(data) {updateGuilds(data); }),
          error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
          complete: (function() { if (window.console) console.log("complete"); }),        
          dataType: 'json', //define the type of data that is going to get back from the server
          data: 'js=1' //Pass a key/value pair
        }); // end of ajax
      
      var updateGuilds = function(data) {
        if (window.console) console.log("updateGuilds");
        // The data parameter is a JSON object.
        var formatted_html = "";
        for(i=0; i<data.length; i++) 
        { 
             if (data[i].title == undefined || data[i].title == "")
             {
               continue;
             }
             if (data[i].guildurl != undefined && data[i].guildurl != "")
             {
               formatted_html += "<a href=\"" + data[i].guildurl + "\">";
             }
             formatted_html += "<h1>" + data[i].title + "</h1>";
             if (data[i].guildurl != undefined && data[i].guildurl != "")
             {
               formatted_html += "</a>";
             }
             formatted_html += "<dl><dt>Guildmaster</dt><dd><strong>" +
             data[i].bossname + "</strong></dd><dt>Created on</dt><dd><strong>" + data[i].creation + 
             "</strong></dd><dt>Description</dt><dd>" + data[i].guilddescription + "</dd></dl>"; 
        }
        $('#karchan_guilds').html(formatted_html); // data.products);
      } // updateGuilds
      }); // karchan_guilds
      
      $('#karchan_charactersheets').each(function() {
        if (window.console) console.log("#karchan_charactersheets");
        $.ajax({
          type: 'GET',
          url: "/resources/public/charactersheets", // Which url should be handle the ajax request.
          success: (function(data) {updateCharactersheets(data); }),
          error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
          complete: (function() { if (window.console) console.log("complete"); }),        
          dataType: 'json', //define the type of data that is going to get back from the server
          data: 'js=1' //Pass a key/value pair
        }); // end of ajax
      
      var updateCharactersheets = function(data) {
        if (window.console) console.log("updateCharactersheets");
        // The data parameter is a JSON object.
        var formatted_html = "<table><tr><td><img src=\"/images/gif/letters/a.gif\"><br/><br/>";
        var column_length = (data.length / 6) + 1;
        var column_pos = 0;
        var first_letter = "A";
        for(i=0; i<data.length; i++) 
        { 
             column_pos ++;
             if (column_pos > column_length)
             {
               formatted_html += "</td><td>";
               column_pos = 0;
             }
             if (data[i].name.toUpperCase().charAt(0) != first_letter)
             {
               first_letter = data[i].name.toUpperCase().charAt(0);
               formatted_html += "<br/><p><img src=\"/images/gif/letters/" + 
               first_letter.toLowerCase() + ".gif\"></p>";
             }
             formatted_html += data[i].name + "<br/>";
        }
        formatted_html += "</td></tr></table>";
        $('#karchan_charactersheets').html(formatted_html); // data.products);
      } // updateCharactersheets
      }); // karchan_charactersheets
      
    } // attach function
  }; // end Drupal.behaviours.karchan

})(jQuery); // end jQuery