package com.guima.kits;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateKit
{
    /**
     * 完整日期时间样式(yyyy-MM-dd hh:mm:ss.SSS)
     */
    public static String DATE_FULL_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 日期时间样式 (yyyy-MM-dd hh:mm:ss)
     */
    public static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日期时间样式 (yyyy-MM-dd hh:mm)
     */
    public static String DATE_TIME_PATTERN1 = "yyyy-MM-dd HH:mm";

    /**
     * 日期小时样式 (yyyy-MM-dd hh)
     */
    public static String DATE_HOUR_PATTERN = "yyyy-MM-dd HH";

    /**
     * 日期样式(yyyy-MM-dd)
     */
    public static String DATE_PATTERN = "yyyy-MM-dd";
    /**
     * 日期样式(yyyy/MM/dd)
     */
    public static String DATE_PATTERN_Backslash = "yyyy/MM/dd";

    public static String format(Date date, String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 获取当前日期，格式为yyyy-MM-dd
     *
     * @return
     */
    public static String curDate()
    {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 获取当前时间，格式为yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String now()
    {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 获取当前时间，格式为yyyy-MM-dd
     *
     * @return
     */
    public static String getDay()
    {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        return format.format(date);
    }

    /**
     * 获取当前时间，格式为yyyy-MM-dd 00:00:00
     *
     * @return
     */
    public static String getDaytime()
    {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        return format.format(date)+" 00:00:00";
    }

    /**
     * 转换日期格式
     *
     * @param source       源日期字符串
     * @param sourceFormat 源日期格式，如：yyyy-MM-dd
     * @param targetFormat 目标日期格式，如：yyyy-MM
     * @return 转换后的日期字符串
     */
    public static String transDate(String source, String sourceFormat, String targetFormat)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat(sourceFormat);
            Date sourceDate = format.parse(source);
            format = new SimpleDateFormat(targetFormat);
            return format.format(sourceDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return source;
    }

    /**
     * 获取一个日期若干天（前/后）的日期
     *
     * @param source  源日期字符串，将会被格式化为yyyy-MM-dd
     * @param forward true为向前，false为向后
     * @param days    要移动的天数
     * @return 返回移动后的日期，格式为毫秒
     * @throws ParseException 当源日期字符串无法被格式为yyyy-MM-dd时抛出
     */
    public static long moveDate(String source, boolean forward, int days) throws ParseException
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(source);
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(date);
        days = forward ? days : -days;
        c.add(Calendar.DATE, days);
        return c.getTimeInMillis();
    }

    public static String increaseByDay(String source,int days){
        try {
            long mills=moveDate(source,true,days);
            return format(new Date(mills),DATE_PATTERN);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将以毫秒数表示的字符串，输出为yyyy-MM-dd表示的字符串
     *
     * @param millisStr 源字符串
     * @return 格式化后的字符串
     */
    public static String transMillis2Date(String millisStr)
    {
        long millis = Long.parseLong(millisStr);
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTimeInMillis(millis);
        Date d = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(d);
    }

    /**
     * 比较两个日期相隔几天
     *
     * @param day1 日期1
     * @param day2 日期2
     * @return 相隔的天数
     */
    public static int compareDays(String day1, String day2)
    {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        int day = 0;
        try
        {
            Date d1 = myFormatter.parse(day1);
            Date d2 = myFormatter.parse(day2);
            float f = (d1.getTime() - d2.getTime()) / (24 * 60 * 60 * 1000);
            day = Math.round(f);
        } catch (Exception ignored)
        {
        }
        return day;
    }

    /**
     * 比较两个日期相隔几秒
     *
     * @param time1 日期1
     * @param time2 日期2
     * @return 相隔的秒数
     */
    public static long compareSecs(String time1, String time2)
    {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long min = 0;
        try
        {
            Date d1 = myFormatter.parse(time1);
            Date d2 = myFormatter.parse(time2);
            min = (d1.getTime() - d2.getTime()) / 1000;
        } catch (Exception ignored)
        {
        }
        return min;
    }

    /**
     * 获取与当天时间相差指定天数的日期
     *
     * @param type
     * @param value
     * @param format
     * @return
     */
    public static String getDate(int type, int value, String format)
    {
        Date dNow = new Date();
        Date dBefore = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(dNow);
        c.add(type, value);
        dBefore = c.getTime();

        return format(dBefore, format);
    }

    public static String getDateOfDay(int differenceDays){
        return DateKit.getDate(Calendar.DATE,differenceDays,DATE_PATTERN)+" 00:00:00";
    }

    /**
     * 获取两个日期中间的日期值，以字符串形式返回
     *
     * @param type
     * @param startDate
     * @param endDate
     * @param format
     * @return
     */
    public static List<String> getDateArray(int type, String startDate, String endDate, String format)
    {
        List<String> list = new ArrayList<String>();
        try
        {
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date start = df.parse(startDate);
            Date end = df.parse(endDate);

            Calendar c = Calendar.getInstance();
            c.setTime(start);
            Date now = c.getTime();
            do
            {
                now = c.getTime();
                list.add(df.format(now));
                c.add(type, 1);
                now = c.getTime();
            } while (now.before(end));
            list.add(endDate);
            return list;
        } catch (Exception ex)
        {
            return list;
        }
    }

    /**
     * 获取月份中的某一天
     *
     * @param offset     相比较当月的偏移量，例如目前8月，想获取7月中的某一天，应传入-1
     * @param dayOfMonth 获取的日期
     * @param format     格式化样式
     * @return
     */
    public static String getMonthDay(int offset, int dayOfMonth, String format)
    {
        Date dNow = new Date();
        Date dBefore = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(dNow);
        c.add(Calendar.MONTH, offset);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dBefore = c.getTime();

        return format(dBefore, format);
    }

    /**
     * 获取当前时间，格式为yyyyMMddHHmmsss作为流水号
     *
     * @return
     */
    public static String getSerialNumberDay()
    {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    public static String getSerialNumber()
    {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmsss");
        return format.format(date);
    }

    public static String getSerialNumberSecond()
    {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("HHmmsss");
        return format.format(date);
    }

    /**
     * 判断当前日期是否过期 按天算
     * @return
     */
    public static boolean isOverdueIncludeDay(String beginDate,String endDate){
        beginDate=transDate(beginDate,DATE_TIME_PATTERN,DATE_PATTERN);
        endDate=transDate(endDate,DATE_TIME_PATTERN,DATE_PATTERN);
        String now=now();
        now=transDate(now,DATE_TIME_PATTERN,DATE_PATTERN);
        return now.compareTo(beginDate)<0 || now.compareTo(endDate)>0;
    }

    public static String getDateDesc(Date date){
       Date now=new Date();
        if(dateToString(now).equals(dateToString(date))){
            return "今天";
        }
        if(date.getYear()==now.getYear()){
            return format(date,"MM-dd");
        }
        return dateToString(date);
    }

    /**
     * 判断当前日期是否过期 按秒算
     * @return
     */
    public static boolean isOverdueIncludeSecond(String beginDate,String endDate){
        String now=now();
        return now.compareTo(beginDate)<0 || now.compareTo(endDate)>0;
    }

    /**
     * 移动月份
     * @param source
     * @param month
     * @return
     * @throws ParseException
     */
    public static String moveDateMonth(String source, int month)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(source);
            Calendar c = Calendar.getInstance();
            c.clear();
            c.setTime(date);
            c.add(Calendar.MONTH, month);
            long mills=c.getTimeInMillis();
            return format(new Date(mills),DATE_PATTERN);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static void main(String[] args) {
//        System.out.println(getSerialNumber());
//        System.out.println(isOverdue());
//        System.out.println(DateKit.moveDateMonth("2017-01-01",-1));
//        String maxNo="R170920-012";
//        if(Kit.isNull(maxNo)){
//            maxNo="000";
//        }
//        String num=maxNo.substring(maxNo.length()-3);
//        int newNum=Integer.valueOf(num)+1;
//        String sNewNum=String.format("%0" + 3 + "d", newNum);
//        System.out.println("R"+DateKit.getFormatDate("yyMMdd")+"-"+sNewNum);

//        System.out.println(new java.math.BigDecimal("100000").add(new java.math.BigDecimal("100")).toString());
        long a=new Date().getTime();
        System.out.println(a);
        for (int i=0;i<10000;i++){
            System.out.println(getDate(Calendar.DATE,-2,DATE_PATTERN));
        }
        long b=new Date().getTime();
        System.out.println(b);
        System.out.println(b-a);
    }

    public static String getFormatDate(String formatStr)
    {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }

    public static String dateToString(Date date){
        try{
            return new SimpleDateFormat(DATE_TIME_PATTERN).format(date);
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }

    }
    public static String dateToString(Timestamp date){
        try{
            return new SimpleDateFormat(DATE_TIME_PATTERN).format(date);
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }

    }
    /**
     * 字符串转换为日期
     * @param str 字符串 (yyyy-MM-dd)
     * @return 日期
     */
    public static Date stringToDate(String str){
        try {
            return new SimpleDateFormat(DATE_PATTERN).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String format(String date){
        return transDate(date, DATE_TIME_PATTERN, DATE_PATTERN);
    }

    public static Date addOneSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, 1);
        return calendar.getTime();
    }

    public static String getStartTime(){
        return getDateOfDay(-10);
    }

}
