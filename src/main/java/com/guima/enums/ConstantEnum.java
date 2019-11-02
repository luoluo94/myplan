package com.guima.enums;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by Ran on 2018/6/16.
 */
public enum ConstantEnum {

    CATEGORY_DAILY("日常","0"),
    CATEGORY_SPORT("健身","1"),
    CATEGORY_STUDY("学习","2"),
    PRIVACY_SELF("仅自己可见","0"),
    PRIVACY_FRIENDS("仅好友可见","1"),
    PRIVACY_PUBLIC("公开","2"),
    STATUS_ONGOING("进行中","1"),
    STATUS_FINISH("已完成","2"),
    STATUS_DRAFT("草稿箱","0"),
    STATUS_NOT_FINISH("未完成","4"),
    STATUS_END("已结束","3");

    private String desc;
    private String value;

    ConstantEnum(String desc, String value){
        this.desc = desc;
        this.value = value;
    }
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static JsonArray list(ConstantEnum[] types){
        JsonArray jsonArray=new JsonArray();
        for(ConstantEnum type:types){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("value",type.getValue());
            jsonObject.addProperty("desc",type.getDesc());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
