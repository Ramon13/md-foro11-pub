const searchForm = document.querySelector("form#soldierSearch");
searchForm.onsubmit = function() {
  searchSoldier(this);
  return false;
}

function searchSoldier(form) {
  let selectedQuarter = document.querySelector("select#yearQuarter");
  form.querySelector("input[type=hidden]#selectedQuarter").value = selectedQuarter.value;
  sendAjaxRequest( form.method, form.action, new FormData(form), displaySearchResult);
}

function displaySearchResult(responseText) {
  let soldiers = JSON.parse(responseText);
  
  let resultList = document.querySelector("div#searchResultList");
  clearChilds(resultList);

  let listTuple;
  let soldier;
  for (let i = 0; i < soldiers.length; i++) {
    soldier = soldiers[i];
    listTuple = cloneNode( document.querySelector("div#baseListTuple") );
    
    setTupleContent(listTuple, soldier);
    setTupleExclusion(listTuple, soldier);       
    
    resultList.append(listTuple);
  }
}

function setTupleContent(listTuple, soldier){     
  listTuple.querySelector(".soldier-id").value = soldier.id;
  listTuple.querySelector(".list-header").textContent = soldier.idInfoAsText;
  listTuple.querySelector(".list-info").textContent = soldier.omandRankAsText;
  listTuple.style.display = "block";
  
  listTuple.onclick = function(){ 
    addToServerList(listTuple, soldier.id);
  };
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