package main;

import java.util.*;

public class UserInfo {
    public static enum Element {
        NAME, PASS_HASH;
    }

    private final Map<Element, Object> userInfo;
    
    public UserInfo() {userInfo = new HashMap<>();}
    
    public void setValue(Element i, Object value) {userInfo.put(i, value);}
    public Object getValue(Element i) {return userInfo.get(i);}
}
