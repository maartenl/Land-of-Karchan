function sendMail() 
{
  var $ = Karchan.$;
  if (window.console) console.log("sendMail");
  var jsonString = JSON.stringify(
    {
      name : $.cookie("name"),
      toname : $('#edit-submitted-to').val(),
      subject : $('#edit-submitted-subject').val(),
      // body : $('#edit-submitted-body').val(),
      body : tinyMCE.get('edit-submitted-body').getContent(),
      lok: $.cookie("lok")
    }
  );
  $.ajax({
    type: 'POST',
    url: "/resources/private/" + $.cookie("name") + "/mail?lok=" + $.cookie("lok"), // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {
      alert("Mail sent."); 
    }),
    error: (function() { alert(Karchan.getGenericError()); }),
    complete: (function() { if (window.console) console.log("complete"); }),        
    dataType: 'json', //define the type of data that is going to get back from the server
    contentType: 'application/json; charset=utf-8',
    data: jsonString // Pass a key/value pair
  }); // end of ajax
  return false;
} // sendMail

function startMeUp($)
{
  if (window.console) console.log("startMeUp");
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
}

