/**
 * The Global Karchan Object. Accessible from *anywhere in the world*.
 * Isn't it amazing? (Exaggeration!)
 * We can use this object to create a namespace for my karchan methods
 * functions and constants.
 */
var Karchan = {};
     
function logon() 
{
  if (window.console) console.log("logon");
  var $ = Karchan.$;
  var name = $("#name").val();
  var password = $("#password").val();
  if (window.console) console.log("logon name=" + name + " password=" + password);

  $.ajax({
    type: 'POST',
    url: "/resources/game/" + name + "/logon" + "?password=" + password, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {processLogon(data); }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),
    dataType: 'text', //define the type of data that is going to get back from the server
    data: 'js=1' //Pass a key/value pair
  }); // end of ajax
  
  var processLogon = function(lok) {
    if (window.console) console.log("processLogon");
    if (window.console) console.log("lok=" + lok);
    $.cookie('lok', lok, {path: '/' }); 
    $.cookie('name', name, {path: '/' }); 
    window.location.href="http://www.karchan.org/node/132";
  } // processLogon
}

function startMeUp($)
{
  if (window.console) console.log("startMeUp");
  Karchan.$ = $;
}

