document.addEventListener("DOMContentLoaded", function () {
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
                placeholder.classList.remove("d-none");
                return;
            }

            orders.forEach(order => {
                let productsHtml = "";
                if (order.orderProducts && order.orderProducts.length > 0) {
                    productsHtml = "<ol>";
                    order.orderProducts.forEach(op => {
                        productsHtml += `<li>Наименование: ${op.productTitle}, Цена: ${op.productPrice}, Количество: ${op.quantity}</li>`;
                    });
                    productsHtml += "</ol>";
                } else {
                    productsHtml = `<p>Нет товаров в заказе</p>`;
                }

                const card = document.createElement("div");
                card.className = "col-md-6 col-lg-4 order-card";
                card.innerHTML = `
                    <div class="card h-100 shadow-sm">
                        <div class="card-body">
                            <p class="order-number">Заказ №${order.id}</p>
                            <p class="card-text">Статус: ${order.status}</p>
                            <p class="card-text">Дата заказа: ${new Date(order.createdAt).toLocaleString()}</p>
                            <p class="order-item">Товары:</p>
                            ${productsHtml}
                            <p class="order-total">Сумма заказа: ${order.totalAmount} руб.</p>
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
