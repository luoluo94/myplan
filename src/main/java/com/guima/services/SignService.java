package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.Sign;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.jfinal.plugin.activerecord.Page;

import java.util.Calendar;

public class SignService extends BaseService_<Sign>
{

    @Override
    protected Sign getConreteObject()
    {
        return new Sign();
    }

    @Override
    public ModelWrapper<Sign> getWrapper(Sign sign)
    {
        return new ModelWrapper(sign);
    }

    @Override
    public Sign getDao()
    {
        return Sign.dao;
    }

    public Page<Sign> listPublicSigns(String pageNumberStr, String pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.gt("create_time", DateKit.getStartTime());
        param.equalsTo(Constant.IS_DELETED_MARK,Constant.ACTIVE);
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr, pageSizeStr);
    }

    public Page<Sign> listMySigns(User user,String pageNumberStr, String pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("creator", user.getId());
        param.equalsTo(Constant.IS_DELETED_MARK,Constant.ACTIVE);
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr, pageSizeStr);
    }

    public Page<Sign> listAllSigns(String pageNumberStr, String pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr, pageSizeStr);
    }

}
