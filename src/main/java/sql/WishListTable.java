package sql;

import java.sql.*;

public class WishListTable extends SQL {
    public static boolean insert(int userID, int gameID) {
        String sql = "INSERT INTO wish_list(user_id, game_id, add_date) "
                + "VALUES(?, ?, ?)";

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setInt(1, (Integer) userID);
            ps.setInt(2, (Integer) gameID);
            ps.setDate(3, (Date) new Date(System.currentTimeMillis()));
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean delete(int userID, int gameID) {
        String sql = "DELETE FROM wish_list WHERE user_id = ? AND game_id = ?";

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setInt(1, (Integer) userID);
            ps.setInt(2, (Integer) gameID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean onWishList(int userID, int gameID) {
        String sql = "SELECT COUNT(*) FROM wish_list WHERE user_id = ? AND game_id = ?";

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setInt(1, userID);
            ps.setInt(2, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return (rs.getInt(1) >= 1);
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static int getWishingUsers(int gameID) {
        String sql = "SELECT COUNT(*) FROM wish_list WHERE game_id = ?";

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }
    
    public static int getWishListSize(int userID) {
        String sql = "SELECT COUNT(*) FROM wish_list WHERE user_id = ?";

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }
    
    public static ResultSet getWishList(int userID, int offset, int limit, String sortTarget, boolean isDesc) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT\n"
                + "ph.*, platform, product_id, name, image_url, wl.add_date\n"
                + "FROM wish_list AS wl\n"
                + "INNER JOIN games AS g ON g.id = wl.game_id AND wl.user_id = ?\n"
                + "INNER JOIN price_history AS ph ON ph.game_id = wl.game_id\n"
                + "WHERE ph.date = ?\n"
                + "ORDER BY ");
        sb.append(sortTarget);
        if (isDesc) sb.append(" DESC\n");
        else sb.append("\n");
        sb.append("OFFSET ? ROWS FETCH FIRST ? ROWS ONLY");
        
        try {
            PreparedStatement ps = getPreStmt(sb.toString());
            ps.setInt(1, userID);
            ps.setDate(2, new Date(System.currentTimeMillis()));
            ps.setInt(3, offset);
            ps.setInt(4, limit);
            return ps.executeQuery();
        } catch (Exception e) {
            return null;
        }
    }
}
