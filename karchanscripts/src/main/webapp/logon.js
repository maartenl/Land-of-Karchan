function webError(jqXHR, textStatus, errorThrown)
{
    if (window.console)
    {
        console.log(jqXHR);
        console.log(textStatus);
        console.log(errorThrown);
    }
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
        var errorDetails = jqXHR.responseJSON;
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
    $("#logon-form-card").append( "<div class=\"alert alert-danger alert-dismissible fade show\" role=\"alert\">" +
        errormessage +
      "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\">" +
        "<span aria-hidden=\"true\">&times;</span>" +
      "</button>" +
    "</div>");
}

function logon()
{
    if (window.console)
        console.log("logon");
    var name = $("#name").val();
    var password = $("#password").val();
    if (window.console)
        console.log("logon name=" + name + " password=" + password);

    $.ajax({
        type: 'PUT',
        url: "/karchangame/resources/game/" + name + "/logon" + "?password=" + password, // Which url should be handle the ajax request.
        cache: false,
        success: (function (data) {
            processLogon();
        }),
        error: webError,
        complete: (function () {
            if (window.console)
                console.log("complete");
        }),
        dataType: 'json', //define the type of data that is going to get back from the server
        data: 'js=1' //Pass a key/value pair
    }); // end of ajax

    var processLogon = function () {
        if (window.console)
            console.log("processLogon");
        if (typeof (Storage) !== "undefined") {
            // Store
            localStorage.setItem("karchanname", name);
        } else {
            Cookies.set('karchanname', name, {path: '/'});
        }
        window.location.href = "/game/settings.html";
    } // processLogon
    return false;
}

