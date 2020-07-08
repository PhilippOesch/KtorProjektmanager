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
    <div id="task-tab" class="project-tab">
        <a href="/project/${project.id}/createtask" class="create-task">
            <span class="icon-plus"></span> Aufgabe erstellen
        </a>
        <#if tasks??>
            <h2 class="tasks-subtitle">Offenstehende Aufgaben</h2>
            <hr>
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