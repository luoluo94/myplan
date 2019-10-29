package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.Admin;
import com.guima.domain.Admin;

public class AdminService extends BaseService_<Admin>
{

    @Override
    protected Admin getConreteObject()
    {
        return new Admin();
    }

    @Override
    public ModelWrapper<Admin> getWrapper(Admin Admin)
    {
        return new ModelWrapper(Admin);
    }

    @Override
    public Admin getDao()
    {
        return Admin.dao;
    }

    public Admin get(String userName,String password)
    {
        QueryParam param = QueryParam.Builder()
                .equalsTo("user_name", userName)
                .equalsTo("password",password);
        return findFirst(param);
    }

}
