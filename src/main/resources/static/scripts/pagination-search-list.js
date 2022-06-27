const currentPage = document.querySelector(".page-number").value;

const pagNext = document.querySelector(".next-page");
pagNext.onclick = function(){
  getPage(+currentPage + 1);
}

const pagPrev = document.querySelector(".previous-page");
pagPrev.onclick = function(){
  getPage(+currentPage - 1);
}

const searchInput = document.querySelector(".search-input");
searchInput.addEventListener('keydown', function(event){
  if (event.keyCode == 13){
    event.preventDefault();
    getPage(0);
  }
});

const searchBtn = document.querySelector(".search-btn");
searchBtn.addEventListener('click', function() {
  getPage(0);
});

setupBtns();

function getPage(pageNumber){
  document.querySelector(".page-number").value = pageNumber;
  document.querySelector(".pagination-form").submit();
}

function setupBtns(){
  let counterPages = document.querySelector(".counter").textContent;
  let pages = counterPages.split("de")[0];
  let total = +counterPages.split("de")[1].trim();
  
  let firstPage = +pages.split("-")[0].trim();
  let lastPage = +pages.split("-")[1].trim();
  
  if (lastPage >= total)
    pagNext.disabled = true;
  if (firstPage <= 1)
    pagPrev.disabled = true;
}

function focusSearchBar(){
  searchInput.focus();
}


             