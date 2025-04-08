<#import "/spring.ftl" as spring />

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="stylesheet" href="/static/css/footer.css">
    <link rel="stylesheet" href="/static/css/navbar.css">
    <link rel="stylesheet" href="/static/css/admin.css">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">
    <title>Flowers shop</title>
</head>
<body style="min-height: 100vh; display: flex; flex-direction: column;">
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
            <li class="nav-item1"><a class="nav-link mx-2 active" style="color:white" href="/info">О нас</a></li>
            <li class="nav-item1"><a class="nav-link mx-2 active" style="color:white" href="/chat">Ассистент</a></li>
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

<main class="flex-grow-1 container py-4">
    <h1 class="text-center mb-4">Панель администратора</h1>
    <hr>

    <div class="table-responsive">
        <table class="table table-hover table-dark" style="color:black; background-color:white; min-width: 768px;">
            <thead>
            <tr>
                <th scope="col">Почта</th>
                <th scope="col">Номер телефона</th>
                <th scope="col">Активность</th>
                <th scope="col">Роли</th>
                <th scope="col">Бан</th>
                <th scope="col">Изменение роли</th>
                <th scope="col">Подробная информация</th>
            </tr>
            </thead>
            <tbody>
            <#list users as user>
                <tr>
                    <td>${user.email}</td>
                    <td>${user.phoneNumber}</td>
                    <td><#if user.active>true<#else>false</#if></td>
                    <td><#list user.roles as role>${role}</#list></td>
                    <td>
                        <form action="/admin/user/ban/${user.id}" method="post">
                            <input type="hidden" name="_csrf" value="${_csrf.token}">
                            <#if user.active>
                                <input type="submit" style="background-color:red" class="btn btn-light" value="Бан">
                            <#else>
                                <input type="submit" style="background-color:#00fe00" class="btn btn-light" value="Разбан">
                            </#if>
                        </form>
                    </td>
                    <td><a href="/admin/user/edit/${user.id}">Изменить роль</a></td>
                    <td><a href="/user/${user.id}">Подробная информация</a></td>
                </tr>
            </#list>
            </tbody>
        </table>
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

<script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
<script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js" crossorigin="anonymous"></script>
</body>
</html>
