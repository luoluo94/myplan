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




}
