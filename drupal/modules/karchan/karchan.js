// $Id:$

/**
 * JavaScript behaviors for the front-end display of karchan.
 * The object Karchan is added to the behaviours of drupal.
 * The "attach" property is automatically executed by drupal.
 * @see http://code.google.com/closure/utilities/
 * @see http://drupal.org/node/756722
 */
(function ($) {
  Drupal.behaviors.karchan = {
    attach: function(context, settings) {
      // http://www.stuff.nl/stuff.html?name=karn
      var urlParam = function(name)
      {
        // lokal = param || default;
        var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
        if (!results) { return 0; }
        return results[1] || 0;
      }

      $('#remove-all-cookies').each(function() {
        $.cookie('karchanname', null, { path: '/' });
        $.cookie('karchanpassword', null, { path: '/' });
        $.cookie('newmail', null, { path: '/' });
      });
      $('#block-block-2').each(function() {
        if ($.cookie("karchanname") != undefined &&
          $.cookie("karchanpassword") != undefined &&
          $.cookie("karchanname") != "" &&
          $.cookie("karchanpassword") != "")
        {
          var stuff = "";
          if ($.cookie("newmail") == undefined)
          {
            $.ajax({
              type: 'GET',
              url: "/resources/private/" + $.cookie("karchanname") + "/newmail?lok=" + $.cookie("karchanpassword"), // Which url should be handle the ajax request.
              success: (function(data1, data2, data3) {
                if (data3.status == 204) {
                  $.cookie('newmail', "false", { path: '/' });
                }
                else
                {
                  $.cookie('newmail', "true", { path: '/' });
                }
              }),
              async: false,
              error: (function() {
                $.cookie('newmail', "false", { path: '/' });
                }),
              complete: (function() { if (window.console) console.log("complete"); }),        
              dataType: 'json', //define the type of data that is going to get back from the server
              data: 'js=1' //Pass a key/value pair
            }); // end of ajax
          }
          if ($.cookie("newmail") == "true")
          {
            stuff = ". You have new mail";
          }
          $('#block-block-2').html("<div class=\"content\">Welcome, " + $.cookie("karchanname") + stuff + ".</div>");
          // $('#block-block-2').html("<div class=\"content\"><img src=\"/images/christmas/sclaus.gif\" style=\"vertical-align:middle\"/>Welcome, " + $.cookie("karchanname") + stuff + ".</div>");
        }
      });

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
      
      var updateFortunes = function(entry) {
	var data = entry.fortune;
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
      
      var updateGuilds = function(entry) {
	var data = entry.publicGuild;
        if (window.console) console.log("updateGuilds");
        // The data parameter is a JSON object.
        var formatted_html = "<p><a href=\"#\" id=\"karchan_show_all\">Expand all</a>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <a href=\"#\" id=\"karchan_collapse_all\">Collapse all</a></p>";
        for(i=0; i<data.length; i++) 
        { 
             if (data[i].title == undefined || data[i].title == "")
             {
               continue;
             }
             formatted_html += "<h1><img src=\"/favicon.ico\" id=\"karchan_guildtitle_" + i + "\"/>";
             if (data[i].guildurl != undefined && data[i].guildurl != "")
             {
               formatted_html += "<a href=\"" + data[i].guildurl + "\">";
             }
             formatted_html += data[i].title;
             if (data[i].guildurl != undefined && data[i].guildurl != "")
             {
               formatted_html += "</a>";
             }
             formatted_html += "</h1><div class=\"karchan_guild\" id=\"karchan_guild_" + i + "\"><dl><dt>Guildmaster</dt><dd><strong>" +
             data[i].bossname + "</strong></dd><dt>Created on</dt><dd><strong>" + data[i].creation + 
             "</strong></dd><dt>Description</dt><dd>" + data[i].guilddescription + "</dd></dl></div>"; 
        }
        $('#karchan_guilds').html(formatted_html); // data.products);
        $('#karchan_show_all').click(function(){
          $("div.karchan_guild").show();
        });
        $('#karchan_collapse_all').click(function(){
          $("div.karchan_guild").hide();
        });
        for(i=0; i<data.length; i++) 
        {
          $("#karchan_guildtitle_" + i).click(function(object){
            $("#karchan_guild_" + object.currentTarget.id.substring(19)).toggle("slow");
          });
        }
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
      
      var updateCharactersheets = function(entry) {
	var data = entry.publicPerson;
        if (window.console) console.log("updateCharactersheets");
        // The data parameter is a JSON object.
        var formatted_html = "";
        var cookiesname = "";
        if ($.cookie("karchanname") != undefined &&
          $.cookie("karchanpassword") != undefined &&
          $.cookie("karchanname") != "" &&
          $.cookie("karchanpassword") != "")
        {
          cookiesname = $.cookie("karchanname");
          formatted_html += "<p>To change or create your charactersheet, click <a href=\"/node/49\">here</a>.</p>";
        }
        formatted_html += "<table><tr><td><img src=\"/images/gif/letters/a.gif\"><br/><br/>";
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
             if (data[i].name == cookiesname)
             {
               formatted_html += "<a href=\"/node/49\"><b>"+ data[i].name + "</b></a><br/>";
             }
             else
             {
               formatted_html += "<a href=\"/node/43?name=" + data[i].name + "\">"+ data[i].name + "</a><br/>";
             }
        }
        formatted_html += "</td></tr></table>";
        $('#karchan_charactersheets').html(formatted_html); // data.products);
      } // updateCharactersheets
      }); // karchan_charactersheets
      
      $('#karchan_status').each(function() {
        if (window.console) console.log("#karchan_status");
        $.ajax({
          type: 'GET',
          url: "/resources/public/status", // Which url should be handle the ajax request.
          success: (function(data) {updateStatus(data); }),
          error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
          complete: (function() { if (window.console) console.log("complete"); }),        
          dataType: 'json', //define the type of data that is going to get back from the server
          data: 'js=1' //Pass a key/value pair
        }); // end of ajax
      
      var updateStatus = function(data) {
        if (window.console) console.log("updateStatus");
        // The data parameter is a JSON object.
        var formatted_html = "";
        for(i=0; i<data.length; i++) 
        { 
             formatted_html += "<p>* " + data[i].name + ", " + data[i].title + "</p>";
        }
        formatted_html += "";
        $('#karchan_status').html(formatted_html); // data.products);
      } // updateStatus
      }); // karchan_status

      $('#karchan_charactersheet').each(function() {
        if (window.console) console.log("#karchan_charactersheet");
        $.ajax({
          type: 'GET',
          url: "/resources/public/charactersheets/" + urlParam("name"), // Which url should be handle the ajax request.
          success: (function(data) {updateCharactersheet(data); }),
          error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
          complete: (function() { if (window.console) console.log("complete"); }),        
          dataType: 'json', //define the type of data that is going to get back from the server
          data: 'js=1' //Pass a key/value pair
        }); // end of ajax
      

      var updateCharactersheet = function(data) {
        if (window.console) console.log("updateCharactersheet");
        // The data parameter is a JSON object.
        var formatted_html = "";
        if (data == undefined || data.name == undefined)
        {
          formatted_html += "Character not found.";
        }
        else
        {
          formatted_html += "<p><img src=\"" + data.imageurl + "\"/></p>";
          formatted_html += "<p><b>Name:</b> " + data.name + "</p>";
          formatted_html += "<p><b>Title:</b> " + data.title + "</p>";
          formatted_html += "<p><b>Sex:</b> " + data.sex + "</p>";
          formatted_html += "<p><b>Description:</b> " + data.description + "</p>";
          formatted_html += "<p><b>Member of:</b> " + data.guild + " </p>";
          formatted_html += "<p><b>Homepage:</b> <a href=\"" + data.homepageurl + "\">" + data.homepageurl + "</a></p>";
          formatted_html += "<p><b>Birth:</b> " + data.dateofbirth + ", in " + data.cityofbirth + " </p>";
          formatted_html += "<p><b>Family relations:</b></p>";
          if (data.familyvalues !== undefined)
          {
            for(i=0; i<data.familyvalues.length; i++) 
            { 
                var record = data.familyvalues[i];
                if (record.has_char_sheet === undefined || record.has_char_sheet === false)
                {
                   formatted_html += "<p>* " + record.description + " of " + record.toname + "</p>";
                }
                else
                {
                   formatted_html += "<p>* " + record.description + " of <a href=\"/node/43?name=" + record.toname + "\">" + record.toname + "</a></p>";
                }
            }
          }
          formatted_html += "<p><b>Last logged on:</b> " + data.lastlogin + " </p>";
          formatted_html += "<p><b>Storyline:</b> " + data.storyline + " </p>";
        }
        $('#karchan_charactersheet').html(formatted_html); // data.products);
        $('#page-title').html("Character Sheet of " + data.name);
      } // updateCharactersheet
      }); // karchan_charactersheet

      $('#karchan_edit_charactersheet').each(function() {
        if (window.console) console.log("#karchan_edit_charactersheet");
        if ($.cookie("karchanname") === undefined ||
          $.cookie("karchanpassword") == undefined ||
          $.cookie("karchanname") === "" ||
          $.cookie("karchanpassword") == "")
        {
          $('#page-title').html("You are not logged in.");
          $('#karchan_edit_charactersheet').html(""); // data.products);
          return;
        }
        $.ajax({
          type: 'GET',
          url: "/resources/public/charactersheets/" + $.cookie("karchanname"), // Which url should be handle the ajax request.
          success: (function(data) {getCharactersheet(data); }),
          error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
          complete: (function() { if (window.console) console.log("complete"); }),        
          dataType: 'json', //define the type of data that is going to get back from the server
          data: 'js=1' //Pass a key/value pair
        }); // end of ajax
      

      var getCharactersheet = function(data) {
        if (window.console) console.log("getCharactersheet");
        if (window.console) console.log(data);
        // The data parameter is a JSON object.
        $('#karchan_edit_charactersheet').html("");
        $('#edit-submit').click(function(){
          updateCharactersheet();
          return false;
        });
        if (data == undefined || data.name == undefined)
        {
          $('#page-title').html("Charactersheet doesn't exist.");
          return;
        }
        var formatted_html = "";
        $("#edit-submitted-homepage-url").val(data.homepageurl);
        $("#edit-submitted-image-url").val(data.imageurl);
        $("#edit-submitted-date-of-birth").val(data.dateofbirth);
        $("#edit-submitted-city-of-birth").val(data.cityofbirth);
        $("#edit-submitted-storyline").val(data.storyline);
        $("#karchan_example_img").attr("src",data.imageurl);
          formatted_html += "<p><b>Family relations:</b></p><table class=\"sticky-enabled\">";
          formatted_html += "<thead><tr></th><th>Relation</a></th><th>Of</a></th><th>Character</th><th></th></tr></thead><tbody id=\"karchan_table_famvalues\">";
                  
          if (data.familyvalues !== undefined)
          {
            for(i=0; i<data.familyvalues.length; i++) 
            { 
                var record = data.familyvalues[i];
                formatted_html += "<tr id=\"data_" + i + "\" class=\""
                          + (i % 2 == 0 ? "even" : "odd") + "\"><td>" + record.description + "</td><td>of</td><td>" + record.toname +
                                        "</td><td><a href=\"#\">Delete</a></td></tr>";
            }
          }
        formatted_html += "</tbody></table>";
        $('#page-title').html("Edit Character Sheet of " + data.name);
        $('#karchan_edit_charactersheet').html(formatted_html);
        $('td a').click(function(object){
          removeFamilyRelation(object);
          return false;
        });
      } // getCharactersheet
      
      var removeFamilyRelation = function(object) {
        if (window.console) console.log("removeFamilyRelation");
        var toptr = $(object.target).closest("tr");
        var tds = toptr.children();
        //if (window.console) console.log($(tds[0]).html());
        //if (window.console) console.log($(tds[1]).html());
        //if (window.console) console.log($(tds[2]).html());
        $.ajax({
          type: 'DELETE',
          url: "/resources/private/" + $.cookie("karchanname") + "/charactersheet/familyvalues/" + $(tds[2]).html() + "?lok=" + $.cookie("karchanpassword"), // Which url should be handle the ajax request.
          success: (function(data) {
             toptr.hide();
          }),
          error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
          complete: (function() { if (window.console) console.log("complete"); })
        }); // end of ajax
        
      } // removeFamilyRelation

      var updateCharactersheet = function() {
        if (window.console) console.log("updateCharactersheet");
        // The data parameter is a JSON object.
        var jsonString = JSON.stringify(
        {
          lok: $.cookie("karchanpassword"),
          name : $.cookie("karchanname"),
          imageurl : $("#edit-submitted-image-url").val(),
          homepageurl : $("#edit-submitted-homepage-url").val(),
          dateofbirth : $("#edit-submitted-date-of-birth").val(),
          cityofbirth : $("#edit-submitted-city-of-birth").val(),
          // body : tinyMCE.get('edit-submitted-body').getContent(),
          storyline : tinyMCE.get('edit-submitted-storyline').getContent()
        });
        $.ajax({
          type: 'PUT',
          url: "/resources/private/" + $.cookie("karchanname") + "/charactersheet", // Which url should be handle the ajax request.
          success: (function(data) {
             alert("You've updated your character sheet.");    
          }),
          error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
          complete: (function() { if (window.console) console.log("complete"); }),        
          dataType: 'json', //define the type of data that is going to get back from the server
          contentType: 'application/json; charset=utf-8',
          data: jsonString // Pass a key/value pair
        }); // end of ajax
        if ($("#edit-submitted-add-family-relation").val() != 0)
        {
          $.ajax({
          // $("#edit-submitted-add-family-relation :selected").text()
            type: 'PUT',
            url: "/resources/private/" + $.cookie("karchanname") + "/charactersheet/familyvalues/" + $("#edit-submitted-of").val() + "/"
              + $("#edit-submitted-add-family-relation").val() + "?lok=" + $.cookie("karchanpassword"), // Which url should be handle the ajax request.
            success: (function(data) {
               // alert("You've added a relation.");    
               $("#karchan_table_famvalues").html($("#karchan_table_famvalues").html() + "<tr class=\"even\"><td>" + $("#edit-submitted-add-family-relation :selected").text()
               + "</td><td>of</td><td>" + $("#edit-submitted-of").val() + "</td><td></td></tr>");
            }),
            error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
            complete: (function() { if (window.console) console.log("complete"); }),        
            // dataType: 'json', //define the type of data that is going to get back from the server
            // contentType: 'application/json; charset=utf-8',
            //data: {'lok' : $.cookie("karchanpassword") } // Pass a key/value pair
          }); // end of ajax
        }
      } // updateCharactersheet
            
      }); // karchan_edit_charactersheet

      $('#karchan_mail').each(function() {
        if (window.console) console.log("#karchan_mail #" + $.cookie("karchanname") + "#" + $.cookie("karchanpassword") + "#");
        $offset = 0;
        if ($.cookie("karchanname") === undefined ||
          $.cookie("karchanpassword") == undefined ||
          $.cookie("karchanname") === "" ||
          $.cookie("karchanpassword") == "")
        {
          $('#page-title').html("You are not logged in.");
          $('#karchan_mail').html(""); // data.products);
          return;
        }
        // turn off newmail, after all, we're reading the mail now, aren't we?
        $.cookie('newmail', "false", { path: '/' });
        $.ajax({
          type: 'GET',
          // url: "/resources/private/Karn/mail/", // Which url should be handle the ajax request.
          url: "/resources/private/" + $.cookie("karchanname") + "/mail", // Which url should be handle the ajax request.
          success: (function(data) {Karchan.updateMail(data); }),
          error: (function(transport) { 
            if(transport.status != 401) {
             alert("An error occurred. Please notify Karn or one of the deps."); }}),
             
          complete: (function(transport) { 
            if(transport.status == 401) {
              $('#page-title').html("You are not authorized.");
              $('#karchan_mail').html(""); // data.products);
            }

            if (window.console) console.log("complete"); 
          }),        
          dataType: 'json', //define the type of data that is going to get back from the server
          data: {'lok' : $.cookie("karchanpassword"), 'offset':$offset} //Pass a key/value pair
        }); // end of ajax
      }); // karchan_mail

      $('#karchan_composemail').each(function() {
        if (window.console) console.log("#karchan_composemail");
        if ($.cookie("karchanname") === undefined ||
          $.cookie("karchanpassword") == undefined ||
          $.cookie("karchanname") === "" ||
          $.cookie("karchanpassword") == "")
        {
          $('#page-title').html("You are not logged in.");
          $('#node-45').toggle();
          return;
        }
        $('#edit-submitted-from').val($.cookie("karchanname"));
        $('#edit-submit').click(function() {
          return Karchan.sendMail();
        });
      }); // karchan_composemail
      
    } // attach function
  }; // end Drupal.behaviours.karchan

})(jQuery); // end jQuery
