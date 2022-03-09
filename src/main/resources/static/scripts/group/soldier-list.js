listDescription.focus();

const backBtn = document.querySelector("button#back");
backBtn.addEventListener("click", function() {
  window.history.back();
});

const saveListBtn = document.querySelector("button#saveList");
saveListBtn.addEventListener("click", saveList);
hideLoadStatus();

function saveList() {
  const form = document.querySelector("#formList");
  showLoadStatus();

  sendAjaxRequest(
    'POST',
    form.action,
    new FormData(form),
    runSavedListSuccessTasks,
    runSavedListFailedTasks);
}

function clearFoundSoldiers(foundSoldiers){
  clearChilds(foundSoldiers);
}

function runSavedListSuccessTasks(responseText) {
  alert(responseText);
  window.location = '[(@{/gp/dw/list})]';
}

function runSavedListFailedTasks(responseText) {
  let spanError = document.querySelector(".error");
  spanError.textContent = responseText;
  spanError.style.display = "";
  saveListBtn.disabled = false;
  hideLoadStatus();
}

function getErrorParagraph(errorMsg) {
  let error = document.createElement("p");
  error.classList.add("error");
  error.style.display = "block";
  error.textContent = errorMsg;
  return error;
}

function showLoadStatus() {
  saveListBtn.querySelector('#loadCircle').style.display = '';
  saveListBtn.disabled = true;
}

function hideLoadStatus() {
  saveListBtn.querySelector('#loadCircle').style.display = 'none';
}

function disableSoldierInfo(errorMsg, soldierInfoDiv){
  let error = getErrorParagraph(errorMsg);
  soldierInfoDiv.append(error);
  soldierInfoDiv.classList.add("disable-div");
}