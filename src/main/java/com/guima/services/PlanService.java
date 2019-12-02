package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.base.service.ServiceManager;
import com.guima.domain.*;
import com.guima.enums.ConstantEnum;
import com.guima.enums.ScoreTypeEnum;
import com.guima.kits.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.util.*;

public class PlanService extends BaseService_<Plan>
{

    private StringBuffer sqlSelect=new StringBuffer().append("select m.id,m.title,m.start_date,m.end_date,m.privacy,m.status,m.creator,m.create_time,")
            .append("m.like_num,")
            .append(" n.name as creator_name,n.header_url as creator_header_url");

    @Override
    protected Plan getConreteObject()
    {
        return new Plan();
    }

    @Override
    public ModelWrapper<Plan> getWrapper(Plan Plan)
    {
        return new ModelWrapper(Plan);
    }

    @Override
    public Plan getDao()
    {
        return Plan.dao;
    }

    private Page<Record> listPlans(int pageNum, int pageSize,StringBuffer conditionSql,List<Object> params){
        StringBuffer sql=new StringBuffer();
        sql.append(" from plan m join user n on m.creator=n.id")
                .append(" where 1=1 ")
                .append(conditionSql);
        return Db.paginate(pageNum,pageSize,sqlSelect.toString(),sql.toString(),params.toArray());
    }

    public Record findRecordById(String id){
        StringBuffer sql=new StringBuffer().append(sqlSelect).append(" from plan m join `user` n on m.creator=n.id where m.id=?");
        List<Object> params=new ArrayList<>();
        params.add(id);
        return Db.findFirst(sql.toString(),params.toArray());
    }

    public Page<Record> listPublicPlans(int pageNum, int pageSize){
        StringBuffer sql=new StringBuffer();
                sql.append(" and m.privacy=?")
                .append(" and m.").append(Constant.IS_DELETED_MARK).append("=?")
                .append(" order by m.create_time desc");
        List<Object> params=new ArrayList<>();
        params.add(ConstantEnum.PRIVACY_PUBLIC.getValue());
        params.add(Constant.ACTIVE);
        return listPlans(pageNum,pageSize,sql,params);
    }

    public Page<Record> listCustomPlans(int pageNum, int pageSize){
        StringBuffer sql=new StringBuffer().append(" and m.status='1' ")
                .append("and m.creator in( select id from custom_user) ");
        return listPlans(pageNum,pageSize,sql,new ArrayList<>());
    }

    public Page<Record> listAllPlans(int pageNum, int pageSize){
        StringBuffer sql=new StringBuffer().append(" order by m.create_time desc");
        return listPlans(pageNum,pageSize,sql,new ArrayList<>());
    }

    public Page<Record> listMyPlans(User user,String status, int pageNumber, int pageSize){
        StringBuffer sql=new StringBuffer();
        List<Object> params=new ArrayList<>();
        sql.append("and m.creator=? ");
        params.add(user.getId());
        if(ConstantEnum.STATUS_END.getValue().equals(status)){
            sql.append(" and m.status in (").append(ConstantEnum.STATUS_FINISH.getValue()).append(",").append(ConstantEnum.STATUS_NOT_FINISH.getValue()).append(")");
        }else {
            sql.append(" and m.status=?");
            params.add(status);
        }
        sql.append(" and m.").append(Constant.IS_DELETED_MARK).append("=?")
                .append(" order by m.create_time desc");
        params.add(Constant.ACTIVE);
        return listPlans(pageNumber,pageSize,sql,params);
    }

    /**
     * 创建/编辑计划
     * @param plan
     * @return
     */
    public boolean createPlan(Plan plan,String[] details){
        PlanDetailService planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        PlanCalendarService planCalendarService=((PlanCalendarService)ServiceManager.instance().getService("plancalendar"));
        return Db.tx(()->{
            boolean isSuccess=false;
            //创建计划
            if(Kit.isNull(plan.getId())){
                isSuccess=plan.save() && planCalendarService.updateCreatePlanNum(plan.getCreator());
            }else {
                isSuccess=plan.update();
            }
            //创建计划的具体事项
            return isSuccess && planDetailService.createPlanDetail(plan,details);
        });
    }

    /**
     * 全部标记完成
     * @param plan
     * @return
     */
    public boolean markFinish(Plan plan){
        PlanDetailService planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        PlanCalendarService planCalendarService=((PlanCalendarService) ServiceManager.instance().getService("plancalendar"));
        return Db.tx(()->{
            //将各事项标记为完成
            boolean isDetailFinish=planDetailService.markFinish(plan.getId());
            //将计划标记完成
            plan.setStatus(ConstantEnum.STATUS_FINISH.getValue());
            boolean isPlanFinish=plan.update();
            //增加得分
            return isDetailFinish && isPlanFinish && planCalendarService.updateFinishPlanNum(plan.getCreator());
        });
    }

    /**
     * 全部标记完成
     * @param plan
     * @return
     */
    public boolean markFinish2(Plan plan){
        PlanDetailService planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        PlanCalendarService planCalendarService=((PlanCalendarService) ServiceManager.instance().getService("plancalendar"));
        return Db.tx(()->{
            //将各事项标记为完成
            boolean isDetailFinish=planDetailService.markFinish2(plan.getId());
            //将计划标记完成
            plan.setStatus(ConstantEnum.STATUS_FINISH.getValue());
            boolean isPlanFinish=plan.update();
            //增加得分
            return isDetailFinish && isPlanFinish && planCalendarService.updateFinishPlanNum(plan.getCreator());
        });
    }

    /**
     * 标记未完成
     * @return
     */
    public boolean markUnFinish(Plan plan){
        PlanCalendarService planCalendarService=((PlanCalendarService) ServiceManager.instance().getService("plancalendar"));
        return Db.tx(()->{
            plan.setStatus(ConstantEnum.STATUS_NOT_FINISH.getValue());
            return plan.update() && planCalendarService.updateUnFinishPlanNum(plan.getCreator());
        });
    }

    /**
     * 点赞
     * @param planComment
     * @param plan
     * @param user
     * @return
     */
    public Integer doLike(PlanComment planComment,Plan plan,User user){
        DoLikeService doLikeService=((DoLikeService) ServiceManager.instance().getService("dolike"));
        DoLike doLike=doLikeService.findDoLike(plan.getId(),user.getId());
        planComment.setId(UUID.randomUUID().toString());
        if(doLike==null){
            DoLike like=new DoLike();
            like.setCreateTime(new Date());
            like.setPlanId(plan.getId());
            like.setCreatorId(user.getId());
            like.setId(UUID.randomUUID().toString());

            plan.setLikeNum(plan.getLikeNum()+1);
            Db.tx(()->{
                return planComment.save() && plan.update() && like.save();
            });

        }
        return plan.getLikeNum();
    }

}
