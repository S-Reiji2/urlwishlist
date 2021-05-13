package sql;

import java.sql.*;

import main.UserInfo;

import static main.UserInfo.Element.*;

public class UsersTable extends SQL {
    public static boolean insert(UserInfo ui) {
        String sql = "INSERT INTO users(name, password) VALUES(?, ?)";
        
        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setString(1, (String) ui.getValue(NAME));
            ps.setString(2, (String) ui.getValue(PASS_HASH));
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean login(UserInfo ui) {
        String sql = "SELECT * FROM users WHERE name = ? AND password = ?";

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setString(1, (String) ui.getValue(NAME));
            ps.setString(2, (String) ui.getValue(PASS_HASH));
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }
    
    public static int getID(UserInfo ui) {
        String sql = "SELECT id FROM users WHERE name = ? AND password = ?";

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setString(1, (String) ui.getValue(NAME));
            ps.setString(2, (String) ui.getValue(PASS_HASH));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }
    
    public static boolean hasDuplicates(UserInfo ui) {
        String sql = "SELECT COUNT(*) FROM users WHERE name = ?";
        
        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setString(1, (String) ui.getValue(NAME));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return (rs.getInt(1) == 1);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean delete(UserInfo ui) {
        String sql = "DELETE FROM users WHERE name = ? AND password = ?";
        
        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setString(1, (String)ui.getValue(NAME));
            ps.setString(2, (String)ui.getValue(PASS_HASH));
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
