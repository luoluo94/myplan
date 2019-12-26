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

    private StringBuffer sqlSelect=new StringBuffer().append("select m.id,m.title,m.status,m.creator")
            .append(",m.create_time,m.start_date,m.end_date,m.participant_num")
            .append(",n.name as creator_name,n.header_url as creator_header_url");

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
        StringBuffer sqlFullSelect=new StringBuffer().append("select m.id,m.title,m.start_date,m.end_date,m.privacy,m.status,m.creator,m.create_time,")
                .append("m.like_num,m.participant_num,m.finish_num,m.un_finish_num,")
                .append(" n.name as creator_name,n.header_url as creator_header_url");
        StringBuffer sql=sqlFullSelect.append(" from plan m join `user` n on m.creator=n.id where m.id=?");
        List<Object> params=new ArrayList<>();
        params.add(id);
        return Db.findFirst(sql.toString(),params.toArray());
    }

    public Page<Record> listPublicPlans(int pageNum, int pageSize){
        StringBuffer sql=new StringBuffer();
                sql.append(" and m.privacy=?")
                        .append(" and m.is_official=?")
                .append(" and m.").append(Constant.IS_DELETED_MARK).append("=?")
                .append(" order by m.create_time desc");
        List<Object> params=new ArrayList<>();
        params.add(ConstantEnum.PRIVACY_PUBLIC.getValue());
        params.add(Constant.ACTIVE);
        params.add(Constant.ACTIVE);
        return listPlans(pageNum,pageSize,sql,params);
    }

    public Page<Record> listUnOfficialPlans(String creatorId, int pageNumber, int pageSize){
        StringBuffer sql=new StringBuffer();
        List<Object> params=new ArrayList<>();
        sql.append("and m.creator=? ");
        params.add(creatorId);
        sql.append("and m.is_official=? ");
        params.add(Constant.MARK_ZERO);
        sql.append(" and m.status=?");
        params.add(ConstantEnum.STATUS_ONGOING.getValue());
        return listPlans(pageNumber,pageSize,sql,params);
    }

    public Page<Record> listAllPlans(int pageNum, int pageSize){
        StringBuffer sql=new StringBuffer().append(" order by m.create_time desc");
        return listPlans(pageNum,pageSize,sql,new ArrayList<>());
    }

    public Page<Record> listMyPlans(boolean isChallenge,User user,String status, int pageNumber, int pageSize){
        StringBuffer sql=new StringBuffer();
        List<Object> params=new ArrayList<>();
        if(user!=null){
            sql.append(" and m.creator=? ");
            params.add(user.getId());
        }
        sql.append(" and m.is_official=? ");
        params.add(Constant.MARK_ZERO);
        if(isChallenge){
            sql.append(" and m.parent_id is not null ");
        }else{
            sql.append(" and m.parent_id is null ");
        }
        listByStatus(status, sql, params);
        return listPlans(pageNumber,pageSize,sql,params);
    }

    private void listByStatus(String status, StringBuffer sql, List<Object> params) {
        if(ConstantEnum.STATUS_END.getValue().equals(status)){
            sql.append(" and m.status in (").append(ConstantEnum.STATUS_FINISH.getValue()).append(",")
                    .append(ConstantEnum.STATUS_NOT_FINISH.getValue()).append(",")
                    .append(ConstantEnum.STATUS_END.getValue()).append(")");
        }else {
            sql.append(" and m.status=?");
            params.add(status);
        }
        sql.append(" and m.").append(Constant.IS_DELETED_MARK).append("=?")
                .append(" order by m.create_time desc");
        params.add(Constant.ACTIVE);
    }

    /**
     * 获取我挑战的计划
     * @param user
     * @param status
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Record> listMyChallengePlans(User user,String status, int pageNumber, int pageSize){
        return listMyPlans(true,user,status,pageNumber,pageSize);
    }

    /**
     * 获取我自己创建的计划
     * @param user
     * @param status
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Record> listMyOwenPlans(User user,String status, int pageNumber, int pageSize){
        return listMyPlans(false,user,status,pageNumber,pageSize);
    }

    /**
     * 获取官方创建的计划
     * @param status
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Record> listOfficialPlans(String status, int pageNumber, int pageSize){
        StringBuffer sql=new StringBuffer();
        List<Object> params=new ArrayList<>();
        sql.append(" and m.is_official=? ");
        params.add(Constant.MARK_ONE);
        listByStatus(status, sql, params);
        return listPlans(pageNumber,pageSize,sql,params);
    }

    /**
     * 创建/编辑计划
     * @param plan
     * @return
     */
    public boolean createPlan(Plan plan,String[] details,String[] planDetailIds){
        PlanDetailService planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        PlanCalendarService planCalendarService=((PlanCalendarService)ServiceManager.instance().getService("plancalendar"));
        UserActiveRecordService userActiveRecordService=((UserActiveRecordService)ServiceManager.instance().getService("useractiverecord"));
        return Db.tx(()->{
            boolean isSuccess=false;
            //创建计划
            if(Kit.isNull(plan.getId())){
                isSuccess=plan.save() && planCalendarService.updateCreatePlanNum(plan.getCreator()) && userActiveRecordService.create(plan.getCreator())
                && planDetailService.createPlanDetail(plan,details);
            }else {
                //编辑计划
                isSuccess=plan.update() && planDetailService.editPlanDetail(plan,details,planDetailIds);
            }
            //创建计划的具体事项
            return isSuccess;
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
        UserActiveRecordService userActiveRecordService=((UserActiveRecordService) ServiceManager.instance().getService("useractiverecord"));
        return Db.tx(()->{
            //将各事项标记为完成
            boolean isDetailFinish=planDetailService.markFinish(plan.getId());
            //将计划标记完成
            plan.setStatus(ConstantEnum.STATUS_FINISH.getValue());
            boolean isPlanFinish=plan.update();
            //增加得分
            return isDetailFinish && isPlanFinish && planCalendarService.updateFinishPlanNum(plan.getCreator()) && userActiveRecordService.create(plan.getCreator());
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
        UserActiveRecordService userActiveRecordService=((UserActiveRecordService) ServiceManager.instance().getService("useractiverecord"));
        return Db.tx(()->{
            //将各事项标记为完成
            boolean isDetailFinish=planDetailService.markFinish2(plan.getId());
            //将计划标记完成
            plan.setStatus(ConstantEnum.STATUS_FINISH.getValue());
            boolean isPlanFinish=plan.update();
            //增加得分
            return isDetailFinish && isPlanFinish && planCalendarService.updateFinishPlanNum(plan.getCreator()) && userActiveRecordService.create(plan.getCreator());
        });
    }

    /**
     * 标记未完成
     * @return
     */
    public boolean markUnFinish(Plan plan){
        PlanCalendarService planCalendarService=((PlanCalendarService) ServiceManager.instance().getService("plancalendar"));
        UserActiveRecordService userActiveRecordService=((UserActiveRecordService) ServiceManager.instance().getService("useractiverecord"));
        return Db.tx(()->{
            plan.setStatus(ConstantEnum.STATUS_NOT_FINISH.getValue());
            return plan.update() && planCalendarService.updateUnFinishPlanNum(plan.getCreator()) && userActiveRecordService.create(plan.getCreator());
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

    /**
     * 复制计划
     * @return
     */
    public boolean copyPlan(Plan plan,User user){
        PlanDetailService planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        Plan newPlan=new Plan();
        newPlan.init(plan.getTitle(),user.getId(),plan.getEndDate(),ConstantEnum.PRIVACY_SELF.getValue(),plan.getStartDate());
        newPlan.setId(UUID.randomUUID().toString());
        newPlan.setParentId(plan.getId());
        newPlan.setExeTimes(plan.getExeTimes());
        List<PlanDetail> details=planDetailService.listPlanDetails(plan.getId());
        return Db.tx(()->{
            if(!newPlan.save())
                return false;

            PlanDetail planDetail;
            for (PlanDetail detail : details) {
                planDetail = new PlanDetail();
                planDetail.init(newPlan.getId(),detail.getPlanDetail(),detail.getSortIndex(),detail.getSignMaxNum());
                if(!planDetail.save())
                    return false;
            }
            return true;
        });
    }

    /**
     * 加入官方挑战
     * 原计划上增加挑战人数、生成新的属于该用户的计划 并且parentId为原计划
     * @param user
     * @param plan
     */
    public boolean joinChallenge(User user,Plan plan){
        if(find(user.getId(),plan.getId())!=null){
            return true;
        }
        synchronized(plan){
            plan.setParticipantNum(plan.getParticipantNum()+1);
            plan.update();
        }
        return copyPlan(plan,user);
    }

    /**
     * 查找是否已经加入计划
     * @param userId
     * @param parentId
     * @return
     */
    public Plan find(String userId,String parentId){
        return findFirst(QueryParam.Builder().equalsTo("creator",userId).equalsTo("parent_id",parentId).equalsTo(Constant.IS_DELETED_MARK,Constant.ACTIVE));
    }

    /**
     * 获取所有未结束的官方计划
     * @return
     */
    public List<Plan> getUnFinishOfficialPlans(){
        return list(QueryParam.Builder().equalsTo("is_official",Constant.MARK_ONE_STR)
                .equalsTo("status",ConstantEnum.STATUS_ONGOING.getValue())
                .equalsTo(Constant.IS_DELETED_MARK,Constant.ACTIVE).lte("end_date",DateKit.getToday()));
    }

    /**
     * 定时修改官方计划的状态
     */
    public void officialPlanTask(){
        //查找所有 进行中 的 指定结束日期小于等于今天 官方计划 ，修改状态为已结束
        List<Plan> unFinishOfficialPlans=getUnFinishOfficialPlans();
        if(unFinishOfficialPlans!=null && unFinishOfficialPlans.size()>0){
            PlanDetailService planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
            for (Plan plan:unFinishOfficialPlans){
                plan.setStatus(ConstantEnum.STATUS_END.getValue());
                plan.setUnFinishNum(plan.getParticipantNum()-plan.getFinishNum());
                plan.update();
                //修改所有该计划下的未完成的子级计划的状态为未完成
                List<PlanDetail> details=planDetailService.listUnFinishPlanDetails(plan.getId());
                for (PlanDetail detail:details){
                    detail.setStatus(ConstantEnum.STATUS_NOT_FINISH.getValue());
                    detail.update();
                }
            }
        }
    }


}
