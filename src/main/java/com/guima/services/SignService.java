package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.base.service.ServiceManager;
import com.guima.domain.PlanDetail;
import com.guima.domain.Sign;
import com.guima.domain.User;
import com.guima.enums.ConstantEnum;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.jfinal.kit.StrKit;
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
                .append("m.plan_id,m.plan_detail_id,")
                .append(" n.name as creator_name,n.header_url as creator_header_url");

        return Db.paginate(pageNumber,pageSize,sqlSelect.toString(),conditionSql.toString(),params.toArray());
    }

    public Page<Record> listPublicSigns(int pageNumber, int pageSize){
        StringBuffer sql=new StringBuffer();
        sql.append(" from sign m join user n on m.creator=n.id")
                .append(" where 1=1 ")
                .append(" and m.privacy=?")
                .append(" and m.").append(Constant.IS_DELETED_MARK).append("=?")
                .append(" order by create_time desc");
        List<Object> params=new ArrayList<>();
        params.add(ConstantEnum.PRIVACY_PUBLIC.getValue());
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

    /**
     * 获取该计划下的打卡情况
     * @param planId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Record> listPlanSigns(String planId,int pageNumber, int pageSize){
        StringBuffer sql=new StringBuffer();
        sql.append(" from sign m join user n on m.creator=n.id")
                .append(" where 1=1 ")
                .append(" and m.plan_id=?")
                .append(" and m.").append(Constant.IS_DELETED_MARK).append("=?")
                .append(" order by create_time desc");
        List<Object> params=new ArrayList<>();
        params.add(planId);
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

    public boolean sign(Sign sign, PlanDetail planDetail){
        if(planDetail==null){
            return sign.save();
        }else{
            planDetail.setFinishPercentage(planDetail.getFinishPercentage()+1);
            return Db.tx(()->{
                return sign.save() && planDetail.update();
            });
        }
    }

    public boolean removeSign(Sign sign){
        sign.setIsDeleted(Constant.IS_DELETED_YES);
        if(!StrKit.isBlank(sign.getPlanDetailId())){
            PlanDetailService planDetailService=((PlanDetailService) ServiceManager.instance().getService("plandetail"));
            PlanDetail planDetail=planDetailService.findById(sign.getPlanDetailId());
            if(planDetail!=null){
                planDetail.setFinishPercentage(planDetail.getFinishPercentage()-1);
                return Db.tx(()->{
                    return sign.update() && planDetail.update();
                });
            }
        }
        return sign.update();
    }

}
