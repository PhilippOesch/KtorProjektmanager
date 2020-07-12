<html>
<head>
    <#include "partials/headerdata.ftl">
    <title>Projekt Erstellen</title>
</head>
<body>
<#include "partials/menu.ftl">
<body>
<div class="container form-container">
    <h1>Aufgabe erstellen</h1>
    <form autocomplete="off" action="/project/${projectId}/createtask" method="post" enctype="application/x-www-form-urlencoded" id="createTaskFrom">
        <div class="form-group">
            <label>Name:</label>
            <input type="text" name="name" required/>
        </div>
        <div class="form-group">
            <label>Beschreibung:</label>
            <textarea form="createTaskFrom" type="text" name="description"></textarea>
        </div>
        <input type="submit" value="Aufgabe erstellen"/>
    </form>
</div>
<script src="/static/scripts.js"></script>
</body>
</html>