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
    <link rel="stylesheet" href="/static/css/user-edit.css">
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
            <li class="nav-item1"><a class="nav-link mx-2 active" style="color:white" href="/info">Каталог</a></li>
            <li class="nav-item"><a class="nav-link mx-2" style="color:white" href="/cart">Корзина</a></li>
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

<main style="display: flex; justify-content: center; align-items: center; flex-direction: column;">
    <div class="edit-wrapper">
        <h1>Редактирование пользователя</h1>
        <h2>${editableUser.name}</h2>
        <form action="/admin/user/edit" method="post" class="edit-form">
            <div class="checkbox-group">
                <#list roles as role>
                    <label class="checkbox-label">
                        <input type="checkbox" name="${role}" ${editableUser.roles?seq_contains(role)?string("checked", "")}>
                        ${role}
                    </label>
                </#list>
            </div>
            <input type="hidden" name="userId" value="${editableUser.id}">
            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <button type="submit" class="btn btn-purple">Сохранить</button>
        </form>
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

<!-- Скрипты -->
<script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
<script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
</body>
</html>
