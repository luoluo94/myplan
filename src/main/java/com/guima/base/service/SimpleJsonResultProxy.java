package com.guima.base.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.SysMsg;
import com.jfinal.plugin.activerecord.Model;
import java.util.List;

@SuppressWarnings("rawtypes")
public class SimpleJsonResultProxy<S extends BaseService_> extends JsonResultProxy<S>
{
    public SimpleJsonResultProxy(S s)
    {
        super(s);
    }

    @SuppressWarnings("unchecked")
    public <M extends Model<M>> JsonObject parseModelList2StrList(List<M> list)
    {
        return parseModelList2StrList(list,false);
    }

    public <M extends Model<M>> JsonObject parseModelList2StrList(List<M> list,boolean isSimple)
    {
        JsonObject result = new JsonObject();
        JsonArray array = new JsonArray();
        for (M m : list)
        {
            ModelWrapper<M> wrapper = s.getListWrapper(m);
            if(isSimple){
                array.add(wrapper.getSimpleJson());
            }else{
                array.add(wrapper.getJSon());
            }
        }
        result.add(SysMsg.OsMsg.get("SIMPLE_LIST"), array);
        result.addProperty("issuccess",true);
        result.addProperty("message","操作成功");
        return result;
    }

    public <M extends Model<M>> JsonObject parseModelList2StrListSimple(List<M> list)
    {
        return parseModelList2StrList(list,true);
    }
}
