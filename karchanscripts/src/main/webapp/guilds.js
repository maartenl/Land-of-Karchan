function showGuilds() 
{
  if (window.console) console.log("showGuilds");
  $.ajax({
    type: 'GET',
    url: "/karchangame/resources/public/guilds", // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {updateGuilds(data); }),
    error: (function() { alert(Karchan.getGenericError()); }),
    complete: (function() { if (window.console) console.log("complete"); }),        
    dataType: 'json', //define the type of data that is going to get back from the server
    data: 'js=1' //Pass a key/value pair
  }); // end of ajax

  var updateGuilds = function(data) {
    if (window.console) console.log("updateGuilds");
    // The data parameter is a JSON object.
    var formatted_html = "<p><a href=\"#\" id=\"karchan_show_all\">Expand all</a>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <a href=\"#\" id=\"karchan_collapse_all\">Collapse all</a></p>";
    for(i=0; i<data.length; i++)
    { 
         if (data[i].title == undefined || data[i].title == "")
         {
           continue;
         }
         formatted_html += "<h1><img src=\"/favicon.ico\" id=\"karchan_guildtitle_" + i + "\"/>";
         if (data[i].guildurl != undefined && data[i].guildurl != "")
         {
           formatted_html += "<a href=\"" + data[i].guildurl + "\">";
         }
         formatted_html += data[i].title;
         if (data[i].guildurl != undefined && data[i].guildurl != "")
         {
           formatted_html += "</a>";
         }
         formatted_html += "</h1><div class=\"karchan_guild\" id=\"karchan_guild_" + i + "\"><dl><dt>Guildmaster</dt><dd><strong>" +
         data[i].bossname + "</strong></dd><dt>Created on</dt><dd><strong>" + data[i].creation +  
         "</strong></dd><dt>Description</dt><dd>" + data[i].guilddescription + "</dd></dl></div>";
    }
    $('#karchan_guilds').html(formatted_html); // data.products);
    $('#karchan_show_all').click(function(){
      $("div.karchan_guild").show();
    });
    $('#karchan_collapse_all').click(function(){
      $("div.karchan_guild").hide();
    });
    for(i=0; i<data.length; i++)
    {
      $("#karchan_guildtitle_" + i).click(function(object){
        $("#karchan_guild_" + object.currentTarget.id.substring(19)).toggle("slow");
      });
    }
    $("div.karchan_guild").hide();
  } // updateGuilds
}

$( document ).ready(showGuilds);
