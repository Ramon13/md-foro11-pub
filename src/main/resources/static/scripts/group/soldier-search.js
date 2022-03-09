const searchForm = document.querySelector("form#soldierSearch");
searchForm.onsubmit = function() {
  searchSoldier(this);
  return false;
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

function showSearchSoldierResults(responseText) {
  let soldiers = JSON.parse(responseText);
  showFoundSoldiers(soldiers);
}

// param:soldiers A list of soldiers in JSON format
function showFoundSoldiers(soldiers) {
  let foundSoldiers = document.querySelector("div#foundSoldiers");
  clearFoundSoldiers(foundSoldiers);

  let newSoldierInfo;
  for (let i = 0; i < soldiers.length; i++) {
    newSoldierInfo = getSoldierInfo(soldiers[i]);
   
    if (soldiers[i].firstExclusion){
      console.log(soldiers[i]);
      disableSoldierInfo(soldiers[i].firstExclusion, newSoldierInfo);
    }
    
    foundSoldiers.append(newSoldierInfo);
  }
}

function getSoldierInfo(soldier){ 
  let baseSoldierInfo = document.querySelector("div#baseSoldierInfo");
  let foundSoldier = baseSoldierInfo.cloneNode(baseSoldierInfo);
  foundSoldier.id = "";

  foundSoldier.querySelector(".soldier-id").value = soldier.id;
  foundSoldier.querySelector(".list-header").textContent = soldier.idInfoAsText;
  foundSoldier.querySelector(".list-info").textContent = soldier.omandRankAsText;
  foundSoldier.style.display = "block";
  
  foundSoldier.addEventListener('click', function(){
    appendToList(foundSoldier);
    addToLocalStorageList(soldier.id);
  });
  
  return foundSoldier;
}

function appendToList(foundSoldier){
  document.querySelector("div#soldiers").prepend(foundSoldier);
  foundSoldier.querySelector(".remove-soldier").style.display = "block";
}

function addToLocalStorageList(soldierId){
  let selectedSoldiers = JSON.parse( localStorage.getItem("selectedSoldiers") );
  if (!selectedSoldiers)
    selectedSoldiers = new Array();
    
  selectedSoldiers.push(soldierId);
  localStorage.setItem("selectedSoldiers", JSON.stringify(selectedSoldiers));
}

function disableSoldierInfo(errorMsg, soldierInfoDiv){
  let error = getErrorParagraph(errorMsg);
  soldierInfoDiv.append(error);
  soldierInfoDiv.classList.add("disable-div");
}