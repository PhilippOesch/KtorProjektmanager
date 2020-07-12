<html>
<head>
    <#include "partials/headerdata.ftl">
    <title>Login</title>
</head>
<body>
<div class="container form-container">
    <h1>Login</h1>
    <#if error??>
        <p style="color:red;">${error}</p>
    </#if>
    <form autocomplete="off" action="/login" method="post" enctype="application/x-www-form-urlencoded">
        <div class="form-group">
            <label>Email:</label>
            <input type="text" name="email" required/>
        </div>
        <div class="form-group">
            <label>Passwort:</label>
            <input type="password" name="password" required/>
        </div>
        <input type="submit" value="Anmelden"/>
    </form>
    <hr>
    <a href="/signup" class="sign-up-link">Einen Account erstellen</a>
</div>
</body>
</html>