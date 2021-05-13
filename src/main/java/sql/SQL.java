package sql;

import java.sql.*;

public abstract class SQL {
    protected static final
            String DB_NAME = "game_db",
            USER = "APP",
            PASS = "app",
            URL = "jdbc:derby://localhost/" + DB_NAME;

    protected static Statement getStmt() throws Exception {
        return DriverManager
                .getConnection(URL, USER, PASS)
                .createStatement();
    }
    
    protected static PreparedStatement getPreStmt(String sql) throws Exception {
        return DriverManager
                .getConnection(URL, USER, PASS)
                .prepareStatement(sql);
    }
}
