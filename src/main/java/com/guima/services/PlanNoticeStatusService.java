package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.PlanNotice;
import com.guima.domain.PlanNoticeStatus;
import com.guima.domain.User;
import com.guima.enums.ConstantEnum;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

public class PlanNoticeStatusService extends BaseService_<PlanNoticeStatus>
{
    @Override
    protected PlanNoticeStatus getConreteObject()
    {
        return new PlanNoticeStatus();
    }

    @Override
    public ModelWrapper<PlanNoticeStatus> getWrapper(PlanNoticeStatus m)
    {
        return new ModelWrapper<>(m);
    }


    @Override
    public PlanNoticeStatus getDao()
    {
        return PlanNoticeStatus.dao;
    }

    public Page<Record> listUserNotices(User user, int pageNumber, int pageSize){
        StringBuffer sqlSelect=new StringBuffer().append("select m.id as read_notice_id,n.id as notice_id,")
                .append(" n.title,n.type,n.receiver_type,n.content,n.create_date,n.end_date,n.is_active,m.read_status ");

        StringBuffer conditionSql=new StringBuffer();
        conditionSql.append(" from plan_notice_status m join plan_notice n on m.notice_id=n.id")
                .append(" where 1=1 ")
                .append(" and m.creator=?")
                .append(" and n.is_active=?")
                .append(" order by create_time desc");
        List<Object> params=new ArrayList<>();
        params.add(user.getId());
        params.add(Constant.ACTIVE);
        return Db.paginate(pageNumber,pageSize,sqlSelect.toString(),conditionSql.toString(),params.toArray());
    }

    public Long getMyUnReadNum(User user){
        StringBuffer sql=new StringBuffer();
        sql.append(" select count(1) from plan_notice_status m ")
                .append(" join plan_notice n on n.id=m.notice_id")
                .append(" where 1=1 ")
                .append(" and m.creator=?")
                .append(" and m.read_status=0")
                .append(" and n.is_active=?");
        List<Object> params=new ArrayList<>();
        params.add(user.getId());
        params.add(Constant.ACTIVE);
        return Db.queryLong(sql.toString(),params.toArray());
    }

    public List<String> listMyNotices(User user){
        StringBuffer sql=new StringBuffer();
        sql.append("select id from plan_notice p where ")
                .append(" p.receiver_type=?")
                .append(" and p.is_active=0 and p.is_deleted=0")
                .append(" and p.create_date<=?")
                .append(" and p.end_date>=?")
                .append(" and p.id not in(")
                .append(" select notice_id from plan_notice_status where creator=?")
                .append(")");
        List<Object> params=new ArrayList<>();
        params.add(ConstantEnum.PRIVACY_PUBLIC.getValue());
        params.add(DateKit.getToday());
        params.add(DateKit.getToday());
        params.add(user.getId());
        return Db.query(sql.toString(),params.toArray());
    }

    public void createMyNotices(User user){
        List<String> notices=listMyNotices(user);
        PlanNoticeStatus planNoticeStatus;
        for(String planNoticeId:notices){
            planNoticeStatus=new PlanNoticeStatus();
            planNoticeStatus.init(user.getId(),planNoticeId);
            planNoticeStatus.save();
        }
    }

    public PlanNoticeStatus find(User user,String noticeId){
        QueryParam queryParam=QueryParam.Builder();
        queryParam.equalsTo("creator",user.getId());
        queryParam.equalsTo("notice_id",noticeId);
        return findFirst(queryParam);
    }

}
