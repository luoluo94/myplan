package com.guima.base.controller;

/**
 * Created by Ran on 2018/1/25.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.guima.base.model.BaseModule;
import com.guima.base.service.SimpleJsonResultProxy;
import com.guima.domain.Admin;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.guima.kits.MapKit;
import com.jfinal.core.Controller;
import com.jfinal.kit.*;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.BaseService_;
import com.guima.kits.Kit;
import com.guima.kits.ShowInfoKit;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaseController<M extends BaseModule<M>> extends Controller
{
    protected BaseService_<M> s;

    private boolean isReadData = false;
    private String readData = "";

    public User getMyUser(){
        return getSessionAttr(SysMsg.OsMsg.get("SESSION_KEY"));
    }

    public Admin getMyAdmin(){
        return getSessionAttr(SysMsg.OsMsg.get("SESSION_KEY_ADMIN"));
    }

    public void delete(){
        String id=getPara("id");
        if(Kit.isNull(id)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        M m=s.findById(id);
        m.setStatus(Constant.DELETED);
        doRender(m.update());
    }

    @Override
    public String getPara() {
        if (getRequest()!=null && ("application/json".equals(getRequest().getContentType())
                || "text/json".equals(getRequest().getContentType()))) {
            if (!isReadData) {
                readData = HttpKit.readData(getRequest());
                isReadData = true;
            }
            return readData;
        }
        return super.getPara();
    }

    @Override
    public String getPara(String name) {
        if (getRequest().getContentType()!=null && ("application/json".equals(getRequest().getContentType().toLowerCase())
                || "text/json".equals(getRequest().getContentType().toLowerCase()))) {
            try {
                JSONObject json = JSONObject.parseObject(getPara());
                return json.getString(name);
            } catch (Exception e) {
                return super.getPara(name);
            }
        }
        return super.getPara(name);
    }

    @Override
    public String getPara(String name, String defaultValue) {
        if (getRequest().getContentType()!=null && ("application/json".equals(getRequest().getContentType().toLowerCase())
                || "text/json".equals(getRequest().getContentType().toLowerCase()))) {
            try {
                JSONObject json = JSONObject.parseObject(getPara());
                return Kit.isNull(json.getString(name)) ? defaultValue:json.getString(name);
            } catch (Exception e) {
                return super.getPara(name);
            }
        }
        return super.getPara(name, defaultValue);
    }

    @Override
    public String[] getParaValues(String name) {
        if (getRequest().getContentType()!=null && ("application/json".equals(getRequest().getContentType().toLowerCase())
                || "text/json".equals(getRequest().getContentType().toLowerCase()))) {
            try {
                JSONObject json = JSONObject.parseObject(getPara());
                Object[] os = json.getJSONArray(name).toArray();
                String[] result = new String [os.length];
                for(int i = 0;i < os.length;i++)
                    result[i] = os[i].toString();
                return result;
            } catch (Exception e) {
                return super.getParaValues(name);
            }
        }
        return super.getParaValues(name);
    }

    @Override
    public Map<String, String[]> getParaMap() {
        if (getRequest().getContentType()!=null && ("application/json".equals(getRequest().getContentType().toLowerCase())
                || "text/json".equals(getRequest().getContentType().toLowerCase()))) {
            try {
                Map<String, Object> map = JSON.parseObject(getPara());
                return MapKit.copyMap(map);
            } catch (Exception e) {
                return super.getParaMap();
            }
        }
        return super.getParaMap();
    }

    public void doRenderPage(Page<M> result)
    {
        try
        {
            SimpleJsonResultProxy<BaseService_<M>> proxy = new SimpleJsonResultProxy<>(s);
            JsonObject json = proxy.pageList(result);
            renderText(json.toString());
        } catch (Exception e)
        {
            e.printStackTrace();
            String errMsg = String.format(SysMsg.OsMsg.get("QUERY_FAIL_REASON"), e.getMessage());
            Log.getLog(s.getClass()).error(errMsg, e);
            doRenderError(errMsg);
        }
    }

    public void doRenderPageRecord(Page<Record> result){
        doRender(result.getList(),result.getTotalPage(),result.getTotalRow());
    }

    public void doRender(String markDesc,String markValue,boolean isSuccess){
        Map map=new HashMap<>();
        map.put(markDesc,markValue);
        doRender(map,isSuccess,isSuccess?"操作成功":"操作失败");
    }



    protected void doRender(Object data,boolean isSuccess,String message) {
        renderText(JsonKit.toJson(ShowInfoKit.getErrorMap(data,isSuccess,message)));
    }

    protected void doRenderSuccess(Object data) {
        doRender(data,true,"操作成功");
    }

    protected void doRenderError(Exception e) {
        doRender("",false,"出错了"+e.getMessage()+e.toString());
    }

    protected void doRenderError(String message) {
        doRender("",false,message);
    }

    protected void doRenderError() {
        doRender("",false,SysMsg.OsMsg.get("ERROR"));
    }

    protected void doRender(boolean isSuccess){
        if(isSuccess){
            doRenderSuccess("");
        }else{
            doRenderError();
        }
    }

    public int getPageNumber(){
        return Kit.isNull(getPara("page_num")) ? 1 : Integer.parseInt(getPara("page_num"));
    }

    public int getPageSize(){
        String pageSizeStr=getPara("page_size");
        if(Kit.isNotNull(pageSizeStr) && Integer.parseInt(getPara("page_size"))<200){
            return Integer.parseInt(getPara("page_size"));
        }
        return 200;
    }


    public void doRender(List<Record> records,int totalPage,int totalRow){
        JSONObject json = new JSONObject();
        JSONArray array = JSON.parseArray(JsonKit.toJson(records));
        json.put(SysMsg.OsMsg.get("SIMPLE_LIST"), array);
        json.put("is_success",true);
        json.put("message","操作成功");
        json.put("total_page", String.valueOf(totalPage));
        json.put("records_total", String.valueOf(totalRow));
        renderText(json.toString());
    }

    public void doRenderJson(com.google.gson.JsonArray jsonArray){
        JsonObject result=new JsonObject();
        result.add("data",jsonArray);
        result.addProperty("is_success",true);
        result.addProperty("message","操作成功");
        renderText(result.toString());
    }

    protected Map<String, String[]> cudMap = new LinkedHashMap<>();

    protected Map<String, String[]> getCudParaMap()
    {
        Map<String, String[]> map = MapKit.cloneMap(getParaMap());
        map.putAll(cudMap);
        cudMap.clear();
        return map;
    }

    public void save()
    {
        try {
            Map<String, String[]> map = getCudParaMap();
            doRender(s.create(map));
        }catch (Exception e){
            doRender(false);
        }
    }

    public void update()
    {
        try {
            Map<String, String[]> map = getCudParaMap();
            doRender(s.update(map));
        }catch (Exception e){
            doRender(false);
        }
    }

    public void saveOrUpdate(){
        if(StrKit.notBlank(getPara("id"))){
            update();
        }else{
            save();
        }
    }

    public void viewById(){
        doRender(s.findById(getPara("id")),true,"");
    }

    public void checkUser(User user){
        if(user==null){
            doRenderError(SysMsg.OsMsg.get("NO_USER"));
            return;
        }
    }


}


