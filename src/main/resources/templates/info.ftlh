<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Flowers shop</title>

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="stylesheet" href="/static/css/footer.css">
    <link rel="stylesheet" href="/static/css/navbar.css">
    <link rel="stylesheet" href="/static/css/info.css">
</head>
<body class="d-flex flex-column min-vh-100">

<nav class="navbar navbar-expand-lg navbar-dark my">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">
            <span class="navbar-brand mb-0 h1">Flowers shop</span>
        </a>
        <input id="menu-toggle" type="checkbox">
        <label class="menu-button-container" for="menu-toggle"><div class="menu-button"></div></label>
        <ul class="menu">
            <li><a class="nav-link mx-2 active" href="/">Каталог</a></li>
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
    <section class="about-section">
        <h1 class="about-title">О компании Flowers shop</h1>
        <p class="about-text">
            Наш интернет-магазин Flowers shop предлагает Вам цветы и флористические услуги только высшего качества.
            Мы внимательно контролируем процесс создания каждого букета. У нас работает команда
            профессиональных флористов, которые подберут и составят яркий букет на любой вкус и случай.
        </p>
    </section>

    <section class="team-section container py-4">
        <h2 class="text-center mb-4">Наша команда</h2>
        <div class="row">
            <div class="col-sm-12 col-md-6 col-lg-4 mb-4 d-flex">
                <div class="card w-100">
                    <img src="/static/images/IMG_5954.JPG" class="card-img-top" style="height:300px; object-fit:cover">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title text-center">Алексеев Илья</h5>
                        <p class="text-muted text-center">Основатель сайта</p>
                        <p>Чемпиона рождает то, что у человека внутри — желания, мечты, цели.</p>
                        <p>Hotway97@yandex.ru</p>
                        <a href="https://vk.com/hotway22" class="btn btn-purple mt-auto">Пообщаться со мной!</a>
                    </div>
                </div>
            </div>

            <div class="col-sm-12 col-md-6 col-lg-4 mb-4 d-flex">
                <div class="card w-100">
                    <img src="/static/images/gort.jpeg" class="card-img-top" style="height:300px; object-fit:cover">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title text-center">Краснова Александра</h5>
                        <p class="text-muted text-center">Дизайнер</p>
                        <p>Создатель наших замечательных букетов. Создаст букет на любой вкус.</p>
                        <p>admin@mail.ru</p>
                        <a href="/user/1" class="btn btn-purple mt-auto">Профиль</a>
                    </div>
                </div>
            </div>

            <div class="col-sm-12 col-md-6 col-lg-4 mb-4 d-flex">
                <div class="card w-100">
                    <img src="/static/images/ol.jpeg" class="card-img-top" style="height:300px; object-fit:cover">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title text-center">Басс Олег</h5>
                        <p class="text-muted text-center">Флорист</p>
                        <p>Человек, который собирает идеальный букет за считанные минуты.</p>
                        <p>lol@yandex.ru</p>
                        <a href="/user/1" class="btn btn-purple mt-auto">Профиль</a>
                    </div>
                </div>
            </div>
        </div>
    </section>
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

<script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
<script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
</body>
</html>