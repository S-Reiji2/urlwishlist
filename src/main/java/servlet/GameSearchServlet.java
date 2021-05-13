package servlet;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

import sql.*;

public class GameSearchServlet extends HttpServlet {
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
        String searchString = (String) request.getParameter("search-str");
        
        if (searchString != null && !searchString.isEmpty()) {
            request.setAttribute("result", GamesTable.searchGameInfo(searchString));
        }
        
        application.getRequestDispatcher("/search").forward(request, response);
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
