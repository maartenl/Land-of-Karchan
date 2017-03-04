/**
 * Function that initialised everything. Called upon page load.
 */          
function initGuild( $ )
{
  if (window.console) console.log("initGuild");
  var name = $.cookie("name");
  var lok = $.cookie("lok");  
  Karchan.name = name;
  Karchan.lok = lok;
  Karchan.offset = 0;
  Karchan.$ = $;
  getGuild();
  getMembers();
  getRanks();
  getHopefuls();
} // initGuild

/**
 * Retrieve the info of your guild.
 */
function getGuild() 
{
  if (window.console) console.log("getGuild");
  var $ = Karchan.$;
  $.ajax({
  type: 'GET',
    url: "/resources/private/" + Karchan.name + "/guild", // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {updateGuild(data); }),
    error: (function(transport) { 
      if(transport.status != 401) {
       alert(Karchan.getGenericError()); }}),
       
    complete: (function(transport) { 
      if(transport.status == 401) {
  	$('#page-title').html("You are not authorized.");
      }
         
      if (window.console) console.log("complete"); 
    }),        
    dataType: 'json', //define the type of data that is going to get back from the server
    data: {'lok' : Karchan.lok} //Pass a key/value pair
  }); // end of ajax
} // getGuild

/**
 * Retrieve the members of your guild.
 */
function getMembers() 
{
  if (window.console) console.log("getMembers");
  var $ = Karchan.$;
  $.ajax({
  type: 'GET',
    url: "/resources/private/" + Karchan.name + "/guild/members", // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {showMembers(data); }),
    error: (function(transport) { 
      if(transport.status != 401) {
       alert(Karchan.getGenericError()); }}),
       
    complete: (function(transport) { 
      if(transport.status == 401) {
  	$('#page-title').html("You are not authorized.");
      }
         
      if (window.console) console.log("complete"); 
    }),        
    dataType: 'json', //define the type of data that is going to get back from the server
    data: {'lok' : Karchan.lok} //Pass a key/value pair
  }); // end of ajax
  
  var showMembers = function(data)
  {
    if (window.console) console.log("showMembers");
    if (window.console) console.log(data);
    var html = "";
    for (i in data)
    {
      html += "<li>" + data[i].name + "</li>";
    }
    $('#members').html("<ul>" + html + "</ul>");
  };
} // getMembers

/**
 * Retrieve the hopefuls, that wish to join the guild.
 */
function getHopefuls() 
{
  if (window.console) console.log("getHopefuls");
  var $ = Karchan.$;
  $.ajax({
  type: 'GET',
    url: "/resources/private/" + Karchan.name + "/guild/hopefuls", // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {showHopefuls(data); }),
    error: (function(transport) { 
      if(transport.status != 401) {
       alert(Karchan.getGenericError()); }}),
       
    complete: (function(transport) { 
      if(transport.status == 401) {
  	$('#page-title').html("You are not authorized.");
      }
         
      if (window.console) console.log("complete"); 
    }),        
    dataType: 'json', //define the type of data that is going to get back from the server
    data: {'lok' : Karchan.lok} //Pass a key/value pair
  }); // end of ajax
  
  var showHopefuls = function(data)
  {
    if (window.console) console.log("showHopefuls");
    if (window.console) console.log(data);
    var html = "";
    for (i in data)
    {
      html += "<li>" + data[i].name + "</li>";
    }
    $('#hopefuls').html("<ul>" + html + "</ul>");
  };
} // getHopefuls

/**
 * Retrieve the ranks available in your guild.
 */
function getRanks() 
{
  if (window.console) console.log("getRanks");
  var $ = Karchan.$;
  $.ajax({
  type: 'GET',
    url: "/resources/private/" + Karchan.name + "/guild/ranks", // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {showRanks(data); }),
    error: (function(transport) { 
      if(transport.status != 401) {
       alert(Karchan.getGenericError()); }}),
       
    complete: (function(transport) { 
      if(transport.status == 401) {
  	$('#page-title').html("You are not authorized.");
      }
         
      if (window.console) console.log("complete"); 
    }),        
    dataType: 'json', //define the type of data that is going to get back from the server
    data: {'lok' : Karchan.lok} //Pass a key/value pair
  }); // end of ajax
  
  var showRanks = function(data)
  {
    if (window.console) console.log("showRanks");
    if (window.console) console.log(data);
    var html = "";
    if (data === null || data.length == 0)
    {
      html = "<p>No ranks are defined in this guild.</p>";
    }
    for (i in data)
    {
      html += "<li>Rank " + data[i].guildlevel + ". " + data[i].title + "</li>";
    }
    $('#ranks').html("<ul>" + html + "<ul>");
  };
} // getRanks

function updateGuild(data) 
{
  if (window.console) console.log("updateGuild");
  if (window.console) console.log(data);
  var $ = Karchan.$;
  $('#page-title').html(data.title);
  var fillArray = [ data.name, data.title, data.guildurl, data.bossname, data.creation, data.guilddescription, data.logonmessage ];
  if (data.bossname === Karchan.name)
  {
    $('#editter').html("<p>You are the Guildmaster. If you wish to make changes to your guild, click <a href=\"/node/137\">here</a>.</p>");
  }
  if (data.image !== undefined)
  {
    $('#guildimage').html("<img src=\"" + data.image + "\"/>");
  }
  $( "dd" ).each(function( index ) {
    var message = fillArray[index];
    if (index < 3) {message = "<strong>" + message + "</strong>";}
    $(this).html(message);
  });
} // updateGuild
  
function startMeUp($)
{
  if (window.console) console.log("startMeUp");
  initGuild($);
}

