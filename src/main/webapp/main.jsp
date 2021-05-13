<%@page import="java.sql.Date"%>
<%@page import="java.text.*"%>
<%@page import="java.util.List"%>

<%@page import="main.*"%>
<%@page import="url.GamePlatform"%>

<%@page import="static main.GameInfo.Element.*"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    session = request.getSession(false);
    UserInfo ui = (UserInfo) session.getAttribute("user-info");

    if (ui == null) {
        response.sendRedirect("./login");
        return;
    }

    Boolean hasList = (Boolean) request.getAttribute("has-list");

    if (hasList == null) {
        response.sendRedirect("./wishlist?action=view");
        return;
    }

    request.setAttribute("has-list", null);
    String name = (String) ui.getValue(UserInfo.Element.NAME);
    String message = (String) session.getAttribute("message");

    Integer listSize = (Integer) request.getAttribute("list-size");
    Integer thisPage = (Integer) request.getAttribute("page");
    Integer lastPage = (Integer) request.getAttribute("last-page");
    if (listSize == null)
        listSize = 0;
    if (thisPage == null)
        thisPage = 1;
    if (lastPage == null)
        lastPage = 1;

    Integer pageSize = (Integer) session.getAttribute("page-size");
    Boolean isDesc = (Boolean) session.getAttribute("is-desc");
    String sortType = (String) session.getAttribute("sort-type");
    if (lastPage == null)
        pageSize = 10;
    if (isDesc == null)
        isDesc = false;
    if (sortType == null)
        sortType = "";
    
    Boolean isJumpToList = (Boolean) request.getAttribute("is-jump-to-list");
    if(isJumpToList == null)
        isJumpToList = false;
%>
<!DOCTYPE html>
<html>
  <head>
    <style><%@include file="./common.css"%></style>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>URL WISHLIST</title>
  </head>
  <%=(isJumpToList) ? "<body onLoad=\"location.href=\'#list\'\">" : "<body>"%>
    <div class="wrap">
      <div class="contents">
        <div class="title"><h1>url/wishlist</h1></div>
        <div class="block">
          <form action="./user?action=logout" method="post">
            <p><b><%=name%></b>でログインしています</p>
            <div style="text-align: center;">
              <input type="submit" value="ログアウト" class="button" />
            </div>
          </form>
        </div>
        <hr/>
        <div class="block">
          <form action="./gamesearch" method="post">
            <p>
              <span>ゲームを検索：</span>
              <input type="text" name="search-str" placeholder="Search" autocomplete="off"/>
            </p>
            <div style="text-align: center;">
              <input type="submit" value="検索" class="button"/>
            </div>
          </form>
        </div>
        <hr/>
        <div class="block">
          <%
              if (message != null) {
                  session.setAttribute("message", null);
          %>
          <p><b><%=message%></b></p>
          <%
              }
          %>
          <p><%=listSize%>件のゲームがウィッシュリストに登録されています</p>
          <form action="./wishlist?action=add" method="post">
            <p>
              <span>URLからゲームを追加：</span>
              <input type="text" name="url" placeholder="https://" autocomplete="off"/>
            </p>
            <div style="text-align: center;">
              <input type="submit" value="追加" class="button"/>
            </div>
          </form>
        </div>
        <div class="block">
          <details>
            <summary>対応プラットフォーム</summary>
            <ul>
              <li>Steam</li>
              <li>UBI Store</li>
              <li>My Nintendo Store</li>
              <li>PlayStation4 Store</li>
              <li>Microsoft Store</li>
            </ul>
          </details>
          <details>
            <summary>URLの対応形式</summary>
            <ul>
              <li>Steam：https://store.steampowered.com/app/[ProductID]...</li>
              <li>UBI：https://store.ubi.com/jp/game?pid=[ProductID]...</li>
              <li>UBI：https://store.ubi.com/jp/[ProductName]/[ProductID].html</li>
              <li>Nintendo：https://store-jp.nintendo.com/list/software/[ProductID].html</li>
              <li>PlayStation4：https://store.playstation.com/ja-jp/product/[ProductID]...</li>
              <li>Microsoft：https://www.microsoft.com/ja-jp/p/[ProductName]/[ProductID]...</li>
            </ul>
          </details>
        </div>
        <%
            if (hasList) {
        %>
        <hr id="list"/>
        <form action="./wishlist?action=sort-setting" method="post">
          <table class="table">
            <tr>
              <th>ソート</th>
              <th>
                <select name="sort-type">
                  <option value="asc"<%=(!isDesc) ? " selected" : ""%>>
                    昇順
                  </option>
                  <option value="desc"<%=(isDesc) ? " selected" : ""%>>
                    降順
                  </option>
                </select>
              </th>
              <th>
                <span>対象</span>
              </th>
              <th>
                <select name="target">
                  <%
                      String[] targetValues = {"title", "add-date", "base-price", "last-price", "discount"};
                      String[] targetTexts = {"タイトル", "追加日", "定価", "価格", "値引率"};

                      for (int i = 0; i < targetValues.length; i++) {
                          String select;
                          select = (sortType.equals(targetValues[i])) ? " selected" : "";
                  %>
                  <option value="<%=targetValues[i]%>"<%=select%>><%=targetTexts[i]%></option>
                  <%
                      }
                  %>
                </select>
              </th>
              <th>
                <input type="submit" value="更新" class="button" />
              </th>
            </tr>
          </table>
        </form>
        <%
            StringBuilder options = new StringBuilder();
            int[] optionValues = {10, 20, 50, 100};

            for (int optionValue : optionValues) {
                String select;
                select = (optionValue == pageSize.intValue()) ? " selected" : "";
                options.append("<option value=\"");
                options.append(optionValue);
                options.append("\"");
                options.append(select);
                options.append(">");
                options.append(optionValue);
                options.append("</option>");
            }
        %>
        <form action="./wishlist?action=display-setting" method="post">
          <table class="table">
            <tr>
              <th>ページあたりの表示数</th>
              <th>
                <select name="page-size">
                  <%=options.toString()%>
                </select>
              </th>
              <th>
                <input type="submit" value="更新" class="button" />
              </th>
            </tr>
          </table>
        </form>
        <table class="table">
          <tr>
            <th>サムネイル</th>
            <th>タイトル</th>
            <th>プラットフォーム</th>
            <th>追加日</th>
            <th>価格</th>
            <th>値引率</th>
            <th>その他</th>
          </tr>
          <%
              String store = "<a href=\"<STORE>\"><IMG></a>";
              String img = "<img src=\"<URL>\" style=\"width: 100px\"/>";
              String gameInfo = ""
                      + "<form action=\"./gameinfo?id=<ID>\" method=\"post\">"
                      + "<input type=\"submit\" value=\"詳細\" class=\"button\" />"
                      + "</form>";
              String remove = ""
                      + "<form action=\"./wishlist?action=remove&game-id=<ID>\" method=\"post\">"
                      + "<input type=\"submit\" value=\"削除\" class=\"button\" />"
                      + "</form>";
              List<GameInfo> gameList = (List<GameInfo>) request.getAttribute("game-list");

              for (GameInfo gi : gameList) {
                  String imgURL = (String) gi.getValue(IMG_URL);
                  Integer gameID = (Integer) gi.getValue(REC_ID);
                  String platform = (String) gi.getValue(PLATFORM);
                  
                  String url = GamePlatform
                          .getPlatformByName(platform)
                          .getSimpleUrl((String) gi.getValue(PROD_ID));
                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM/dd");
                  String priceFormat = "¥ %,d";
          %>
          <tr>
            <td style="text-align: center">
              <%=store.replaceAll("<STORE>", url)
                      .replaceAll("<IMG>", img.replaceAll("<URL>", imgURL))%>
            </td>
            <td><%=(String) gi.getValue(NAME)%></td>
            <td><%=platform%></td>
            <td><%=sdf.format((Date) gi.getValue(ADD_DATE))%></td>
            <td>
              <%=String.format(
                      priceFormat,
                      (Integer) gi.getValue(LAST_PRICE)
              )%>
            </td>
            <td>
              <%=(Integer) gi.getValue(DISCOUNT) + "%"%>
            </td>
            <td>
              <p><%=gameInfo.replaceAll("<ID>", gameID + "")%></p>
              <p><%=remove.replaceAll("<ID>", gameID + "")%></p>
            </td>
          </tr>
          <%
              }
          %>
        </table>
        <%
            String link = "<a href=\"./wishlist?action=turn-page&page=<P>\"><V></a>";
        %>
        <p align="center">
          <%if (lastPage != 1) {%>
          <%if (1 < thisPage) {%>
          <span>
            <%=link.replaceAll("<P>", (thisPage - 1) + "").replaceAll("<V>", "＜")%>
          </span>
          <%}%>
          <%if (3 < thisPage) {%>
          <span><%=link.replaceAll("<P>|<V>", "1")%></span>
          <span>...</span>
          <%}%>
          <%for (int i = thisPage - 2; i <= thisPage + 2; i++) {%>
          <%if (i == thisPage) {%>
          <span><%=i%></span>
          <%} else if (1 <= i && i <= lastPage) {%>
          <span><%=link.replaceAll("<P>|<V>", i + "")%></span>
          <%}%>
          <%}%>
          <%if (thisPage < lastPage - 2) {%>
          <span>...</span>
          <span><%=link.replaceAll("<P>|<V>", lastPage + "")%></span>
          <%}%>
          <%if (thisPage < lastPage) {%>
          <span>
            <%=link.replaceAll("<P>", (thisPage + 1) + "").replaceAll("<V>", "＞")%>
          </span>
          <%}%>
        </p>
        <%
                }
            }
        %>
      </div>
    </div>
  </body>
</html>