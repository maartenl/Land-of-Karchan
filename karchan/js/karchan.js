function bigtalk() 
{
	var form = document.getElementById("CommandForm");;
	for (var i = 0 ; i < form.length ; i++)
	{
		var child = form.childNodes[i];
		// alert(child.type);
		// alert(child.name);
		// alert(child.value);
		if (child.type == "text")
		{
			// alert("Text found!");
			var message = child.value;
			var newnode = document.createElement("textarea");
			newnode.name="command";
			newnode.id = "command";
			newnode.value = message;
			newnode.rows = 20;
			newnode.cols = 85;
			form.replaceChild(newnode, child);
			newnode.focus();
		}
		else
		if (child.type == "textarea")
		{
			// alert("textarea found!");
			var message = child.value;
			var newnode = document.createElement("input");
			newnode.type="text";
			newnode.name="command";
			newnode.id = "command";
			newnode.value = message;
			newnode.size = 50
			form.replaceChild(newnode, child);
			newnode.focus();
		}
	}
}

function setfocus() 
{
	document.CommandForm.command.focus();
}

/**
 * changes the image of the button. Useful for mouseover and mouseout
 * There are two types of buttons:
 * a. http://localhost/images/gif/webpic/new/button3.gif
 * b. http://localhost/images/gif/webpic/button3.gif
 * This method strips or adds the 'new' whenever appropriate.
 * @param imgName the id of the image
 */
function changeImage(imgName)
{
	var theImage = document.getElementById(imgName);
	if (theImage == null)
	{
		alert("Id not found in html dom");
		return;
	}
	if (theImage.src.indexOf("webpic/new/") != -1)
	{
		var mybutton =
		theImage.src.substring(theImage.src.indexOf("webpic/new/") +
		11);
		theImage.src = "/images/gif/webpic/" + 
			mybutton;
	}
	else
	{
		var mybutton =
		theImage.src.substring(theImage.src.indexOf("webpic/") +
		7);
		theImage.src = "/images/gif/webpic/new/" +
			mybutton;
	}
}
