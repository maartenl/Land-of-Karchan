/**
 * LogonObject
 * received form info from the webpage,
 * and sends it onto the server using ajax
 * expects JSON back and junks it into the page.
 * 
 * uses karchan.js functions
 */

/**
* constructor
*/
function LogonObject() 
{
	
	/**
	* @var String m_sMudLog The name of the DOM element to display the log in
	*/
	this.m_sMudLog = "mudlog";

	/**
	* @var String m_sRoomDescription The name of the DOM element to display the roomdescription in
	*/
	this.m_sRoomDescription = "mudroomdescription";

	/**
	* @var XMLHttpRequest m_oRequest The XMLHttpReuqest object to use for Ajax
	*/
	this.m_oRequest = null;

	/**
	* @var String m_sURL The URL to send the request to
	*/
	// this.m_sURL = "/Webdesign/voorbeelden/php/getArticles.php";
	this.m_sURL = "/scripts/mud.php";
}

/**
* handleResponse The callback function for the Ajax request
* return Boolean True if the response is handled correctly
*/
LogonObject.prototype.handleResponse = function () 
{
	var bResult = false;
//	try {
		if (this.m_oRequest.readyState == 4) 
		{
			var mystatus = 0;
			try
			{
				mystatus = this.m_oRequest.status;
			}
			catch (e) 
			{
				// in a lot of cases, the status field in an XMLHttpRequest has not been set
				// normally, in javascript, this would default to "undefined", but 
				// firefox adheres rigidly to the specification, saying that an exception
				// should be thrown if the variable is used but not set.
			}		
			// check if the server responsed correctly
			// alert("LogonObject.handleResponse - final" + mystatus);
			// alert("LogonObject.handleResponse - final" + this.m_oRequest.responseText);
			// alert("LogonObject.handleResponse - final" + this.m_oRequest.responseXML);
			if (mystatus == 200) 
			{
				var oResponse = this.m_oRequest.responseText;
				// this request is finished, remove the object
				this.m_oRequest = null;
				var myObject = eval('(' + oResponse + ')');
				// alert(myObject.roomdescription);
				var oRoomDescription = document.getElementById("mudroomdescription");
				oRoomDescription.innerHTML = myObject.roomdescription;
				// alert(oRoomDescription.innerHTML);
			}
		}
//	} catch (e) 
//	{
//		bResult = false;
//		this.m_oRequest = null;
//		alert("LogonObject::handleResponse - " + e.name + ", " + e.message);
//	}
	return bResult;
}

/**
* load Sends form info to the server through an ajax request.
* @param String p_sCommand the command in the form
* @param String p_sName the name of the user (also stored in cookie though)
* @param String p_sFrames the interface to use
* return Boolean True if the request is send correctly
*/
LogonObject.prototype.load = function (p_sCommand, p_sName, p_sFrames) 
{
//	alert("LogonObject.load " + p_sCommand + "," + p_sName + "," + p_sFrames);
	var bResult = false;
//	try {
		if (this.m_oRequest == null) 
		{
			// create a request object
			this.m_oRequest = createXMLHttpRequest();
			// alert("this.m_oRequest" + this.m_oRequest);
			// set the callback function (special case for method)
			this.m_oRequest.onreadystatechange = this.handleResponse.bind(this);
			var queryString = "command=" + escape(p_sCommand) + "&name=" + escape(p_sName) + "&frames=" + escape(p_sFrames);
		//	this.m_oRequest.setRequestHeader("Content-Type", "text/plain;charset=UTF-8");
 	
			// alert(this.m_sURL);
			// alert(window.location.href);
			// alert(document.baseURI);
		//	alert("this.m_oRequest " + this.m_sURL);
		//	alert("this.m_oRequest " + queryString);			this.m_oRequest.open("POST", this.m_sURL, false);
			this.m_oRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		//	this.m_oRequest.setRequestHeader("Content-length", queryString.length);
		//	this.m_oRequest.setRequestHeader("Connection", "close");
//			this.m_oRequest.open("GET", this.m_sURL + queryString, true);
			// do the actual request
			this.m_oRequest.send(queryString);
		//	alert("LogonObject.handleResponse - final" + this.m_oRequest.responseText);
		//	alert("LogonObject.handleResponse - final" + this.m_oRequest.responseXML);

			//this.m_oRequest.send(null);
			this.handleResponse();
		} else {
			alert("Request already busy. Please be patient.");
			// request busy, don't start new one
		}
//	} catch(e) {
//		bResult = false;
//		this.m_oRequest = null;
//		alert("LogonObject::load - " + e.name + ", " + e.message);
//	}
	return bResult;
}



