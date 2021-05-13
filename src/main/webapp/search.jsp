<%@page import="java.sql.Date"%>
<%@page import="java.text.*"%>
<%@page import="java.util.List"%>

<%@page import="main.*"%>
<%@page import="url.GamePlatform"%>

<%@page import="static main.GameInfo.Element.*"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    List<GameInfo> result = (List<GameInfo>) request.getAttribute("result");
    String searchString = (String) request.getParameter("search-str");
%>
<!DOCTYPE html>
<html>
  <head>
    <style><%@include file="./common.css"%></style>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>SEARCH | URL WISHLIST</title>
  </head>
  <body>
    <div class="wrap">
      <div class="contents">
        <div class="title"><h1>SEARCH</h1></div>
        <div class="block">
          <form action="./gamesearch" method="post">
            <p>
              <span>ゲームを検索：</span>
              <input type="text" name="search-str" <%=(searchString != null) ? "value=\"" + searchString + "\"" : ""%> placeholder="Search" autocomplete="off"/>
            </p>
            <div style="text-align: center;">
              <input type="submit" value="検索" class="button"/>
            </div>
          </form>
          <div style="text-align: center;">
            <p><input type="button" onclick="location.href='./main'" value="サイトトップ" class="button" /></p>
          </div>
        </div>
        <%if (result != null) {%>
        <hr/>
        <div class="title"><h1>SEARCH RESULT</h1></div>
        <%if (result.isEmpty()) {%>
        <p style="text-align: center">一致するゲームが存在しません</p>
        <%} else {%>
        <table class="table">
          <tr>
            <th>サムネイル</th>
            <th>タイトル</th>
            <th>プラットフォーム</th>
            <th>追加日</th>
            <th>詳細</th>
          </tr>
          <%
              String img = "<img src=\"<URL>\" style=\"width: 100px\"/>";
              String gameInfo = ""
                  + "<form action=\"./gameinfo?id=<ID>\" method=\"post\">"
                  + "<input type=\"submit\" value=\"詳細\" class=\"button\" />"
                  + "</form>";

              for (GameInfo gi : result) {
                  String imgURL = (String) gi.getValue(IMG_URL);
                  Integer gameID = (Integer) gi.getValue(REC_ID);
                  String platform = (String) gi.getValue(PLATFORM);

                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM/dd");
          %>
          <tr>
            <td style="text-align: center">
              <%=img.replaceAll("<URL>", imgURL)%>
            </td>
            <td><%=(String) gi.getValue(NAME)%></td>
            <td><%=platform%></td>
            <td><%=sdf.format((Date) gi.getValue(ADD_DATE))%></td>
            <td>
              <%=gameInfo.replaceAll("<ID>", gameID + "")%>
            </td>
          </tr>
          <%}%>
          <%}%>
        </table>
        <%}%>
      </div>
    </div>
  </body>
</html>
