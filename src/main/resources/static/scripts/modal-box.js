// Get the <span> element that closes the modal
const spanList = document.querySelectorAll(".close");
for (let i = 0; i < spanList.length; i++){
  spanList[i].onclick = function(){
    closeModal(this.closest(".modal")); 
  }
}

function displayModal(modal){
  clearInputFields(modal);
  modal.style.display = "block";
}

function closeModal(modal){
  modal.style.display = "none";
}

/*
// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
}
*/