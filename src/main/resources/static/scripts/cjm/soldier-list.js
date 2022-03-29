focusSearchBar();
returnPage();

const soldiers = document.querySelectorAll("div.soldier-info");
for (let i = 0; i < soldiers.length; i++) {
  soldiers[i].onclick = function() {
    location.href = this.dataset.sdprofile;
  }
}