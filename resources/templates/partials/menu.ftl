<nav>
    <div class="left-nav">
        <a href="/"><span class="home-icon icon-home"></span>Home</a>
    </div>
    <div class="right-nav">
        <a class="show-profil-info"><span class="profil-icon icon-user"></span></a>
    </div>
</nav>
<div class="profil-info">
    <span class="close-info icon-cross"></span>
    <p class="profil-name">${data.name}</p>
    <hr>
    <a href="/user/${data.email}">Mein Profil</a>
    <hr>
    <a href="/logout">Abmelden</a>
</div>