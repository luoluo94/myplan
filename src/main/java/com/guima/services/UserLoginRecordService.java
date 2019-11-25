package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.UserLoginRecord;

import java.util.UUID;

public class UserLoginRecordService extends BaseService_<UserLoginRecord>
{

    @Override
    protected UserLoginRecord getConreteObject()
    {
        return new UserLoginRecord();
    }

    @Override
    public ModelWrapper<UserLoginRecord> getWrapper(UserLoginRecord userLoginRecord)
    {
        return new ModelWrapper(userLoginRecord);
    }

    @Override
    public UserLoginRecord getDao()
    {
        return UserLoginRecord.dao;
    }

    public UserLoginRecord get(String userId)
    {
        QueryParam param = QueryParam.Builder()
                .equalsTo("creator_id", userId);
        return findFirst(param);
    }

    public void userLoginRecord(String userId){
        try {
            UserLoginRecord userLoginRecord=get(userId);
            if(userLoginRecord==null){
                userLoginRecord=new UserLoginRecord();
                userLoginRecord.setCreatorId(userId);
                userLoginRecord.setBrowseNum(1);
                userLoginRecord.setId(UUID.randomUUID().toString());
                userLoginRecord.save();
            }else{
                userLoginRecord.setBrowseNum(userLoginRecord.getBrowseNum()+1);
                userLoginRecord.update();
            }
        }catch (Exception e){

        }

    }

}
