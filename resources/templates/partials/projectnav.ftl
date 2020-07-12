<div class="tab-container">

    <a href="/project/${project.id}" class="tabbutton <#if site == "tasks">active</#if>" id="task-tab-button">Aufgaben</a>
    <a href="/project/${project.id}/settings" class="tabbutton <#if site == "settings">active</#if>" id="einstellungen-tab-button">Einstellungen</a>
    <a href="/project/${project.id}/files" class="tabbutton <#if site == "files">active</#if>" id="file-tab-button">Dateien</a>
    <a href="/project/${project.id}/messages" class="tabbutton <#if site == "messages">active</#if>" id="messages-tab-button">Nachrichten</a>
</div>