<#import "/spring.ftl" as spring />

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Flowers shop</title>

    <!-- Bootstrap -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">

    <!-- Custom Styles -->
    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="stylesheet" href="/static/css/footer.css">
    <link rel="stylesheet" href="/static/css/navbar.css">
    <link rel="stylesheet" href="/static/css/product-info.css">
</head>
<body class="d-flex flex-column min-vh-100">

<nav class="navbar navbar-expand-lg navbar-dark my">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">Flowers shop</a>
        <input id="menu-toggle" type="checkbox">
        <label class="menu-button-container" for="menu-toggle"><div class="menu-button"></div></label>
        <ul class="menu">
            <li><a class="nav-link mx-2" href="/">Каталог</a></li>
            <li>
                <a class="nav-link mx-2" href="/cart">Корзина
                    <#if user?? && user.cart?? && user.cart.cartItems?size gt 0>
                        <#assign totalQuantity = 0>
                        <#list user.cart.cartItems as item>
                            <#assign totalQuantity += item.quantity>
                        </#list>
                        <span class="badge badge-light ml-1">${totalQuantity}</span>
                    </#if>
                </a>
            </li>
            <li><a class="nav-link mx-2" href="/info">О нас</a></li>
            <li><a class="nav-link mx-2" href="/chat">Ассистент</a></li>
            <li><a class="nav-link mx-2" href="/profile">Профиль</a></li>
        </ul>
        <#if user.email??>
            <img src="/static/images/avatar.png" width="32" height="32" class="rounded-circle">
        <#else>
            <button class="btn btn-light" onclick="location.href='/login'">Войти</button>
        </#if>
    </div>
</nav>

<main class="flex-grow-1">
    <div id="myCarousel" class="carousel slide product-carousel" data-ride="carousel">
        <ol class="carousel-indicators">
            <#list images as image>
                <li data-target="#myCarousel" data-slide-to="${image?index}" class="<#if image.previewImage>active</#if>"></li>
            </#list>
        </ol>
        <div class="carousel-inner">
            <#list images as image>
                <div class="carousel-item <#if image.previewImage>active</#if>">
                    <img class="d-block w-100 product-img" src="/images/${image.id}" alt="product">
                </div>
            </#list>
        </div>
        <a class="carousel-control-prev" href="#myCarousel" role="button" data-slide="prev">
            <span class="carousel-control-prev-icon custom-icon" aria-hidden="true"></span>
        </a>
        <a class="carousel-control-next" href="#myCarousel" role="button" data-slide="next">
            <span class="carousel-control-next-icon custom-icon" aria-hidden="true"></span>
        </a>
    </div>

    <div class="container text-center mt-4">
        <h1>${product.title} | ${product.price} ₽</h1>
        <h5>${product.description}</h5>
        <form action="/cart/add/${product.id}" method="post">
            <input type="hidden" name="_csrf" value="${_csrf.token!}"/>
            <input type="hidden" name="redirectUrl" value="/product/${product.id}">
            <button type="submit" class="btn btn-purple">
                Добавить в корзину
            </button>
        </form>
        <p style="margin-top: 5%">Имя продавца: <b>${authorProduct.name}</b></p>
        <p>Номер телефона продавца: <b>${authorProduct.phoneNumber}</b></p>
        <p> Профиль продавца:
            <a href="/user/${authorProduct.id}">
                 ${authorProduct.name}
            </a>
        </p>
    </div>
</main>

<footer class="footer mt-auto">
    <ul class="social-icon">
        <li><a class="social-icon__link" href="https://vk.com/hotway22"><ion-icon name="logo-vk"></ion-icon></a></li>
        <li><a class="social-icon__link" href="https://wa.me/79877381210"><ion-icon name="logo-whatsapp"></ion-icon></a></li>
        <li><a class="social-icon__link" href="http://instagram.com/ilya.alekseev"><ion-icon name="logo-instagram"></ion-icon></a></li>
        <li><a class="social-icon__link" href="#"><ion-icon name="logo-facebook"></ion-icon></a></li>
    </ul>
    <p>&copy;2025 Алексеев Илья ИКБО-10-21 | Все права зарезервированы</p>
</footer>

<!-- Скрипты -->
<script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
<script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
</body>
</html>
