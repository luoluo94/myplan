package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.Plan;
import com.guima.domain.PlanComment;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.taobao.api.internal.toplink.embedded.websocket.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PlanCommentService extends BaseService_<PlanComment>
{
    @Override
    protected PlanComment getConreteObject()
    {
        return new PlanComment();
    }

    @Override
    public ModelWrapper<PlanComment> getWrapper(PlanComment planComment)
    {
        return new ModelWrapper(planComment);
    }

    @Override
    public PlanComment getDao()
    {
        return PlanComment.dao;
    }

    public Page<Record> pageList(String planId,String isMarkDeleted, int pageNumber, int pageSize){
        StringBuffer sqlSelect=new StringBuffer().append("select m.id,m.create_time,m.comment,m.creator_id,m.plan_id,m.remark,m.mark_deleted,")
                .append(" n.name as creator_name,n.header_url as creator_header_url");
        StringBuffer sql=new StringBuffer();
        sql.append(" from plan_comment m join user n on m.creator_id=n.id")
                .append(" where 1=1 ");
        List<Object> params=new ArrayList<>();
        if(StringUtils.isNotEmpty(planId)){
            sql.append(" and m.plan_id=?");
            params.add(planId);
        }
        if(StringUtils.isNotEmpty(isMarkDeleted)){
            sql.append(" and m.mark_deleted=?");
            params.add(isMarkDeleted);
        }
        sql.append(" order by create_time desc");
        return Db.paginate(pageNumber,pageSize,sqlSelect.toString(),sql.toString(),params.toArray());
    }

}
