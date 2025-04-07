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
    </head>
    <body>
     <nav class="navbar navbar-expand-lg navbar-dark my">
             <div class="container-fluid">
               <a class="navbar-brand" href="/">
                 <span class="navbar-brand mb-0 h1">Flowers shop</span>
               </a>
               <input id="menu-toggle" type="checkbox" >
                   <label class='menu-button-container' for="menu-toggle">
                   <div class='menu-button'></div>
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
                     <li class="nav-item">
                         <a class="nav-link mx-2" style="color:white" href="/profile">Профиль</a>
                     </li>
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
    <div class="container mt-4">
    </div>
    <h1 style="text-align: center">Мои цветы</h1>
    <hr>
    <a class="btn btn-dark mt-3 mb-3" style="text-align: center; background:#7b63f4;width: 70%; margin-left:15%"
       data-toggle="collapse" href="#collapseExample" role="button"
       aria-expanded="true" aria-controls="collapseExample">
        Добавить новые цветы
    </a>
    <div class="collapse mb-3" id="collapseExample" style="">
        <form action="/product/create" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label for="productName">Название товара</label>
                <input type="text" required class="form-control"
                       id="productName" name="title">
            </div>
            <div class="form-group">
                <label for="price">Цена товара</label>
                <div class="input-group mb-3">
                    <input type="number" required class="form-control" id="price" name="price">
                    <div class="input-group-append">
                        <span class="input-group-text">₽</span>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <label for="description">Описание товара</label>
                <textarea required class="form-control" id="description" name="description"></textarea>
            </div>
            <div class="form-group">
                <div class="custom-file">
                    <input required type="file" name="file1" id="file1">
                    <label class="custom-file-label" for="file1">Первая фотография к товару</label>
                </div>
            </div>
            <div class="form-group">
                <div class="custom-file">
                    <input type="file" name="file2" id="file2">
                    <label class="custom-file-label" for="file2">Вторая фотография к товару</label>
                </div>
            </div>
            <div class="form-group">
                <div class="custom-file">
                    <input type="file" name="file3" id="file3">
                    <label class="custom-file-label" for="file3">Третья фотография к товару</label>
                </div>
            </div>
            <input type="hidden" name="_csrf" value="${_csrf.token}"/>

            <button type="submit" style="width: 100%" class="btn btn-dark">Добавить</button>
        </form>
    </div>
    <div class="card-columns">
        <#list products as product>
            <a href="/product/${product.id}" class="product-link">
                <div class="card text-white bg-dark my-3 product">
                    <div class="card-top">
                        <form action="/product/delete/${product.id}" method="post">
                            <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                            <button type="submit" class="btn btn-danger m-3">
                                Удалить
                            </button>
                        </form>
                    </div>
                    <img src="/images/${product.previewImageId}"
                         class="card-img-top">
                    <div class="card-body">
                        <h5 class="card-title">${product.title}</h5>
                        <p class="card-text">${product.price} ₽</p>
                        <p class="card-text">${product.description}</p>
                    </div>
                </div>
            </a>
        <#else>
            <h1 style="text-align: center">Не найдено</h1>
        </#list>
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
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js" integrity="sha384-smHYKdLADwkXOn1EmN1qk/HfnUcbVRZyYmZ4qpPea6sjB/pTJ0euyQp0Mk8ck+5T" crossorigin="anonymous"></script>
    </body>
</html>