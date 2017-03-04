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
} // initGuild

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
    bossname : Karchan.name,
    guilddescription : tinyMCE.get('edit-submitted-description').getContent(),
    image: "",
    name : $("#edit-submitted-name").val(),
    logonmessage : tinyMCE.get('edit-submitted-logonmessage').getContent()
  });
  $.ajax({
    type: 'POST',
    url: "/resources/private/" + Karchan.name + "/guild?lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {
       window.location.href="http://www.karchan.org/node/136";
    }),
    error: (function() { alert(Karchan.getGenericError()); }),
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

