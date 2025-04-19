<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Flowers shop</title>

    <!-- Стили -->
    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="stylesheet" href="/static/css/footer.css">
    <link rel="stylesheet" href="/static/css/navbar.css">
    <link rel="stylesheet" href="/static/css/profile.css">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">
</head>
<body>

<!-- Навбар -->
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
            <li class="nav-item">
                <a class="nav-link mx-2" style="color:white" href="/cart">
                    Корзина
                    <#assign totalQuantity = 0>
                    <#if user?? && user.cart??>
                        <#list user.cart.cartItems as item>
                            <#assign totalQuantity += item.quantity>
                        </#list>
                    </#if>
                    <span class="badge badge-light ml-1" style="${(totalQuantity == 0)?string('display:none;', '')}">
    ${totalQuantity}
</span>
                </a>
            </li>
            <li class="nav-item"><a class="nav-link mx-2 active" style="color:white" href="/info">О нас</a></li>
            <li class="nav-item"><a class="nav-link mx-2 active" style="color:white" href="/chat">Ассистент</a></li>
        </ul>

        <#if user.email??>
            <div class="dropdown text-end">
                <img src="/static/images/avatar.png" alt="avatar" width="32" height="32" class="rounded-circle">
            </div>
        <#else>
            <button class="btn btn-light my-2 my-sm-0" type="button" onclick="window.location.href = '/login'">
                Войти
            </button>
        </#if>
    </div>
</nav>

<!-- Контент -->
<div class="container my-4">
    <h1 class="text-center">Личный кабинет</h1>
    <hr>

    <div class="text-center">
        <img src="/static/images/avatar.png" style="border:2px solid black; border-radius: 20px" height="120" />
        <br><br>
        <h2>${user.name}</h2>
        <p>Электронная почта: <b>${user.email}</b></p>
        <p>Номер телефона: <b>${user.phoneNumber}</b></p>
        <br>

        <#if user.isAdmin()>
            <a class="btn btn-dark mb-3" style="width: 80%" href="/admin">Панель администратора</a><br>
            <a class="btn btn-purple mb-3" style="width: 80%;" href="/my/products">Мои цветы</a>
        </#if>

        <!-- Кнопка выхода -->
        <form class="formprof" action="/logout" method="post">
            <input type="hidden" name="_csrf" value="${_csrf.token}"/>
            <button type="submit" style="width: 100%" class="btn btn-danger mb-4">Выйти из аккаунта</button>
        </form>
    </div>

    <!-- Мои Заказы -->
    <div class="mb-2">
        <h2 class="profile-orders-header fw-bold">Мои Заказы</h2>

        <div class="orders-scroll-container">
            <div class="row g-4" id="productsContainer"></div>

            <!-- Заглушка, если заказов нет -->
            <div id="noProductsPlaceholder" class="text-center py-5 rounded d-none">
                <i class="fas fa-book-open fa-3x text-muted mb-3"></i>
                <h4>Вы еще не оформили ни одного заказа</h4>
                <p class="text-muted">Найдите подходящий товар!</p>
                <a href="/" class="btn btn-purple mt-2">Перейти к товарам</a>
            </div>
        </div>
    </div>
</div>

<!-- Футер -->
<footer class="footer">
    <ul class="social-icon">
        <li class="social-icon__item"><a class="social-icon__link" href="https://vk.com/hotway22"><ion-icon name="logo-vk"></ion-icon></a></li>
        <li class="social-icon__item"><a class="social-icon__link" href="https://wa.me/79877381210"><ion-icon name="logo-whatsapp"></ion-icon></a></li>
        <li class="social-icon__item"><a class="social-icon__link" href="http://instagram.com/ilya.alekseev"><ion-icon name="logo-instagram"></ion-icon></a></li>
        <li class="social-icon__item"><a class="social-icon__link" href="#"><ion-icon name="logo-facebook"></ion-icon></a></li>
    </ul>
    <p>&copy;2025 Алексеев Илья ИКБО-10-21 | Все права зарезервированы</p>
</footer>

<!-- Скрипты -->
<script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
<script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
<script>
    var currentUserId = "${user.id}";
    var csrfHeader = "${_csrf.headerName}";
    var csrfToken = "${_csrf.token}";
</script>
<script src="/scripts/profileOrders.js"></script>
</body>
</html>