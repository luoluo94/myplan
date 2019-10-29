package com.guima.base.kits.miniProgram;

import com.alibaba.fastjson.JSONObject;
import com.guima.kits.FileKit;
import com.jfinal.weixin.sdk.kit.ParaMap;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.jfinal.weixin.sdk.utils.RetryUtils;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by Ran on 2018/5/18.
 */
public class MiniProgramApi {

    private static String url = "https://api.weixin.qq.com/sns/jscode2session?grant_type=authorization_code";
    private static String accessTokenUri = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
    private static String qrcodeUri ="https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=";

    public MiniProgramApi() {
    }

    /**
     * 获取AccessToken
     * @param appId
     * @param secret
     * @return
     */
    public static String getAccessToken(String appId, String secret) {
        final Map queryParas = ParaMap.create("appid", appId).put("secret", secret).getData();
        String json = HttpUtils.get(accessTokenUri, queryParas);
        JSONObject jsonObject=JSONObject.parseObject(json);
        return jsonObject.getString("access_token");
    }

    /**
     * 获取小程序码
     * @return
     */
    public static String getQrcode(String appId, String secret,String scene,String page,int width) {
        String accessToken=getAccessToken(appId,secret);
        JSONObject queryParas=new JSONObject();
        queryParas.put("scene",scene);
        queryParas.put("page",page);
        queryParas.put("width",width);
        InputStream inputStream = HttpUtils.download(qrcodeUri+accessToken, queryParas.toString());
        return FileKit.uploadInputStream(inputStream,null,"png");
    }


    /**
     * 获取登录信息 openid等
     * @param appId
     * @param secret
     * @param code
     * @return
     */
    public static MiniProgramLoginInfo getLoginInfo(String appId, String secret, String code) {
        final Map queryParas = ParaMap.create("appid", appId).put("secret", secret).put("js_code", code).getData();
        return RetryUtils.retryOnException(3, () ->{
                String json = HttpUtils.get(url, queryParas);
                return new MiniProgramLoginInfo(json);
        });
    }

}
