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
    <div id="file-tab" class="project-tab">
        <form action="/project/${project.id}/files/upload" method="post" enctype="multipart/form-data">
            <input type="file" name="uploadfile" required>
            <input type="submit" value="Datei hochladen"/>
        </form>

        <h3>Dateien</h3>
        <hr>
        <#if files??>
            <#list files as file>
                <a class="btn-wide" target="_blank" href="/project/${project.id}/files/${file.filename}">${file.originalFilename}</a>
            </#list>
        </#if>
    </div>
</div>
<script src="/static/scripts.js"></script>
</body>
</html>