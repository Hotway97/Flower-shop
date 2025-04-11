document.addEventListener("DOMContentLoaded", function () {
    const userId = currentUserId;

    // Функция для обновления счетчика корзины
    const updateCartCounter = () => {
        fetch('/cart/count', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            }
        })
            .then(response => {
                if (!response.ok) throw new Error('Ошибка получения количества товаров');
                return response.json();
            })
            .then(data => {
                const counterElement = document.querySelector('.nav-item [href="/cart"] .badge');
                if (counterElement) {
                    counterElement.textContent = data.count;
                    counterElement.style.display = data.count > 0 ? 'inline-block' : 'none';
                }
            })
            .catch(error => {
                console.error('Ошибка обновления счетчика корзины:', error);
            });
    };

    // Обновляем счетчик при загрузке страницы
    updateCartCounter();

    // Загружаем заказы пользователя
    fetch('/api/orders/user/' + userId, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    })
        .then(response => {
            if (!response.ok) throw new Error('Ошибка загрузки заказов');
            return response.json();
        })
        .then(orders => {
            const container = document.getElementById("productsContainer");
            const placeholder = document.getElementById("noProductsPlaceholder");

            // Очищаем контейнер перед добавлением новых элементов
            if (container) container.innerHTML = '';

            if (!orders || orders.length === 0) {
                if (placeholder) placeholder.classList.remove("d-none");
                return;
            }

            // Скрываем заглушку, если есть заказы
            if (placeholder) placeholder.classList.add("d-none");

            // Добавляем каждый заказ в контейнер
            orders.forEach(order => {
                let productsHtml = "";
                if (order.orderProducts && order.orderProducts.length > 0) {
                    productsHtml = "<ol class='order-products-list'>";
                    order.orderProducts.forEach(op => {
                        productsHtml += `
                        <li>
                            <span class="product-title">${op.productTitle}</span>
                            <span class="product-price">${op.productPrice} руб.</span>
                            <span class="product-quantity">× ${op.quantity}</span>
                        </li>
                    `;
                    });
                    productsHtml += "</ol>";
                } else {
                    productsHtml = `<p class="no-products">Нет товаров в заказе</p>`;
                }

                const card = document.createElement("div");
                card.className = "col-md-6 col-lg-4 order-card mb-4";
                card.innerHTML = `
                <div class="card h-100 shadow-sm">
                    <div class="card-body">
                        <h5 class="order-number">Заказ №${order.id}</h5>
                        <p class="order-status"><strong>Статус:</strong> ${order.status}</p>
                        <p class="order-date"><strong>Дата:</strong> ${new Date(order.createdAt).toLocaleString()}</p>
                        <div class="order-items">
                            <h6>Товары:</h6>
                            ${productsHtml}
                        </div>
                        <div class="order-total mt-3">
                            <strong>Итого:</strong> ${order.totalAmount} руб.
                        </div>
                    </div>
                </div>
            `;

                if (container) container.appendChild(card);
            });

            // Обновляем счетчик после загрузки заказов
            updateCartCounter();
        })
        .catch(error => {
            console.error("Ошибка загрузки заказов:", error);
            const placeholder = document.getElementById("noProductsPlaceholder");
            if (placeholder) {
                placeholder.classList.remove("d-none");
                placeholder.innerHTML = `
                <i class="fas fa-exclamation-triangle fa-3x text-warning mb-3"></i>
                <h4>Ошибка загрузки заказов</h4>
                <p class="text-muted">Попробуйте обновить страницу</p>
                <button onclick="location.reload()" class="btn btn-primary mt-2">Обновить</button>
            `;
            }
        });

    // Обновляем счетчик при возвращении на вкладку
    document.addEventListener('visibilitychange', function() {
        if (document.visibilityState === 'visible') {
            updateCartCounter();
        }
    });

    // Опционально: периодическое обновление (каждые 30 секунд)
});