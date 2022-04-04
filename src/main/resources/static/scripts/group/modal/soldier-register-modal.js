const snackbar = document.querySelector("div#snackbar");
const registerForm = document.querySelector("div#soldierRegister form");

onSubmitRegisterForm();

function onSubmitRegisterForm() {
  registerForm.onsubmit = function(event) {
    event.preventDefault();
    
    removeErrorNodes();
    removeClassErrors();
    showCreatingSoldierSnackbar(); 
    
    save(getSoldier(), this.action);
    
    hideSnackbar(snackbar);
    return false;
  } 
}

function save(soldier, endpoint) {
  const xhr = new XMLHttpRequest();
  xhr.open(POST_METHOD, endpoint, true);
  xhr.setRequestHeader(requestHeader.contentType, JSON_CONTENT_TYPE);
  
  xhr.send( JSON.stringify(soldier) );
  
  xhr.onreadystatechange = function() {
    if (xhr.readyState == XMLHttpRequest.DONE) {
      
      if (xhr.status == HTTP_CREATED) {
        showCreatedSoldierSnackbar();
        clearInputFields(registerForm);
      
      }else if (xhr.status == HTTP_UNPROCESSABLE_ENTITY) {
        const validationErrors = JSON.parse(xhr.responseText);
        showErrors(validationErrors);
      
      }else{
        alert(INTERNAL_SERVER_ERROR_ALERT);
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
    
    const inputNode = registerForm.querySelector("." + fieldName);
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
    name: registerForm.querySelector("input[type=text].name").value,
    militaryOrganization: { id: registerForm.querySelector("select.militaryOrganization").value },
    militaryRank: { id: registerForm.querySelector("select.militaryRank").value },
    phone: registerForm.querySelector("input[type=text].phone").value,
    email: registerForm.querySelector("input[type=text].email").value
  };
  
  return soldier;
}

function showCreatingSoldierSnackbar(){
  snackbar.textContent = SAVING_NEW_SOLDIER;
  showSnackbar(snackbar);
}

function showCreatedSoldierSnackbar() {
  snackbar.textContent = NEW_SOLDIER_CREATED;
  temporarySnackbar(snackbar);
}