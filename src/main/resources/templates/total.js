let cards = document.getElementsByName('card-text');
function calcCartPrice() {
	const cartWrapper = document.querySelector('.card-body');
	const priceElements = cartWrapper.querySelectorAll('.card-price');
	const totalPriceEl = document.querySelector('.total-price');


	// Общая стоимость товаров
	let priceTotal = 0;

	// Обходим все блоки с ценами в корзине
	priceElements.forEach(function (item) {
		// Находим количество товара
		const amountEl = item.closest('.card-body').querySelector('[data-counter]');
		// Добавляем стоимость товара в общую стоимость (кол-во * цену)
		priceTotal += parseInt(item.innerText);
	});

	// Отображаем цену на странице
	totalPriceEl.innerText = priceTotal;
}