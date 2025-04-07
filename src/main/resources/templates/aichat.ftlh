<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Персональный помощник</title>
    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="stylesheet" href="/static/css/footer.css">
    <link rel="stylesheet" href="/static/css/navbar.css">
    <link rel="stylesheet" href="/static/css/aichat.css">
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
            <li class="nav-item1"><a class="nav-link mx-2 active" href="/">Каталог</a></li>
            <li class="nav-item"><a class="nav-link mx-2" href="/cart">Корзина</a></li>
            <li class="nav-item1"><a class="nav-link mx-2 active" href="/info">О нас</a></li>
            <li class="nav-item1"><a class="nav-link mx-2 active" href="/chat">Ассистент</a></li>
            <li class="nav-item"><a class="nav-link mx-2" href="/profile">Профиль</a></li>
        </ul>
        <#if user?? && user.email??>
            <div class="dropdown text-end">
                <img src="/static/images/avatar.png" alt="avatar" width="32" height="32" class="rounded-circle">
            </div>
        </#if>
    </div>
</nav>

<main class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="mb-0">Персональный помощник</h1>
        <button id="clearChatBtn" class="btn btn-outline-danger">Очистить историю</button>
    </div>

    <div class="row">
        <div class="col-md-4 mb-3">
            <button id="createChatBtn" class="btn btn-primary w-100 mb-3">Создать чат</button>
            <ul id="chatList" class="list-group"></ul>
        </div>
        <div class="col-md-8">
            <div id="response" class="chat-window border rounded p-3 mb-3" style="height: 400px; overflow-y: auto;"></div>
            <form id="chatForm" class="d-flex">
                <input id="userInput" type="text" class="form-control me-2" placeholder="Введите сообщение..." required>
                <button type="submit" class="btn btn-primary">Отправить</button>
            </form>
        </div>
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
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="/scripts/aichat.js"></script>
</body>
</html>
