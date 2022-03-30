// Get the <span> element that closes the modal
const spanList = document.querySelectorAll(".close");
for (let i = 0; i < spanList.length; i++){
  spanList[i].onclick = function(){
    closeModal(this.closest(".modal")); 
  }
}

function displayModal(modal){
  clearInputFields(modal);
  setSelectsToDefault(modal);
  modal.style.display = "block";
}

function closeModal(modal){
  modal.style.display = "none";
}

function clearInputFields(modal) {
  const inputs = modal.querySelectorAll("input");
  for (let i = 0; i < inputs.length; i++) inputs[i].value = '';
}

function setSelectsToDefault(modal) {
  const selects = modal.querySelectorAll("select");
  for (let i = 0; i < selects.length; i++) selects[i].options.selectedIndex = 0;
}

/*
// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
}
*/