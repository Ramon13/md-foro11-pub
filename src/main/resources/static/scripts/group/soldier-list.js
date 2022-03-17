listDescription.focus();

const saveListBtn = document.querySelector("button#saveList");
saveListBtn.addEventListener("click", saveList);
hideLoadStatus();

const soldierInfoTuples = document.querySelectorAll("div.soldier-info");
for (let i = 0; i < soldierInfoTuples.length; i++){
  soldierInfoTuples[i].onclick = function(){
    openProfile(this);
  }
}

const removeSoldierBtns = document.querySelectorAll("button.remove-soldier");
for (let i = 0; i < removeSoldierBtns.length; i++){
  removeSoldierBtns[i].onclick = function(event){
    event.stopPropagation();
    let soldierId = this.dataset.soldierid;
    removeFromServerList(soldierId);
  }
}

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

function runSavedListSuccessTasks(responseText) {
  alert(responseText);
  window.location = listsEndpoint;
}

function runSavedListFailedTasks(responseText) {
  let spanError = document.querySelector(".error");
  spanError.textContent = responseText;
  spanError.style.display = "";
  saveListBtn.disabled = false;
  hideLoadStatus();
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

function addToServerList(listTuple){
  let endpoint = addSoldierEndpoint;
  let soldierId = getSoldierId(listTuple); 
  
  showAddingSoldierSnackbar();
  let soldierListDTO = getSoldierToListDTOObj(soldierId);
  
  sendJSONAsyncRequest(
    endpoint,
    JSON.stringify(soldierListDTO),
    function(xhr){
      if (xhr.status === 201){
        listTuple.onclick = function(e){ e.stopPropagation(); };    
        showRemoveBtn( listTuple.querySelector(".remove-soldier"), soldierId );
        appendToHTMLList(listTuple);
      }
      
      hideSnackbar(snackbar);
    }
  );
}

function removeFromServerList(soldierId){
  let endpoint = removeSoldierEndpoint;
  let soldierListDTO = getSoldierToListDTOObj(soldierId);
  
  
  showRemoveSoldierSnackbar();
  sendJSONAsyncRequest(
    endpoint,
    JSON.stringify(soldierListDTO),
    function(xhr){
      if (xhr.status === 204){
        location.reload();
      }else{
        alert("O militar não pôde ser removido da lista.");
      }
      
      hideSnackbar(snackbar);
    }
  );
}

function sendJSONAsyncRequest(endpoint, content, onReadyTask){
  let xhr = new XMLHttpRequest();
  xhr.open("POST", endpoint, true);
  xhr.setRequestHeader("Content-Type", "application/json");
  
  xhr.send(content);
  
  xhr.onreadystatechange = function(){
    if (xhr.readyState == XMLHttpRequest.DONE){
      onReadyTask(xhr);
    }
  }
}

function getSoldierToListDTOObj(soldierId){
  let listId = document.querySelector("input[type=hidden]#listId").value;
  let yearQuarter = document.querySelector("select#yearQuarter").value;
  return {soldierId: soldierId, listId: listId, yearQuarter: yearQuarter};
}

function showAddingSoldierSnackbar(){
  const snackbar = document.querySelector("div#snackbar");
  snackbar.textContent = "Adicionando militar à lista..."
  showSnackbar(snackbar);
}

function showRemoveSoldierSnackbar(){
  const snackbar = document.querySelector("div#snackbar");
  snackbar.textContent = "Removendo militar, aguarde..."
  showSnackbar(snackbar);
}

function appendToHTMLList(listTuple){
  document.querySelector("div#soldiers").prepend(listTuple);  
}

function showRemoveBtn(removeBtn, soldierId){  
  removeBtn.onclick = function(){ removeFromServerList(soldierId) }
  removeBtn.style.display = "block";  
}

function openProfile(listTuple){
  historyPush();
  let soldierId = getSoldierId(listTuple);
  location.href = soldierProfileEndpoint + "/" + soldierId;
}

function getSoldierId(listTuple){
  return listTuple.querySelectorAll(".soldier-id")[0].value;
}