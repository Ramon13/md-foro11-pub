historyPush();

const newListBtn = document.querySelector("button#newList");
addListener(newListBtn, "click", function(){
  location.href = newEndpoint;
}); 

const dropdownMenu = document.querySelectorAll("div .dropdown-content");

let listId, removeList, duplicateList;
for (let i = 0; i < dropdownMenu.length; i++){
  removeList = dropdownMenu[i].querySelector(".removeList");
  duplicateList = dropdownMenu[i].querySelector(".duplicateList");
  
  removeList.addEventListener("click", function(){
    if (window.confirm( DELETE_DRAW_LIST_MESSAGE )) {
      let form = getPostForm();
      listId = dropdownMenu[i].dataset.listid;  
      form.action = removeEndpoint + listId;
      form.submit();
    }
  });
  
  duplicateList.addEventListener("click", function(){
    let form = getPostForm();
    listId = dropdownMenu[i].dataset.listid;
    form.action = duplicateEndpoint + listId;
    form.submit();
  });
}

function getPostForm() {
  let form = document.createElement("form");
  form.method = "POST";
  form.style.display = "none";
  document.body.appendChild(form);
  
  return form;
}