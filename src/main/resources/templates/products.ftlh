<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="stylesheet" href="/static/css/footer.css">
    <link rel="stylesheet" href="/static/css/navbar.css">
    <link rel="stylesheet" href="/static/css/products.css">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
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
            <li class="nav-item1"> <a class="nav-link mx-2 active" style="color:white" aria-current="page" href="/chat">Ассистент</a></li>
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

<main class="products-wrapper">
    <div class="container mt-4">
        <h1 class="text-center">Каталог товаров</h1>
        <hr>
        <!-- Форма поиска -->
        <form method="get" action="/">
            <div class="form-row">
                <div class="form-group col-12 col-md-8 mx-auto text-center">
                    <label for="searchWord" class="w-100">Поиск по названию</label>
                    <input type="text" class="form-control" name="searchWord" id="searchWord"
                           <#if searchWord??>value="${searchWord}"</#if>>

                    <!-- Скрытые поля для сохранения параметров -->
                    <input type="hidden" name="sortBy" value="${sortBy!''}">
                    <input type="hidden" name="minPrice" value="${minPrice!''}">
                    <input type="hidden" name="maxPrice" value="${maxPrice!''}">
                </div>
                <div class="form-group col-12 col-md-8 mx-auto text-center">
                    <button type="submit" class="btn mt-2 w-100" style="background-color: #7b63f4; color: white; border: none;">
                        Поиск
                    </button>
                </div>
            </div>
        </form>
        <hr>

        <!-- Блок сортировки и фильтрации -->
        <div class="filter-sort-bar mb-4">
            <div class="d-flex justify-content-between">
                <!-- Кнопка фильтров -->
                <button class="btn btn-filter" data-toggle="modal" data-target="#filterModal">
                    <i class="fas fa-filter mr-2"></i> Фильтрация
                </button>

                <!-- Кнопка сортировки с динамическим текстом -->
                <button class="btn btn-sort" data-toggle="modal" data-target="#sortModal">
                    <i class="fas fa-sort mr-2"></i>
                    <#if sortBy??>
                        <#if sortBy == "title_asc">Название (А-Я)
                        <#elseif sortBy == "title_desc">Название (Я-А)
                        <#elseif sortBy == "price_asc">Сначала дешевле
                        <#elseif sortBy == "price_desc">Сначала дороже
                        <#else>Сортировка
                        </#if>
                    <#else>
                        Сортировка
                    </#if>
                </button>
            </div>
        </div>

        <!-- Модальное окно фильтров -->
        <div class="modal fade" id="filterModal" tabindex="-1" role="dialog">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Фильтр товаров</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Закрыть">
                            <span>&times;</span>
                        </button>
                    </div>
                    <form id="filterForm" action="/" method="get">
                        <div class="modal-body">
                            <div class="form-group">
                                <label for="minPrice">Минимальная цена</label>
                                <input type="text" class="form-control" id="minPrice" name="minPrice" value="${minPrice!''}">
                            </div>
                            <div class="form-group">
                                <label for="maxPrice">Максимальная цена</label>
                                <input type="text" class="form-control" id="maxPrice" name="maxPrice" value="${maxPrice!''}">
                            </div>
                        </div>
                        <div class="modal-footer d-flex justify-content-between">
                            <button type="button" id="clearFilters" class="btn btn-secondary">Сбросить</button>
                            <button type="submit" class="btn btn-purple">Применить</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Модальное окно сортировки -->
        <div class="modal fade" id="sortModal" tabindex="-1" role="dialog">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Сортировка</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Закрыть">
                            <span>&times;</span>
                        </button>
                    </div>
                    <div class="modal-body p-0">
                        <form id="sortForm" method="get" action="/">
                            <div class="list-group list-group-flush">
                                <label class="list-group-item d-flex align-items-center">
                                    <input type="radio" name="sortBy" value="title_asc" class="mr-3"
                                           <#if sortBy?? && sortBy == "title_asc">checked</#if>>
                                    <span>Название (от А до Я)</span>
                                </label>
                                <label class="list-group-item d-flex align-items-center">
                                    <input type="radio" name="sortBy" value="title_desc" class="mr-3"
                                           <#if sortBy?? && sortBy == "title_desc">checked</#if>>
                                    <span>Название (от Я до А)</span>
                                </label>
                                <label class="list-group-item d-flex align-items-center">
                                    <input type="radio" name="sortBy" value="price_asc" class="mr-3"
                                           <#if sortBy?? && sortBy == "price_asc">checked</#if>>
                                    <span>Сначала дешевле</span>
                                </label>
                                <label class="list-group-item d-flex align-items-center">
                                    <input type="radio" name="sortBy" value="price_desc" class="mr-3"
                                           <#if sortBy?? && sortBy == "price_desc">checked</#if>>
                                    <span>Сначала дороже</span>
                                </label>
                            </div>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            <input type="hidden" name="searchWord" value="${searchWord!''}">
                            <input type="hidden" name="minPrice" value="${minPrice!''}">
                            <input type="hidden" name="maxPrice" value="${maxPrice!''}">
                        </form>
                    </div>
                    <div class="modal-footer d-flex justify-content-between">
                        <button type="button" id="clearSorting" class="btn btn-secondary">Сбросить</button>
                        <button type="submit" form="sortForm" class="btn btn-purple">Применить</button>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <#list products as product>
                <div class="col-sm-12 col-md-6 col-lg-4 mb-4 d-flex">
                    <a href="/product/${product.id}" class="product-link w-100">
                        <div class="card text-white bg-dark h-100">
                            <img src="/images/${product.previewImageId}" class="card-img-top">
                            <div class="card-body text-center">
                                <h5 class="card-title">${product.title}</h5>
                                <p class="card-price">${product.price} ₽</p>
                                <p class="card-text">${product.description}</p>
                                <form action="/cart/add/${product.id}" method="post">
                                    <input type="hidden" name="_csrf" value="${_csrf.token!}"/>
                                    <button type="submit" class="btn btn-purple">
                                        Добавить в корзину
                                    </button>
                                </form>
                            </div>
                        </div>
                    </a>
                </div>
            <#else>
                <h1 class="text-center">Не найдено</h1>
            </#list>
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

<script src="/scripts/filter.js"></script>
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
<script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
<script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
</body>
</html>