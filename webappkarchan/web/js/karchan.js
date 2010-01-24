function bigtalk() 
{
	var form = document.getElementById("CommandForm");
	var child = document.getElementById("command");
	// alert(child);
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


/**
* This adds a new property bind to the Function class. With this you can set a method of an object as a callback function
* TODO handle arguments
*/
Function.prototype.bind = function() {
  var __method = this;
  var args= new Array();
  for (var i=0; i < arguments.length; i++ ) {
  	args.push(arguments[i]);
  }
  var object = args.shift();
  return function() {
    return __method.apply(object, arguments);
  }
}

/**
 * function to create a XMLHttpRequest object independent of the browser
 */
function createXMLHttpRequest() 
{
	var oRequest = null;
	try 
	{
		oRequest = new XMLHttpRequest();
	} catch (e) 
	{
		try 
		{
			oRequest = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) 
		{
			try 
			{
				oRequest = new ActiveXObject("MicroSoft.XMLHTTP");
			} catch (e) 
			{
				oRequest = null;
				alert("Could not create XMLHttpRequest");
			}
		}

	}
	return oRequest;
}

