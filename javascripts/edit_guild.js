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
//  getMembers();
//  getRanks();
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
    success: (function(data) {prefillForm(data); }),
    error: (function(transport) { 
      if(transport.status != 401) {
       alert("An error occurred. Please notify Karn or one of the deps."); }}),
       
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
       alert("An error occurred. Please notify Karn or one of the deps."); }}),
       
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
       alert("An error occurred. Please notify Karn or one of the deps."); }}),
       
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

function prefillForm(data) 
{
  if (window.console) console.log("prefillForm");
  if (window.console) console.log(data);
  var $ = Karchan.$;
  $('#page-title').html(data.title);
  $("#edit-submitted-name").val(data.name);
  $("#edit-submitted-title").val(data.title);
  $("#edit-submitted-guildurl").val(data.guildurl);
  $("#edit-submitted-guildmaster").val(data.bossname);
  $("#edit-submitted-description").val(data.guilddescription);
  $("#edit-submitted-logonmessage").val(data.logonmessage);
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
} // prefillForm

/**
 * Submit changes to the server.
 */
function updateGuild()
{
  var $ = Karchan.$;
  if (window.console) console.log("updateGuild");
  // The data parameter is a JSON object.
  var jsonString = JSON.stringify(
  {
    guildurl : $("#edit-submitted-guildurl").val(),
    title : $("#edit-submitted-title").val(),
    bossname : $("#edit-submitted-guildmaster").val(),
    guilddescription : tinyMCE.get('edit-submitted-description').getContent(),
    image: "",
    logonmessage : tinyMCE.get('edit-submitted-logonmessage').getContent()
  });
  $.ajax({
    type: 'PUT',
    url: "/resources/private/" + Karchan.name + "/guild?lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {
       alert("You've updated your guild.");
    }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),        
    dataType: 'json', //define the type of data that is going to get back from the server
    contentType: 'application/json; charset=utf-8',
    data: jsonString // Pass a key/value pair
  }); // end of ajax
  return false;
} // updateGuild
  
function startMeUp($)
{
  if (window.console) console.log("startMeUp");
  initGuild($);
  Karchan.$ = $;
}

