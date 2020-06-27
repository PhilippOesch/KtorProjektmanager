<html>
<head>
    <#include "partials/headerdata.ftl">
    <title>Home</title>
</head>
<body>
<#include "partials/menu.ftl">
<div class="container">
    <h1 class="site-title">${project.name}</h1>
    <div class="tab-container">
        <div class="tab active" id="task-tab-button">Aufgaben</div>
        <div class="tab" id="einstellungen-tab-button">Einstellungen</div>
    </div>
    <div id="einstellung-tab">
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
        <form action="/project/${project.id}" method="post" enctype="application/x-www-form-urlencoded">
            <div class="project-form-group">
                <input type="text" name="email" placeholder="Email" required/>
                <input class="add-user" type="submit" value="+"/>
            </div>
        </form>
    </div>
    <div id="task-tab">
        <a href="/project/${project.id}/createtask" class="create-task">
            <span class="icon-plus"></span> Aufgabe erstellen
        </a>
        <h2 class="tasks-subtitle">Offenstehende Aufgaben</h2>
        <hr>
        <#if tasks??>
            <#list tasks?filter(t -> t.task.status == "OPEN") as task>
                <#include "partials/task.ftl">
            </#list>
            <h2 class="tasks-subtitle">Aufgaben in Bearbeitung</h2>
            <hr>
            <#list tasks?filter(t -> t.task.status == "INWORK") as task>
                <#include "partials/task.ftl">
            </#list>
            <h2 class="tasks-subtitle">Abgeschlossene Aufgaben</h2>
            <hr>
            <#list tasks?filter(t -> t.task.status == "COMPLETED") as task>
                <#include "partials/task.ftl">
            </#list>
        </#if>

    </div>
</div>
<#include "partials/task_overlay.ftl">
<script src="/static/scripts.js"></script>
</body>
</html>