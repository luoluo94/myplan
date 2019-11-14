package com.guima.kits;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jfinal.plugin.activerecord.Record;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Kit
{
    /**
     * 将驼峰字符串转换为带下划线的字符串，例如MyAccout，转换为my_account
     *
     * @param str 源字符串
     * @return 转换后的字符串
     */
    public static String camel2Line(String str)
    {
        char[] chars = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++)
        {
            char c = chars[i];
            if (i > 0 && Character.isUpperCase(c))
                sb.append("_");
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    public static boolean isMobileNO(String mobile)
    {
        if (!isNotNull(mobile))
            return true;
        // Pattern p = Pattern
        // .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Pattern p = Pattern.compile("1[3,4,5,8]{1}\\d{9}");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    public static void main(String[] args)
    {
//        String a=new String("aa");
//        String b=new String("aa");
//        System.out.println(a==b);
//        a=a.intern();
//        b=b.intern();
//        System.out.println(a==b);
//        System.out.println(getPercentage("0","0",0));
        System.out.println(Kit.MD5("1tingni6"));
//        System.out.println(new BigDecimal("1").multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).divide(new BigDecimal("3"),0,BigDecimal.ROUND_DOWN).toString()+"%");
    }

    public static boolean isNum(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    private static boolean isChinese(char c)
    {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    public static boolean isChinese(String strName)
    {
        char[] ch = strName.toCharArray();
        for (char c : ch)
        {
            if (isChinese(c))
                return true;
        }
        return false;
    }

    /**
     * 给定一个编码，获取下一个
     *
     * @param curCode
     * @return
     */
    public static String getNextDm(String curCode)
    {
        String tempDm = "";
        if (!"".equals(curCode) && null != curCode)
        {
            long dm = Long.valueOf(curCode);
            dm += 1;
            tempDm = String.valueOf(dm);
            for (int i = tempDm.length(); i < curCode.length(); i++)
            {
                tempDm = "0" + tempDm;
            }
        }
        return tempDm;
    }

    /**
     * 去除字符串的空格，主要用于从数据库中取出字段时的处理，如果出现Null值，直接trim()会报异常，使用本方法则返回一个空的字符串
     *
     * @param s
     * @return String
     */
    public static String strTrim(String s)
    {
        if (s == null || s.trim().equals(""))
            return "";
        return s.trim();
    }

    /**
     * 获取给定字符串的int值，如果是Null值或无法转换为int，则返回0
     *
     * @param s
     * @return
     */
    public static String getIntValue(String s)
    {
        String value = strTrim(s);
        if (value.equals(""))
            value = "0";
        return value;
    }

    /**
     * 从数据库中读取值,如果值为空,则返回空格(&nbsp;)
     *
     * @param value
     * @return
     */
    public static String dbValue(Object value)
    {
        String returnString = "";
        try
        {
            returnString = (String) value;
            if (returnString.trim().equals(""))
            {
                returnString = "&nbsp;";
            }
        } catch (Exception ex)
        {
            returnString = "&nbsp;";
        }
        return returnString;
    }

    /**
     * 从数据库中读取值,如果值为空,则返回空格(&nbsp;),如果值超过长度,则按长度截断值+..
     *
     * @param value
     * @return
     */
    public static String dbValue(Object value, int lenght)
    {
        String str = dbValue(value);
        if (!str.equals("&nbsp;") && str.length() > lenght && lenght - 2 > 0)
        {
            str = "<span title=\"" + str + "\">" + str.substring(0, lenght - 2)
                    + "..</span>";
        }
        return str;
    }

    /**
     * 把ISO8859_1的字串转换为GBK的字串
     *
     * @param str 要转换的字串 &@return 转换后的字串
     */
    public static String webEncode(String str)
    {
        String temp = "";
        try
        {
            temp = new String(str.getBytes("ISO8859_1"), "GBK");
        } catch (Exception e)
        {
        }
        return temp;
    }

    /**
     * 把GBK的字串转换为ISO8859_1的字串
     *
     * @param str 要转换的字串
     * @return 转换后的字串
     */
    public static String webDecode(String str)
    {
        String temp = "";
        try
        {
            temp = new String(str.getBytes("GBK"), "ISO8859_1");
        } catch (Exception e)
        {
        }
        return temp;
    }

    /**
     * 把HTML里的SCRIPT转换成可存储的字符串
     *
     * @param str 要转换的字串
     * @return 转换后的字串
     */
    public static String htmlEncode(String str)
    {
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("\"", "&quot;");
        str = str.replaceAll("\t", "		");
        str = str.replaceAll("\r\n", "\n");
        str = str.replaceAll("\n", "<br>");
        str = str.replaceAll(" ", "&nbsp;");
        str = str.replaceAll("'", "''");
        return str;
    }

    /**
     * 把SCRIPT里的HTML转换成可执行字符串
     *
     * @param str 要转换的字串
     * @return 转换后的字串
     */
    public static String htmlDecode(String str)
    {
        str = str.replaceAll("''", "'");
        str = str.replaceAll("&nbsp;", " ");
        str = str.replaceAll("<br>", "\n");
        str = str.replaceAll("\n", "\r\n");
        str = str.replaceAll("		", "\t");
        str = str.replaceAll("&quot;", "\"");
        str = str.replaceAll("&gt;", ">");
        str = str.replaceAll("&lt;", "<");
        str = str.replaceAll("&amp;", "&");
        return str;
    }

    /**
     * 把字串里的单引号转成双的单引号
     *
     * @param str 要转换的字串
     * @return 车换后的字串
     */
    public static String marksEncode(String str)
    {
        str = str.replaceAll("'", "''");
        return str;
    }

    /**
     * 把字串里的双单引号转成单引号
     *
     * @param str 要转换的字串
     * @return 车换后的字串
     */
    public static String marksDecode(String str)
    {
        str = str.replaceAll("''", "'");
        return str;
    }

    /**
     * 把所传入的字串进行URL编码
     *
     * @param val 要编码的字串
     * @return 编码结果
     */
    public static String urlEncode(String val,HttpServletRequest request)
    {
        String temp = "";
        try
        {
            if(isMSBrowser(request)){
                temp = URLEncoder.encode(val, "UTF-8");
            }else{//如果是谷歌、火狐则解析为ISO-8859-1
                temp = new String(val.getBytes("UTF-8"), "ISO-8859-1");
            }
//            temp = URLEncoder.encode(val, "UTF-8");
        } catch (UnsupportedEncodingException uns)
        {
        }
        return temp;
    }

    /**
     * 判断是否为IE浏览器
     * @param request
     * @return
     */
    public static boolean isMSBrowser(HttpServletRequest request) {
        String[] IEBrowserSignals = {"MSIE", "Trident", "Edge"};
        String userAgent = request.getHeader("User-Agent");
        for (String signal : IEBrowserSignals) {
            if (userAgent.contains(signal)){
                return true;
            }
        }
        return false;
    }

    /**
     * 把所传入的URL编码字串进行URL解码
     *
     * @param val 要解码的字串
     * @return 解码结果
     */
    public static String urlDecode(String val)
    {
        String temp = "";
        try
        {
            temp = URLDecoder.decode(val, "UTF-8");
        } catch (UnsupportedEncodingException uns)
        {
        }
        return temp;
    }

    /**
     * 得到GUID
     */
    public static String getUID()
    {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    /**
     * 将字符串进行MD5加密
     *
     * @param s 原字符串
     * @return String 加密过后的字符串
     */
    public static String MD5(String s)
    {
        char hexDigits[] =
                {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
                        'E', 'F'};
        String returnStr = "";
        try
        {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++)
            {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            returnStr = new String(str);
        } catch (Exception e)
        {
            // e.printStackTrace();
        }
        return returnStr;
    }

    /**
     * 字符替换
     *
     * @param rStr 被替换的字符串
     * @param rFix 被替代的字符串
     * @param rRep 替代的字符串
     * @return String 将字符串rStr中的字符串rFix替换成rRep
     */
    public static String replace(String rStr, String rFix, String rRep)
    {
        int idx1 = 0;
        int idx2 = rStr.indexOf(rFix, idx1);
        int len = rFix.length();
        StringBuffer sb = new StringBuffer();
        while (true)
        {
            if (idx2 == -1)
            {
                sb.append(rStr.substring(idx1, rStr.length()));
                break;
            }
            sb.append(rStr.substring(idx1, idx2));
            sb.append(rRep);
            idx1 = idx2 + len;
            idx2 = rStr.indexOf(rFix, idx1);
        }
        String gRtnStr = sb.toString();
        return gRtnStr;
    }

    /**
     * ----
     */
    public static String decode(String s, String encoding) throws Exception
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            switch (c)
            {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    try
                    {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3),
                                16));
                    } catch (NumberFormatException e)
                    {
                        throw new IllegalArgumentException();
                    }
                    i += 2;
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        // Undo conversion to external encoding
        String result = sb.toString();
        byte[] inputBytes = result.getBytes("8859_1");
        return new String(inputBytes, encoding);
    }

    // startTime:HH:mm endTime:HHh:mm
    public static String operationTime(String startTime, String endTime)
    {
        String result = "";
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        try
        {
            Date d1 = df.parse(startTime);
            Date d2 = df.parse(endTime);
            long diff = d2.getTime() - d1.getTime();
            long day = diff / (24 * 60 * 60 * 1000);
            long hour = (diff / (60 * 60 * 1000) - day * 24);
            long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            // long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min
            // * 60);
            result = hour + ":" + min;
        } catch (ParseException e)
        {
            result = "";
        }
        return result;
    }

    public static String subStr(String str, int len)
    {
        if (!"".equals(str) && null != str)
        {
            if (str.length() > len)
            {
                str = str.substring(0, len);
            }
        }
        return str;
    }

    public static boolean isNotNull(String id)
    {
        if (null != id && !id.equals(""))
            return true;
        return false;
    }

    public static String getRandom()
    {
        return getRandom(8);
    }

    public static String getRandom(int length)
    {
        long temp = Math.abs(UUID.randomUUID().getMostSignificantBits());
        String out = String.valueOf(temp);
        if (out.length() > (length + 1))
            out = out.substring(0, (length + 1));
        return out;
    }

    public static int getInt(String value)
    {
        int result = 0;
        try
        {
            result = Integer.valueOf(value);
        } catch (Exception e)
        {

        }
        return result;
    }

    public static double getDouble(String str)
    {
        double result = 0.0;
        try
        {
            result = Double.valueOf(str);
        } catch (Exception e)
        {
            return result;
        }
        return result;
    }

    public static String double2Str(double value)
    {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        return format.format(value);
    }

    public static boolean isNull(String name)
    {
        return null == name || Kit.strTrim(name).equals("");
    }

    public static String Base64(String str)
    {
        String result;
        try
        {
            Base64.Encoder encoder = Base64.getEncoder();
            result = encoder.encodeToString(str.getBytes("UTF-8"));
        } catch (Exception e)
        {
            result = str;
        }
        return result;
    }

    public static boolean isSuccess(int[] result){
        for(int i:result){
            if(i<=0){
                return false;
            }
        }
        return true;
    }

    /**
     * 格式化价格 形如 100.99  小数点位数直接截断
     * @param price
     * @return
     */
    public static String formatPrice(BigDecimal price){
        return price.setScale(2,BigDecimal.ROUND_DOWN).toString();
    }

    /**
     * 格式化消暑  小数点位数直接截断
     * @param price
     * @param scale 小数点位数
     * @return
     */
    public static String formatDecimal(BigDecimal price,int scale){
        return price.setScale(scale,BigDecimal.ROUND_DOWN).toString();
    }

    public static String getPercentage(String value1,String value2,int scale){
        if(value2.equals("0")){
            return "0%";
        }
        return new BigDecimal(value1).multiply(BigDecimal.TEN)
                .multiply(BigDecimal.TEN).divide(new BigDecimal(value2),scale,BigDecimal.ROUND_DOWN).toString()+"%";

    }

    /**
     * 处理返回记录中的null数据
     * @param record
     */
    public static void dealNullData(Record record){
        for (String name:record.getColumnNames()){
            if(record.get(name)==null){
                record.set(name,"");
            }
        }
    }

    /**
     * 去除重复项
     * @param ids
     * @return
     */
    public static Set<String> getDistinct(String[] ids){
        Set<String> set=new HashSet<>();
        set.addAll(Arrays.asList(ids));
        return set;
    }

    /**
     * 转化为in 查询 所用格式('1','2')
     * @param list
     * @return
     */
    public static String getFormatIds(List<String> list) {
        StringBuffer ids = new StringBuffer();
        if(list!=null && list.size()>0){
            for(int i=0;i<list.size();i++){
                ids.append("\'"+list.get(i)+"\'"+ Constant.SEPARATE_SIGN);
            }
            if(ids.length()>0){
                ids.deleteCharAt(ids.length()-1);
            }
        }
        return ids.toString();

    }

//    /**
//     * 获取最大的播放序号
//     * @param maxNo
//     * @param insertType
//     * @return
//     */
//    public static String getPlayIdx(String maxNo, ConstantEnum insertType){
//        if(Kit.isNull(maxNo)){
//            maxNo="000000";
//        }
//        String num=maxNo.substring(1);
//        int newNum=Integer.valueOf(num)+1;
//        String sNewNum=String.format("%0" + 6 + "d", newNum);
//        return (insertType==ConstantEnum.SUPER_PLAY?"0":"1")+sNewNum;
//    }

    public static String dealNull(String  obj){
        return obj==null?"":obj;
    }

    public static String formatIn(String value){
        String[] v = value.split(",");
        value = "";
        for(String s:v){
            value += "'" + s + "',";
        }
        value = value.substring(0, value.lastIndexOf(","));
        return value;
    }

    /**
     * 获取用户的ip地址
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        if (ip.split(",").length > 1) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

}