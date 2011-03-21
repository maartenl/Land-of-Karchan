// $Id:$

/**
 * JavaScript behaviors for the front-end display of karchan.
 * @see http://drupal.org/node/756722
 */

(function ($) {

  Drupal.behaviors.karchan = {
    attach: function(context, settings) {
      // $('.example', context).click(function () {
      // $(this).next('ul').toggle('show');
      // alert("Karn is working on it, yes...");
      $('#karchan_news').each(function() {
        if (window.console) console.log("#karchan_news");
        $.ajax({
          type: 'GET',
          url: "/resources/public/news", // Which url should be handle the ajax request. This is the url defined in the <a> html tag
          success: (function(data) {updateNews(data); }),
          error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
          complete: (function() { if (window.console) console.log("complete"); }),        
        //          success: updateNews, // The js function that will be called upon success request
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
      }); // karchan_news
      
    } // attach function
  }; // end Drupal.behaviours.karchan

})(jQuery); // end jQuery