var loadCircle = document.getElementById("loadCircle");
loadCircle.style.display = "none";

const redefinedPass = document.querySelector("input#redefinedPass");
if (redefinedPass) showRedefinePassAlert;
  

function onSubmit(token) {
    document.getElementById("loginForm").submit();
    loadCircle.style.display = 'block';
}

function showRedefinePassAlert() {
  alert(RESET_PASSWORD_SUCCESS);
}