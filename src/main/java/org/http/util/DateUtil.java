package org.http.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class DateUtil {

    public static final String DATETIME_PATTERN_DETAIL = "yyyy-MM-dd HH:mm:ss:SSS";
    public static final String DATETIME_PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_PATTERN_CN = "yyyy年MM月dd日 HH:mm:ss";

    public static final String DATE_PATTERN_DEFAULT = "yyyy-MM-dd";
    public static final String DATE_PATTERN_CN = "yyyy年MM月dd日";

    public static final String TIME_PATTERN_DEFAULT = "HH:mm:ss";
    public static final String TIME_PATTERN_CN = "HH时mm分ss秒";

    
    public static String getNow2String(){
        LocalDateTime now = LocalDateTime.now();
        return DateTimeFormatter.ofPattern(DATETIME_PATTERN_DEFAULT).format(now);
    }

    public static String getNow2String(String pattern){
        LocalDateTime now = LocalDateTime.now();
        return DateTimeFormatter.ofPattern(DATETIME_PATTERN_DETAIL).format(now);
    }

    public static String getDatetime2String(LocalDateTime localDateTime){
        return DateTimeFormatter.ofPattern(DATETIME_PATTERN_DEFAULT).format(localDateTime);
    }

    public static String getDate2String(Date date){
        SimpleDateFormat simpleFormatter = new SimpleDateFormat(DATETIME_PATTERN_DEFAULT);
        return simpleFormatter.format(date);
    }

    public static void main(String[] args) {
        System.out.println(getNow2String());
    }
}
