package com.guima.kits;

import com.guima.base.kits.SysMsg;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ran on 2018/1/25.
 */
public class ShowInfoKit {

    public static Map<String,Object> getErrorMap(String message){
        return getErrorMap("",false,message);
    }

    public static Map<String,Object> getErrorMap(Object data,boolean isSuccess,String message){
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("data",data);
        map.put("is_success",isSuccess);
        map.put("message",Kit.isNull(message)?(isSuccess?"操作成功": SysMsg.OsMsg.get("ERROR")):message);
        return map;
    }
}
