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
      if (typeof startMeUp != "undefined") 
      {
        startMeUp($);
      }
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
	      cache: false,
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
	  cache: false,
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
	  cache: false,
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
          url: "/resources/private/" + $.cookie("karchanname") + "/charactersheet?lok=" + $.cookie("karchanpassword"), // Which url should be handle the ajax request.
	  cache: false,
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
	    cache: false,
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
	  cache: false,
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
