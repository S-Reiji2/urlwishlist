<%@page import="main.UserInfo"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    session = request.getSession(false);
    UserInfo ui = (UserInfo) session.getAttribute("user-info");

    if (ui != null) {
        response.sendRedirect("./main");
        return;
    }

    String message = (String) request.getAttribute("message");
%>
<!DOCTYPE html>
<html lang="ja">
  <head>
    <style><%@include file="../common.css"%></style>
    <style>
      table {width: 80%;}
      td {font-size: 18px;}

      input[type="text"], input[type="password"] {
          font-size: 16px;
          width: 80%;
      }
    </style>
    <meta charset="UTF-8">
    <title>SIGN UP AND RESISTER | URL WISHLIST</title>
  </head>
  <body>
    <div class="wrap">
      <div class="contents">
        <%if (message != null) {%>
        <p><b><%=message%></b></p>
        <%}%>
        <div class="title"><h1>LOGIN</h1></div>
        <form method="post" action="./user?action=login" autocomplete="off">
          <table>
            <tr>
              <td>ユーザ名</td>
              <td><input type="text" name="name" size="16" placeholder="Name"></td>
            </tr>
            <tr>
              <td>パスワード</td>
              <td><input type="password" name="pass" size="16" placeholder="Password"></td>
            </tr>
            <tr>
              <td colspan="2" align="center">
                <input type="submit" value="ログイン" class="button">
              </td>
            </tr>
          </table>
        </form>
        <hr/>
        <div class="title"><h1>RESISTER</h1></div>
        <form method="post" action="./user?action=registration" autocomplete="off">
          <table>
            <tr>
              <td>ユーザ名</td>
              <td><input type="text" name="name" size="16" placeholder="Name"></td>
            </tr>
            <tr>
              <td>パスワード</td>
              <td><input type="password" name="pass" size="16" placeholder="Password"></td>
            </tr>
            <tr>
              <td>パスワード(確認用)</td>
              <td><input type="password" name="pass2" size="16" placeholder="Password"></td>
            </tr>
            <tr>
              <td colspan="2" align="center">
                <input type="submit" value="新規登録" class="button">
              </td>
            </tr>
          </table>
        </form>
        <hr/>
        <div class="title"><h1>SEARCH</h1></div>
        <div class="block">
          <form action="./gamesearch" method="post">
            <table>
              <tr>
                <td>ゲームを検索</td>
                <td>
                  <input type="text" name="search-str" placeholder="Search" autocomplete="off"/>
                </td>
              </tr>
              <tr>
                <td colspan="2" align="center">
                  <input type="submit" value="検索" class="button"/>
                </td>
              </tr>
            </table>
          </form>
        </div>
      </div>
    </div>
  </body>
</html>