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
    success: (function(data) {prefillGuildForm(data); }),
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
    success: (function(data) {Karchan.members = data; }),
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
} // getMembers
  
function showMembers()
{
  var data = Karchan.members;
  var $ = Karchan.$;
  if (window.console) console.log("showMembers");
  if (window.console) console.log(data);
  var html = "<table id=\"membertable\"><thead><tr><th>Name</th><th>Rank</th><th></th></tr></thead>";
  for (i in data)
  {
    html += "<tr id=\"memb_" + i + "\"><td>" + data[i].name + "</td><td>";
    // select stuff
    if (Karchan.ranks.length != 0) 
    {
      html += "<select>";
      html += "<option value=\"-\" " + (data[i].title === undefined ? "selected" : "") + ">-</option>";  
      for (j in Karchan.ranks)
      {
        var rank = Karchan.ranks[j];
        html += "<option value=\"" + rank.guildlevel + "\" ";
        html += (data[i].title !== undefined && data[i].title === rank.title ? "selected" : "");  
        html += ">" + rank.title + "</option>";

      }
      html += "</select>";
    }
    else
    {
       html += "-";
    }
    html += "</td><td><a href=\"javascript:void(0)\">Delete</a></td></tr>";
  }
  html += "</table>";
  $('#members').html(html);
  $("#members td a").click(function (object) {
      if ($(object.target).html() == "Delete")
      {
        deleteMember(object, data);
      }
      return false;
  });
    $("#members select").change(function () {
      var membername = $(this).parent().parent().children().first().html();
      var guildlevel = $(this).val(); // -, 1, 2
      assignRankToMember(membername, guildlevel);
      return false;
    });
}

function assignRankToMember(membername, guildlevel)
{
  var $ = Karchan.$;
  if (window.console) console.log("assignRankToMember");
  // The data parameter is a JSON object.
  var updatedMember = {
    name : membername
  };
  if (guildlevel !== "-") {updatedMember.guildlevel = guildlevel;}
  var jsonString = JSON.stringify(updatedMember);
  $.ajax({
    type: 'PUT',
    url: "/resources/private/" + Karchan.name + "/guild/members/" + membername + "?lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {
      // indication of success is not required.
    }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),        
    dataType: 'json', //define the type of data that is going to get back from the server
    contentType: 'application/json; charset=utf-8',
    data: jsonString // Pass a key/value pair
  }); // end of ajax
}

function deleteGuild()
{
  var $ = Karchan.$;
  if (window.console) console.log("deleteGuild");
  if (!confirm('This will delete your guild and cannot be undone. Are you sure?')) 
  {
    // do nothing!
    return false;
  }
  if (!confirm('One more time. Are you very very sure?')) 
  {
    // do nothing!
    return false;
  }
  var deleteGuildSucceeded = false;
  $.ajax({
  type: 'DELETE',
    url: "/resources/private/" + Karchan.name + "/guild?lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {
      window.location.href="http://www.karchan.org/node/43";
      deleteGuildSucceeded = true;
    }),
    error: (function(transport) { 
      if(transport.status != 401) {
       alert("An error occurred. Please notify Karn or one of the deps."); }}),
       
    complete: (function(transport) { 
      if(transport.status == 401) {
  	$('#page-title').html("You are not authorized.");
      }
         
      if (window.console) console.log("complete"); 
    })
  }); // end of ajax
  return deleteGuildSucceeded;
} // deleteGuild

function deleteMember(object, data)
{
  var $ = Karchan.$;
  if (window.console) console.log("deleteMember");
  if (window.console) console.log(data);
  var $ = Karchan.$;
  var data_pos = $(object.target).parent().parent().attr("id");
  data_pos = data_pos.substring(5);
  if (window.console) console.log(data[data_pos]);
  $.ajax({
  type: 'DELETE',
    url: "/resources/private/" + Karchan.name + "/guild/members/" + data[data_pos].name + "?lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {getMembers(); }),
    error: (function(transport) { 
      if(transport.status != 401) {
       alert("An error occurred. Please notify Karn or one of the deps."); }}),
       
    complete: (function(transport) { 
      if(transport.status == 401) {
  	$('#page-title').html("You are not authorized.");
      }
         
      if (window.console) console.log("complete"); 
    })
  }); // end of ajax
} // deleteMember

function deleteHopeful(object, data)
{
  var $ = Karchan.$;
  if (window.console) console.log("deleteHopeful");
  if (window.console) console.log(data);
  var $ = Karchan.$;
  var data_pos = $(object.target).parent().parent().attr("id");
  data_pos = data_pos.substring(5);
  if (window.console) console.log(data[data_pos]);
  $.ajax({
  type: 'DELETE',
    url: "/resources/private/" + Karchan.name + "/guild/hopefuls/" + data[data_pos].name + "?lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {getHopefuls(); }),
    error: (function(transport) { 
      if(transport.status != 401) {
       alert("An error occurred. Please notify Karn or one of the deps."); }}),
       
    complete: (function(transport) { 
      if(transport.status == 401) {
  	$('#page-title').html("You are not authorized.");
      }
         
      if (window.console) console.log("complete"); 
    })
  }); // end of ajax
} // deleteHopeful

function createMember(object, data)
{
  var $ = Karchan.$;
  if (window.console) console.log("createMember");
  if (window.console) console.log(data);
  var $ = Karchan.$;
  var data_pos = $(object.target).parent().parent().attr("id");
  data_pos = data_pos.substring(5);
  if (window.console) console.log(data[data_pos]);
  var jsonString = JSON.stringify({name: data[data_pos].name});
  $.ajax({
    type: 'POST',
    url: "/resources/private/" + Karchan.name + "/guild/members?lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {
       getMembers();getHopefuls();
    }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),        
    dataType: 'json', //define the type of data that is going to get back from the server
    contentType: 'application/json; charset=utf-8',
    data: jsonString // Pass a key/value pair
  }); // end of ajax
} // createMember

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
    Karchan.ranks = data;
    showMembers();
    var html = "";
    html = "<table id=\"ranktable\"><thead><tr><th>Ranknumber</th><th>Title</th><th></th></tr></thead>";
    for (i in data)
    {
      html += "<tr id=\"rank_" + i + "\"><td>" + data[i].guildlevel + "</td><td>" + data[i].title + "</td><td><a href=\"javascript:void(0)\">Edit</a> <a href=\"javascript:void(0)\">Delete</a></td></tr>";
    }
    html += "</table><p><a href=\"javascript:void(0)\" onclick=\"newRank();\">New</a></p>";
    $('#ranks').html(html);
    $("#ranks td a").click(function (object) {
      if (window.console) console.log($(object.target).html());
        if ($(object.target).html() == "Edit")
        {
          prefillRankForm(object, data);
        }
        if ($(object.target).html() == "Delete")
        {
          deleteRank(object, data);
        }
        return false;
    });
  };
} // getRanks

/**
 * Retrieve the hopefuls, that wish to join the guild.
 */
function getHopefuls()
{
  if (window.console) console.log("getHopefuls");
  var $ = Karchan.$;
  $.ajax({
  type: 'GET',
    url: "/resources/private/" + Karchan.name + "/guild/hopefuls", // Which url should be handle the ajax req
    cache: false,
    success: (function(data) {showHopefuls(data); }),
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
  
  var showHopefuls = function(data)
  {
    if (window.console) console.log("showHopefuls");
    if (window.console) console.log(data);
    var html = "<table id=\"hopefultable\"><thead><tr><th>Name</th><th></th></tr></thead>"; 
    for (i in data)
    {
      html += "<tr id=\"hope_" + i + "\"><td>" + data[i].name + "</td><td><a href=\"javascript:void(0)\">Accept</a> <a href=\"javascript:void(0)\">Reject</a></td></tr>";
    }
    html += "</table>"
    $('#hopefuls').html(html);
    $("#hopefuls td a").click(function (object) {
      if (window.console) console.log($(object.target).html());
        if ($(object.target).html() == "Accept")
        {
          createMember(object, data);
        }
        if ($(object.target).html() == "Reject")
        {
          deleteHopeful(object, data);
        }
        return false;
    });
  };
} // getHopefuls

/**
 * Used when you try to "edit" a guild rank.
 */
function prefillRankForm(object, data)
{
  var $ = Karchan.$;
  if (window.console) console.log("prefillRankForm");
  if (window.console) console.log(data);
  var data_pos = $(object.target).parent().parent().attr("id");
  data_pos = data_pos.substring(5);
  if (window.console) console.log(data[data_pos]);
  var rank = data[data_pos];
  $("#edit-submitted-ranktitle").val(rank.title);
  $("#edit-submitted-guildlevel").val(rank.guildlevel);
  $("#edit-submitted-guildlevel").attr("readonly","readonly");
  if (rank.accept_access)
  {
    $("#edit-submitted-accept_access").attr('checked', 'checked');
  }
  else
  {
    $("#edit-submitted-accept_access").removeAttr('checked');
  }
  if (rank.reject_access)
  {
    $("#edit-submitted-reject_access").attr('checked', 'checked');
  }
  else
  {
    $("#edit-submitted-reject_access").removeAttr('checked');
  }
  if (rank.settings_access)
  {
    $("#edit-submitted-settings_access").attr('checked', 'checked');
  }
  else
  {
    $("#edit-submitted-settings_access").removeAttr('checked');
  }
  if (rank.logonmessage_access)
  {
    $("#edit-submitted-logonmessage_access").attr('checked', 'checked');
  }
  else
  {
    $("#edit-submitted-logonmessage_access").removeAttr('checked');
  }
}

/**
 * Used when you try to create a new guild rank.
 */
function newRank()
{
  var $ = Karchan.$;
  if (window.console) console.log("newRank");
  $("#edit-submitted-ranktitle").val("");
  $("#edit-submitted-guildlevel").val("");
  $("#edit-submitted-guildlevel").removeAttr("readonly");
  $("#edit-submitted-accept_access").removeAttr('checked');
  $("#edit-submitted-reject_access").removeAttr('checked');
  $("#edit-submitted-settings_access").removeAttr('checked');
  $("#edit-submitted-logonmessage_access").removeAttr('checked');
}

/**
 * Removes a Rank.
 */
function deleteRank(object, data)
{
  var $ = Karchan.$;
  if (window.console) console.log("deleteRank");
  if (window.console) console.log(data);
  var $ = Karchan.$;
  var data_pos = $(object.target).parent().parent().attr("id");
  data_pos = data_pos.substring(5);
  if (window.console) console.log(data[data_pos]);
  var rank = data[data_pos];
  $.ajax({
  type: 'DELETE',
    url: "/resources/private/" + Karchan.name + "/guild/ranks/" + rank.guildlevel + "?lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {getRanks(); }),
    error: (function(transport) { 
      if(transport.status != 401) {
       alert("An error occurred. Please notify Karn or one of the deps."); }}),
       
    complete: (function(transport) { 
      if(transport.status == 401) {
  	$('#page-title').html("You are not authorized.");
      }
         
      if (window.console) console.log("complete"); 
    })
  }); // end of ajax
} // deleteRank

function prefillGuildForm(data) 
{
  if (window.console) console.log("prefillGuildForm");
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
} // prefillGuildForm

/**
 * Can be used for both updating a rank and adding a new rank.
 */
function updateRank()
{
  var $ = Karchan.$;
  if (window.console) console.log("updateRank");
  // The data parameter is a JSON object.
  var guildrank = {
    title : $("#edit-submitted-ranktitle").val(),
    guildlevel : $("#edit-submitted-guildlevel").val(),
    accept_access : $("#edit-submitted-accept_access").attr('checked'),
    reject_access : $("#edit-submitted-reject_access").attr('checked'),
    settings_access: $("#edit-submitted-settings_access").attr('checked'),
    logonmessage_access : $("#edit-submitted-logonmessage_access").attr('checked')
  };
  var jsonString = JSON.stringify(guildrank);
  // true=> put, false=> post
  var putorpost = false;
  for (i in Karchan.ranks)
  {
    if (Karchan.ranks[i].guildlevel == guildrank.guildlevel)
    {
      putorpost = true;
    }
  }
  $.ajax({
    type: (putorpost ? 'PUT' : 'POST'),
    url: "/resources/private/" + Karchan.name + "/guild/ranks" + (putorpost ? "/" + guildrank.guildlevel : "") + "?lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {
       getRanks();
    }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),        
    dataType: 'json', //define the type of data that is going to get back from the server
    contentType: 'application/json; charset=utf-8',
    data: jsonString // Pass a key/value pair
  }); // end of ajax
  return false;
}

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

