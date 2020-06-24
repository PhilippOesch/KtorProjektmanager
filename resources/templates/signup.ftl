<html>
<head>
    <#include "partials/headerdata.ftl">
    <title>Signup</title>
</head>
<body>
<div class="container form-container">
    <#if error??>
        <p style="color:red;">${error}</p>
    </#if>
    <h1>Registrierung</h1>
    <form action="/signup" method="post" enctype="application/x-www-form-urlencoded">
        <div class="form-group">
            <label>Email:</label>
            <input type="email" name="email"/>
        </div>
        <div class="form-group">
            <label>Full Name:</label>
            <input type="text" name="name"/>
        </div>
        <div class="form-group">
            <label>Password:</label>
            <input type="password" name="password"/>
        </div>
        <div class="form-group">
            <label>Confirm Password:</label>
            <input type="password" name="confirmPassword"/>
        </div>
        <input type="submit" value="Registrieren"/>
        <hr>
        <a href="/login" class="sign-up-link">Sie haben bereits einen Account? Zur Anmeldung</a>
    </form>
</div>
</body>
</html>