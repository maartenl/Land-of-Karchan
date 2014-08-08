/**
 * The Global Karchan Object. Accessible from *anywhere in the world*.
 * Isn't it amazing? (Exaggeration!)
 * We can use this object to create a namespace for my karchan methods
 * functions and constants.
 */
var Karchan = {};

function isVowel(aChar)
{
  return aChar === 'a' || 
         aChar === 'e' || 
         aChar === 'u' || 
         aChar === 'o' || 
         aChar === 'i' || 
         aChar === 'A' || 
         aChar === 'E' || 
         aChar === 'U' || 
         aChar === 'I' || 
         aChar === 'O';
}
                                                      
function processCall(command, log, processor)
{
  var $ = Karchan.$;
  $.ajax({
    type: 'POST',
    url: "/resources/game/" + Karchan.name + "/play?offset=" + Karchan.logOffset + "&log=" + log + "&lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {processor(data); }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),
    dataType: 'json', //define the type of data that is going to get back from the server
    contentType: 'text/plain; charset=utf-8',
    data: command
  }); // end of ajax
}
     
function writeStuff(data)
{
    var image = data.image;
    var body = data.body;
    var title = data.title;
    var log = data.log.log;

    var $ = Karchan.$;
    var imageTag = (image == null || image == "") ? "" : "<img src=\"" + image + "\"/>";
    var capitalChar = body.charAt(0).toLocaleLowerCase();
    // http://www.karchan.org/images/gif/letters/w.gif
    var capital = "<img align=\"left\" src=\"/images/gif/letters/" + capitalChar + ".gif\" alt=\"" + capitalChar.toLocaleUpperCase() + "\"/>";
    $('#page-title').html(imageTag + title);
    var body = "<p>" + capital + body.substring(1) + "</p>";
    if (data.north !== undefined || data.west !== undefined || data.east !== undefined || data.south !== undefined || data.up !== undefined || data.down != undefined)
    {
      body += "<p>[";
      if (data.west !== undefined) {body += "west ";}
      if (data.east !== undefined) {body += "east ";}
      if (data.north !== undefined) {body += "north ";}
      if (data.south !== undefined) {body += "south ";}
      if (data.up !== undefined) {body += "up ";}
      if (data.down !== undefined) {body += "down ";}
      body += "]</p>";
    }
    if (data.persons !== undefined)
    {
      if (data.persons.lenght !== 0) {body += "<p>";}
      for (i in data.persons)
      {
        // A human called Ryan is here.
        if (isVowel(data.persons[i].race.charAt(0))) 
        {body += "An "} else {body += "A ";}
        body += data.persons[i].race + " called " + data.persons[i].name + " is here.<br/>";
      }
      if (data.persons.lenght !== 0) {body += "</p>";}
    }
    if (data.items !== undefined)
    {
      if (data.items.lenght !== 0) {body += "<p>";}
      for (index in data.items)
      {
        var item = data.items[index];
        var i = 0;
        var description = "";
        //  A massive, gray, stone boulder is here.
        if (item.adject1 !== undefined && item.adject1 != "")
        {
          i++;description += item.adject1;
        }
        if (item.adject2 !== undefined && item.adject2 != "")
        {
          if (i == 1) {description += ", ";}
          i++;description += item.adject2;
        }
        if (item.adject3 !== undefined && item.adject3 != "")
        {
          if (i > 0) {description += ", ";}
          i++;description += item.adject3;
        }
        description += " " + item.name;
        if (item.amount > 1) 
        {description +=" are";} else {description += " is";}
        description += " here.<br/>";
        if (item.amount > 1)
        {
          body += item.amount + " ";
        } else 
        {
          if (isVowel(description.charAt(0)))
          {body += "An ";} else {body += "A ";}
        }
        body += description;
      }
      if (data.items.lenght !== 0) {body += "</p>";}
    }
    
    $("#karchan_body").html(body);
    $("#karchan_log").html(log);
    Karchan.logSize = log.length;
    $('#command').focus();
}

function playInit($) 
{
  if (window.console) console.log("playInit");
  var name = $.cookie("name");
  var lok = $.cookie("lok");
  Karchan.name = name;
  Karchan.lok = lok;
  Karchan.logOffset = 0; 
  Karchan.sleep = false;
  if (window.console) console.log("playInit name=" + name + " lok=" + lok);
  var command = "l";
  if (window.console) console.log("playInit command=" + command); 
  var processPlay = function(data) {
    if (window.console) console.log("processPlay");
    writeStuff(data);
  } // processPlay
  
  processCall(command, true, processPlay);

  $(window).scroll(function(){
    $("#block-block-5").css({"margin-top": ($(window).scrollTop()) + "px", "margin-left":($(window).scrollLeft()) + "px"});
  });
}

/**
 * Called by the form, upon submit via javascript:
 * onsubmit="play(); return false;"
 */
function play()
{
  if (window.console) console.log("play");
  var $ = Karchan.$;
  var processPlay = function(data) {
    if (window.console) console.log("processPlay");
    writeStuff(data);
    $('#command').val("");    
  } // processPlay

  processCall($("#command").val(), true, processPlay); 
}

function quit()
{
  if (window.console) console.log("quit");
  var $ = Karchan.$;
  var quitSucceeded = false;
  $.ajax({
    type: 'GET',
    url: "/resources/game/" + Karchan.name + "/quit?lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {quitSucceeded = true; }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),
    async:   false // requirement, we can only show the quit page, if the quit actually succeeded.
  }); // end of ajax
  return quitSucceeded;
}

function toggleSleep()
{
  var $ = Karchan.$;
  if (window.console) console.log("toggleSleep sleep was " + Karchan.sleep);
  if (Karchan.sleep)
  {
    if (window.console) console.log("wakeUp");
    $("#sleepButtonSpanId").html("Sleep");
    Karchan.sleep = false;
  }
  else
  {
    if (window.console) console.log("sleep");
    $("#sleepButtonSpanId").html("Awaken");
    Karchan.sleep = true;
  }
}

function toggleEntry()
{
  if (window.console) console.log("toggleEntry");
  return true;
}

function clearLog()
{
  if (window.console) console.log("clearLog");
  var $ = Karchan.$;
  Karchan.logOffset = Karchan.logOffset + Karchan.logSize;
  Karchan.logSize = 0;
  if (window.console) console.log("clearLog setting offset to " + Karchan.logOffset);
  $("#karchan_log").html("");
  $('#command').focus();
}

function startMeUp($)
{
  if (window.console) console.log("startMeUp");
  Karchan.$ = $;
  playInit( $ );
}

