package sql;

import java.sql.*;

public class LastUpdateTable extends SQL {
    public static boolean isLatestDB() {
        String sql = "SELECT date FROM last_update WHERE id = 0 AND date >= CURRENT_DATE";
        
        try {
            return getStmt().executeQuery(sql).next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean update() {
        String sql = "UPDATE last_update SET date = ? WHERE id = 0";
        
        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setDate(1, new Date(System.currentTimeMillis()));
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
