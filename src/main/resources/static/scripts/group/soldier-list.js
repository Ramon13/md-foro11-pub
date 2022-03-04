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
  let selectedQuarter = document.querySelector("select#yearQuarter");
  form.querySelector("input[type=hidden]#selectedQuarter").value = selectedQuarter.value;
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

  let baseSoldierInfo = document.querySelector("div#baseSoldierInfo");
  let newSoldierInfo;
  for (let i = 0; i < soldiers.length; i++) {
    newSoldierInfo = baseSoldierInfo.cloneNode(baseSoldierInfo);

    newSoldierInfo.querySelector(".soldier-id").value = soldiers[i].id;
    newSoldierInfo.querySelector(".list-header").textContent = soldiers[i].idInfoAsText;
    newSoldierInfo.querySelector(".list-info").textContent = soldiers[i].omandRankAsText;
    newSoldierInfo.querySelector(".remove-soldier").style.display = "none";
    newSoldierInfo.style.display = "block";

    soldiersDiv.append(newSoldierInfo);
   
    if (soldiers[i].firstExclusion){
      disableSoldierInfo(soldiers[i].firstExclusion, newSoldierInfo);
    }else{
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