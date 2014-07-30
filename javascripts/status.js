function showStatus( $ ) 
{
  if (window.console) console.log("showStatus");
  $.ajax({
    type: 'GET',
    url: "/resources/public/status", // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {updateStatus(data); }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),
    dataType: 'json', //define the type of data that is going to get back from the server
    data: 'js=1' //Pass a key/value pair
  }); // end of ajax

  var updateStatus = function(data) {
    if (window.console) console.log("updateStatus");
    // The data parameter is a JSON object.
    var formatted_html = "";
    for(i=0; i<data.length; i++)
    { 
         formatted_html += "<p>* " + data[i].name + ", " + data[i].title + "</p>";
    }
    formatted_html += "";
    $('#karchan_status').html(formatted_html); // data.products);
  } // updateStatus
}


function startMeUp($)
{
  if (window.console) console.log("startMeUp");
  showStatus($);
}

