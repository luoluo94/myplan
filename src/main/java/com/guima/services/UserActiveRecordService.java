package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.base.service.ServiceManager;
import com.guima.domain.User;
import com.guima.domain.UserActiveRecord;
import com.guima.domain.UserActiveRecord;
import com.guima.kits.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserActiveRecordService extends BaseService_<UserActiveRecord>
{

    @Override
    protected UserActiveRecord getConreteObject()
    {
        return new UserActiveRecord();
    }

    @Override
    public ModelWrapper<UserActiveRecord> getWrapper(UserActiveRecord userActiveRecord)
    {
        return new ModelWrapper(userActiveRecord);
    }

    @Override
    public UserActiveRecord getDao()
    {
        return UserActiveRecord.dao;
    }

    public List<UserActiveRecord> listUserActiveRecords(User user,String month){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("user_id",user.getId());
        param.like("date",month+'%');
        param.ascBy("date");
        return list(param);
    }

    public UserActiveRecord get(String userId,String date){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("user_id",userId);
        param.equalsTo("date",date);
        return findFirst(param);
    }

    public boolean create(String userId){
        String today=DateKit.getToday();
        UserActiveRecord userActiveRecord=get(userId,today);
        if(userActiveRecord==null){
            PlanCalendarService planCalendarService=((PlanCalendarService)ServiceManager.instance().getService("plancalendar"));;
            return new UserActiveRecord(userId,today).save() && planCalendarService.updateActiveDays(userId);
        }
        return true;
    }

}
