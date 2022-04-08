function temporarySnackbar(snackbar) {
  showSnackbar(snackbar);
  setTimeout(function(){ hideSnackbar(snackbar); }, 3000);
}

function showSnackbar(snackbar, zIndex){
  snackbar.className = "show";
  snackbar.style.zIndex = zIndex;
}

function hideSnackbar(snackbar){
  snackbar.className = snackbar.className.replace("show", "");
}