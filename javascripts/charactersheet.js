var urlParam = function(name)
{
  // lokal = param || default;
  var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
  if (!results) { return 0; }
  return results[1] || 0;
}
                                                  
function showCharactersheet( $ ) 
{
  if (window.console) console.log("showCharactersheet");
  var name = $.cookie("name");
  var lok = $.cookie("lok"); 
  Karchan.name = name;
  Karchan.lok = lok;
  $.ajax({
    type: 'GET',
    url: "/resources/public/charactersheets/" + urlParam("name"), // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {updateCharactersheet(data); }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),
    dataType: 'json', //define the type of data that is going to get back from the server
    data: 'js=1' //Pass a key/value pair
  }); // end of ajax
  
  var updateCharactersheet = function(data) {
    if (window.console) console.log("updateCharactersheet");
    // The data parameter is a JSON object.
    var formatted_html = "";
    if (data == undefined || data.name == undefined)
    {
      formatted_html += "Character not found.";
    }
    else
    {
      if (name === data.name) 
      {
        formatted_html += "<p><a href=\"/node/135\">Edit your charactersheet</a></p>";
        if (data.guild !== undefined && data.guild !== null && data.guild !== "")
        {
          formatted_html += "<p><a href=\"/node/136\">View your guild</a></p>";
        }
      }
      formatted_html += "<p><img src=\"" + data.imageurl + "\"/></p>";
      formatted_html += "<p><b>Name:</b> " + data.name + "</p>";
      formatted_html += "<p><b>Title:</b> " + data.title + "</p>";
      formatted_html += "<p><b>Sex:</b> " + data.sex + "</p>";
      formatted_html += "<p><b>Description:</b> " + data.description + "</p>";
      formatted_html += "<p><b>Member of:</b> " + data.guild + " </p>";
      formatted_html += "<p><b>Homepage:</b> <a href=\"" + data.homepageurl + "\">" + data.homepageurl + "</a></p>";
      formatted_html += "<p><b>Birth:</b> " + data.dateofbirth + ", in " + data.cityofbirth + " </p>";
      formatted_html += "<p><b>Family relations:</b></p>";
      if (data.familyvalues !== undefined)
      {
        for(i=0; i<data.familyvalues.length; i++)
        {
            var record = data.familyvalues[i];
            if (record.has_char_sheet === undefined || record.has_char_sheet === false)
            {
               formatted_html += "<p>* " + record.description + " of " + record.toname + "</p>";
            }
            else
            {
               formatted_html += "<p>* " + record.description + " of <a href=\"/node/43?name=" + record.toname + "\">" + record.toname + "</a></p>";
            }
        }
      }
      formatted_html += "<p><b>Last logged on:</b> " + data.lastlogin + " </p>";
      formatted_html += "<p><b>Storyline:</b> " + data.storyline + " </p>";
    }
    $('#karchan_charactersheet').html(formatted_html); // data.products);
    $('#page-title').html("Character Sheet of " + data.name);
  } // updateCharactersheet
}

function startMeUp($)
{
  if (window.console) console.log("startMeUp");
  showCharactersheet($);
}

