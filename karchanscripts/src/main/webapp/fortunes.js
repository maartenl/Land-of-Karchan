
function showFortunes() 
{
  // Code to run when the document is ready.
  if (window.console) console.log("showFortunes");
  $.ajax({
      type: 'GET',
      url: "/karchangame/resources/public/fortunes", // Which url should be handle the ajax request.
      cache: false,
      success: (function(data) {updateFortunes(data); }),
      error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
      complete: (function() { if (window.console) console.log("complete"); }),        
      dataType: 'json', //define the type of data that is going to get back from the server
      data: 'js=1' //Pass a key/value pair
  }); // end of ajax
      
  var updateFortunes = function(data) 
  {
    if (window.console) console.log("updateFortunes");
    // The data parameter is a JSON object.
    var formatted_html = "<table><tr><td><b>Position</b></td><td><b>Name</b></td><td><b>Money</b></td></tr>";
    for(i=0; i<data.length; i++) 
    { 
       formatted_html += "<tr><td>" + (i+1) + "</td><td>" + data[i].name + "</td><td>" +
       data[i].gold + " gold, " + data[i].silver + " silver, " + data[i].copper + " copper</td></tr>"; 
    }
    $('#karchan_fortunes').html(formatted_html); // data.products);
  } // updateFortunes
}

$( document ).ready(showFortunes);
