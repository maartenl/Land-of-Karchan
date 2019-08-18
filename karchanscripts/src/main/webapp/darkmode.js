
function darkmode() {  
  if (console.log) {
    console.log("darkmode: cookie value is " + Cookies.get("karchandarkmode"));
  }
  if (Cookies.get("karchandarkmode") === undefined) {
    if (console.log) {
      console.log("Turning darkmode on.");
    }
    Cookies.set('karchandarkmode', {expires: 365, path: '/'});
    $('#darkmodeToggleText').html("on");
    $('#darkmodeToggle').html("Turn darkmode off");
    $('link[href="/css/bootstrap.min.css"]').attr('href', '/css/bootstrap.cyborg.min.css');
    if (console.log) {
      console.log("Turning darkmode on finished.");
    }
  }
  else {
    if (console.log) {
      console.log("Turning darkmode off.");
    }
    Cookies.remove('karchandarkmode', {expires: 365, path: '/'});
    $('#darkmodeToggleText').html("off");
    $('#darkmodeToggle').html("Turn darkmode on");
    $('link[href="/css/bootstrap.cyborg.min.css"]').attr('href', '/css/bootstrap.min.css');
    if (console.log) {
      console.log("Turning darkmode off finished.");
    }
  }
//  window.location.href = "/help/darkmode.html";    
}

