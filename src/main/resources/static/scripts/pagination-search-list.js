const pagNext = document.querySelector("#pagNext");
const pagPrev = document.querySelector("#pagPrev");


const selectedPage = document.querySelector("input#selectedPage");
const firstResult = document.querySelector("input#firstResult");
const lastResult = document.querySelector("input#lastResult");
const total = document.querySelector("input#total");

function submitForm(){
  let form = document.querySelector("form#paginationForm");
  form.submit();
}

showPages();

function showPages(){
	const firstResultSpan = document.createElement('span');
	const lastResultSpan = document.createElement('span');
	const totalSpan = document.createElement('span');
	
	firstResultSpan.textContent = firstResult.value;
	lastResultSpan.textContent = lastResult.value;
	totalSpan.textContent = total.value;
	
	const prevPag = document.createElement('input');
	prevPag.id = 'prevPag';
	prevPag.type = 'button';
	prevPag.value = '<';
	prevPag.disabled = true;
	
	if (+firstResult.value > 1)
		prevPag.disabled = false;	 
	
	const nextPag = document.createElement('input');
	nextPag.id = 'nextPag';
	nextPag.type = 'button';
	nextPag.value = '>';
	nextPag.disabled = true;
	
	if (+lastResult.value < +total.value)
		nextPag.disabled = false;
	
	const parentSpan = document.createElement('span');
	parentSpan.append(firstResultSpan);
	parentSpan.append(' - ');
	parentSpan.append(lastResultSpan);
	parentSpan.append(' de ');
	parentSpan.append(totalSpan);
	parentSpan.append(prevPag);
	parentSpan.append(nextPag);
	
	const pagesDiv = document.querySelector("#pages");
	pagesDiv.append(parentSpan);
	
	let currentPage = +selectedPage.value;
	nextPag.addEventListener('click', function(){
	  selectedPage.value = ++currentPage;
		submitForm();
	});
	
	prevPag.addEventListener('click', function(){
		selectedPage.value = --currentPage;
    submitForm();
	});
}
             