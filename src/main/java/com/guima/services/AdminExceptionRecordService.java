package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.Plan;
import com.guima.domain.User;
import com.guima.domain.AdminExceptionRecord;
import com.guima.enums.ConstantEnum;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import java.util.List;

public class AdminExceptionRecordService extends BaseService_<AdminExceptionRecord>
{

    @Override
    protected AdminExceptionRecord getConreteObject()
    {
        return new AdminExceptionRecord();
    }

    @Override
    public ModelWrapper<AdminExceptionRecord> getWrapper(AdminExceptionRecord adminExceptionRecord)
    {
        return new ModelWrapper(adminExceptionRecord);
    }

    @Override
    public AdminExceptionRecord getDao()
    {
        return AdminExceptionRecord.dao;
    }

    public List<AdminExceptionRecord> list(String creator)
    {
        QueryParam param = QueryParam.Builder()
                .equalsTo("creator", creator);
        return list(param);
    }

    public Page<AdminExceptionRecord> pageList(String pageNumberStr, String pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr, pageSizeStr);
    }

}
