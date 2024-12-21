<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로그인 페이지</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="login-container">
        <h2>로그인</h2>
        <form action="./login.do" method="post">
            <div class="form-group">
                <label for="id">아이디</label>
                <input type="text" id="id" name="id" required>
            </div>
            <div class="form-group">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="login-button">로그인</button>
        </form>
        <div class="register-link">
            <p>아직 계정이 없으신가요? <a href="./member_register.jsp">회원가입</a></p>
        </div>
    </div>
</body>
</html>