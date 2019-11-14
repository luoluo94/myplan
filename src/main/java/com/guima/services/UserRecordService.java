package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.User;
import com.guima.domain.UserRecord;
import com.guima.kits.Constant;
import com.jfinal.plugin.activerecord.Db;

import java.util.List;

public class UserRecordService extends BaseService_<UserRecord>
{

    @Override
    protected UserRecord getConreteObject()
    {
        return new UserRecord();
    }

    @Override
    public ModelWrapper<UserRecord> getWrapper(UserRecord userRecord)
    {
        return new ModelWrapper(userRecord);
    }

    @Override
    public UserRecord getDao()
    {
        return UserRecord.dao;
    }

    public List<UserRecord> list(String creator)
    {
        QueryParam param = QueryParam.Builder()
                .equalsTo("creator", creator);
        return list(param);
    }

    /**
     * 禁止用户/解禁用户
     * @param userRecord
     * @param user
     * @return
     */
    public boolean banned(UserRecord userRecord, User user,boolean isBanned){
        return Db.tx(()->{
            user.setBanned(isBanned?Constant.MARK_ONE:Constant.MARK_ZERO);
            return userRecord.save() && user.update();
        });
    }

}
