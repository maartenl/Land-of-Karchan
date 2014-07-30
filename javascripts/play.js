/**
 * The Global Karchan Object. Accessible from *anywhere in the world*.
 * Isn't it amazing? (Exaggeration!)
 * We can use this object to create a namespace for my karchan methods
 * functions and constants.
 */
var Karchan = {};
     
function processCall(command, log, processor)
{
  var $ = Karchan.$;
  $.ajax({
    type: 'POST',
    url: "/resources/game/" + Karchan.name + "/play?offset=" + Karchan.offset + "&log=" + log + "&lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {processor(data); }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),
    dataType: 'json', //define the type of data that is going to get back from the server
    contentType: 'text/plain; charset=utf-8',
    data: command
  }); // end of ajax
}
     
function writeStuff(image, body, title, log)
{
    var $ = Karchan.$;
    var imageTag = (image == null || image == "") ? "" : "<img src=\"" + image + "\"/>";
    var capitalChar = body.substring(0, 1).toLocaleLowerCase();
    // http://www.karchan.org/images/gif/letters/w.gif
    var capital = "<img align=\"left\" src=\"/images/gif/letters/" + capitalChar + ".gif\" alt=\"" + capitalChar.toLocaleUpperCase() + "\"/>";
    $('#page-title').html(imageTag + title);
    $("#karchan_body").html(capital + body.substring(1));
    $("#karchan_log").html(log);
    $('#command').focus();
}

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
  var processPlay = function(data) {
    if (window.console) console.log("processPlay");
    writeStuff(data.image, data.body, data.title, data.log.log);
  } // processPlay
  
  processCall(command, true, processPlay);
}

function play()
{
  if (window.console) console.log("play");
  var $ = Karchan.$;
  var processPlay = function(data) {
    if (window.console) console.log("processPlay");
    writeStuff(data.image, data.body, data.title, data.log.log);
    $('#command').val("");    
  } // processPlay

  processCall($("#command").val(), true, processPlay); 
}

function startMeUp($)
{
  if (window.console) console.log("startMeUp");
  Karchan.$ = $;
  playInit( $ );
}

