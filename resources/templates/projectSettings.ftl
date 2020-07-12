<html>
<head>
    <#include "partials/headerdata.ftl">
    <title>Home</title>
</head>
<body>
<#include "partials/menu.ftl">
<div class="container">
    <h1 class="site-title">${project.name}</h1>
    <#include "partials/projectnav.ftl">
    <div id="einstellung-tab" class="project-tab">
        <div class="project-section">
            <h3 class="beschreibung-überschrift">Beschreibung:</h3>
            <p class="beschreibung-text">${project.description}</p>
        </div>
        <div class="project-section">
            <h3 class="beschreibung-überschrift team-miglieder-titel">Team Mitglieder:</h3>
            <ul>
                <#list users as user>
                    <li>${user.name}</li>
                </#list>
            </ul>
        </div>
        <hr>

        <h2>Person hinzufügen</h2>
        <form autocomplete="off" action="/project/${project.id}/settings" method="post" enctype="application/x-www-form-urlencoded">
            <div class="project-form-group">
                <input type="text" name="email" placeholder="Email" required/>
                <input class="add-user" type="submit" value="+"/>
            </div>
        </form>
    </div>
</div>
<script src="/static/scripts.js"></script>
</body>
</html>