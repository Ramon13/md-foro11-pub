onSubmitSearchForm();
onClickNewSoldierBtn();
onSearchSoldierWithJsonFile();

function onClickNewSoldierBtn() {
  document.querySelector("div#newSoldierProfile a").onclick = function( event ){
    event.preventDefault();
    
    const soldierRegister = document.querySelector("div#soldierRegister");
    displayModal( soldierRegister, 2 );
  }  
}

function onSubmitSearchForm() {
  document.querySelector("form#soldierSearch").onsubmit = function(event) {
    event.preventDefault();
    
    const keys = getSearchedKeys();
    clearResultList();
    
    const search = {
      key: "",
      yearQuarter: getSelectedYearQuarter(),
      listId: getListId()
    };
    
    for (let key of keys) {
      search.key = key;
      
      void async function() {
        let soldiers = await searchSoldier(search);
        displaySearchResult(soldiers);
        
        if (soldiers.length == 0)
          console.log(`[${key}] not found.`);
      }();
    }
    
    return false;
  } 
}

function onSearchSoldierWithJsonFile() {
  const searchJsonModal = document.querySelector("div#searchWithJsonModal");
  
  document.querySelector("a#searchWithJsonData").onclick = function( event ) {
    event.preventDefault();
    
    displayModal(searchJsonModal, 3);
  }
  
  searchJsonModal.querySelector("button.searchSoldier").onclick = function() {
    const rawSoldiers = searchJsonModal.querySelector("textarea.jsonData").value;
    let soldiers = JSON.parse(rawSoldiers);
    
    const search = {
      key: "",
      yearQuarter: getSelectedYearQuarter(),
      listId: getListId()
    };
    
    soldiers.forEach(soldier => {
      void async function() {
        search.key = soldier.name;
        
        let resultList = await searchSoldier(search);
        displaySearchResult(resultList);
        
        if (resultList.length == 0) {
          console.log(`[${soldier.name}] not found... Trying to create a new profile`);
          saveSoldier(
            soldier,
            function() {
              console.log(`${soldier.name} was created.`);
            },
            function(responseText) {
              console.log(`validation error when trying to create ${soldier.name}. see log for details: ${responseText}`)
            }
          );
        }
          
      }();
    });
  }
}

function getSearchedKeys() {
  const inputText = document.querySelector("#soldierSearch input#searchKey").value;
  
  let keys = inputText.split(",");
  for (let i = 0; i < keys.length; i++) keys[i] = keys[i].trim();
  
  return keys;
}

function searchSoldier(search) {
  return new Promise(resolve => {
    sendAjaxRequest( 
      POST_METHOD,
      getSearchEndpoint(),
      JSON.stringify(search),
      function(responseText) {
        resolve(JSON.parse(responseText));
      },
      null,
      JSON_CONTENT_TYPE
    );
  });
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

function displaySearchResult(soldiers) {
  setResultListContent(soldiers);
}

function setResultListContent(soldiers){
  let resultList = document.querySelector("div#searchResultList");
  
  if (soldiers.length == 0){
    showNoContentMessage(resultList);
    return;
  }
 
  for (let i = 0; i < soldiers.length; i++) {
   resultList.append( getTupleContent(soldiers[i]) );
  }
}

function getTupleContent(soldier){
  const listTuple = cloneNode( document.querySelector("div#baseListTuple") );
      
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

function clearResultList() {
  let resultList = document.querySelector("div#searchResultList");
  clearChilds(resultList);
}

function getSearchEndpoint() {
  return getSoldierEndpoint + "/search";
}