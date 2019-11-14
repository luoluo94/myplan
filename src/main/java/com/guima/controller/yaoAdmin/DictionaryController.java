package com.guima.controller.yaoAdmin;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.cache.RedisCacheManager;
import com.guima.domain.InterfaceConfig;
import com.guima.domain.UserRecord;
import com.guima.kits.Kit;
import com.guima.services.DictionaryService;
import com.guima.services.InterfaceConfigService;
import com.jfinal.plugin.activerecord.Page;


/**
 * Created by Ran on 2018/5/19.
 */
public class DictionaryController extends BaseController{

    private final DictionaryService dictionaryService;
    private final InterfaceConfigService interfaceConfigService;

    public DictionaryController()
    {
        dictionaryService = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
        interfaceConfigService = ((InterfaceConfigService) ServiceManager.instance().getService("interfaceconfig"));
        this.s = dictionaryService;
    }


    public void list(){
        String type=getPara("type");
        if(Kit.isNull(type)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        doRenderSuccess(dictionaryService.list(type,getPara("background"),getPara("name")));
    }

    public void reset()
    {
        RedisCacheManager.instance().reloadAllCache();
        renderText("ok");
    }

    /**
     * 列出所有接口配置
     */
    public void listConfigs(){
        Page<InterfaceConfig> interfaceConfigPage=interfaceConfigService.pageList(getPageNumber(),getPageSize());
        doRenderSuccess(interfaceConfigPage);
    }
}
