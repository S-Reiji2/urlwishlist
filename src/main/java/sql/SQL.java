package sql;

import java.sql.*;
import java.net.*;

public abstract class SQL {
    private static final String DB_SCHEME = "jdbc:postgresql://";
    
    protected static Connection getCon() throws Exception {
        URI dbURI = new URI(System.getenv("DATABASE_URL"));
        return DriverManager.getConnection(
            DB_SCHEME + dbURI.getHost() + dbURI.getPath() + ":" + dbURI.getPort(),
            dbURI.getUserInfo().split(":")[0],
            dbURI.getUserInfo().split(":")[1]
        );
    }
    
    protected static Statement getStmt() throws Exception {
        return getCon().createStatement();
    }
    
    protected static PreparedStatement getPreStmt(String sql) throws Exception {
        return getCon().prepareStatement(sql);
    }
}
