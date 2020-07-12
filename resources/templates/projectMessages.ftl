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
    <div id="messages-tab">
        <h3>Nachricht erstellen</h3>
        <hr>
        <form autocomplete="off" id="createMessageForm" action="/project/${project.id}/messages" method="post" enctype="application/x-www-form-urlencoded">
            <div class="form-group">
                <textarea form="createMessageForm" type="text" name="text" required></textarea>
            </div>
            <input type="submit" value="Nachricht veröffentlichen"/>
        </form>
        <h2 class="add-padding">Nachrichten</h2>
        <hr>
        <#if messages??>
        <div class="messages-container">
                <#list messages as message>
                    <div class="message">
                        <h3 class="message-user-name">${message.user.name}</h3>
                        <span class="message-datum">${message.message.timestamp}</span>
                        <p class="message-text">${message.message.text}</p>
                    </div>
                </#list>
        </div>
        </#if>
    </div>
</div>
<script src="/static/scripts.js"></script>
</body>
</html>