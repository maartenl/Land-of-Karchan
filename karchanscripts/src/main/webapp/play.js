// http://stackoverflow.com/questions/1144783/replacing-all-occurrences-of-a-string-in-javascript
String.prototype.replaceAll = function (find, replace) {
  var str = this;
  return str.replace(new RegExp(find.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'), 'g'), replace);
};
    
/**
 * The Global Karchan Object. Accessible from *anywhere in the world*.
 * Isn't it amazing? (Exaggeration!)
 * We can use this object to create a namespace for my karchan methods
 * functions and constants.
 */
var Karchan = Karchan || {};
Karchan.logSize = 0;

function webError(jqXHR, textStatus, errorThrown) 
{ 
  if (window.console) 
  {
    console.log(jqXHR);
    console.log(textStatus);
    console.log(errorThrown);
  }
  try
  {
    var errorDetails = JSON.parse(jqXHR.responseText);    
    if (window.console) console.log(errorDetails);
  } catch(e)
  {
    alert("An error occurred. Please notify Karn or one of the deps.");
    if (window.console) console.log(e);
    return;
  }
  if (errorDetails.stacktrace !== undefined)
  {
    var buffer = "Timestamp: " + errorDetails.timestamp + "<br/>";
    buffer += "Errormessage: " + errorDetails.errormessage + "<br/>";
    buffer += "Stacktrace: " + errorDetails.stacktrace + "<br/>";
    buffer += "User: " + errorDetails.user + "<br/>";
    buffer += "Browser CodeName: " + navigator.appCodeName + "<br/>";
    buffer += "Browser Name: " + navigator.appName + "<br/>";
    buffer += "Browser Version: " + navigator.appVersion + "<br/>";
    buffer += "Cookies Enabled: " + navigator.cookieEnabled + "<br/>";
    buffer += "Platform: " + navigator.platform + "<br/>";
    buffer += "User-agent header: " + navigator.userAgent + "<br/>";
    $("#warning").html(buffer);
  }
  alert(errorDetails.errormessage);
}

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
                    
function retrieveName()
{
    if (window.console)
        console.log("retrieveName");
    if (typeof (Storage) !== "undefined") {
        // Store
        return localStorage.getItem("karchanname");
    } else {
        return Cookies.get('karchanname');
    }
    alert("Unable to find you. Are you sure you are logged in?");
}

function processCall(command, log, processor)
{
  if (command === "clear")
  {
    Karchan.offset = 0;
    log = true;
  }
  $.ajax({
    type: 'POST',
    url: "/karchangame/resources/game/" + Karchan.name + "/play?offset=" + Karchan.logOffset + "&log=" + log, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {processor(data); }),
    error: webError,
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

    var imageTag = (typeof data.image === "undefined" || image === null || image === "") ? "" : "<img style=\"vertical-align: text-bottom;\" src=\"" + image + "\"/>";
    var capitalChar = body.charAt(0).toLocaleLowerCase();
    // http://www.karchan.org/images/gif/letters/w.gif
    var capital = "<img align=\"left\" src=\"/images/gif/letters/" + capitalChar + ".gif\" alt=\"" + capitalChar.toLocaleUpperCase() + "\"/>";
    if (capitalChar === " " || capitalChar === "<")
    {
      capital = capitalChar;
    }
    $('#page-title').html("<h1 style=\"font-family: 'GabrielleRegular';\">" + imageTag + title + "</h1>");
    var body = "<p>" + capital + body.substring(1) + "</p>";
    if (data.north !== undefined || data.west !== undefined || data.east !== undefined || data.south !== undefined || data.up !== undefined || data.down != undefined)
    {
      body += "<p>[";
      if (data.west !== undefined) {body += "<a href=\"javascript:void(0)\" onclick='goWest();'>west</a> ";}
      if (data.east !== undefined) {body += "<a href=\"javascript:void(0)\" onclick='goEast();'>east</a> ";}
      if (data.north !== undefined) {body += "<a href=\"javascript:void(0)\" onclick='goNorth();'>north</a> ";}
      if (data.south !== undefined) {body += "<a href=\"javascript:void(0)\" onclick='goSouth();'>south</a> ";}
      if (data.up !== undefined) {body += "<a href=\"javascript:void(0)\" onclick='goUp();'>up</a> ";}
      if (data.down !== undefined) {body += "<a href=\"javascript:void(0)\" onclick='goDown();'>down</a> ";}
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
        body += data.persons[i].race + " called <a href=\"javascript:void(0)\" onclick=\"lookat(this);\">" + data.persons[i].name + "</a> is here.<br/>";
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
        {
          body += item.amount + " ";
        } else 
        {
          if (isVowel(description.charAt(0)))
          {body += "An ";} else {body += "A ";}
        }
        body += "<span onclick=\"lookat(this);\">" + description + "</span>";
        if (item.amount > 1) 
        {body +=" are";} else {body += " is";}
        body += " here.<br/>";
      }
      if (data.items.lenght !== 0) {body += "</p>";}
    }
    
    $("#karchan_body").html(body);
    if (data.log !== undefined && data.log.log !== undefined)
    {
      $("#karchan_log").html(data.log.log);
      Karchan.logSize = data.log.log.length;
    }
    $('#command').focus();
}

function playInit() 
{
  if (window.console) console.log("playInit");
  var name = retrieveName();
  Karchan.name = name;
  Karchan.logOffset = 0; 
  Karchan.sleep = false;
  if (window.console) console.log("playInit name=" + namek);
  var command = "l";
  if (window.console) console.log("playInit command=" + command); 
  var processPlay = function(data) {
    if (window.console) console.log("processPlay");
    writeStuff(data);
  } // processPlay
  processCall(command, true, processPlay);
}

/**
 * Called by the form, upon submit via javascript:
 * onsubmit="play(); return false;"
 */
function play()
{
  if (window.console) console.log("play");
  var processPlay = function(data) {
    if (window.console) console.log("processPlay");
    writeStuff(data);
    if (Karchan.bigEntry === undefined || Karchan.bigEntry === false)
    {
      $('#command').val("");
    }
    else
    {
      tinyMCE.get('bigcommand').setContent("");
    }
  } // processPlay
  if (Karchan.bigEntry === undefined || Karchan.bigEntry === false)
  {
    var command = $("#command").val();
  } else
  {
    var command = tinyMCE.get('bigcommand').getContent();
    if (command.substring(0,3) === "<p>") {command = command.substring(3);}
    while (command.substring(0,6) === "&nbsp;") {command = command.substring(6);}
    if (command.substring(command.length - 4, command.length) === "</p>") {command = command.substring(0, command.length - 4);}
  }
  processCall(command, true, processPlay); 
}

function quit()
{
  if (window.console) console.log("quit");
  var quitSucceeded = false;
  $.ajax({
    type: 'GET',
    url: "/karchangame/resources/game/" + Karchan.name + "/quit", // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {quitSucceeded = true; }),
    error: webError,
    complete: (function() { if (window.console) console.log("complete"); }),
    async:   false // requirement, we can only show the quit page, if the quit actually succeeded.
  }); // end of ajax
  return quitSucceeded;
}

function toggleSleep()
{
  if (window.console) console.log("toggleSleep sleep was " + Karchan.sleep);
  if (Karchan.sleep)
  {
    // AWAKEN!!!
    if (window.console) console.log("wakeUp");
    $("#sleepButtonSpanId").html("Sleep");
    Karchan.sleep = false;
    processCall("awaken", true, writeStuff);
  }
  else
  {
    // GO TO SLEEP!!
    if (window.console) console.log("sleep");
    $("#sleepButtonSpanId").html("Awaken");
    Karchan.sleep = true;
    processCall("sleep", true, writeStuff);
  }
}

function toggleEntry()
{
  if (window.console) console.log("toggleEntry");
  if (Karchan.bigEntry === undefined || Karchan.bigEntry === false)
  {
    // we need Big Entry command!
    $("#command").css("display","none");
    if (Karchan.bigEntry === undefined)
    {
      $("#bigcommand").css("display","block");
      tinyMCE.init({
        // General options
        mode : "textareas",
        theme : "modern",
        plugins : ["textcolor,table,save,insertdatetime,preview,searchreplace,contextmenu,",
                   "paste,directionality,fullscreen,noneditable,visualchars,nonbreaking",
                   "wordcount,advlist,autosave,code,visualblocks,hr,charmap"
                  ],
        toolbar1: "insertfile undo redo | styleselect | bold italic | alignleft " +
                  "aligncenter alignright alignjustify | bullist numlist outdent indent" +
                  " | link image",
        toolbar2: "print preview media | forecolor backcolor emoticons",
        image_advtab: true
      });
    }
    else
    {
      tinyMCE.get('bigcommand').show();
      //$("#mceu_16").css("display","block");
    }
  
    Karchan.bigEntry = true;
  } else
  {
    // change back to teh usual!
    $("#command").css("display","block");
    tinyMCE.get('bigcommand').hide();
    $("#bigcommand").css("display","none");
    Karchan.bigEntry = false;
  }
  return false;
}

function clearLog()
{
  if (window.console) console.log("clearLog");
  Karchan.logOffset = Karchan.logOffset + Karchan.logSize;
  Karchan.logSize = 0;
  if (window.console) console.log("clearLog setting offset to " + Karchan.logOffset);
  $("#karchan_log").html("");
  $("#warning").html("");
  $('#command').focus();
}

function lookat(name) 
{
  if (window.console) console.log("lookat");
  if (window.console) console.log(name.innerHTML);
  processCall("look at " + name.innerHTML.replaceAll(",",""), true, writeStuff);
  return false;
}

function goWest() {processCall("go west", false, writeStuff);return false;}
function goEast() {processCall("go east", false, writeStuff);return false;}
function goNorth() {processCall("go north", false, writeStuff);return false;}
function goSouth() {processCall("go south", false, writeStuff);return false;}
function goUp() {processCall("go up", false, writeStuff);return false;}
function goDown() {processCall("go down", false, writeStuff);return false;}

function startMeUp()
{
  if (window.console) console.log("startMeUp");
  // very specific Drupal - Bartik - Display settings
  // $("#main").css("width","100%");
  // $("#content").css("width", (window.innerWidth-260) + "px");
  playInit();
}

$( document ).ready(startMeUp);
