/**
 * The Global Karchan Object. Accessible from *anywhere in the world*.
 * Isn't it amazing? (Exaggeration!)
 * We can use this object to create a namespace for my karchan methods
 * functions and constants.
 */
var Karchan = {};
     
function playInit($) 
{
  if (window.console) console.log("playInit");
  var name = $.cookie("name");
  var lok = $.cookie("lok");
  Karchan.name = name;
  Karchan.lok = lok;
  Karchan.offset = 0; 
  if (window.console) console.log("playInit name=" + name + " lok=" + lok);
  var command = "l";
  if (window.console) console.log("playInit command=" + command); 

  $.ajax({
    type: 'POST',
    url: "/resources/game/" + name + "/play?offset=0&log=true&lok=" + lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {processPlay(data); }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),
    dataType: 'json', //define the type of data that is going to get back from the server
    contentType: 'text/plain; charset=utf-8',
    data: command
  }); // end of ajax
  
  var processPlay = function(data) {
    if (window.console) console.log("processPlay");
    if (window.console) console.log(data.title);
    var image = "<img src=\"" + data.image + "\"/>";
    var capitalChar = data.body.substring(0, 1).toLocaleLowerCase();
    // http://www.karchan.org/images/gif/letters/w.gif
    var capital = "<img align=\"left\" src=\"/images/gif/letters/" + capitalChar + ".gif\" alt=\"" + capitalChar.toLocaleUpperCase() + "\"/>";
    $('#page-title').html(image + data.title);
    $("#karchan_body").html(capital + data.body.substring(1));
    $("#karchan_log").html(data.log.log);
  } // processPlay
}

function play()
{
  if (window.console) console.log("play");
  var $ = Karchan.$;
}

function startMeUp($)
{
  if (window.console) console.log("startMeUp");
  Karchan.$ = $;
  playInit( $ );
}

