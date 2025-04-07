<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="stylesheet" href="/static/css/footer.css">
    <link rel="stylesheet" href="/static/css/navbar.css">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">
    <title>Flowers shop</title>
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
                <a class="nav-link mx-2 active" style="color:white" href="/info">О нас</a>
            </li>
            <li class="nav-item">
                <a class="nav-link mx-2" style="color:white" href="/profile">Профиль</a>
            </li>
        </ul>

        <#if user?? && user.email??>
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

<div class="container mt-4">
    <h1 style="text-align: center">Поиск товаров</h1>
    <hr>
    <form method="get" action="/">
        <div class="form-row" style="width: 100%">
            <div class="form-group" style="width: 100%">
                <label class="label1" style="width: 70%" for="searchWord">Поиск по названию</label>
                <input class="input1" style="width: 70%" type="text" name="searchWord"
                       <#if searchWord??>value="${searchWord}"</#if>
                       class="form-control" id="searchWord">
            </div>
            <button style="background-color: #7b63f4;width: 70%;margin-left: 15%;" type="submit"
                    class="btn btn-dark">Поиск
            </button>
        </div>
    </form>
    <hr>

    <div class="card-columns">
        <#list products as product>
            <a href="/product/${product.id}" class="product-link">
                <div class="card text-white bg-dark my-3 product">
                    <img src="/images/${product.previewImageId}" class="card-img-top">
                    <div class="card-body" style="text-align: center;">
                        <h5 class="card-title">${product.title}</h5>
                        <p class="card-price">${product.price} ₽</p>
                        <p class="card-text">${product.description}</p>
                        <form action="/cart/add/${product.id}" method="post" style="text-align: center;">
                            <input type="hidden" name="_csrf"
                                   value="<#if _csrf??>${_csrf.token}<#else></#if>"/>
                            <button type="submit" class="btn btn-light my-2 my-sm-0" style="background-color: #7b63f4; color: white">
                                Добавить в корзину
                            </button>
                        </form>
                    </div>
                </div>
            </a>
        <#else>
            <h1 style="text-align: center">Не найдено</h1>
        </#list>
    </div>
</div>

<footer class="footer">
    <ul class="social-icon">
        <li class="social-icon__item"><a class="social-icon__link" href="https://vk.com/hotway22">
                <ion-icon name="logo-vk"></ion-icon>
            </a></li>
        <li class="social-icon__item"><a class="social-icon__link" href="#">
                <ion-icon name="logo-steam"></ion-icon>
            </a></li>
        <li class="social-icon__item"><a class="social-icon__link" href="https://wa.me/79877381210">
                <ion-icon name="logo-whatsapp"></ion-icon>
            </a></li>
        <li class="social-icon__item"><a class="social-icon__link" href="http://instagram.com/ilya.alekseev">
                <ion-icon name="logo-instagram"></ion-icon>
            </a></li>
    </ul>
    <p>&copy;2025 Алексеев Илья ИКБО-10-21 | Все права зарезервированы</p>
</footer>

<script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
<script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
</body>
</html>
