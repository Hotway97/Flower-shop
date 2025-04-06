<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="ru">
    <head>
        <!-- Required meta tags -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="stylesheet" href="/static/css/style.css">
        <link rel="stylesheet" href="/static/css/about.css">
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
                    <li class="nav-item1">
                       <a class="nav-link mx-2 active" style="color:white" aria-current="page" href="/">Главная</a>
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
    <div style="background-color: #ff82d9d9" class="about-section">
      <h1>О компании Flowers shop </h1>
      <p>Наш интернет магазин Flowers shop предлагает Вам цветы и флористические услуги только высшего качества.
         Мы внимательно контролируем процесс создания каждого букета. У нас работает команда
         профессиональных флористов, которые подберут и составят яркий букет на любой вкус и случай.</p>
    </div>

    <h2 style="text-align:center">Наша команда</h2>
    <div class="row">
      <div class="column">
        <div class="card">
          <img src="/static/images/IMG_5954.JPG" alt="I" style="width:100%; height:300px">
          <div class="container">
            <h2>Алексеев Илья</h2>
            <p class="title">Основатель сайта</p>
            <p>Чемпиона рождает то, что у человека внутри — желания, мечты, цели.</p>
            <p>Hotway97@yandex.ru</p>
            <p><a href="https://vk.com/hotway22"><button class="button" style="background:#f560c1">Пообщаться со мной!</button></a></p>
          </div>
        </div>
      </div>

      <div class="column">
        <div class="card">
          <img src="/static/images/gort.jpeg" alt="Sasha" style="width:100%; height:300px">
          <div class="container">
            <h2>Краснова Александра</h2>
            <p class="title">Дизайнер</p>
            <p>Создатель наших замечателльных букетов.Создаст букет на любой вкус.</p>
            <p>admin@mail.ru</p>
            <p><a href="http://localhost:8080/user/1"><button class="button" style="background:#f560c1">Профиль</button></a><p>
          </div>
        </div>
      </div>

      <div class="column">
        <div class="card">
          <img src="/static/images/ol.jpeg" alt="Oleg" style="width:100%; height:300px">
          <div class="container">
            <h2>Басс Олег</h2>
            <p class="title">Флорист</p>
            <p>Человек, который собирает идеальный букеты за считанные минуты.</p>
            <p>lol@yandex.ru</p>
            <p><a href="http://localhost:8080/user/1"><button class="button" style="background:#f560c1">Профиль</button></a></p>
          </div>
        </div>
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
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js" integrity="sha384-smHYKdLADwkXOn1EmN1qk/HfnUcbVRZyYmZ4qpPea6sjB/pTJ0euyQp0Mk8ck+5T" crossorigin="anonymous"></script>
    </body>
</html>