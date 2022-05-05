listDescription.focus();

openSearchSoldierModalListener();
onclickToOpenProfile();
onclickToRemoveSoldier();
onSaveList();

const listInfo = document.querySelector("div#listInfo");

function onclickToOpenProfile() {
  document.querySelectorAll("div#soldiers .soldier-info").forEach(soldier => {
    soldier.onclick = async function() {
      let soldierId = soldier.querySelector(".soldier-id").value;
      
      displayProfileModal(await getSoldierOnServer(soldierId));
    }
  }); 
}

function openSearchSoldierModalListener() {
  document.querySelector("button#addSoldier").onclick = function() {
    const soldierModal = document.querySelector("div#searchSoldierModal");
    
    displayModal( soldierModal );            
  };  
}

function onclickToRemoveSoldier() {
  document.querySelectorAll("button.remove-soldier").forEach(removeBtn => {
    removeBtn.onclick = function(event) {
      event.stopPropagation();
      
      const soldier = {
        soldierId: removeBtn.dataset.soldierid,
        listId: getListId(),
        yearQuarter: getSelectedYearQuarter(),
        endpoint: removeSoldierEndpoint,
        
        removeSoldier() {
          removeBtn.closest("div.soldier-info").remove();
        }
      }
      
      showRemoveSoldierSnackbar();
      if ( window.confirm(REMOVE_SOLDIER_FROM_LIST) ) removeFromServerList(soldier);
      hideSnackbar(snackbar);
    }
  });
}

function onSaveList() {
  document.querySelector("button#saveList").onclick = function(event) {
    event.preventDefault();
    
    drawList = {
      id: getListId(),
      description: listInfo.querySelector("input#listDescription").value,
      yearQuarter: getSelectedYearQuarter(),
      endpoint: listInfo.querySelector("form#formList").action
    };
    
    saveList(drawList);
  } 
}

function getSoldierOnServer(soldierId) {
  const endpoint = getSoldierEndpoint + "/" + soldierId;
  
  return new Promise (resolve => {
    sendAjaxRequest(
      GET_METHOD,
      endpoint,
      null,
      function(responseText) {
        resolve(JSON.parse(responseText));       
      }
    ); 
  });
}

function saveList(drawList) {
  const xhr = new XMLHttpRequest();
  
  xhr.open(PUT_METHOD, drawList.endpoint, true);
  xhr.setRequestHeader(requestHeader.contentType, JSON_CONTENT_TYPE);
  
  xhr.send( JSON.stringify(drawList) );
  
  xhr.onreadystatechange = function() {
    if (xhr.readyState == XMLHttpRequest.DONE) {
      
      if (xhr.status == HTTP_OK) {
        window.location = listsEndpoint;
      
      }else if (xhr.status == HTTP_UNPROCESSABLE_ENTITY) {
        const validationErrors = JSON.parse(xhr.responseText);
        console.log(validationErrors);
      }else{
        alert(INTERNAL_SERVER_ERROR_ALERT);
      }
        
    }
  }
}

function disableSoldierInfo(errorMsg, soldierInfoDiv){
  let error = getErrorParagraph(errorMsg);
  soldierInfoDiv.append(error);
  soldierInfoDiv.classList.add("disable-div");
}

function addToServerList(listTuple){
  let endpoint = addSoldierEndpoint;
  
  showAddingSoldierSnackbar();
  
  let soldierListDTO = {
    soldierId: getSoldierId(listTuple),
    listId: getListId(),
    yearQuarter: getSelectedYearQuarter()
  };
  
  sendJSONAsyncRequest(
    endpoint,
    JSON.stringify(soldierListDTO),
    function(xhr){
      if (xhr.status === 201){
        listTuple.onclick = function(e){ e.stopPropagation(); };    
        showRemoveBtn( listTuple.querySelector(".remove-soldier"), soldierListDTO.soldierId );
        appendToHTMLList(listTuple);
        onclickToOpenProfile();
      }
      
      hideSnackbar(snackbar);
    }
  );
}

function removeFromServerList(soldier){
  let xhr = new XMLHttpRequest();
  
  xhr.open(POST_METHOD, soldier.endpoint, true);
  xhr.setRequestHeader(requestHeader.contentType, JSON_CONTENT_TYPE);
  
  xhr.send( JSON.stringify(soldier) );
  
  xhr.onreadystatechange = function() {
    if (xhr.readyState == XMLHttpRequest.DONE) {
      if (xhr.status == HTTP_NO_CONTENT) {
        soldier.removeSoldier();
      
      }else{
        alert(SOLDIER_CANNOT_BE_REMOVED);
      }
    }
  }
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

function getSoldierId(listTuple){
  return listTuple.querySelectorAll(".soldier-id")[0].value;
}

function getListId() {
  return document.querySelector("input[type=hidden]#listId").value;
}

function getSelectedYearQuarter() {
  return document.querySelector("select#yearQuarter").value
}