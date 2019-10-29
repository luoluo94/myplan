package com.guima.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.OsResult;
import com.guima.base.kits.QueryParam;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.BaseService_;
import com.guima.cache.CacheModel;
import com.guima.domain.Dictionary;
import com.guima.kits.Constant;
import com.guima.kits.Kit;
import com.guima.kits.MapKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryService extends BaseService_<Dictionary>
{
    @Override
    public boolean getEnableCache()
    {
        this.enableCache = false;
        return super.getEnableCache();
    }

    @Override
    protected Dictionary getConreteObject()
    {
        return new Dictionary();
    }

    @Override
    public ModelWrapper<Dictionary> getWrapper(Dictionary dictionary)
    {
        return new ModelWrapper(dictionary);
    }

    @Override
    public Dictionary getDao()
    {
        return Dictionary.dao;
    }

    @Override
    public List<Dictionary> list(QueryParam param,String resultFields)
    {
        if (!param.hasQueryItem("status"))
            param.equalsTo("status", "0");
        if (param.getOrderItems().isEmpty())
            param.ascBy("sort_idx");
        return super.list(param,resultFields);
    }


    @Override
    public Page<Dictionary> pageList(QueryParam param, String pageNumberStr, String pageSizeStr)
    {
        param = param == null ? QueryParam.Builder() : param;
        if (!param.hasQueryItem("status"))
            param.equalsTo("status", "0");
        param.ascBy("type").ascBy("sort_idx");
        return super.pageList(param, pageNumberStr, pageSizeStr);
    }

    /**
     * 根据类型获取对应的字典项
     * @param type
     * @return
     */
    public List<Dictionary> listType(String type,String isBackground){
        QueryParam param = QueryParam.Builder().equalsTo("type", type);
        if(StrKit.notBlank(isBackground)){
            param.equalsTo("is_background",isBackground);
        }
        String resultFields=" id,name ";
        param.equalsTo("status", Constant.ACTIVE);
        param.ascBy("sort_idx");
        return list(param,resultFields);
    }

    public List<Dictionary> list(String type,String isBackground,String name){
        QueryParam param = QueryParam.Builder().equalsTo("type", type);
        if(StrKit.notBlank(isBackground)){
            param.equalsTo("is_background",isBackground);
        }
        if(StrKit.notBlank(name)){
            param.like("name","%"+name+"%");
        }
        param.equalsTo("status",Constant.ACTIVE);
        param.ascBy("sort_idx");
        return list(param);
    }


    public List<Dictionary> listBackgroundMusicTheme(){
        return listType(Constant.MUSIC_THEME,"0");
    }

    /**
     * 获取指定类型和父级节点id的子级节点
     *
     * @param type     类型
     * @param parentId 父级节点id
     */
    public List<Dictionary> listChild(String type, String parentId)
    {
        QueryParam param = QueryParam.Builder().equalsTo("type", type)
                .equalsTo("parent_id", parentId);
        return list(param);
    }

    public boolean create(String id,String name,String type,int sortIdx){
        Map map=new HashMap<>();
        MapKit.setValueToMap(map, "id",id);
        MapKit.setValueToMap(map, "name",name);
        MapKit.setValueToMap(map, "type",type);
        MapKit.setValueToMap(map, "sort_idx",sortIdx+"");
        return super.create(map);
    }

    public Dictionary get(String type, String name)
    {
        QueryParam param = QueryParam.Builder().equalsTo("type", type)
                .equalsTo("name", name);
        return findFirst(param);
    }

}
