function showWho()
{
    if (window.console)
        console.log("showWho");
    var getWho = function () {
        if (window.console)
            console.log("getWho");
        $.ajax({
            type: 'GET',
            url: "/karchangame/resources/public/who", // Which url should be handle the ajax request.
            cache: false,
            success: (function (data) {
                updateWho(data);
            }),
            error: (function () {
                alert("An error occurred. Please notify Karn or one of the deps.");
            }),
            complete: (function () {
                if (window.console)
                    console.log("complete");
            }),
            dataType: 'json', //define the type of data that is going to get back from the server
            data: 'js=1' //Pass a key/value pair
        }); // end of ajax
    };

    getWho();
    var who_interval_id = setInterval(getWho, 60000);

    var updateWho = function (data) {
        if (data === null || data.length === 0)
        {
            $('#karchan_who').html("<p>There are no people online. Get online, quick!</p>");
            return;
        }
        if (window.console)
            console.log("updateWho");
        // The data parameter is a JSON object.
        var formatted_html = "<style>\n" +
                "ul.wholist li {\n" +
                "padding:12px;\n" +
                "}\n" +
                "ul.wholist li img {\n" +
                "vertical-align: text-bottom;\n" +
                "}\n" +
                "</style>";
        if (data.length === 1)
        {
            formatted_html += "<p>There is one person online.</p><ul>";
        }
        else
        {
            formatted_html += "<p>There are " + data.length + " people online.</p><ul class=\"wholist\">";
        }
        for (i = 0; i < data.length; i++)
        {
            var character = data[i];
            formatted_html += "<li>" + character.name + " , " + data[i].title + ", " +
                    data[i].sleep + " " + data[i].area + " (logged on " + data[i].min + " min ago)</li>";
        }
        formatted_html += "</ul>";
        $('#karchan_who').html(formatted_html);
    } // updateWho
}

$(document).ready(showWho);
