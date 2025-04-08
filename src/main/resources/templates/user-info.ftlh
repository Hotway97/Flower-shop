<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Flowers shop</title>

    <!-- Стили -->
    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="stylesheet" href="/static/css/footer.css">
    <link rel="stylesheet" href="/static/css/navbar.css">
    <link rel="stylesheet" href="/static/css/user-info.css">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark my">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">
            <span class="navbar-brand mb-0 h1">Flowers shop</span>
        </a>
        <input id="menu-toggle" type="checkbox">
        <label class='menu-button-container' for="menu-toggle">
            <div class='menu-button'></div>
        </label>
        <ul class="menu">
            <li class="nav-item"><a class="nav-link mx-2" style="color:white" href="/">Каталог</a></li>
            <li class="nav-item">
                <a class="nav-link mx-2" style="color:white" href="/cart">
                    Корзина
                    <#if user?? && user.cart?? && user.cart.cartItems?size gt 0>
                        <#assign totalQuantity = 0>
                        <#list user.cart.cartItems as item>
                            <#assign totalQuantity += item.quantity>
                        </#list>
                        <span class="badge badge-light ml-1">${totalQuantity}</span>
                    </#if>
                </a>
            </li>
            <li class="nav-item"><a class="nav-link mx-2 active" style="color:white" href="/info">О нас</a></li>
            <li class="nav-item"><a class="nav-link mx-2 active" style="color:white" href="/chat">Ассистент</a></li>
            <li class="nav-item"><a class="nav-link mx-2" style="color:white" href="/profile">Профиль</a></li>
        </ul>
        <#if user.email??>
            <div class="dropdown text-end">
                <img src="/static/images/avatar.png" alt="mdo" width="32" height="32" class="rounded-circle">
            </div>
        <#else>
            <button class="btn btn-light my-2 my-sm-0" type="button" onclick="window.location.href = '/login'">Войти</button>
        </#if>
    </div>
</nav>

<main class="user-info-wrapper">
    <section class="user-header text-center">
        <h1>Информация о продавце</h1>
        <img src="/static/images/avatar.png" alt="Аватар" class="seller-avatar" height="120">
        <h2>${user.name}</h2>
        <p>Номер телефона: <b>${user.phoneNumber}</b></p>
    </section>

    <section class="seller-products container">
        <h2 class="text-center mt-5 mb-4">Цветы продавца</h2>
        <div class="row">
            <#list products as product>
                <div class="col-sm-12 col-md-6 col-lg-4 mb-4 d-flex">
                    <a href="/product/${product.id}" class="product-link w-100">
                        <div class="card text-white bg-dark h-100">
                            <img src="/images/${product.previewImageId}" class="card-img-top" alt="product">
                            <div class="card-body d-flex flex-column">
                                <h5 class="card-title text-center">${product.title}</h5>
                                <p class="card-price text-center">${product.price} ₽</p>
                                <p class="card-text">${product.description}</p>
                                <form action="/cart/add/${product.id}" method="post" class="mt-auto">
                                    <input type="hidden" name="_csrf" value="${_csrf.token!}">
                                    <input type="hidden" name="redirectUrl" value="/user/${user.id}">
                                    <button type="submit" class="btn btn-purple mt-3 w-100">
                                        Добавить в корзину
                                    </button>
                                </form>
                            </div>
                        </div>
                    </a>
                </div>
            <#else>
                <h3 class="text-center w-100">Товары отсутствуют</h3>
            </#list>
        </div>
    </section>
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

<script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
<script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
</body>
</html>
