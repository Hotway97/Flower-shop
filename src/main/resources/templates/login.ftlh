<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="stylesheet" href="/static/css/footer.css">
    <link rel="stylesheet" href="/static/css/navbar.css">
    <link rel="stylesheet" href="/static/css/login.css">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">
    <title>Вход | Flowers shop</title>
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
            <li class="nav-item1"><a class="nav-link mx-2 active" href="/">Каталог</a></li>
            <li class="nav-item"><a class="nav-link mx-2" href="/cart">Корзина</a></li>
            <li class="nav-item1"><a class="nav-link mx-2 active" href="/info">О нас</a></li>
            <li class="nav-item1"><a class="nav-link mx-2 active" href="/chat">Ассистент</a></li>
            <li class="nav-item"><a class="nav-link mx-2" href="/profile">Профиль</a></li>
        </ul>
        <#if user?? && user.email??>
            <div class="dropdown text-end">
                <img src="/static/images/avatar.png" alt="mdo" width="32" height="32" class="rounded-circle">
            </div>
        </#if>
    </div>
</nav>

<main class="login-container">
    <div class="login-box">
        <h2>Авторизация</h2>
        <form action="/login" method="post">
            <div class="form-group">
                <label for="email">Электронная почта</label>
                <input type="email" class="form-control <#if error??>is-invalid</#if>" id="email" name="username" required>
                <#if error??>
                    <div class="invalid-feedback">Неверная электронная почта</div>
                </#if>
            </div>
            <div class="form-group">
                <label for="password">Пароль</label>
                <input type="password" class="form-control <#if error??>is-invalid</#if>" id="password" name="password" required>
                <#if error??>
                    <div class="invalid-feedback">Неверный пароль</div>
                </#if>
            </div>

            <#if _csrf??>
                <input type="hidden" name="_csrf" value="${_csrf.token}">
            </#if>

            <button type="submit" class="btn login-btn">Войти</button>
        </form>
        <hr>
        <p class="text-center">Нет аккаунта? <a href="/registration">Зарегистрируйтесь!</a></p>
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
