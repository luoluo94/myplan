package com.guima.base.kits.miniProgram;

import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.utils.JsonUtils;
import com.jfinal.weixin.sdk.utils.RetryUtils;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by Ran on 2018/5/18.
 */
public class MiniProgramLoginInfo implements RetryUtils.ResultCheck, Serializable {

    private String openid;
    private String unionid;
    private Integer errcode;
    private String errmsg;
    private String sessionKey;
    private String json;

    public MiniProgramLoginInfo(String jsonStr) {
        this.json = jsonStr;

        try {
            Map e =JsonUtils.parse(jsonStr, Map.class);
            this.openid = (String)e.get("openid");
            this.unionid = (String)e.get("unionid");
            this.errcode = this.getInt(e, "errcode");
            this.errmsg = (String)e.get("errmsg");
            this.sessionKey=(String)e.get("session_key");
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    public String getJson() {
        return this.json;
    }

    private Integer getInt(Map<String, Object> temp, String key) {
        Number number = (Number)temp.get(key);
        return number == null?null:Integer.valueOf(number.intValue());
    }

    public String getOpenid() {
        return this.openid;
    }

    public Integer getErrorCode() {
        return this.errcode;
    }

    public String getErrorMsg() {
        return this.errmsg;
    }

    public String getUnionid() {
        return this.unionid;
    }

    public boolean matching() {
        return StrKit.notBlank(this.openid);
    }

    public String getSessionKey() {
        return this.sessionKey;
    }
}
