function accordion(acc){
	for (var i = 0; i < acc.length; i++) {
	  acc[i].addEventListener("click", function() {
	    this.classList.toggle("active");
	    var panel = this.nextElementSibling;
	    if (panel.style.maxHeight) {
	      panel.style.maxHeight = null;
	    } else {
	      panel.style.maxHeight = panel.scrollHeight + "px";
	    } 
	  });
	}
}

function filterSoldiersByName() {
  filterTable("soldiers");
}

function filterOMByName() {
  filterTable("organizationsTb");
}

function filterTable(tableId){
	var input, filter, table, tr, td, i, txtValue;
	  input = document.getElementById("myInput");
	  filter = input.value.toUpperCase();
	  table = document.getElementById(tableId);
	  tr = table.getElementsByTagName("tr");
	  for (i = 0; i < tr.length; i++) {
	    td = tr[i].getElementsByTagName("td")[0];
	    if (td) {
	      txtValue = td.textContent || td.innerText;
	      if (txtValue.toUpperCase().indexOf(filter) > -1) {
	        tr[i].style.display = "";
	      } else {
	        tr[i].style.display = "none";
	      }
	    }       
	  }
}

// Open and send an asynchronous xmlHttpRequest. If the server response is 
// HTTP.OK (200) then call successFunction, else call errorFunction.
function sendAjaxRequest(
	httpMethod,
	action,
	sendContent, 
	successFunction,
	errorFunction,
	contentType){
	
	let httpRequest = new XMLHttpRequest();
	
	httpRequest.onreadystatechange = function(){
		if (httpRequest.readyState === XMLHttpRequest.DONE){
			if (httpRequest.status === 200){
				successFunction(httpRequest.responseText);
			}else if (httpRequest.status === 400){
				errorFunction(httpRequest.responseText);
			}else{
				alert("There was an internal server error. Please try again later");
        errorFunction("There was an internal server error. Please try again later");
			}
		}	
	}
	
	httpRequest.open(httpMethod, action, true);
	httpRequest.setRequestHeader( "Content-type", (contentType || "text/plain;charset=UTF-8") );
  httpRequest.send(sendContent);
}

function includeJs(jsFilePath){
  let js = document.createElement("script");
  
  js.type = "text/javascript";
  js.src = jsFilePath;
  
  document.body.appendChild(js);
}

function validatePass(pass0, pass1){
  return pass0 === pass1;
}

function addListener(element, event, executor){
  if (element != null)
    element.addEventListener(event, executor);
}

function historyPush(){
  let history = JSON.parse(window.localStorage.getItem("appHistory"));
  
  if (history == null)
    history = [];

  let url = window.location.href;
  let historySize = history.length;
  
  if (historySize > 0 && history[historySize - 1] == url)
    return;    
  
  history.push(url);
  window.localStorage.setItem("appHistory", JSON.stringify(history));  
}

function historyPop(){
  let history = JSON.parse(window.localStorage.getItem("appHistory"));
  let popped = history.pop();
  
  window.localStorage.setItem("appHistory", JSON.stringify(history));
  return popped;
}

function toggleName(element, value){
  if (!element.hasAttribute("name")){
    element.toggleAttribute("name");
    element.name = value;
    return;
  } 
  element.toggleAttribute("name");
}

function clearChilds(element) {
  element.innerHTML = "";
}