package com.guima.kits;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ran on 2018/1/5.
 */
public class ErrorInfoKit {

    public static Map<String,Object> getErrorMap(String message){
        return getErrorMap("",false,message);
    }

    public static Map<String,Object> getErrorMap(Object data,boolean isSuccess,String message){
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("data",data);
        map.put("issuccess",isSuccess);
        map.put("message",message);
        return map;
    }
}
