package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Ad;
import com.guima.domain.Advice;
import com.guima.domain.Sign;
import com.guima.domain.User;
import com.guima.enums.ConstantEnum;
import com.guima.kits.Constant;
import com.guima.services.AdviceService;
import com.guima.services.ScoreRecordService;
import com.guima.services.SignService;
import com.guima.services.UserService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.taobao.api.internal.toplink.embedded.websocket.util.StringUtil;
import com.taobao.api.internal.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ran on 2019/8/30.
 */
public class AdviceController extends BaseController{

    private AdviceService adviceService;

    public AdviceController()
    {
        adviceService=((AdviceService) ServiceManager.instance().getService("advice"));
    }

    /**
     * 创建问题
     */
    public void saveAdvice(){
        User user=getMyUser();
        checkUser(user);
        String content=getPara("content");
        if(StrKit.isBlank(content)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        Advice advice=new Advice();
        advice.init(user.getHeaderUrl(),user.getId(),user.getName(),content);
        advice.save();
        doRender(StrKit.notBlank(advice.getId()));
    }

    /**
     * 获取问题反馈列表 时间倒序
     */
    public void listMyAdvices(){
        User user=getMyUser();
        checkUser(user);
        Page<Advice> page=adviceService.listAdvices(user,getPageNumber(),getPageSize());
        List<Map> data=new ArrayList<>();
        List<Advice> advices=page.getList();
        Map map;
        for (Advice advice:advices){
            map=new HashMap<>();
            map.put("create_time",advice.getCreateTime());
            map.put("content",advice.getAdviceContent());
            map.put("creator_header_url",advice.getCreatorHeaderUrl());
            map.put("is_official",false);
            data.add(map);
            if(!StringUtils.isEmpty(advice.getReply())){
                map=new HashMap<>();
                map.put("create_time",advice.getReplyTime());
                map.put("content",advice.getReply());
                map.put("creator_header_url","https://yaoplan.oss-cn-beijing.aliyuncs.com/admin_images/flag_official.jpg");
                map.put("is_official",true);
                data.add(map);
            }
        }
        doRenderSuccess(data);
    }
}
