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
        <a href="/createProjekt">
            <div class="projekt-item new-project">
                <div class="table-cell">
                    <h2><span class="icon-plus"></span></h2>
                    <h2>neues Projekt</h2>
                </div>
            </div>
        </a>
        <#list projects as project>
            <a class="projekt-link" href="/project/${project.id}">
                <div class="projekt-item">
                    <h2>${project.name}</h2>
                </div>
            </a>
        </#list>
    </div>
    <div id="user-tasks">
        <h1>Dir zugeteteilte Aufgaben</h1>
        <#if tasks??>
        <#list tasks as task>
            <a class="btn-wide user-task" href="/project/${task.pid}#task${task.id}">${task.name}</a>
        </#list>
        </#if>
    </div>
</div>
<script src="/static/scripts.js"></script>
</body>
</html>
