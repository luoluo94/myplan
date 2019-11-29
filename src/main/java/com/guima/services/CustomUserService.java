package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.CustomUser;
import com.guima.domain.User;
import com.jfinal.plugin.activerecord.Page;
import org.apache.commons.lang3.StringUtils;

public class CustomUserService extends BaseService_<CustomUser>
{

    @Override
    protected CustomUser getConreteObject()
    {
        return new CustomUser();
    }

    @Override
    public ModelWrapper<CustomUser> getWrapper(CustomUser customUser)
    {
        return new ModelWrapper(customUser);
    }

    @Override
    public CustomUser getDao()
    {
        return CustomUser.dao;
    }

}
