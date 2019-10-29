package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.ScoreRecord;
import com.guima.enums.ScoreTypeEnum;
import com.guima.kits.DateKit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScoreRecordService extends BaseService_<ScoreRecord>
{
    @Override
    protected ScoreRecord getConreteObject()
    {
        return new ScoreRecord();
    }

    @Override
    public ModelWrapper<ScoreRecord> getWrapper(ScoreRecord scoreRecord)
    {
        return new ModelWrapper(scoreRecord);
    }

    @Override
    public ScoreRecord getDao()
    {
        return ScoreRecord.dao;
    }

    @Override
    public List<ScoreRecord> list(QueryParam param)
    {
        param = param == null ? QueryParam.Builder() : param;
        if (!param.hasQueryItem("type"))
            return new ArrayList<>();
        if (!param.hasQueryItem("status"))
            param.equalsTo("status", "0");
        if (param.getOrderItems().isEmpty())
            param.ascBy("sort_idx");
        return super.list(param);
    }

    public boolean createScore(ScoreTypeEnum scoreType, String userId){
        ScoreRecord scoreRecord=new ScoreRecord();
        Date date=new Date();
        String dateStr= DateKit.format(date,DateKit.DATE_TIME_PATTERN1);
        scoreRecord.setCreateFullTime(date);
        scoreRecord.setCreateDate(dateStr.substring(0,10));
        scoreRecord.setCreateTime(dateStr.substring(11));
        scoreRecord.setScore(scoreType.getScore());
        scoreRecord.setScoreMessage(scoreType.getDesc());
        scoreRecord.setUserId(userId);
        return scoreRecord.save();
    }



}
