var toc1on = new Image;
toc1on.src = "../images/gif/webpic/new/buttonk.gif";
var toc2on = new Image;
toc2on.src = "../images/gif/webpic/new/buttonj.gif";
var toc3on = new Image;
toc3on.src ="../images/gif/webpic/new/buttonr.gif";
var toc1off = new Image;
toc1off.src = "../images/gif/webpic/buttonk.gif";
var toc2off = new Image;
toc2off.src = "../images/gif/webpic/buttonj.gif";
var toc3off = new Image;
toc3off.src = "../images/gif/webpic/buttonr.gif";
var tocAwakenon = new Image;
tocAwakenon.src = "../images/gif/webpic/new/buttonl.gif";
var tocAwakenoff = new Image;
tocAwakenoff.src = "../images/gif/webpic/buttonl.gif";

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

function img_act(imgName) 
{
	imgOn = eval(imgName + "on.src");
	document [imgName].src = imgOn;
}

function img_inact(imgName) 
{
	imgOff = eval(imgName + "off.src");
	document [imgName].src = imgOff;
}

