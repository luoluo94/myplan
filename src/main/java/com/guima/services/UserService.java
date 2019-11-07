package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.base.service.ServiceManager;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.guima.kits.Kit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigInteger;
import java.util.List;

public class UserService extends BaseService_<User>
{
    @Override
    protected User getConreteObject()
    {
        return new User();
    }

    @Override
    public ModelWrapper<User> getWrapper(User m)
    {
        return new ModelWrapper<>(m);
    }


    @Override
    public User getDao()
    {
        return User.dao;
    }

    public boolean createUser(User user){
        PlanCalendarService planCalendarService=((PlanCalendarService) ServiceManager.instance().getService("plancalendar"));
        return Db.tx(()->{
            return user.superSave() && planCalendarService.savePlanCalendar(user.getId());
        });
    }

    public Page<User> pageList(String name,int pageNumberStr, int pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("status", Constant.ACTIVE);
        if(StrKit.notBlank(name)){
            param.like("name","%"+name+"%");
        }
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr+"", pageSizeStr+"");
    }

    public User findByOpenid(String openid)
    {
        QueryParam param = QueryParam.Builder().equalsTo("open_id", openid);
        return findFirst(param);
    }

    public User findByToken(String token)
    {
        QueryParam param = QueryParam.Builder().equalsTo("token", token);
        return findFirst(param);
    }

    public String getHotPointPercentage(User user){
        StringBuffer sql=new StringBuffer();
        sql.append(" select count(*) as num from user u")
                .append(" where 1=1 ")
                .append(" and u.province=?")
                .append(" and u.hot_point<").append(Integer.parseInt(user.getHotPoint()))
                .append(" union all ")
                .append(" select count(*) as num from user u")
                .append(" where u.province=? ");
        List<Long> result=Db.query(sql.toString(),new String[]{user.getProvince(),user.getProvince() });
        return Kit.getPercentage(String.valueOf(result.get(0)),String.valueOf(result.get(1)),0);
    }


}
