package com.guima.kits;

/**
 * Created by Ran on 2018/4/27.
 * 获取某些常用的sql
 */
public class SqlKit {

    public static String doNull(String attr,String alias){
        return "IFNULL("+attr+",'') as "+alias;
    }

    public static String getActivityStatus(String tableAlias){
        return "(case "+tableAlias+".`status` when '0' then '正在进行' when '2' then  '已结束' end ) as status_desc";
    }

    public static String getYesOrNo(String tableAlias,String attrName){
        return "(case "+tableAlias+" when '0' then '是' when '1' then  '否' end ) as "+attrName;
    }



}
