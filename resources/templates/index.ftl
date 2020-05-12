<#-- @ftlvariable name="data" type="com.example.IndexData" -->
<html>
    <body>
        <#if username??>
           <p>You are logged in as <b>${username}</b></p>
            <a href="/logout">logout</a>
        <#else>
            <p>You are not logged</p>
            <a href="/login">Login</a>
        </#if>
    </body>
</html>
