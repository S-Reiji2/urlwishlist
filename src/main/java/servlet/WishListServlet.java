package servlet;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;

import main.*;
import sql.*;
import url.GamePlatform;

import static main.GameInfo.Element.*;

public class WishListServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        ServletContext application = getServletContext();
        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);

        if (session == null) {
            application.getRequestDispatcher("/login").forward(request, response);
            return;
        }

        UserInfo ui = (UserInfo) session.getAttribute("user-info");
        int userID = UsersTable.getID(ui);
        int page;

        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (Exception e) {
            page = 1;
        }

        if (action != null) {
            switch (action) {
                case "add":
                    session.setAttribute("message", addWishList(request, userID));
                    break;
                case "add-by-id":
                    session.setAttribute("message",
                        addWishListByID(
                            Integer.parseInt(request.getParameter("id")), userID));
                    break;
                case "remove":
                    session.setAttribute("message", removeWishList(request, userID));
                    break;
                case "sort-setting":
                    sortSetting(request, session);
                    break;
                case "display-setting":
                    displaySetting(request, session);
                    break;
                case "turn-page":
                    request.setAttribute("is-jump-to-list", true);
                case "view":
                    updateAllPriceHistory();
                    getWishList(request, session, userID, page);
                    break;
                default:
                    return;
            }
        }

        application.getRequestDispatcher("/main").forward(request, response);
    }

    private String addWishList(HttpServletRequest request, int userID) {
        String url = request.getParameter("url");
        GamePlatform gp = GamePlatform.getPlatformByUrl(url);
        if (gp == null)
            return "未対応のURLです";

        String prodID = gp.getProdID(url);

        GameInfo gi = new GameInfo();
        gi.setValue(PLATFORM, gp.getName());
        gi.setValue(PROD_ID, prodID);

        int gameID = GamesTable.getIndex(gi);

        if (GamesTable.hasDuplicate(gi)) {
            return addWishListByID(gameID, userID);
        } else {
            gi.setValue(REC_ID, gameID);
            if (!addGameRecord(gi, gp, prodID))
                return "ウィッシュリストの追加に失敗しました";

            gi = new GameInfo();
            gi.setValue(PROD_ID, prodID);
            gameID = GamesTable.getIndex(gi);
            gi.setValue(REC_ID, gameID);
            if (WishListTable.insert(userID, gameID))
                return "ウィッシュリストに追加しました";
            return "ウィッシュリストの追加に失敗しました";
        }
    }

    private String addWishListByID(int gameID, int userID) {
        if (WishListTable.onWishList(userID, gameID))
            return "既にウィッシュリスト内に存在します";
        if (WishListTable.insert(userID, gameID))
            return "ウィッシュリストに追加しました";
        else
            return "ウィッシュリストの追加に失敗しました";
    }

    private boolean addGameRecord(GameInfo gi, GamePlatform gp, String prodID) {
        Date date = new Date(System.currentTimeMillis());
        String html = gp.getHtml(gp.getSimpleUrl(prodID));

        gi.setValue(ADD_DATE, date);
        gi.setValue(PROD_ID, prodID);
        gi.setValue(NAME, gp.getGameTitle(html));
        gi.setValue(PLATFORM, gp.getName());
        gi.setValue(IMG_URL, gp.getImageUrl(html));

        if (GamesTable.insert(gi)) {
            int recID = GamesTable.getIndex(gi);
            gi.setValue(REC_ID, recID);

            try {
                gp.getPriceInfo(gi, prodID);
            } catch (Exception e) {
            }

            PriceHistoryTable.insert(gi);
            return true;
        } else
            return false;
    }

    private String removeWishList(HttpServletRequest request, int userID) {
        int gameID = Integer.parseInt(request.getParameter("game-id"));
        if (WishListTable.delete(userID, gameID))
            return "ウィッシュリストからゲームを削除しました";
        else
            return "ゲームの削除に失敗しました";
    }

    private void getWishList(HttpServletRequest request, HttpSession session, int userID, int page) {
        int listSize = WishListTable.getWishListSize(userID);

        if (listSize == 0) {
            request.setAttribute("has-list", false);
            return;
        }

        int limit = (Integer) session.getAttribute("page-size");
        int maxPage = ((listSize - 1) / limit) + 1;
        if (maxPage < page)
            page = maxPage;
        int offset = (page - 1) * limit;

        request.setAttribute("list-size", listSize);
        request.setAttribute("page", page);
        request.setAttribute("last-page", maxPage);
        String sortType = (String) session.getAttribute("sort-type");
        String sortTarget;

        try {
            switch (sortType) {
                case "add-date":
                    sortTarget = "g.add_date";
                    break;
                case "base-price":
                    sortTarget = "ph.base_price";
                    break;
                case "last-price":
                    sortTarget = "ph.last_price";
                    break;
                case "discount":
                    sortTarget = "ph.discount_amount";
                    break;
                case "title":
                default:
                    sortTarget = "g.name";
                    break;
            }
        } catch (Exception e) {
            sortTarget = "g.name";
        }

        Boolean isDescAttribute = (Boolean) session.getAttribute("is-desc");
        boolean isDesc;
        if (isDescAttribute == null)
            isDesc = false;
        else
            isDesc = isDescAttribute;

        ResultSet rs = WishListTable.getWishList(userID, offset, limit, sortTarget, isDesc);
        List<GameInfo> list = new ArrayList<>();
        request.setAttribute("game-list", null);

        try {
            while (rs.next()) {
                GameInfo gi = new GameInfo();
                String platform = rs.getString("platform");
                String prodID = rs.getString("product_id");
                GamePlatform gp = GamePlatform.getPlatformByName(platform);

                gi.setValue(NAME, rs.getString("name"));
                gi.setValue(PLATFORM, platform);
                gi.setValue(IMG_URL, rs.getString("image_url"));
                gi.setValue(BASE_PRICE, rs.getInt("base_price"));
                gi.setValue(LAST_PRICE, rs.getInt("last_price"));
                gi.setValue(PROD_ID, prodID);
                gi.setValue(REC_ID, GamesTable.getIndex(gi));
                gi.setValue(DISCOUNT, rs.getInt("discount_amount"));
                gi.setValue(ADD_DATE, rs.getDate("add_date"));
                gi.setValue(STORE_URL, gp.getSimpleUrl(prodID));

                list.add(gi);
            }

            request.setAttribute("game-list", list);
            request.setAttribute("has-list", true);
        } catch (Exception e) {
            request.setAttribute("has-list", false);
        }
    }

    private void updateAllPriceHistory() {
        if (LastUpdateTable.isLatestDB())
            return;
        ResultSet rs = GamesTable.getAllTwoIdWithPlatform();
        if (rs == null)
            return;

        try {
            while (rs.next()) {
                int recID = rs.getInt("id");
                String prodID = rs.getString("product_id");
                String platform = rs.getString("platform");

                GameInfo gi = new GameInfo();
                gi.setValue(REC_ID, recID);

                GamePlatform gp = GamePlatform.getPlatformByName(platform);

                try {
                    gp.getPriceInfo(gi, prodID);
                    PriceHistoryTable.insert(gi);
                } catch (Exception e) {
                }
            }

            LastUpdateTable.update();
        } catch (Exception e) {
        }
    }

    private void sortSetting(HttpServletRequest request, HttpSession session) {
        String type = request.getParameter("sort-type");
        String target = request.getParameter("target");

        switch (type) {
            case "desc":
                session.setAttribute("is-desc", true);
                break;
            case "asc":
            default:
                session.setAttribute("is-desc", false);
                break;
        }

        session.setAttribute("sort-type", target);
    }

    private void displaySetting(HttpServletRequest request, HttpSession session) {
        session.setAttribute(
            "page-size", Integer.parseInt(
                request.getParameter("page-size")));
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
