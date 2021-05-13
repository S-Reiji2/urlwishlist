<%@page import="java.sql.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>

<%@page import="main.GameInfo"%>

<%@page import="static main.GameInfo.Element.*"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    session = request.getSession(false);
    
    GameInfo gameInfo = (GameInfo) request.getAttribute("game-info");

    if (gameInfo == null) {
        response.sendRedirect("./main");
        return;
    }

    List<GameInfo> priceHist = (List<GameInfo>) request.getAttribute("price-info");
    Integer wishingUsers = (Integer) request.getAttribute("wishing-users");
    if (wishingUsers == null)
        wishingUsers = -1;
    Boolean onWishList = (Boolean) request.getAttribute("on-wish-list");

    String img = "<img src=\"<URL>\" />";
    String imgURL = (String) gameInfo.getValue(IMG_URL);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM/dd");
%>
<!DOCTYPE html>
<html>
  <head>
    <style><%@include file="../common.css"%></style>
    <style>
      img {
          max-width: 100%;
      }
      
      .canvas-area {
          position: relative;
          height: 400px;
          margin: auto;
          overflow: hidden;
      }
    </style>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <%=((String) gameInfo.getValue(NAME)).toUpperCase()%> | URL WISHLIST
    </title>
  </head>
  <body>
    <div class="wrap">
      <div class="contents">
        <div class="title"><h1><%=(String) gameInfo.getValue(NAME)%></h1></div>
        <div class="box">
          <div class="image">
            <p>
              <%if (imgURL.isEmpty()) {%>
              NO IMAGE
              <%} else {%>
              <%=img.replaceAll("<URL>", imgURL)%>
              <%}%>
            </p>
          </div>
          <div class="text">
            <p>
              <span>このゲームをウィッシュリストに<br/>追加したユーザー数：</span>
              <%if (wishingUsers < 0) {%>
              <span>0</span>
              <%} else {%>
              <span><%=wishingUsers%></span>
              <%}%>
            </p>
            <p>
              <span>このサイトへ追加された日：</span>
              <br/>
              <span><%=sdf.format((Date) gameInfo.getValue(ADD_DATE))%></span>
            </p>
            <p>
              <span>プラットフォーム：</span>
              <span><%=(String) gameInfo.getValue(PLATFORM)%></span>
            </p>
            <%
                String onClick = "onclick=\"location.href='<URL>'\"";
                String addWishList = "./wishlist?action=add-by-id&id="
                    + (String) request.getParameter("id");
                String storeLink = (String) gameInfo.getValue(STORE_URL);
            %>
            <%if (onWishList) {%>
            <p>あなたはこのゲームをウィッシュリストに保存しています</p>
            <%} else if (session != null) {%>
            <p><input type="button" <%=onClick.replaceAll("<URL>", addWishList)%> value="ウィッシュリストに追加" class="button" /></p>
              <%}%>
            <p><input type="button" <%=onClick.replaceAll("<URL>", storeLink)%> value="ストアページ" class="button" /></p>
            <p><input type="button" <%=onClick.replaceAll("<URL>", "./main")%> value="サイトトップ" class="button" /></p>  
          </div>
        </div>
        <hr/>
        <table class="table">
          <h3 align="center">履歴（最新10件）</h3>
          <tr>
            <th>日付</th>
            <th>定価</th>
            <th>価格</th>
            <th>値引率</th>
          </tr>
          <%
              Integer[] basePriceLine = new Integer[priceHist.size()];
              Integer[] lastPriceLine = new Integer[priceHist.size()];
              String[] dateLine = new String[priceHist.size()];
              int last = priceHist.size() - 10;
              if (last < 0)
                  last = 0;

              for (int i = priceHist.size() - 1; i >= last; i--) {
                  String priceFormat = "¥ %,d";

                  Integer basePrice = (Integer) priceHist
                          .get(i)
                          .getValue(BASE_PRICE);
                  Integer lastPrice = (Integer) priceHist
                          .get(i)
                          .getValue(LAST_PRICE);
                  String date = sdf.format((Date) priceHist
                          .get(i)
                          .getValue(ADD_DATE));

                  basePriceLine[i] = basePrice;
                  lastPriceLine[i] = lastPrice;
                  dateLine[i] = date;
          %>
          <tr>
            <td><%=date%></td>
            <td>
              <%=String.format(
                      priceFormat,
                      basePrice
              )%>
            </td>
            <td>
              <%=String.format(
                      priceFormat,
                      lastPrice
              )%>
            </td>
            <td><%=(Integer) priceHist.get(i).getValue(DISCOUNT) + "%"%></td>
          </tr>
          <%
              }
          %>
        </table>
        <hr/>
        <div class="canvas-area" id="canvasArea">
          <canvas id="chart" width="1360" height="768">
            Your browser cannot available HTML5 Canvas.
          </canvas>
        </div>
      </div>
    </div>
    <%
        StringBuilder basePriceData = new StringBuilder();
        StringBuilder lastPriceData = new StringBuilder();
        StringBuilder dateData = new StringBuilder();

        for (int i = 0; i < basePriceLine.length; i++) {
            basePriceData.append((i == 0) ? "" : ",");
            basePriceData.append("'" + basePriceLine[i] + "'");
        }

        for (int i = 0; i < lastPriceLine.length; i++) {
            lastPriceData.append((i == 0) ? "" : ",");
            lastPriceData.append("'" + lastPriceLine[i] + "'");
        }

        for (int i = 0; i < dateLine.length; i++) {
            dateData.append((i == 0) ? "" : ",");
            dateData.append("'" + dateLine[i] + "'");
        }
    %>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@2.8.0"></script>
    <script>
        window.onload = function () {
            var canvas = document.getElementById('chart');
            var canvasArea = document.getElementById('canvasArea');

            var chart = new Chart(
                canvas, {
                    type: 'line',
                    data: {
                        labels: [<%=dateData.toString()%>],
                        datasets: [
                            {
                                label: '定価 (円)',
                                borderColor: 'rgba(0,0,255,1)',
                                backgroundColor: 'rgba(0,0,0,0)',
                                lineTension: 0,
                                data: [<%=basePriceData.toString()%>]
                            },
                            {
                                label: '実売価格 (円)',
                                borderColor: 'rgba(0,255,0,1)',
                                backgroundColor: 'rgba(0,0,0,0)',
                                lineTension: 0,
                                data: [<%=lastPriceData.toString()%>]
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        title: {
                            display: true,
                            text: '価格履歴'
                        },
                        scales: {
                            yAxes: [{ticks: {precision: 0}}]
                        }
                    }
                }
            );
        };
    </script>
  </body>
</html>
