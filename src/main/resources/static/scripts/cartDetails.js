document.addEventListener('DOMContentLoaded', function () {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

    // 🔁 Если корзина была оплачена — очищаем DOM и отправляем запрос
    if (localStorage.getItem('cartPaid') === 'true') {
        localStorage.removeItem('cartPaid');
        const userId = document.body.dataset.userId;

        $.ajax({
            url: `/cart/clear/${userId}`,
            method: 'POST',
            headers: { [csrfHeader]: csrfToken },
            success: function () {
                clearCartUI();
            },
            error: function (xhr, status, error) {
                console.error('Ошибка при очистке корзины:', error);
            }
        });
    }

    // Модалка для оформления заказа
    const enrollBtn = document.getElementById('enrollBtn');
    const paymentModal = new bootstrap.Modal(document.getElementById('paymentModal'));

    if (enrollBtn) {
        enrollBtn.addEventListener('click', function (e) {
            e.preventDefault();
            paymentModal.show();
            document.getElementById('modalOrderName').textContent = 'Ваш заказ';
            document.getElementById('modalOrderPrice').textContent =
                document.querySelector('.text-primary').textContent;
        });
    }

    const logInToPayBtn = document.getElementById('LogInToPayBtn');
    if (logInToPayBtn) {
        logInToPayBtn.addEventListener('click', function () {
            window.location.href = '/login';
        });
    }

    const proceedToPaymentBtn = document.getElementById('proceedToPaymentBtn');
    if (proceedToPaymentBtn) {
        proceedToPaymentBtn.addEventListener('click', function () {
            const cartItems = document.querySelectorAll('.cart-item');
            let productIds = [];
            cartItems.forEach(item => {
                const productId = item.dataset.productId;
                const quantityElem = item.querySelector('.quantity');
                const quantity = quantityElem ? parseInt(quantityElem.textContent, 10) : 1;
                for (let i = 0; i < quantity; i++) {
                    productIds.push(productId);
                }
            });

            const userId = document.body.dataset.userId;
            const originalText = proceedToPaymentBtn.innerHTML;
            proceedToPaymentBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Обработка...';
            proceedToPaymentBtn.disabled = true;

            $.ajax({
                url: '/api/orders',
                method: 'POST',
                contentType: 'application/json',
                headers: {
                    [csrfHeader]: csrfToken
                },
                data: JSON.stringify({
                    status: "pending",
                    product_ids: productIds,
                    user_id: userId
                }),
                success: function (response) {
                    if (response.paymentUrl) {
                        localStorage.setItem('cartPaid', 'true');
                        window.location.href = response.paymentUrl;
                    }
                },
                error: function (xhr, status, error) {
                    console.error('Ошибка при создании заказа:', {
                        status: status,
                        error: error,
                        response: xhr.responseText
                    });
                    proceedToPaymentBtn.innerHTML = originalText;
                    proceedToPaymentBtn.disabled = false;
                    const errorMessage = xhr.responseJSON && xhr.responseJSON.message
                        ? xhr.responseJSON.message
                        : 'Произошла ошибка при создании заказа';
                    alert(errorMessage);
                }
            });
        });
    }

    // Удаление всех единиц товара
    $(document).on('click', '.remove-all-btn', function (e) {
        e.preventDefault();
        const url = $(this).data('url');
        $.ajax({
            url: url,
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            },
            success: function () {
                location.reload();
            },
            error: function (xhr, status, error) {
                console.error('Ошибка при удалении товара:', error);
                alert('Ошибка при удалении товара');
            }
        });
    });

    // Кнопка +
    $(document).on('click', '.btn-plus', function (e) {
        e.preventDefault();
        const cartItemId = $(this).data('cartitem-id');
        $.ajax({
            url: '/cart/item/increase/' + cartItemId,
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            },
            success: function () {
                location.reload();
            },
            error: function (xhr, status, error) {
                console.error('Ошибка при увеличении количества товара:', error);
            }
        });
    });

    // Кнопка -
    $(document).on('click', '.btn-minus', function (e) {
        e.preventDefault();
        const cartItemId = $(this).data('cartitem-id');
        $.ajax({
            url: '/cart/item/decrease/' + cartItemId,
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            },
            success: function () {
                location.reload();
            },
            error: function (xhr, status, error) {
                console.error('Ошибка при уменьшении количества товара:', error);
            }
        });
    });

    // 👉 Очистка корзины на фронте
    function clearCartUI() {
        document.querySelectorAll('.cart-item').forEach(item => item.remove());

        const totalTextElems = document.querySelectorAll('.text-primary, .text-muted, h5.mb-0.text-primary');
        totalTextElems.forEach(el => el.textContent = '0 ₽');

        const cartHeader = document.querySelector('h1');
        if (cartHeader) cartHeader.textContent = 'Ваша корзина пуста';

        const checkoutSection = document.querySelector('.card.shadow-sm');
        if (checkoutSection) checkoutSection.remove();

        const emptyCartBlock = document.querySelector('.text-center.py-5');
        if (emptyCartBlock) emptyCartBlock.classList.remove('d-none');

        // 🧹 Убираем data-cartitem-id, чтобы кнопки не вызывали ошибки
        document.querySelectorAll('[data-cartitem-id]').forEach(el => {
            el.removeAttribute('data-cartitem-id');
        });
    }
});