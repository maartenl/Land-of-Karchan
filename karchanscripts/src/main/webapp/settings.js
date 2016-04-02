/**
 * The Global Karchan Object. Accessible from *anywhere in the world*.
 * Isn't it amazing? (Exaggeration!)
 * We can use this object to create a namespace for my karchan methods
 * functions and constants.
 */
var Karchan = Karchan || {};

function webError(jqXHR, textStatus, errorThrown)
{
    if (window.console)
    {
        console.log(jqXHR);
        console.log(textStatus);
        console.log(errorThrown);
    }
    try
    {
        var errorDetails = JSON.parse(jqXHR.responseText);
        if (window.console)
            console.log(errorDetails);
    } catch (e)
    {
        alert("An error occurred. Please notify Karn or one of the deps.");
        if (window.console)
            console.log(e);
        return;
    }
    if (errorDetails.stacktrace !== undefined)
    {
        var buffer = "Timestamp: " + errorDetails.timestamp + "<br/>";
        buffer += "Errormessage: " + errorDetails.errormessage + "<br/>";
        buffer += "Stacktrace: " + errorDetails.stacktrace + "<br/>";
        buffer += "User: " + errorDetails.user + "<br/>";
        buffer += "Browser CodeName: " + navigator.appCodeName + "<br/>";
        buffer += "Browser Name: " + navigator.appName + "<br/>";
        buffer += "Browser Version: " + navigator.appVersion + "<br/>";
        buffer += "Cookies Enabled: " + navigator.cookieEnabled + "<br/>";
        buffer += "Platform: " + navigator.platform + "<br/>";
        buffer += "User-agent header: " + navigator.userAgent + "<br/>";
        $("#warning").html(buffer);
    }
    alert(errorDetails.errormessage);
}

function retrieveName()
{
    if (window.console)
        console.log("retrieveName");
    if (typeof (Storage) !== "undefined") {
        // Store
        return localStorage.getItem("karchanname");
    } else {
        return Cookies.get('karchanname');
    }
    alert("Unable to find you. Are you sure you are logged in?");
}

function clearName()
{
    if (window.console)
        console.log("clearName");
    if (typeof (Storage) !== "undefined") {
        // Store
        localStorage.setItem("karchanname", null);
    } else {
        Cookies.set('karchanname', null, {path: '/'});
    }
}

function playGame()
{
    if (window.console)
        console.log("playGame");
    $.ajax({
        type: 'POST',
        url: "/karchangame/resources/game/" + name + "/enter", // Which url should be handle the ajax request.
        cache: false,
        success: (function (data) {
            enterGame();
        }),
        error: webError,
        complete: (function () {
            if (window.console)
                console.log("complete");
        }),
        dataType: 'json', //define the type of data that is going to get back from the server
        data: 'js=1' //Pass a key/value pair
    }); // end of ajax

    var enterGame = function () {
        if (window.console)
            console.log("enterGame");
        window.location.href = "/web/guest/play";
    } // enterGame
    return false;
}

function logoff()
{
    if (window.console)
        console.log("logoff");
    var name = Karchan.name;

    $.ajax({
        type: 'PUT',
        url: "/karchangame/resources/game/" + name + "/logoff", // Which url should be handle the ajax request.
        cache: false,
        success: (function (data) {
            processLogoff();
        }),
        error: webError,
        complete: (function () {
            if (window.console)
                console.log("complete");
        }),
        dataType: 'json', //define the type of data that is going to get back from the server
        data: 'js=1' //Pass a key/value pair
    }); // end of ajax

    var processLogoff = function () {
        if (window.console)
            console.log("processLogoff");
        clearName();
        window.location.href = "/web/guest/goodbye";
    } // processLogoff
    return false;
}

function startMeUp()
{
    var name = retrieveName();
    if (window.console)
        console.log("startMeUp " + name);
    Karchan.name = name;
    $('#karchancharactername').html(name);
}

$(document).ready(startMeUp);
