package main;

import java.util.*;

public class GameInfo {
    public static enum Element { 
        NAME, PLATFORM, PROD_ID, STORE_URL, IMG_URL, REC_ID,
        BASE_PRICE, LAST_PRICE, DISCOUNT, ADD_DATE;
    }
 
    private final Map<Element, Object> gameInfo;
    
    public GameInfo() {gameInfo = new HashMap<>();}
    
    public void setValue(Element i, Object value) {gameInfo.put(i, value);}
    public Object getValue(Element i) {return gameInfo.get(i);}
}
