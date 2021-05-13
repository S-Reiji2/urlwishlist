package sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import main.GameInfo;

import static main.GameInfo.Element.*;

public class GamesTable extends SQL {
    public static boolean insert(GameInfo gi) {
        String sql = "INSERT INTO games(name, platform, product_id, image_url, add_date) "
                + "VALUES(?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setString(1, (String) gi.getValue(NAME));
            ps.setString(2, (String) gi.getValue(PLATFORM));
            ps.setString(3, (String) gi.getValue(PROD_ID));
            ps.setString(4, (String) gi.getValue(IMG_URL));
            ps.setDate(5, (Date) gi.getValue(ADD_DATE));
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean hasDuplicate(GameInfo gi) {
        String sql = "SELECT id FROM games WHERE platform = ? AND product_id = ?";

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setString(1, (String) gi.getValue(PLATFORM));
            ps.setString(2, (String) gi.getValue(PROD_ID));
            ResultSet rs = ps.executeQuery();
            
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static GameInfo getRecordByID(int id) {
        String sql = "SELECT * FROM games WHERE id = ?";
        
        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (!rs.next()) return null;
            
            GameInfo gi = new GameInfo();
            gi.setValue(REC_ID, id);
            gi.setValue(NAME, rs.getString("name"));
            gi.setValue(PLATFORM, rs.getString("platform"));
            gi.setValue(PROD_ID, rs.getString("product_id"));
            gi.setValue(IMG_URL, rs.getString("image_url"));
            gi.setValue(ADD_DATE, rs.getDate("add_date"));
            
            return gi;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static int getIndex(GameInfo gi) {
        String sql = "SELECT id FROM games WHERE product_id = ?";

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setString(1, (String) gi.getValue(PROD_ID));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
            return -1;
        } catch (Exception e) {
            return -2;
        }
    }
    
    public static boolean updateImageURL(String url) {
        String sql = "UPDATE games SET image_url = ? WHERE product_id = ?";

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setString(1, url);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static ResultSet getAllTwoIdWithPlatform() {
        String sql = "SELECT id, product_id, platform FROM games";

        try {
            return getStmt().executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static List<GameInfo> searchGameInfo(String searchString) {
        String sql = "SELECT * FROM games WHERE UPPER(name) LIKE UPPER(?)";
        StringBuilder sb = new StringBuilder();
        
        for (String s : searchString.split("\\W")) {
            if (!s.isEmpty()) {
                sb.append("%");
                sb.append(s);
                sb.append("%");
            }
        }

        try {
            PreparedStatement ps = getPreStmt(sql);
            ps.setString(1, sb.toString());
            ResultSet rs = ps.executeQuery();
            List<GameInfo> list = new ArrayList();
            
            while (rs.next()) {                
                GameInfo gi = new GameInfo();

                gi.setValue(NAME, rs.getString("name"));
                gi.setValue(PLATFORM, rs.getString("platform"));
                gi.setValue(IMG_URL, rs.getString("image_url"));
                gi.setValue(REC_ID, rs.getInt("id"));
                gi.setValue(ADD_DATE, rs.getDate("add_date"));
                
                list.add(gi);
            }
            
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
