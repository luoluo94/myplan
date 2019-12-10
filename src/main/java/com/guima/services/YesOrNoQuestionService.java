package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.YesOrNoQuestion;
import com.guima.kits.DateKit;
import com.jfinal.plugin.activerecord.Db;

public class YesOrNoQuestionService extends BaseService_<YesOrNoQuestion>
{
    @Override
    protected YesOrNoQuestion getConreteObject()
    {
        return new YesOrNoQuestion();
    }

    @Override
    public ModelWrapper<YesOrNoQuestion> getWrapper(YesOrNoQuestion m)
    {
        return new ModelWrapper<>(m);
    }


    @Override
    public YesOrNoQuestion getDao()
    {
        return YesOrNoQuestion.dao;
    }

    public Long getTotal(){
        return Db.queryLong("select count(*) as num from yes_or_no_question");
    }

}
