const backBtn = document.querySelector("button#back");
backBtn.addEventListener("click", function() {
  window.history.back();
});

const saveListBtn = document.querySelector("button#saveList");
saveListBtn.addEventListener("click", saveList);
hideLoadStatus();

const searchForm = document.querySelector("form#soldierSearch");
searchForm.onsubmit = function() {
  searchSoldier(this);
  return false;
}

listDescription.focus();

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

function searchSoldier(form) {
  sendAjaxRequest(
    form.method,
    form.action,
    new FormData(form),
    showSearchSoldierResults,
    function() { }
  );
}

// param:soldiers A list of soldiers in JSON format
function showFoundSoldiers(soldiers) {
  let soldiersDiv = document.querySelector("div#foundSoldiers");
  clearChilds(soldiersDiv);

  let soldierInfo = document.querySelectorAll("div.soldier-info")[0];
  let newSoldierInfo;
  for (let i = 0; i < soldiers.length; i++) {
    newSoldierInfo = soldierInfo.cloneNode(soldierInfo);

    newSoldierInfo.querySelector(".soldier-id").value = soldiers[i].id;
    newSoldierInfo.querySelector(".list-header").textContent = soldiers[i].idInfoAsText;
    newSoldierInfo.querySelector(".list-info").textContent = soldiers[i].omandRankAsText;
    newSoldierInfo.querySelector(".remove-soldier").style.display = "none";

    soldiersDiv.append(newSoldierInfo);

    if (isInList(soldiers[i].id)) {
      let error = getErrorParagraph("militar já incluso na lista em edição.");
      newSoldierInfo.append(error);

    } else {
      newSoldierInfo.onclick = function() { addToList(this) };
    }
  }
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

function showSearchSoldierResults(responseText) {
  let soldiers = JSON.parse(responseText);
  showFoundSoldiers(soldiers);
}

function addToList(soldier) {
  soldier.querySelector(".remove-soldier").style.display = "block";
  document.querySelector("div#soldiers").prepend(soldier);
  soldier.onclick = function() { };
}

function isInList(soldierId) {
  let ids = document.querySelectorAll("div#soldiers .soldier-id");
  for (let i = 0; i < ids.length; i++) {
    if (+ids[i].value === +soldierId) {
      console.log(ids[i].value)
      return true;
    }
  }

  return false;
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