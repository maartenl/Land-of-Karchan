/**
 * The Global Karchan Object. Accessible from *anywhere in the world*.
 * Isn't it amazing? (Exaggeration!)
 * We can use this object to create a namespace for my karchan methods
 * functions and constants.
 */
var Karchan = Karchan || {};

/**
 * Function that initialised everything. Called upon page load.
 */          
function initMail( $ )
{
  if (window.console) console.log("initMail");
  var name = $.cookie("name");
  var lok = $.cookie("lok");  
  Karchan.name = name;
  Karchan.lok = lok;
  Karchan.offset = 0;
  Karchan.$ = $;
  getMail();
} // initMail

/**
 * Retrieve the current mail list with an offset and display it.
 */
function getMail() 
{
  if (window.console) console.log("getMail");
  var $ = Karchan.$;
  $.ajax({
  type: 'GET',
  // url: "/resources/private/Karn/mail", // Which url should be handle the ajax request.
    url: "/resources/private/" + Karchan.name + "/mail", // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {updateMail(data); }),
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
    data: {'lok' : Karchan.lok, 'offset':Karchan.offset} //Pass a key/value pair
  }); // end of ajax
} // getMail

function readMail(object, data) 
{
  if (window.console) console.log("readMail");
  if (window.console) console.log(object);
  if (window.console) console.log(data);
  var $ = Karchan.$;
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
    url: "/resources/private/" + Karchan.name + "/mail/" + data[data_pos].id, // Which url should be handle the ajax request.
    cache: false,
    dataType: 'json', //define the type of data that is going to get back from the server
    data: {'lok' : Karchan.lok} //Pass a key/value pair
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
} // readMail
  
// var createMudMailObject = function(privateMail) {
// if (window.console) console.log("createMudMailObject");
// var item_id = ($('#karchan_create_item').val() === undefined ? 0 : $('#karchan_create_item').val());
// var item_name = ($('#karchan_create_item').val() === undefined ? "another copy of the mail" :  $('#karchan_create_item :selected').text());
// $.ajax({
//   type: 'GET',
//   url: "/resources/private/" + Karchan.name + "/mail/" + privateMail.id + "/createMailItem/" + $('#karchan_create_item').val(), // Which url should be handle the ajax request.
//   cache: false,
//   success: (function(data) {
//     alert("You've written " + item_name);
//   }),
//   error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
//   complete: (function() { if (window.console) console.log("complete"); }),        
//   data: 'lok=' + $.cookie("karchanpassword") //Pass a key/value pair
// }); // end of ajax
// } // createMudMailObject
// 
function deleteMail(object, data) 
{
  if (window.console) console.log("deleteMail");
  var $ = Karchan.$;
  var data_pos = $(object.target).parent().parent().attr("id");
  data_pos = data_pos.substring(5);
  if (window.console) console.log(data_pos);
  if (window.console) console.log(data[data_pos]);
  if (window.console) console.log(data[data_pos].id);
  $.ajax({
    type: 'DELETE',
    url: "/resources/private/" + Karchan.name + "/mail/" + data[data_pos].id + "?lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function() {
      getMail();
      // $(object.target).parent().parent().toggle();
    }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); })
  }); // end of ajax
}  // deleteMail

/**
 * Redisplay the list of mail.s
 */
function updateMail(data) 
{
  if (window.console) console.log("updateMail");
  var $ = Karchan.$;
  //  The data parameter is a JSON object.
  var formatted_html = "";
  formatted_html += "<p>" + (Karchan.offset + 1) + " - " + (Karchan.offset + 20) + " <a href=\"#\" id=\"karchan_previous_page\">Previous</a> <a href=\"#\" id=\"karchan_refresh_page\">Refresh</a> <a href=\"#\" id=\"karchan_next_page\">Next</a></p> <table class=\"sticky-enabled\">";
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
  $('#page-title').html(Karchan.name + "'s Mail");
  $('#karchan_refresh_page').click(function (object) { 
    getMail();
    return false;
  });
  $('#karchan_previous_page').click(function (object) { 
    Karchan.offset -= 20;
    if (Karchan.offset < 0)
    {
      Karchan.offset = 0;
    }
    getMail();
    return false;
  });
  $('#karchan_next_page').click(function (object) { 
    Karchan.offset += 20;
    getMail();
    return false;
  });
  $("td a").click(function (object) { 
    if (window.console) console.log($(object.target).html());
    if ($(object.target).html() == "Read")
    {
      readMail(object,data);
    }
    if ($(object.target).html() == "Delete")
    {
      deleteMail(object,data);
    }
    return false;
  });
} // updateMail
      
function startMeUp($)
{
  if (window.console) console.log("startMeUp");
  initMail($);
}

