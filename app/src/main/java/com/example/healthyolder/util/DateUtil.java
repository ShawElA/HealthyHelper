package com.example.healthyolder.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    //获取当前时间前后几天的时间
    public static String beforeAfterDate(int days) {
        long nowTime = System.currentTimeMillis();
        long changeTimes = days * 24L * 60 * 60 * 1000;
        return getStrTime(String.valueOf(nowTime + changeTimes), "MM-dd");
    }

    //时间戳转字符串
    public static String getStrTime(String timeStamp, String format) {
        String timeString= null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long l = Long.valueOf(timeStamp);
        timeString= sdf.format(new Date(l));//单位秒
        return timeString;
    }
}
