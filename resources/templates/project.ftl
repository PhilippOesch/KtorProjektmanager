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
            <#list tasks?filter(t -> t.status == "OPEN") as task>
                <div class="task" onclick="openTaskOverlay(this)">
                    ${task.name}
                    <div class="information">
                        <span name="id">${task.id}</span>
                        <span name="name">${task.name}</span>
                        <span name="description">${task.description}</span>
                        <span name="pid">${project.id}</span>
                        <span name="status">${task.status}</span>
                    </div>
                </div>
            </#list>
            <h2 class="tasks-subtitle">Aufgaben in Bearbeitung</h2>
            <hr>
            <#list tasks?filter(t -> t.status == "INWORK") as task>
                <div class="task" onclick="openTaskOverlay(this)">
                    ${task.name}
                    <div class="information">
                        <span name="id">${task.id}</span>
                        <span name="name">${task.name}</span>
                        <span name="description">${task.description}</span>
                        <span name="pid">${project.id}</span>
                        <span name="status">${task.status}</span>
                    </div>
                </div>
            </#list>
            <h2 class="tasks-subtitle">Abgeschlossene Aufgaben</h2>
            <hr>
            <#list tasks?filter(t -> t.status == "COMPLETED") as task>
                <div class="task" onclick="openTaskOverlay(this)">
                    ${task.name}
                    <div class="information">
                        <span name="id">${task.id}</span>
                        <span name="name">${task.name}</span>
                        <span name="description">${task.description}</span>
                        <span name="pid">${project.id}</span>
                        <span name="status">${task.status}</span>
                    </div>
                </div>
            </#list>
        </#if>

    </div>
</div>
<div class="overlay-task-container">
    <div class="overlay-task">
        <div class="overlay-task-content">
            <h3><span class="close-task-overlay icon-cross"></span></h3>
            <h1 class="taskname">Test</h1>
        <#--    <p class="taskdescription">Test</p>-->
            <form action="/test" method="post" enctype="application/x-www-form-urlencoded" id="changeTask">
                <div class="form-group">
                    <label>Beschreibung:</label>
                    <textarea form="changeTask" type="text" name="description"></textarea>
                </div>
                <input type="text" class="project-Id" name="pId" value=""/>
                <input type="submit" value="Änderungen Speichern"/>
            </form>

            <form class="start-task" action="/test" method="post" enctype="application/x-www-form-urlencoded">
                <input type="text" class="project-Id" name="pId" value=""/>
                <input type="submit" value="Aufgabe Beginnen"/>
            </form>

            <form class="end-task" action="/test" method="post" enctype="application/x-www-form-urlencoded">
                <input type="text" class="project-Id" name="pId" value=""/>
                <input type="submit" value="Aufgabe Beginnen"/>
            </form>
        </div>
    </div>
</div>
<script src="/static/scripts.js"></script>
</body>
</html>