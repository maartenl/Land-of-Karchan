function createNewchar() 
{
  var $ = Karchan.$;
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
  if (window.console) console.log(characterObject);
  var jsonString = JSON.stringify(characterObject);
  $.ajax({
    type: 'POST',
    url: "/resources/game/" + characterObject.name, // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {
      alert("Your character has been created."); 
    }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),        
    dataType: 'json', //define the type of data that is going to get back from the server
    contentType: 'application/json; charset=utf-8',
    data: jsonString // Pass a key/value pair
  }); // end of ajax
  return false;
} // createNewchar

function startMeUp($)
{
  if (window.console) console.log("startMeUp");
  Karchan.$ = $;
}
