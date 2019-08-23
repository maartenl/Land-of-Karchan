function showError(jqXHR, textStatus, errorThrown)
{
  if (window.console) {console.log(jqXHR);console.log(textStatus);console.log(errorThrown);}
  if (errorThrown !== undefined) 
  {
    var errormessage = errorThrown;
  } 
  else
  {
    var errormessage = "An error occurred.";
  }
  try
  {
      var errorDetails = JSON.parse(jqXHR.responseText);
      if (window.console)
          console.log(errorDetails);
  } catch (e)
  {
      if (window.console)
          console.log(e);        
      return;
  }
  if (errorDetails !== undefined && errorDetails.errormessage!== undefined)
  {
      if (window.console) console.log(errorDetails);
      var errormessage = errorDetails.errormessage;
  }
  $("#newchar-form-id").append( "<div class=\"alert alert-danger alert-dismissible fade show\" role=\"alert\">" +
      errormessage +
    "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\">" +
      "<span aria-hidden=\"true\">&times;</span>" +
    "</button>" +
  "</div>");
}

function createNewchar() 
{
  if (window.console) console.log("createNewchar");
  var characterObject = {
       "name": $('#edit-submitted-fictional-name').val(),
       "title": $('#edit-submitted-title-except-name').val(),
       "sex": $("input[name='submitted[sex]']:checked").val(),
       "password" : $('#edit-submitted-password').val(),
       "password2" : $('#edit-submitted-password2').val(),
       "realname" : $('#edit-submitted-real-name').val(),
       "email" : $('#edit-submitted-email').val(),
       "race" : $('#edit-submitted-race').val(),
       "age" : $('#edit-submitted-age').val(),
       "height" : $('#edit-submitted-length').val(),
       "width" : $('#edit-submitted-width').val(),
       "complexion" : $('#edit-submitted-complexion').val(),
       "eyes" : $('#edit-submitted-eyes').val(),
       "face" : $('#edit-submitted-face').val(),
       "hair" : $('#edit-submitted-hair').val(),
       "beard" : $('#edit-submitted-beard').val(),
       "arm" : $('#edit-submitted-arms').val(),
       "leg" : $('#edit-submitted-legs').val()
  };
  var name = characterObject.name;
  var password = characterObject.password;
  if (window.console) console.log(characterObject);
  var jsonString = JSON.stringify(characterObject);
  $.ajax({
    type: 'POST',
    url: "/karchangame/resources/game/" + characterObject.name, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {
      if (window.console) console.log("character " + name + " has been created."); 
      logon(name, password);
      return;
    }),
    error: showError,
    complete: (function() { if (window.console) console.log("complete"); }),        
    dataType: 'text', //define the type of data that is going to get back from the server
    contentType: 'application/json; charset=utf-8',
    data: jsonString // Pass a key/value pair
  }); // end of ajax
  return false;
} // createNewchar


function logon(name, password)
{
  if (window.console)
    console.log("logon");
  if (window.console)
    console.log("logon name=" + name + " password=" + password);

  $.ajax({
    type: 'GET',
    url: "/",
    cache: false,
    headers: {
      "Authorization": "Basic " + btoa(name + ":" + password)
    },
    success: (function (data) {
      processLogon();
    }),
    error: showError,
    complete: (function () {
      if (window.console)
        console.log("complete");
    }),
    dataType: 'html' //define the type of data that is going to get back from the server
  }); // end of ajax

  var processLogon = function () {
    if (window.console)
      console.log("processLogon");
    window.location.href = "/";
  } // processLogon
  return false;
}
