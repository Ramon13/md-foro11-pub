accordion(document.getElementsByClassName("accordion"));
      
const auditorshipSelect = document.querySelector('select#auditorship');
auditorshipSelect.addEventListener('change', function(){
  let selectedAuditorship = auditorshipSelect.options[auditorshipSelect.selectedIndex];
  location.href = selectedAuditorship.dataset.url;
});

const editDrawList = document.querySelectorAll("a.edit-draw");
for (let i = 0; i < editDrawList.length; i++){
  editDrawList[i].addEventListener('click', function(){
    location.href = this.dataset.url;
  });
}