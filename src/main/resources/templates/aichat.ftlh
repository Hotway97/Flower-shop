<!DOCTYPE html>
<html lang="">
<head>
    <meta charset="UTF-8">
    <title>Персональный помощник</title>
    <link rel="stylesheet" href="/static/css/aistyle.css">
    <link rel="stylesheet" href="/static/css/navbar.css">
    <link rel="stylesheet" href="/static/css/footer.css">
</head>
<body>
<h1>Персональный помощник</h1>

<!-- Кнопка для открытия панели -->
<button id="sidePanelToggle">&#9776;</button>

<!-- Панель с чатами -->
<div id="sidePanel">
    <h2>Чаты</h2>
    <ul id="chatList">
        <!-- Удалены Freemarker-теги, список будет заполняться через JS -->
        <li class="placeholder">Загрузка чатов...</li>
    </ul>
    <button id="createChatBtn">Создать чат</button>
</div>

<div id="chat">
    <div id="response"></div>
</div>

<form id="chatForm" method="post" action="/sendMessage">
    <input type="text" id="userInput" name="userInput" required placeholder="Введите ваш запрос...">
    <button type="submit">Отправить</button>
</form>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="../static/scripts/aiscript.js" defer></script>
</body>
</html>