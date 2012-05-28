// $Id:$

/**
* JavaScript behaviors for the front-end display of karchan.
* The object Karchan is added to the behaviours of drupal.
* The "attach" property is automatically executed by drupal.
* @see http://code.google.com/closure/utilities/
* @see http://drupal.org/node/756722
*/
(function ($) {
var getMail = function() {
if (window.console) console.log("getMail");
$.ajax({
  type: 'GET',
  // url: "/resources/private/Karn/mail", // Which url should be handle the ajax request.
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
} // getMail

var showMail = function(object, data) {
if (window.console) console.log("showMail");
if (window.console) console.log(object);
if (window.console) console.log(data);
var data_pos = $(object.target).parent().parent().attr("id");
data_pos = data_pos.substring(5);
if (window.console) console.log(data_pos);
var formatted_html = "<p><b>Subject</b>: " + data[data_pos].subject + "</p>" +
  "<p><b>From</b>: " + data[data_pos].name + "</p>" +
  "<p><b>Sent</b>: " + data[data_pos].whensent + "</p>" +
  "<p><b>Body</b>:</p><div> " + data[data_pos].body;
$.ajax({
  type: 'GET',
  // url: "/resources/private/Karn/mail/12354", // Which url should be handle the ajax request.
  url: "/resources/private/" + $.cookie("karchanname") + "/mail/" + data[data_pos].id, // Which url should be handle the ajax request.
  cache: false,
  dataType: 'json', //define the type of data that is going to get back from the server
  data: {'lok' : $.cookie("karchanpassword")} //Pass a key/value pair
}); // end of ajax
// TODO : fix this back up!
//if (data[data_pos].item_id === undefined || data[data_pos].item_id === 0)
//{
//  formatted_html += "</div>" +
//  "<select name=\"karchan_create_item\" id=\"karchan_create_item\">" + 
//  "<option value=\"0\">an old faded yellow parchment</option>" + 
//  "<option value=\"1\">a crumpled piece of paper</option>" + 
//  "<option value=\"2\">an official-looking sealed legal document</option>" + 
//  "<option value=\"3\">an impressive-looking forged legal document</option>" + 
//  "<option value=\"4\">a hand-written folded ink-spattered letter</option>" + 
//  "<option value=\"5\">a short rushed squareshaped memo</option>" + 
//  "<option value=\"6\">an embossed beautifully written leaflet</option>" + 
//  "<option value=\"7\">an embossed beautifully written invitation</select>";
//}
//formatted_html += 
//  "<a href=\"#\" id=\"karchan_create_mudmail_object\" title=\"Create a copy of the mudmail in your inventory\">" +
//  "<span>Create</span></a>";
$('#karchan_singlemail').html(formatted_html); // data.products);
$('#karchan_create_mudmail_object').click(function (object) { 
  createMudMailObject(data[data_pos]);
  return false;
});
} // showMail

var createMudMailObject = function(privateMail) {
if (window.console) console.log("createMudMailObject");
var item_id = ($('#karchan_create_item').val() === undefined ? 0 : $('#karchan_create_item').val());
var item_name = ($('#karchan_create_item').val() === undefined ? "another copy of the mail" :  $('#karchan_create_item :selected').text());
$.ajax({
  type: 'GET',
  url: "/resources/private/" + $.cookie("karchanname") + "/mail/" + privateMail.id + "/createMailItem/" + $('#karchan_create_item').val(), // Which url should be handle the ajax request.
  cache: false,
  success: (function(data) {
    alert("You've written " + item_name);
  }),
  error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
  complete: (function() { if (window.console) console.log("complete"); }),        
  data: 'lok=' + $.cookie("karchanpassword") //Pass a key/value pair
}); // end of ajax
} // createMudMailObject

var deleteMail = function(object, data) {
if (window.console) console.log("deleteMail");
var data_pos = $(object.target).parent().parent().attr("id");
data_pos = data_pos.substring(5);
if (window.console) console.log(data_pos);
if (window.console) console.log(data[data_pos]);
if (window.console) console.log(data[data_pos].id);
$.ajax({
  type: 'DELETE',
  url: "/resources/private/" + $.cookie("karchanname") + "/mail/" + data[data_pos].id + "?lok=" + $.cookie("karchanpassword"), // Which url should be handle the ajax request.
  cache: false,
  success: (function(data) {
    $(object.target).parent().parent().toggle();
  }),
  error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
  complete: (function() { if (window.console) console.log("complete"); })        
  //data: 'lok=' + $.cookie("karchanpassword") //Pass a key/value pair
}); // end of ajax
} // deleteMail

Karchan.updateMail = function(data) {
if (window.console) console.log("updateMail");
// The data parameter is a JSON object.
var formatted_html = "";
formatted_html += "<p>" + ($offset + 1) + " - " + ($offset + 20) + " <a href=\"#\" id=\"karchan_previous_page\">Previous</a> <a href=\"#\" id=\"karchan_refresh_page\">Refresh</a> <a href=\"#\" id=\"karchan_next_page\">Next</a></p> <table class=\"sticky-enabled\">";
formatted_html += "<thead><tr></th><th>Subject</a></th><th>Author</a></th><th>Sent</th><th></th></tr></thead>";
formatted_html += "<tbody>";
if (data != undefined && data != null)
{
  var bold = "";
  var unbold = "";
  for(i=0; i<data.length; i++) 
  { 
    if (data[i].haveread == "false")
    {
      bold = "<b>";
      unbold = "</b>";
    }
    else
    {
      bold = "";
      unbold = "";
    }
    formatted_html += "<tr id=\"data_" + i + "\" class=\""
      + (i % 2 == 0 ? "even" : "odd") + "\"><td>" + bold + data[i].subject + unbold + "</td><td>" + bold + data[i].name + 
      unbold + "</td><td>" + bold + data[i].whensent + unbold + 
      "</td><td><a href=\"#\">Read</a> <a href=\"#\">Delete</a></td></tr>";
  }
}
formatted_html += "</tbody></table>";
$('#karchan_mail').html(formatted_html); // data.products);
$('#page-title').html($.cookie("karchanname") + "'s Mail");
        $('#karchan_refresh_page').click(function (object) { 
          getMail();
          return false;
        });
        $('#karchan_previous_page').click(function (object) { 
          $offset -= 20;
          if ($offset < 0)
          {
            $offset = 0;
          }
          getMail();
          return false;
        });
        $('#karchan_next_page').click(function (object) { 
          $offset += 20;
          getMail();
          return false;
        });
        $("td a").click(function (object) { 
          if (window.console) console.log($(object.target).html());
          if ($(object.target).html() == "Read")
          {
            showMail(object,data);
          }
          if ($(object.target).html() == "Delete")
          {
            deleteMail(object,data);
          }
          return false;
        });
                   
      } // updateMail
      
      Karchan.sendMail = function() {
        if (window.console) console.log("sendMail");
        var jsonString = JSON.stringify(
          {
            name : $.cookie("karchanname"),
            toname : $('#edit-submitted-to').val(),
            subject : $('#edit-submitted-subject').val(),
            // body : $('#edit-submitted-body').val(),
            body : tinyMCE.get('edit-submitted-body').getContent(),
            lok: $.cookie("karchanpassword")
          }
        );
        $.ajax({
          type: 'POST',
          url: "/resources/private/" + $.cookie("karchanname") + "/mail?lok=" + $.cookie("karchanpassword"), // Which url should be handle the ajax request.
	  cache: false,
          success: (function(data) {
            alert("Mail send."); 
          }),
          error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
          complete: (function() { if (window.console) console.log("complete"); }),        
          dataType: 'json', //define the type of data that is going to get back from the server
          contentType: 'application/json; charset=utf-8',
          data: jsonString // Pass a key/value pair
        }); // end of ajax
        return false;
      } // sendMail

})(jQuery); // end jQuery
