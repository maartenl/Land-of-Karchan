/**
 * Initialise upon page load, the character sheet form with info.
 */
function loadCharactersheet( $ ) 
{
  if (window.console) console.log("loadCharactersheet");
  var name = $.cookie("name");
  var lok = $.cookie("lok"); 
  Karchan.name = name;
  Karchan.lok = lok;
  $.ajax({
    type: 'GET',
    url: "/resources/public/charactersheets/" + name, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {prefillForm(data); }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),
    dataType: 'json', //define the type of data that is going to get back from the server
    data: 'js=1' //Pass a key/value pair
  }); // end of ajax
  
  var prefillForm = function(data) {
    if (window.console) console.log("prefillForm");
    // The data parameter is a JSON object.
    var formatted_html = "";
    if (data == undefined || data.name == undefined)
    {
      alert("Character sheet not found.");
      return;
    }
    $("#edit-submitted-image-url").val(data.imageurl);
    $("#karchan_example_img").attr("src", data.imageurl);
    $("#karchan_example_img").attr("alt", data.imageurl);
    $("#edit-submitted-title").val(data.title);
    $("#edit-submitted-storyline").val(data.storyline);
    $("#edit-submitted-homepage-url").val(data.homepageurl);
    $("#edit-submitted-city-of-birth").val(data.cityofbirth);
    $("#edit-submitted-date-of-birth").val(data.dateofbirth);
    var formatted_html = "<p><b>Family relations:</b></p><table class=\"sticky-enabled\">";
    formatted_html += "<thead><tr></th><th>Relation</a></th><th>Of</a></th><th>Character</th><th></th></tr></thead><tbody id=\"karchan_table_famvalues\">";
            
    if (data.familyvalues !== undefined)
    {
      for(i=0; i<data.familyvalues.length; i++) 
      { 
          var record = data.familyvalues[i];
          formatted_html += "<tr id=\"data_" + i + "\" class=\""
                    + (i % 2 == 0 ? "even" : "odd") + "\"><td>" + record.description + "</td><td>of</td><td>" + record.toname +
                                  "</td><td><a href=\"#\">Delete</a></td></tr>";
      }
    }
    formatted_html += "</tbody></table>";
    $('#karchan_edit_familyvalues').html(formatted_html);
    $('td a').click(function(object){
      removeFamilyRelation(object);
      return false;
    });
    $('#page-title').html("Edit Character Sheet of " + data.name);
  } // prefillForm
} // loadCharactersheet

/**
 * Submit changes to the server.
 */
function updateCharactersheet() 
{
  var $ = Karchan.$;
  if (window.console) console.log("updateCharactersheet");
  // The data parameter is a JSON object.
  var jsonString = JSON.stringify(
  {
    name : Karchan.name,
    imageurl : $("#edit-submitted-image-url").val(),
    homepageurl : $("#edit-submitted-homepage-url").val(),
    dateofbirth : $("#edit-submitted-date-of-birth").val(),
    cityofbirth : $("#edit-submitted-city-of-birth").val(),
    title : $("#edit-submitted-title").val(),
    // body : tinyMCE.get('edit-submitted-body').getContent(),
    storyline : tinyMCE.get('edit-submitted-storyline').getContent()
  });
  $.ajax({
    type: 'PUT',
    url: "/resources/private/" + Karchan.name + "/charactersheet?lok=" + Karchan.lok, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {
       alert("You've updated your character sheet.");
    }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),        
    dataType: 'json', //define the type of data that is going to get back from the server
    contentType: 'application/json; charset=utf-8',
    data: jsonString // Pass a key/value pair
  }); // end of ajax
  return false;
} // updateCharactersheet

/**
 * Add a family relation, or replace an existing one.
 */
function addFamilyValues() 
{
  if (window.console) console.log("addFamilyValues");
  var $ = Karchan.$;
  if ($("#edit-submitted-add-family-relation").val() != 0)
  {
    $.ajax({
    // $("#edit-submitted-add-family-relation :selected").text()
      type: 'PUT',
      url: "/resources/private/" + Karchan.name + "/charactersheet/familyvalues/" + $("#edit-submitted-of").val() + "/"
        + $("#edit-submitted-add-family-relation").val() + "?lok=" + Karchan.lok, // Which url should be handle the ajax request.
      cache: false,
      success: (function(data) {
         // alert("You've added a relation.");    
         $("#karchan_table_famvalues").html($("#karchan_table_famvalues").html() + "<tr class=\"even\"><td>" + $("#edit-submitted-add-family-relation :selected").text()
         + "</td><td>of</td><td>" + $("#edit-submitted-of").val() + "</td><td></td></tr>");
      }),
      error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
      complete: (function() { if (window.console) console.log("complete"); }),        
      // dataType: 'json', //define the type of data that is going to get back from the server
      // contentType: 'application/json; charset=utf-8',
      //data: {'lok' : Karchan.lok } // Pass a key/value pair
    }); // end of ajax
  }
} // addFamilyValues

/**
 * Removes a family relation from the database.
 */
function removeFamilyRelation(object) 
{
  if (window.console) console.log("removeFamilyRelation");
  var $ = Karchan.$;
  var toptr = $(object.target).closest("tr");
  var tds = toptr.children();
  //if (window.console) console.log($(tds[0]).html());
  //if (window.console) console.log($(tds[1]).html());
  //if (window.console) console.log($(tds[2]).html());
  $.ajax({
    type: 'DELETE',
    url: "/resources/private/" + Karchan.name + "/charactersheet/familyvalues/" + $(tds[2]).html() + "?lok=" + Karchan.lok, // Which url should be handle 
    cache: false,
    success: (function(data) {
       toptr.hide();
    }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); })
  }); // end of ajax
} // removeFamilyRelation

function startMeUp($)
{
  if (window.console) console.log("startMeUp");
  Karchan.$ = $;
  loadCharactersheet( $ );
  // updateCharactersheet or removeFamilyRelation
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

