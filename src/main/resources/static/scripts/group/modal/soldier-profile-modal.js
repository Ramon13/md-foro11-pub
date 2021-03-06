"use strict"

const profileModal = document.querySelector("div#soldierProfileModal");
const exclusionList = profileModal.querySelector("div#exclusionList");

onSubmitNewExclusionForm();

function displayProfileModal(soldier) {
  
  displaySoldierInfo(soldier);
  displayExclusions(soldier.exclusions);
  onRemoveExclusion();
  onEditProfile(soldier);
  
  displayModal( profileModal );
  
  profileModal.querySelector("input#soldierId").value = soldier.id;
  profileModal.querySelector("input#startDate").value = getInputStarterDate().toISOString().split('T')[0];
  profileModal.querySelector("input#endDate").value = getInputEndDate().toISOString().split('T')[0];
}

function getInputStarterDate() {
  const currDate = new Date(); 
  
  const nextYearQuarter = getYearQuarter( new Date(currDate.setMonth(currDate.getMonth() + 3)) );
  
  const starterDate = fromYearQuarterToDate(nextYearQuarter);
  return starterDate;
}

function getInputEndDate() {
  const starterDate = getInputStarterDate();
  let endDate = new Date(starterDate.setMonth(starterDate.getMonth() + 3));
  return new Date(endDate - 1);
}

function onSubmitNewExclusionForm() {
  const form = document.querySelector("form#newExclusion")
  form.onsubmit = function(event) {
    event.preventDefault();
    
    let exclusion = {
      soldierId: form.querySelector("input#soldierId").value,
      startDate: form.querySelector("input#startDate").value,
      endDate: form.querySelector("input#endDate").value,
      message: form.querySelector("textarea#message").value,
      endpoint: form.action
    }
    
    removeErrorNodes();
    removeClassErrors();
    saveExclusion(exclusion);    
  }  
}

function onEditProfile(soldier) {
  document.querySelector("a#editProfile").onclick = function() {
    const soldierRegister = document.querySelector("div#soldierRegister");
    displayModal( soldierRegister, 2 );
    
    setEditProfileModalTexts(soldier);
    fillRegisterForm(soldier);
  } 
}

function displaySoldierInfo(soldier) {
  profileModal.querySelector("h1.pageTitle").textContent = soldier.name;
  profileModal.querySelector("#sdId").value = 1;
  profileModal.querySelector("p#omName").textContent = soldier.militaryOrganization.name;
  profileModal.querySelector("p#rankName").textContent = soldier.militaryRank.name;
  profileModal.querySelector("p#soldierPhone").textContent = soldier.phone;
  profileModal.querySelector("p#soldierEmail").textContent = soldier.email;
}

function displayExclusions(exclusions) {
  clearChilds(exclusionList);
  
  if (exclusions.length > 0){
    
    exclusions.forEach(exclusion => {
      addExclusion(exclusion);    
    });  
         
  }else {
    displayEmptyListMessage();
  }
}

function saveExclusion(exclusion) {
  const xhr = new XMLHttpRequest();
  
  xhr.open(POST_METHOD, exclusion.endpoint, true);
  xhr.setRequestHeader(requestHeader.contentType, JSON_CONTENT_TYPE);
  
  xhr.send( JSON.stringify(exclusion) );
  
  xhr.onreadystatechange = function() {
    if (xhr.readyState == XMLHttpRequest.DONE) {
      
      if (xhr.status == HTTP_CREATED) {
        const exclusion = JSON.parse(xhr.responseText);
        addExclusion(exclusion);
      
      }else if (xhr.status == HTTP_UNPROCESSABLE_ENTITY) {
        const validationErrors = JSON.parse(xhr.responseText);
        showErrors(validationErrors);
      }else{
        alert(INTERNAL_SERVER_ERROR_ALERT);
      }
        
    }
  }
}

function onRemoveExclusion() {
  exclusionList.querySelectorAll("img.remove-exclusion").forEach(removeImage => {
    removeImage.onclick = function() {
      const exclusion = {
        id: removeImage.dataset.exclusionid,
        
        getEndpoint() {
          return removeExclusionEndpoint + "/" + this.id;
        },
        
        removeTuple() {
          removeImage.closest(".app-list").remove();
        }
      }
      
      if ( window.confirm(DELETE_EXCLUSION_CONFIRMATION_MESSAGE) ) removeExclusion(exclusion) ;
    }
  });
}

function removeExclusion(exclusion) {
  const xhr = new XMLHttpRequest();

  xhr.open(DELETE_METHOD, exclusion.getEndpoint(), true);
  xhr.send();
  
  xhr.onreadystatechange = function() {
    if (xhr.readyState == XMLHttpRequest.DONE) {
      
      if (xhr.status == HTTP_NO_CONTENT) {
         exclusion.removeTuple();
      }else{
        alert(INTERNAL_SERVER_ERROR_ALERT);
      }
    }
  }
}

function addExclusion(exclusion) {
  hideEmptyMessageIfExists();
  
  const div = document.createElement("div");
  div.classList.add("app-list");
  
  const listHeader = document.createElement("p");
  listHeader.classList.add("list-header");
  listHeader.textContent = exclusion.periodAsText;
  div.append(listHeader);
  
  const listInfo = document.createElement("p");
  listInfo.classList.add("list-info");
  listInfo.textContent = exclusion.message;
  div.append(listInfo);
  
   
  const removeImage = new Image;
  removeImage.classList.add("remove-exclusion");
  removeImage.dataset.exclusionid = exclusion.id;
  div.prepend(removeImage);
  removeImage.src = removeExclusionImgSrc;
  
  exclusionList.prepend(div);
}

function displayEmptyListMessage() {
  const div = document.createElement("div");
  div.classList.add("empty-list");
  
  const span = document.createElement("span");
  span.textContent = SOLDIER_HAS_NO_EXCLUSION;
  div.append(span);
  
  exclusionList.append(div);  
}

function hideEmptyMessageIfExists() {
  if ( exclusionList.querySelector(".empty-list") )
    exclusionList.querySelector(".empty-list").style.display = "none";
}

function showErrors(validationErrors) {
  const form = document.querySelector("form#newExclusion")
  
  validationErrors.forEach(validationError => {
    let inputNode = form.querySelector("#" + validationError.fieldName);
    let errorNode = getErrorMessage( validationError.errorMessage );
    
    inputNode.classList.add("error");
    form.prepend(errorNode);
  });
}