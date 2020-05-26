<html>
<head>
    <#include "partials/headerdata.ftl">
    <title>Home</title>
</head>
    <body>
    <#include "partials/menu.ftl">
    <div class="container">
        <h1>Meine Projekte</h1>
        <div class="wrapper">
            <a href="/createProjekt"><div class="projekt-item new-project">
                <div class="table-cell">
                    <h2><span class="icon-plus"></span></h2>
                    <h2>neues Projekt</h2>
                </div>
            </div></a>
            <a class="projekt-link"><div class="projekt-item">
                <h2>Projekt 1</h2>
            </div></a>
            <a class="projekt-link"><div class="projekt-item">
                <h2>Projekt 2</h2>
            </div></a>
        </div>
    </div>
    <script src="/static/scripts.js"></script>
    </body>
</html>
