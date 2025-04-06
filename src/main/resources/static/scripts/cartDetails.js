document.addEventListener('DOMContentLoaded', function() {
    const enrollBtn = document.getElementById('enrollBtn');
    const paymentModal = new bootstrap.Modal(document.getElementById('paymentModal'));

    if (enrollBtn) {
        enrollBtn.addEventListener('click', function(e) {
            e.preventDefault(); // Предотвращаем действие по умолчанию
            paymentModal.show();

            // Заполняем данные в модальном окне
            document.getElementById('modalCourseName').textContent = 'Ваш заказ';
            document.getElementById('modalCoursePrice').textContent =
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
    // Обработчик кнопки "Перейти к оплате"
    const proceedToPaymentBtn = document.getElementById('proceedToPaymentBtn');
    if (proceedToPaymentBtn) {
        proceedToPaymentBtn.addEventListener('click', function () {
            // Получаем все товары из корзины
            const cartItems = document.querySelectorAll('[data-product-id]');
            const productIds = Array.from(cartItems).map(item => item.dataset.productId);

            // Показываем индикатор загрузки
            const originalText = proceedToPaymentBtn.innerHTML;
            proceedToPaymentBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Обработка...';
            proceedToPaymentBtn.disabled = true;

            // Отправляем запрос на создание заказа
            $.ajax({
                url: '/api/orders',
                method: 'POST',
                contentType: 'application/json',
                headers: {
                    'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
                },
                data: JSON.stringify({
                    status: "pending",
                    product_ids: productIds, // Передаем массив ID товаров
                    user_id: null
                }),
                success: function (response) {
                    // Обработка успешного создания заказа
                    console.log('Order created:', response);

                    // Перенаправление на страницу оплаты
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

                    // Восстанавливаем кнопку
                    proceedToPaymentBtn.innerHTML = originalText;
                    proceedToPaymentBtn.disabled = false;

                    // Показываем ошибку пользователю
                    const errorMessage = xhr.responseJSON && xhr.responseJSON.message
                        ? xhr.responseJSON.message
                        : 'Произошла ошибка при создании заказа';
                    alert(errorMessage);
                }
            });
        });
    }
})