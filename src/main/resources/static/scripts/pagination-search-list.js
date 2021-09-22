function showPages(firstResult, lastResult, total, currentPage){
	const firstResultSpan = document.createElement('span');
	const lastResultSpan = document.createElement('span');
	const totalSpan = document.createElement('span');
	
	firstResultSpan.textContent = firstResult;
	lastResultSpan.textContent = lastResult;
	totalSpan.textContent = total;
	
	const prevPag = document.createElement('input');
	prevPag.id = 'prevPag';
	prevPag.type = 'button';
	prevPag.value = '<';
	prevPag.disabled = true;
	
	if (+firstResult > 1)
		prevPag.disabled = false;	 
	
	const nextPag = document.createElement('input');
	nextPag.id = 'nextPag';
	nextPag.type = 'button';
	nextPag.value = '>';
	nextPag.disabled = true;
	
	if (+lastResult < total)
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
	
	nextPag.addEventListener('click', function(){
		++currentPage;
		location.href = paginationURL + "/" + currentPage;
	});
	
	prevPag.addEventListener('click', function(){
		--currentPage;
		location.href = paginationURL + "/" + currentPage;
	});
}
             