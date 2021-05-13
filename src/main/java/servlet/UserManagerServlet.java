package servlet;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.util.Arrays;
import javax.servlet.*;
import javax.servlet.http.*;

import main.UserInfo;
import sql.UsersTable;

import static main.UserInfo.Element.*;

public class UserManagerServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
                
        ServletContext application = getServletContext();
        String action = request.getParameter("action");

        try {
            switch (action) {
                case "registration":
                    doSignUp(request);
                    application.getRequestDispatcher("/login").forward(request, response);
                    break;
                case "login":
                    if (doSignOn(request)) {
                        application.getRequestDispatcher("/login").forward(request, response);
                    } else {
                        application.getRequestDispatcher("/main").forward(request, response);
                    }
                    
                    break;
                case "logout":
                    request.getSession().invalidate();
                    request.setAttribute("message", "ログアウトしました");
                    application.getRequestDispatcher("/login").forward(request, response);
                    break;
            }
        } catch (Exception e) {
        }
    }
    
    private boolean doSignUp(HttpServletRequest request) throws Exception {
        String userName = request.getParameter("name");
        String userPass = request.getParameter("pass");
        String userPass2 = request.getParameter("pass2");
        String message;
        boolean isError;

        if (userName.isEmpty()) {
            message = "ユーザー名が空白です";
            isError = true;
        } else if (!userPass.equals(userPass2)) {
            message = "確認パスワードが一致しません";
            isError = true;
        } else {
            String md5Pass = md5(userPass);
            UserInfo ui = new UserInfo();
            ui.setValue(NAME, userName);
            ui.setValue(PASS_HASH, md5Pass);
            
            if (UsersTable.hasDuplicates(ui)) {
                message = "既に存在するユーザー名です";
                isError = true;
            } else {
                if (UsersTable.insert(ui)) {
                    message = userName + " をユーザーとしてDBに登録しました";
                    isError = false;
                } else {
                    message = "DBに登録できませんでした";
                    isError = true;
                }
            }
        }
        
        request.setAttribute("message", message);
        return isError;
    }
    
    private boolean doSignOn(HttpServletRequest request) throws Exception {
        String userName = request.getParameter("name");
        String userPass = request.getParameter("pass");
        String md5Pass = md5(userPass);
        String message;
        boolean isError;
        
        if (userName.isEmpty()) {
            message = "ユーザー名が空白です";
            isError = true;
        } else {
            UserInfo ui = new UserInfo();
            ui.setValue(NAME, userName);
            ui.setValue(PASS_HASH, md5Pass);
            
            if (UsersTable.login(ui)) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user-info", ui);
                session.setAttribute("sort-type", "title");
                session.setAttribute("is-desc", false);
                session.setAttribute("page-size", 10);
                message = userName + " さん，ようこそ！";
                isError = false;
            } else {
                message = "入力されたユーザーは存在しないか、パスワードが間違っています";
                isError = true;
            }
        }

        request.setAttribute("message", message);
        return isError;
    }
    
    private static String md5(String password) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(password.getBytes());
        String digest = new BigInteger(1, md5.digest()).toString(16);
        char[] buf = new char[32];
        Arrays.fill(buf, '0');
        System.arraycopy(digest.toCharArray(), 0, buf,
                buf.length - digest.length(), digest.length());
        return new String(buf);
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
