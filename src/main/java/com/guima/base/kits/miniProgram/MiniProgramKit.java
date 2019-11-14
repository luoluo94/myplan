package com.guima.base.kits.miniProgram;

import com.guima.kits.EncryptionKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.ApiConfig;

/**
 * Created by Ran on 2018/5/18.
 */
public class MiniProgramKit {

    /**
     * 获取登录信息
     * @param code
     * @return
     */
    public static MiniProgramLoginInfo getLoginInfo(String code){
        ApiConfig apiConfig=getApiConfig();
        return MiniProgramApi.getLoginInfo(apiConfig.getAppId(), apiConfig.getAppSecret(), code);
    }

    /**
     * 获取AccessToken
     * @return
     */
    public static String getAccessToken(){
        ApiConfig apiConfig=getApiConfig();
        return MiniProgramApi.getAccessToken(apiConfig.getAppId(), apiConfig.getAppSecret());
    }

    /**
     * 获取小程序码
     */
    public static String getQrcode(String scene, String page){
        ApiConfig apiConfig=getApiConfig();
        return MiniProgramApi.getQrcode(apiConfig.getAppId(), apiConfig.getAppSecret(),scene,page,430);
    }

    private static ApiConfig getApiConfig() {
        ApiConfig ac = new ApiConfig();
        Prop prop=PropKit.use("miniProgramConfig.properties");
        ac.setAppId(EncryptionKit.decryptByAES(prop.get("appId")));
        ac.setAppSecret(EncryptionKit.decryptByAES(prop.get("appSecret")));
        return ac;
    }


    public static void main(String[] args) {
        System.out.println(getAccessToken());
    }
}
