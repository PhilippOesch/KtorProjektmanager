<html>
<head>
    <#include "partials/headerdata.ftl">
    <title>Projekt Erstellen</title>
</head>
<body>
<#include "partials/menu.ftl">
<div class="container login-container">
    <h1>Projekt erstellen</h1>
    <#if error??>
        <p style="color:red;">${error}</p>
    </#if>
    <form action="/createProjekt" method="post" enctype="application/x-www-form-urlencoded" id="createProjectFrom">
        <div class="form-group">
            <label>Name:</label>
            <input type="text" name="email" required/>
        </div>
        <div class="form-group">
            <label>Beschreibung:</label>
            <textarea form="createProjectFrom" type="text" name="description"></textarea>
        </div>
        <input type="submit" value="Projekt Erstellen"/>
    </form>
</div>
</body>
</html>