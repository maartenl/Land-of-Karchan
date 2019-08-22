
function setHideTableOfContents() {
  if (console.log) {
    console.log("hideToc: cookie value is " + Cookies.get("karchanhidetoc"));
  }
  if (Cookies.get("karchanhidetoc") === undefined) {
    if (console.log) {
      console.log("Turning hideToc on.");
    }
    Cookies.set('karchanhidetoc', {expires: 365, path: '/wiki'});
    $('#hideTocText').html("Show");
    $('#content').removeClass("col-md-10");
    $('#toc').removeClass("d-md-block");
    if (console.log) {
      console.log("Turning hideToc on finished.");
    }
  } else {
    if (console.log) {
      console.log("Turning hideToc off.");
    }
    Cookies.remove('karchanhidetoc');
    $('#hideTocText').html("Hide");
    $('#content').addClass("col-md-10");
    $('#toc').addClass("d-md-block");
    if (console.log) {
      console.log("Turning hideToc off finished.");
    }
  }
  return false;
}

