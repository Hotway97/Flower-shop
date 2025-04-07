<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="ru">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="stylesheet" href="/static/css/footer.css">
    <link rel="stylesheet" href="/static/css/navbar.css">
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">
    <title>Flowers shop</title>
    <style>
        /* Стили для блока заказов */
        .order-card .order-number {
            font-weight: bold;
        }
        .order-card .order-total {
            font-size: 1.1em;
            font-weight: bold;
            margin-top: 10px;
        }
        /* Дополнительные отступы для карточки */
        .order-card {
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
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

            <li class="nav-item">
                <a class="nav-link mx-2" style="color:white" href="/">Каталог</a>
            </li>
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
            <li class="nav-item1">
                <a class="nav-link mx-2 active" style="color:white" aria-current="page" href="/info">О нас</a>
            </li>
            <li class="nav-item1"> <a class="nav-link mx-2 active" style="color:white" aria-current="page" href="/chat">Ассистент</a></li>
        </ul>
        <#if user.email??>
            <div class="dropdown text-end">
                <img src="/static/images/avatar.png" alt="mdo" width="32" height="32" class="rounded-circle">
            </div>
        <#else>
            <button class="btn btn-light my-2 my-sm-0" type="button"
                    onclick="window.location.href = '/login'">
                Войти
            </button>
        </#if>
    </div>
</nav>
<div class="container mt-4"></div>
<h1 style="text-align: center">Личный кабинет</h1>
<hr>
<br>
<div style="text-align: center; height:100%">
    <img src="/static/images/avatar.png" style="border:2px solid black; border-radius: 20px" height="120">
    <br><br>
    <h2>${user.name}</h2>
    <br>
    Электронная почта: <b>${user.email}</b>
    <br>
    Номер телефона: <b>${user.phoneNumber}</b>
    <br><br>

    <!-- Мои Заказы -->
    <div class="mb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold">Мои Заказы</h2>
            <a href="/" class="btn btn-outline-primary">Искать товары</a>
        </div>

        <!-- Контейнер для карточек заказов -->
        <div class="row g-4" id="productsContainer"></div>

        <!-- Заглушка, если заказов нет -->
        <div id="noProductsPlaceholder" class="text-center py-5 bg-light rounded d-none">
            <i class="fas fa-book-open fa-3x text-muted mb-3"></i>
            <h4>Вы еще не оформили ни одного заказа</h4>
            <p class="text-muted">Найдите подходящий товар!</p>
            <a href="/" class="btn btn-primary mt-2">Перейти к товарам</a>
        </div>
    </div>
    <br>
    <#if user.isAdmin()>
        <a class="btn btn-dark mb-3" style="text-align: center; width: 80%" href="/admin">
            Панель администратора
        </a><br>
    </#if>
    <#if user.isAdmin()>
        <a class="btn btn-dark mb-3" style="text-align: center; background:#7b63f4; width: 80%;" href="/my/products">
            Мои цветы
        </a>
    </#if>

    <form class="formprof" action="/logout" method="post">
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
        <button type="submit" style="text-align: center; width: 100%" class="btn btn-danger mb-2">
            Выйти из аккаунта
        </button>
    </form>
</div>
<footer class="footer">
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
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
        integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
        integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
        crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"
        integrity="sha384-smHYKdLADwkXOn1EmN1qk/HfnUcbVRZyYmZ4qpPea6sjB/pTJ0euyQp0Mk8ck+5T"
        crossorigin="anonymous"></script>
<!-- Перед подключением внешнего JS устанавливаем глобальную переменную с user.id -->
<script>
    var currentUserId = "${user.id}";
</script>
<script src="/scripts/profileOrders.js"></script>
</body>
</html>