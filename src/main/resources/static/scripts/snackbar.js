function temporarySnackbar(snackbar) {
  showSnackbar(snackbar);
  setTimeout(function(){ hideSnackbar(snackbar); }, 3000);
}

function showSnackbar(snackbar){
  snackbar.className = "show";
}

function hideSnackbar(snackbar){
  snackbar.className = snackbar.className.replace("show", "");
}