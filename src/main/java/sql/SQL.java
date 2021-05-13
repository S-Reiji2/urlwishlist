package sql;

import java.sql.*;

public abstract class SQL {
    protected static Connection getCon() throws Exception {
        return DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
    }
    
    protected static Statement getStmt() throws Exception {
        return getCon().createStatement();
    }
    
    protected static PreparedStatement getPreStmt(String sql) throws Exception {
        return getCon().prepareStatement(sql);
    }
}
