package sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import main.GameInfo;

import static main.GameInfo.Element.*;

public class PriceHistoryTable extends SQL {
    private static final int MAX_DATE_SIZE = 1000;
    
    public static boolean insert(GameInfo gi) {
        String sql = "INSERT INTO "
                + "price_history(game_id, date, base_price, last_price, discount_amount) "
                + "VALUES(?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setInt(1, (Integer)gi.getValue(REC_ID));
            ps.setDate(2, new Date(System.currentTimeMillis()));
            ps.setInt(3, (Integer)gi.getValue(BASE_PRICE));
            ps.setInt(4, (Integer)gi.getValue(LAST_PRICE));
            ps.setInt(5, (Integer)gi.getValue(DISCOUNT));
            if (ps.executeUpdate() == 1) trimRecord((Integer) gi.getValue(REC_ID));
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    private static void trimRecord(int gameID) {
        String sql = "SELECT COUNT(*) FROM price_history WHERE game_id = ?";
        
        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) if (rs.getInt(1) > MAX_DATE_SIZE) removeOldestRecord(gameID);
        } catch (Exception e) {
        }
    }
    
    private static void removeOldestRecord(int gameID) {
        String sql = "DELETE FROM price_history AS ph "
                + "WHERE game_id = ? AND (SELECT MIN(date) FROM ph)";
        
        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setInt(1, gameID);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public static List<GameInfo> getPriceHistory(int gameID) {
        String sql = "SELECT * FROM price_history WHERE game_id = ?";

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            List<GameInfo> list = new ArrayList<>();
            
            while (rs.next()) {
                GameInfo gi = new GameInfo();
                // gi.setValue(REC_ID, gameID);
                gi.setValue(ADD_DATE, rs.getDate("date"));
                gi.setValue(BASE_PRICE, rs.getInt("base_price"));
                gi.setValue(LAST_PRICE, rs.getInt("last_price"));
                gi.setValue(DISCOUNT, rs.getInt("discount_amount"));
                list.add(gi);
            }
            
            return list;
        } catch (Exception e) {
            return null;
        }
    }
}
