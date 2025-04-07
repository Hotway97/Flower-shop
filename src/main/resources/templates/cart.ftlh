<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/static/css/footer.css">
    <link rel="stylesheet" href="/static/css/navbar.css">
    <link rel="stylesheet" href="/static/css/cart.css">

    <title>Корзина | Flowers shop</title>
</head>
<body data-user-id="${user.id}">
<nav class="navbar navbar-expand-lg navbar-dark my">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">
            <span class="navbar-brand mb-0 h1">Flowers shop</span>
        </a>
        <input id="menu-toggle" type="checkbox">
        <label class="menu-button-container" for="menu-toggle">
            <div class="menu-button"></div>
        </label>
        <ul class="menu">
            <li class="nav-item"><a class="nav-link mx-2" style="color:white" href="/">Каталог</a></li>
            <li class="nav-item1"><a class="nav-link mx-2 active" style="color:white" href="/info">О нас</a></li>
            <li class="nav-item1"><a class="nav-link mx-2 active" style="color:white" aria-current="page" href="/chat">Ассистент</a></li>
            <li class="nav-item"><a class="nav-link mx-2" style="color:white" href="/profile">Профиль</a></li>
        </ul>
        <#if user?? && user.email??>
            <div class="dropdown text-end">
                <#if user.avatar??>
                    <img src="/images/${user.avatar.id}" alt="Аватар" width="32" height="32" class="rounded-circle">
                <#else>
                    <img src="/static/images/avatar.png" alt="Аватар" width="32" height="32" class="rounded-circle">
                </#if>
            </div>
        <#else>
            <button class="btn btn-light my-2 my-sm-0" onclick="window.location.href = '/login'">Войти</button>
        </#if>
    </div>
</nav>

<main class="flex-grow-1 d-flex flex-column">
    <div class="container mt-4 mb-5">
        <h1 class="text-center mb-4">Ваша корзина</h1>

        <#if user?? && user.cart?? && user.cart.cartItems?has_content>
            <div class="card shadow-sm mb-4">
                <div class="card-header text-dark bg-white">
                    <h5 class="text-center mb-0">Товары</h5>
                </div>
                <div class="card-body p-0">
                    <#list user.cart.cartItems as item>
                        <div class="cart-item" data-cartitem-id="${item.id}" data-product-id="${item.product.id}">
                            <a href="/product/${item.product.id}" class="mr-3">
                                <img src="/images/${item.product.previewImageId}" alt="${item.product.title}">
                            </a>
                            <div class="cart-item-details">
                                <h5 class="mb-1">${item.product.title}</h5>
                                <p class="mb-1 text-muted">${item.product.description}</p>
                                <h5 class="mb-1">${item.product.price} ₽</h5>
                            </div>
                            <div class="cart-item-quantity">
                                <button class="btn btn-outline-secondary btn-minus" data-cartitem-id="${item.id}" data-product-id="${item.product.id}">–</button>
                                <span class="quantity" data-cartitem-id="${item.id}">${item.quantity}</span>
                                <button class="btn btn-outline-secondary btn-plus" data-cartitem-id="${item.id}" data-product-id="${item.product.id}">+</button>
                            </div>
                            <div class="remove-all-btn" data-url="/cart/item/removeAll/${item.id}">
                                <button type="button" class="btn btn-outline-danger">
                                    <ion-icon name="trash-outline"></ion-icon>
                                </button>
                            </div>
                        </div>
                    </#list>
                </div>
                <div class="card-body">
                    <div class="d-flex justify-content-between mb-3">
                        <h5 class="mb-0">Общая сумма:</h5>
                        <h5 class="mb-0 text-primary" style="color: black !important;">${user.cart.totalPrice} ₽</h5>
                    </div>
                    <button id="enrollBtn" class="btn checkout-btn btn-block btn-lg">Оформить заказ</button>
                </div>
            </div>
        <#else>
            <div class="text-center py-5">
                <div class="empty-cart-icon">
                    <ion-icon name="cart-outline"></ion-icon>
                </div>
                <h3 class="mb-3">Ваша корзина пуста</h3>
                <p class="text-muted mb-4">Добавьте товары, чтобы продолжить покупки</p>
                <a href="/" class="btn btn-primary px-4 py-2">
                    <ion-icon name="arrow-back-outline" class="mr-2"></ion-icon>Вернуться к покупкам
                </a>
            </div>
        </#if>
    </div>
</main>

<footer class="footer mt-auto">
    <ul class="social-icon">
        <li class="social-icon__item"><a class="social-icon__link" href="https://vk.com/hotway22"><ion-icon name="logo-vk"></ion-icon></a></li>
        <li class="social-icon__item"><a class="social-icon__link" href="https://wa.me/79877381210"><ion-icon name="logo-whatsapp"></ion-icon></a></li>
        <li class="social-icon__item"><a class="social-icon__link" href="http://instagram.com/ilya.alekseev"><ion-icon name="logo-instagram"></ion-icon></a></li>
        <li class="social-icon__item"><a class="social-icon__link" href="#"><ion-icon name="logo-facebook"></ion-icon></a></li>
    </ul>
    <p>&copy;2025 Алексеев Илья ИКБО-10-21 | Все права зарезервированы</p>
</footer>

<!-- Модальное окно -->
<div class="modal fade" id="paymentModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header border-0 justify-content-center position-relative">
                <h5 class="modal-title text-center font-weight-bold">Оформление заказа</h5>
                <button type="button" class="modal-close-btn position-absolute" style="top: 1rem; right: 1rem;" data-bs-dismiss="modal" aria-label="Закрыть">
                    <i class="bi bi-x" style="font-size: 1.5rem;"></i>
                </button>
            </div>
            <div class="modal-body">
                <div id="paymentError" class="alert alert-danger d-none"></div>
                <p style="font-weight: bold;" id="modalOrderName">Состав заказа:</p>
                <div id="modalProductList" class="mb-3"></div>
                <div class="d-flex justify-content-between align-items-center mt-4">
                    <p style="font-weight: bold;">Сумма к оплате:</p>
                    <p style="font-weight: bold;" id="modalOrderPrice">${user.cart.totalPrice} ₽</p>
                </div>
                <div class="d-grid gap-2 mt-4">
                    <#if !user?? || !user.email??>
                        <button id="LogInToPayBtn" class="btn checkout-btn btn-block py-2">Войдите, чтобы оплатить</button>
                    <#else>
                        <button id="proceedToPaymentBtn" class="btn checkout-btn btn-block py-2">Перейти к оплате</button>
                    </#if>
                    <button type="button" class="btn btn-outline-secondary btn-block py-2 mt-2" data-bs-dismiss="modal">Отмена</button>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Скрипты -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
<script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
<script src="/scripts/cartDetails.js"></script>
</body>
</html>
