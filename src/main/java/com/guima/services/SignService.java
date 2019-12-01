package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.Sign;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    private Page<Record> list(int pageNumber, int pageSize,StringBuffer conditionSql,List<Object> params){
        StringBuffer sqlSelect=new StringBuffer().append("select m.id,m.describer,m.creator,m.is_deleted,m.photo_url,m.create_time,m.privacy,")
                .append(" n.name as creator_name,n.header_url as creator_header_url");

        return Db.paginate(pageNumber,pageSize,sqlSelect.toString(),conditionSql.toString(),params.toArray());
    }

    public Page<Record> listPublicSigns(int pageNumber, int pageSize){
        StringBuffer sql=new StringBuffer();
        sql.append(" from sign m join user n on m.creator=n.id")
                .append(" where 1=1 ")
                .append(" and m.").append(Constant.IS_DELETED_MARK).append("=?")
                .append(" order by create_time desc");
        List<Object> params=new ArrayList<>();
        params.add(Constant.ACTIVE);
        return list(pageNumber,pageSize,sql,params);
    }

    public Page<Record> listMySigns(User user,int pageNumber, int pageSize){
        StringBuffer sql=new StringBuffer();
        sql.append(" from sign m join user n on m.creator=n.id")
                .append(" where 1=1 ")
                .append(" and m.creator=?")
                .append(" and m.").append(Constant.IS_DELETED_MARK).append("=?")
                .append(" order by create_time desc");
        List<Object> params=new ArrayList<>();
        params.add(user.getId());
        params.add(Constant.ACTIVE);
        return list(pageNumber,pageSize,sql,params);
    }

    public Page<Record> listAllSigns(int pageNumber, int pageSize){
        StringBuffer sql=new StringBuffer();
        sql.append(" from sign m join user n on m.creator=n.id")
                .append(" where 1=1 ")
                .append(" order by create_time desc");
        return list(pageNumber,pageSize,sql,new ArrayList<>());
    }

    public Page<Sign> listAllSigns(String pageNumberStr, String pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr, pageSizeStr);
    }

}
