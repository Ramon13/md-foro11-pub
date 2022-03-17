const newSoldierBtn = document.querySelector("div#newSoldierProfile a");
newSoldierBtn.onclick = function(event){
  event.preventDefault();
  historyPush();
  location.href = this.href;
}

const searchSoldierBtn = document.querySelector("button#openModalBtn");
searchSoldierBtn.onclick = function(){
  const soldierModal = document.querySelector("div#searchSoldierModal");
  displayModal( soldierModal );
}

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
  setResultListContent(soldiers);
}

function setResultListContent(soldiers){
  let resultList = document.querySelector("div#searchResultList");
  clearChilds(resultList);
  
  if (soldiers.length == 0){
    let p = document.createElement("p");
    p.textContent = "Nenhum resultado encontrado.";
    resultList.append(p);
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