let currentTab = 0;

function showTab(n) {
	currentTab = n;

  // This function will display the specified tab of the form...
	var tabs = document.getElementsByClassName("tab");
  	tabs[n].style.display = "block";
  //... and fix the Previous/Next buttons:
  	if (n == 0) {
		document.getElementById("prevBtn").style.display = "none";
  	} else {
    	document.getElementById("prevBtn").style.display = "inline";
  	}
  	if (n == (tabs.length - 1)) {
    	document.getElementById("nextBtn").style.display = "none";
  	} else {
		document.getElementById("nextBtn").style.display = "inline";
    	document.getElementById("nextBtn").innerHTML = "Próximo";
  	}
	//... and run a function that will display the correct step indicator:
  	fixStepIndicator(n);
}

function nextPrev(n) {
	// This function will figure out which tab to display
	var tab = document.getElementsByClassName("tab");
	// Exit the function if any field in the current tab is invalid:
  	//if (n == 1 && !validateForm()) return false;
  	// Hide the current tab:
  	tab[currentTab].style.display = "none";
  	// Increase or decrease the current tab by 1:
  	currentTab = +currentTab + n;
  	// if you have reached the end of the form...
  	if (currentTab >= tab.length) {
    	// ... the form gets submitted:
    	document.getElementsByClassName("multipleForm")[0].submit();
    	return false;
  	}
	
  	// Otherwise, display the correct tab:
  	showTab(currentTab);
}

function fixStepIndicator(n){
	var tabs = document.getElementsByClassName("step");
    var i;
    for (i = 0; i < tabs.length; i++){
    	tabs[i].className = tabs[i].className.replace(" active", "");	
    }
    
    tabs[n].className += " active";
}