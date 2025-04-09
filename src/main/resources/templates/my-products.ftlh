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
    <link rel="stylesheet" href="/static/css/my-products.css">
</head>
<body class="d-flex flex-column min-vh-100">
<nav class="navbar navbar-expand-lg navbar-dark my">
    <div class="container-fluid">
        <a class="navbar-brand" href="/"><span class="navbar-brand mb-0 h1">Flowers shop</span></a>
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
    <h1 class="text-center my-4">Добавление товаров</h1>

    <form action="/product/create" method="post" enctype="multipart/form-data" class="product-form">
        <div class="form-group">
            <label for="productName">Название товара</label>
            <input type="text" required class="form-control" id="productName" name="title">
        </div>

        <div class="form-group">
            <label for="price">Цена товара</label>
            <div class="input-group">
                <input type="number" required class="form-control" id="price" name="price">
                <div class="input-group-append">
                    <span class="input-group-text ruble">₽</span>
                </div>
            </div>
        </div>

        <div class="form-group">
            <label for="description">Описание товара</label>
            <textarea required class="form-control" id="description" name="description"></textarea>
        </div>

        <div class="form-group">
            <div class="custom-file">
                <input type="file" required class="custom-file-input" id="file1" name="file1">
                <label class="custom-file-label" for="file1">Загрузить первую фотографию</label>
            </div>
        </div>

        <div class="form-group">
            <div class="custom-file">
                <input type="file" class="custom-file-input" id="file2" name="file2">
                <label class="custom-file-label" for="file2">Загрузить вторую фотографию</label>
            </div>
        </div>

        <div class="form-group">
            <div class="custom-file">
                <input type="file" class="custom-file-input" id="file3" name="file3">
                <label class="custom-file-label" for="file3">Загрузить третью фотографию</label>
            </div>
        </div>

        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
        <button type="submit" class="btn btn-purple mt-3 w-100">Добавить</button>
    </form>


    <h2 class="text-center mt-5 mb-4">Мои товары</h2>

    <div class="container">
        <div class="row">
            <#list products as product>
                <div class="col-sm-12 col-md-6 col-lg-4 mb-4 d-flex">
                    <a href="/product/${product.id}" class="product-link w-100">
                        <div class="card h-100">
                            <div class="card-top text-right">
                                <form action="/product/delete/${product.id}" method="post">
                                    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                                    <button type="submit" class="btn btn-danger m-2">Удалить</button>
                                </form>
                            </div>
                            <img src="/images/${product.previewImageId}" class="card-img-top" alt="product">
                            <div class="card-body d-flex flex-column">
                                <h5 class="card-title text-center">${product.title}</h5>
                                <p class="card-price text-center">${product.price} ₽</p>
                                <p class="card-text">${product.description}</p>
                            </div>
                        </div>
                    </a>
                </div>
            <#else>
                <h3 class="text-center w-100">Товары отсутствуют</h3>
            </#list>
        </div>
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

<script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
<script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
<script src="/scripts/my-products.js"></script>
</body>
</html>
