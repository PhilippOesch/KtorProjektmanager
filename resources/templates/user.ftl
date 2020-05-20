<html>
<head>
    <#include "partials/headerdata.ftl">
    <title>Home</title>
</head>
<#include "partials/menu.ftl">
<body>
<div class="container login-container">
    <h1>${data.name}</h1>
    <hr>
    <form action="/user/${data.email}" method="post" enctype="application/x-www-form-urlencoded" id="updateUserForm">
        <div class="form-group email-display">
            <span class="title">Email:</span>
            <span class="content">${data.email}</span>
        </div>
        <div class="form-group">
            <label>Full Name:</label>
            <input type="text" name="fullname" value="${data.name}"/>
        </div>
        <div class="form-group">
            <label>Biographie:</label>
            <textarea form="updateUserForm" type="text" name="biography">${data.biography}</textarea>
        </div>
        <input type="submit" value="Änderungen speichern"/>
    </form>
</div>
<script src="/static/scripts.js"></script>
</body>
</html>