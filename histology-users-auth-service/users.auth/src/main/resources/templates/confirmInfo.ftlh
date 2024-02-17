<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Histoexam Подтверждение почты</title>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/css/styles.css'/>"/>
    <link rel="shortcut icon" href="<@spring.url '/img/icon.png'/>" type="image/png">
</head>
<body>
<div class="middle">
    <main>
        <#if error??>
            <#if error>
                Ошибка. Обратитесь в тех. поддержку - histoexam@yandex.ru <br><br>
                Error. Contact technical support - histoexam@yandex.ru<br><br>
                <img src="<@spring.url '/api/users/auth/1/img/error.png'/>" alt="error">
            <#else>
                OK. Почта подтверждена.<br><br>
                OK. Email confirmed.<br><br>
                <img src="<@spring.url '/img/success.png'/>" alt="success">
            </#if>
        <#else>
            OK. Почта подтверждена.<br><br>
            OK. Email confirmed. <br><br>
            <img src="<@spring.url '/img/success.png'/>" alt="success">
        </#if>
    </main>
</div>
</body>
</html>