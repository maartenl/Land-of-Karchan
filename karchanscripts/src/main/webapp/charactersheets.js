function showCharactersheets() 
{
  if (window.console) console.log("showCharactersheets");  
  $.ajax({
    type: 'GET',
    url: "/karchangame/resources/public/charactersheets", // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {updateCharactersheets(data); }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),
    dataType: 'json', //define the type of data that is going to get back from the server
    data: 'js=1' //Pass a key/value pair
  }); // end of ajax

  var updateCharactersheets = function(data) 
  {
    if (window.console) console.log("updateCharactersheets");
    // The data parameter is a JSON object.
    var formatted_html = "";
    formatted_html += "<table><tr><td><img src=\"/images/gif/letters/a.gif\"><br/><br/>";
    var column_length = (data.length / 6) + 1;
    var column_pos = 0;
    var first_letter = "A";
    for(i=0; i<data.length; i++) 
    { 
         column_pos ++;
         if (column_pos > column_length)
         {
           formatted_html += "</td><td>";
           column_pos = 0;
         }
         if (data[i].name.toUpperCase().charAt(0) !== first_letter)
         {
           first_letter = data[i].name.toUpperCase().charAt(0);
           formatted_html += "<br/><p><img src=\"/images/gif/letters/" +
           first_letter.toLowerCase() + ".gif\"></p>";
         }
         formatted_html += "<a href=\"/web/guest/person?name=" + data[i].name + "\">"+ data[i].name + "</a><br/>";
    }
    formatted_html += "</td></tr></table>";
    $('#karchan_charactersheets').html(formatted_html); // data.products);
  } // updateCharactersheets
}

$( document ).ready(showCharactersheets);

