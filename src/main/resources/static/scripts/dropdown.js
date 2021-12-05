const dropdown = document.querySelectorAll("img.dropdown");
addDropdownListeners();

function addDropdownListeners(){
	let dropdownContent;
	for (let i = 0; i < dropdown.length; i++){
		dropdown[i].addEventListener('click', function(){
			dropdownContent = dropdown[i].parentNode.querySelector(".dropdown-content");
			dropdownContent.classList.toggle('show');
		});
	}	
}

window.onclick = function(event){
	if (!event.target.matches('.dropdown')){
		for (let i = 0; i < dropdown.length; i++){
			dropdownContent = dropdown[i].parentNode.querySelector(".dropdown-content");
			if (dropdownContent.classList.contains('show')){
				dropdownContent.classList.remove('show');
			}
		}
	}	
}