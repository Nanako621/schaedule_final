package com.example.schedule;

/**
 * 星期轉換工具類
 */
public class DayUtils {

    /**
     * 根據「星期一」~「星期日」回傳 1~7
     * 若非有效格式則回傳 0
     */
    public static int getDayIndex(String day) {
        switch (day) {
            case "星期一": return 1;
            case "星期二": return 2;
            case "星期三": return 3;
            case "星期四": return 4;
            case "星期五": return 5;
            case "星期六": return 6;
            case "星期日": return 7;
            default: return 0;
        }
    }
}
