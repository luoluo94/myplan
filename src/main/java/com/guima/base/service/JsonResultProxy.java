package com.guima.base.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.kits.SysMsg;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class JsonResultProxy<S extends BaseService_>
{
    protected S s;

    public JsonResultProxy(S s)
    {
        this.s = s;
    }

    @SuppressWarnings("unchecked")
    public JsonObject list(QueryParam param)
    {
        List<Model> list = s.list(param);
        return parseModelList2StrList(list);
    }

    @SuppressWarnings("unchecked")
    public JsonObject findFirst(QueryParam param)
    {
        Model m = s.findFirst(param);
        return s.getWrapper(m).getJSon();
    }

    @SuppressWarnings("unchecked")
    public JsonObject pageList(String draw, QueryParam param, String pageNumberStr, String pageSizeStr)
    {
        Page<Model> page = s.pageList(param, pageNumberStr, pageSizeStr);
        return pageList(draw, page);
    }

    @SuppressWarnings("unchecked")
    public JsonObject findById(String id)
    {
    	JsonObject result = new JsonObject();
        Model m = s.findById(id);
        
        result.add(SysMsg.OsMsg.get("SIMPLE_LIST"), s.getWrapper(m).getJSon());
        result.addProperty("issuccess",true);
        result.addProperty("message","操作成功");
        return result;
    }

    @SuppressWarnings("unchecked")
    public JsonObject findById(Map<String,String[]> map)
    {
        JsonObject result = new JsonObject();
        Model m = s.findById(map);
        
        result.add(SysMsg.OsMsg.get("SIMPLE_LIST"), s.getWrapper(m).getJSon());
        result.addProperty("issuccess",true);
        result.addProperty("message","操作成功");
        return result;
    }

    @SuppressWarnings("unchecked")
    public <M extends Model> JsonObject pageList(String draw, Page<M> page)
    {
        JsonObject result = parseModelList2StrList(page.getList());
        result.addProperty("draw", draw);
        result.addProperty("totalPage", String.valueOf(page.getTotalPage()));
        result.addProperty("recordsTotal", String.valueOf(page.getTotalRow()));
        result.addProperty("recordsFiltered", String.valueOf(page.getTotalRow()));
        return result;
    }

    public <M extends Model> JsonObject pageList(Page<M> page)
    {
        JsonObject result = parseModelList2StrList(page.getList());
        result.addProperty("totalPage", String.valueOf(page.getTotalPage()));
        result.addProperty("recordsTotal", String.valueOf(page.getTotalRow()));
        return result;
    }

    @SuppressWarnings("unchecked")
    public <M extends Model<M>> JsonObject parseModelList2StrList(List<M> list)
    {
        JsonObject result = new JsonObject();
        JsonArray array = new JsonArray();
        for (M m : list)
        {
            ModelWrapper<M> wrapper = s.getListWrapper(m);
            array.add(wrapper.getJSon());
        }
        result.add(SysMsg.OsMsg.get("LIST"), array);
        return result;
    }
}
