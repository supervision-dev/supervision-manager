package com.rmbank.supervision.common.utils;

/**
 * Created by sam on 2017/2/15.
 */
//鍙楅檺妯″潡
public enum LimitFunctionUrlUtils {
    PERMISSION("system/permission/permissionList.do", 1),
    MENU("system/function/functionList.do", 2),
    RESOURCE("system/resource/resourceList.do", 3);

    private String name;
    private int index;
    // 鏋勯�鏂规硶
    private LimitFunctionUrlUtils(String name, int index) {
        this.name = name;
        this.index = index;
    }
    // 鏅�鏂规硶
    public static String getName(int index) {
        for (LimitFunctionUrlUtils c : LimitFunctionUrlUtils.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
    public static boolean isLimit(String name){
        for (LimitFunctionUrlUtils c : LimitFunctionUrlUtils.values()) {
            if (c.name.equals(name)) {
                return true;
            }
        }
        return false;
    }
    // get set 鏂规硶
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
}
