document.addEventListener("DOMContentLoaded", function () {
    // Используем переменную, определённую в шаблоне
    const userId = currentUserId;

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

            if (!orders || orders.length === 0) {
                placeholder.classList.remove("d-none"); // Показать заглушку, если заказов нет
                return;
            }

            console.log("Полученные заказы:", orders);

            orders.forEach(order => {
                let productsHtml = "";
                if (order.orderProducts && order.orderProducts.length > 0) {
                    order.orderProducts.forEach(op => {
                        productsHtml += `<li>Продукт ID: ${op.productId}, Количество: ${op.quantity}</li>`;
                    });
                } else {
                    productsHtml = `<li>Нет товаров в заказе</li>`;
                }

                const card = document.createElement("div");
                card.className = "col-md-6 col-lg-4";
                card.innerHTML = `
                <div class="card h-100 shadow-sm">
                    <div class="card-body">
                        <h5 class="card-title">Заказ №${order.id}</h5>
                        <p class="card-text">Статус: ${order.status}</p>
                        <p class="card-text">Дата заказа: ${order.createdAt}</p>
                        <p class="card-text">Товары:</p>
                        <ul>
                            ${productsHtml}
                        </ul>
                    </div>
                    <div class="card-footer bg-transparent d-flex justify-content-end">
                        <a href="/orders/${order.id}" class="btn btn-sm btn-outline-primary">Подробнее</a>
                    </div>
                </div>
            `;
                container.appendChild(card);
            });
        })
        .catch(error => {
            console.error("Ошибка загрузки заказов:", error);
        });
});
