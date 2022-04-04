onSubmitSearchForm();
onClickNewSoldierBtn();

function onClickNewSoldierBtn() {
  document.querySelector("div#newSoldierProfile a").onclick = function( event ){
    event.preventDefault();
    
    const soldierRegister = document.querySelector("div#soldierRegister");
    displayModal( soldierRegister );
  }  
}

function onSubmitSearchForm() {
  document.querySelector("form#soldierSearch").onsubmit = function(event) {
    event.preventDefault();
    
    const form = document.querySelector("form#soldierSearch");
    const search = {
      key: form.querySelector("input#searchKey").value,
      yearQuarter: document.querySelector("select#yearQuarter").value,
      listId: document.querySelector("input#listId").value,
      action: form.action     
    };
        
    searchSoldier(search);
    return false;
  }  
}

function searchSoldier(search) {
  sendAjaxRequest( 
    POST_METHOD,
    search.action,
    JSON.stringify(search),
    displaySearchResult,
    null,
    JSON_CONTENT_TYPE);
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

function displaySearchResult(responseText) {
  let soldiers = JSON.parse(responseText);
  setResultListContent(soldiers);
}

function setResultListContent(soldiers){
  let resultList = document.querySelector("div#searchResultList");
  clearChilds(resultList);
  
  if (soldiers.length == 0){
    showNoContentMessage(resultList);
    return;
  }
  
  for (let i = 0; i < soldiers.length; i++) {
   resultList.append( getTupleContent(soldiers[i]) );
  }
}

function getTupleContent(soldier){
  let listTuple = cloneNode( document.querySelector("div#baseListTuple") );
      
  listTuple.querySelector(".soldier-id").value = soldier.id;
  listTuple.querySelector(".list-header").textContent = soldier.idInfoAsText;
  listTuple.querySelector(".list-info").textContent = soldier.omandRankAsText;
  listTuple.style.display = "block"; 
  
  setTupleExclusion(listTuple, soldier);       

  listTuple.onclick = function(){ 
    addToServerList(listTuple);
  };
  
  return listTuple;
}

function setTupleExclusion(listTuple, soldier){
  let errorMsg;
  
  if ( (errorMsg = soldier.firstExclusion) ){
    listTuple.append( getErrorParagraph(errorMsg) );    
    disableTupleContent(listTuple);
  }  
}

function disableTupleContent(listTuple){
  listTuple.classList.add("disable-div");
}

function getErrorParagraph(errorMsg) {
  let error = document.createElement("p");
  error.classList.add("error");
  error.style.display = "block";
  error.textContent = errorMsg;
  return error;
}

function showNoContentMessage(parentNode) {
  let p = document.createElement("p");
  p.textContent = NO_CONTENT_FOUND;
  parentNode.append(p);
}