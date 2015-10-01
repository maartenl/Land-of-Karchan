function showNews( $ ) 
{
  if (window.console) console.log("showNews");
  $.ajax({
    type: 'GET',
    url: "/resources/public/news", // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {updateNews(data); }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),
    dataType: 'json', //define the type of data that is going to get back from the server
    data: 'js=1' //Pass a key/value pair
  }); // end of ajax

  var updateNews = function(data) {
    if (window.console) console.log("updateNews");
    // The data parameter is a JSON object.
    var formatted_html = "";
    for(i=0; i<data.length; i++)
    { 
       formatted_html += "<hr/><p>" + data[i].posttime + "</p><p>" +
       data[i].message + "</p><p><i>" + data[i].name +
       "</i></p>"; 
    }
    $('#karchan_news').html(formatted_html); // data.products);
  } // updateNews
}


function startMeUp($)
{
  if (window.console) console.log("startMeUp");
  showNews($);
}

