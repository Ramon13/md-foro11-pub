const soldierRegisterForm = document.querySelector("div#soldierRegister form");
soldierRegisterForm.onsubmit = function(event) {
  event.preventDefault();
  
  removeErrorNodes();
  removeClassErrors();
  
  save(getSoldier(), saveSoldierEndpoint);
  
  return false;
}

function save(soldier, endpoint) {
  const xhr = new XMLHttpRequest();
  xhr.open(POST_METHOD, endpoint, true);
  xhr.setRequestHeader(requestHeader.contentType, JSON_CONTENT_TYPE);
  
  xhr.send( JSON.stringify(soldier) );
  
  xhr.onreadystatechange = function() {
    if (xhr.readyState == XMLHttpRequest.DONE) {
      if (xhr.status == HTTP_CREATED) {
        //showSuccessMessage();
        //clearForm();
      
      }else if (xhr.status == HTTP_UNPROCESSABLE_ENTITY) {
        const validationErrors = JSON.parse(xhr.responseText);
        showErrors(validationErrors);
      
      }else{
        //alert("There was an internal server error. Please try again later");
      }
    }
  }
}

function showErrors(validationErrors) {
  let fieldName;
  let errorMessage;
  
  for (let i = 0; i < validationErrors.length; i++) {
    fieldName = validationErrors[i].fieldName;
    errorMessage = validationErrors[i].errorMessage;
    
    const inputNode = soldierRegisterForm.querySelector("." + fieldName);
    const errorNode = getErrorMessage(errorMessage);
    
    inputNode.classList.add("error");
    inputNode.parentNode.prepend(errorNode);
  }
}

function showSaveSoldierSnackbar(){
  const snackbar = document.querySelector("div#snackbar");
  snackbar.textContent = SAVING_NEW_SOLDIER;
  showSnackbar(snackbar);
}

function getSoldier() {
  let soldier = {
    name: soldierRegister.querySelector("input[type=text].name").value,
    militaryOrganization: { id: soldierRegister.querySelector("select.militaryOrganization").value },
    militaryRank: { id: soldierRegister.querySelector("select.militaryRank").value },
    phone: soldierRegister.querySelector("input[type=text].phone").value,
    email: soldierRegister.querySelector("input[type=text].email").value
  };
  
  return soldier;
}

