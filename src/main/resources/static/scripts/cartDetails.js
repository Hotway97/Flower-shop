document.addEventListener('DOMContentLoaded', function() {
    // Получаем CSRF-токен и имя заголовка из метатегов
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

    // Обработчик для кнопки оформления заказа
    const enrollBtn = document.getElementById('enrollBtn');
    const paymentModal = new bootstrap.Modal(document.getElementById('paymentModal'));

    if (enrollBtn) {
        enrollBtn.addEventListener('click', function(e) {
            e.preventDefault();
            paymentModal.show();
            document.getElementById('modalOrderName').textContent = 'Ваш заказ';
            document.getElementById('modalOrderPrice').textContent =
                document.querySelector('.text-primary').textContent;
        });
    }

    // Обработчик кнопки "Войдите, чтобы оплатить"
    const logInToPayBtn = document.getElementById('LogInToPayBtn');
    if (logInToPayBtn) {
        logInToPayBtn.addEventListener('click', function() {
            window.location.href = '/login';
        });
    }

    // Обработчик кнопки "Перейти к оплате"
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

            // Получаем userId из <body data-user-id="...">
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
                    console.log('Order created:', response);
                    if (response.paymentUrl) {
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

    // Обработчик для кнопки удаления всех единиц товара
    $(document).on('click', '.remove-all-btn', function(e) {
        e.preventDefault();
        const url = $(this).data('url');
        $.ajax({
            url: url,
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            },
            success: function(response) {
                location.reload();
            },
            error: function(xhr, status, error) {
                console.error('Ошибка при удалении товара:', error);
                alert('Ошибка при удалении товара');
            }
        });
    });

    // Кнопка + для увеличения количества товара
    $(document).on('click', '.btn-plus', function(e) {
        e.preventDefault();
        const cartItemId = $(this).data('cartitem-id');
        $.ajax({
            url: '/cart/item/increase/' + cartItemId,
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            },
            success: function() {
                location.reload();
            },
            error: function(xhr, status, error) {
                console.error('Ошибка при увеличении количества товара:', error);
            }
        });
    });

    // Кнопка – для уменьшения количества товара
    $(document).on('click', '.btn-minus', function(e) {
        e.preventDefault();
        const cartItemId = $(this).data('cartitem-id');
        $.ajax({
            url: '/cart/item/decrease/' + cartItemId,
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            },
            success: function() {
                location.reload();
            },
            error: function(xhr, status, error) {
                console.error('Ошибка при уменьшении количества товара:', error);
            }
        });
    });
});
