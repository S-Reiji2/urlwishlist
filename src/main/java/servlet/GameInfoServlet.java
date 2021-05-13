package servlet;

import java.io.IOException;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import main.*;
import sql.*;
import url.GamePlatform;

import static main.GameInfo.Element.*;

public class GameInfoServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ServletContext application = getServletContext();
        int gameID = -1;

        try {
            gameID = Integer.parseInt(request.getParameter("id"));
        } catch (Exception e) {
            application.getRequestDispatcher("/main").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        GameInfo gi = GamesTable.getRecordByID(gameID);
        boolean onWishList = false;
        int wishingUserNum = WishListTable.getWishingUsers(gameID);
        
        if (session != null) {
            UserInfo ui = (UserInfo) session.getAttribute("user-info");
            if (ui != null) {
                int userID = UsersTable.getID(ui);
                onWishList = WishListTable.onWishList(userID, gameID);
            }
        }

        if (gi == null) {
            application.getRequestDispatcher("/main").forward(request, response);
            return;
        }
        
        GamePlatform gp = GamePlatform.getPlatformByName((String) gi.getValue(PLATFORM));
        gi.setValue(STORE_URL, gp.getSimpleUrl((String) gi.getValue(PROD_ID)));

        List<GameInfo> priceHist = PriceHistoryTable.getPriceHistory(gameID);
        
        request.setAttribute("game-info", gi);
        request.setAttribute("price-info", priceHist);
        request.setAttribute("wishing-users", wishingUserNum);
        request.setAttribute("on-wish-list", onWishList);
        
        application.getRequestDispatcher("/game").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
