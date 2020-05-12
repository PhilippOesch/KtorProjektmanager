<html>
<head>
    <link rel="stylesheet" href="/static/styles.css">
    <title>Signup</title>
</head>
<body>
<#if error??>
    <p style="color:red;">${error}</p>
</#if>
<h1>Sign up</h1>
<form action="/signup" method="post" enctype="application/x-www-form-urlencoded">
    <div>Email:</div>
    <div><input type="email" name="email" /></div>
    <div>Full Name:</div>
    <div><input type="text" name="name" /></div>
    <div>Password:</div>
    <div><input type="password" name="password" /></div>
    <div>Confirm Password:</div>
    <div><input type="password" name="confirmPassword" /></div>
    <div><input type="submit" value="Login" /></div>
</form>
</body>
</html>