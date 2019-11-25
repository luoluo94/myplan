package com.guima.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.cache.RedisCacheManager;
import com.guima.enums.ConstantEnum;
import com.guima.kits.Constant;
import com.guima.kits.Kit;
import com.guima.services.DictionaryService;

import java.util.EnumSet;

/**
 * Created by Ran on 2018/5/19.
 */
public class DictionaryController extends BaseController{

    private final DictionaryService s;

    public DictionaryController()
    {
        this.s = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
    }

    public void list(){
        String type=getPara("type");
        if(Kit.isNull(type)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        doRenderSuccess(s.listType(type,null));
    }

    public void getMusicTheme(){
        doRenderSuccess(s.listBackgroundMusicTheme());
    }

    /**
     * 获取隐私设置
     */
    public void getPrivacy(){
        ConstantEnum[] privacy={ConstantEnum.PRIVACY_SELF,ConstantEnum.PRIVACY_FRIENDS,ConstantEnum.PRIVACY_PUBLIC};
        doRenderJson(ConstantEnum.list(privacy));
    }

    /**
     * 获取分类
     */
    public void getCategory(){
        ConstantEnum[] category={ConstantEnum.CATEGORY_DAILY,ConstantEnum.CATEGORY_SPORT,ConstantEnum.CATEGORY_STUDY};
        doRenderJson(ConstantEnum.list(category));
    }

}
