package com.guima.base.kits;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jfinal.plugin.activerecord.Model;

import java.util.Map;

public class ModelWrapper<M extends Model<M>>
{
    protected M m;

    public ModelWrapper(M m)
    {
        this.m = m;
    }

    public String getId()
    {
        return m.getStr("id");
    }

    public void fill(Map<String, String[]> map)
    {
        for (Map.Entry<String, String[]> entry : map.entrySet())
        {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (key.equals("sort_idx"))
                m.set(key, Integer.parseInt(values[0]));
            else
                m.set(key, values[0]);
        }
    }

    public JsonObject getJSon()
    {
        JsonObject json = new JsonObject();
        if (m != null)
        {
            Gson gson = new Gson();
            JsonElement elem = gson.toJsonTree(m);
            json = elem.getAsJsonObject().getAsJsonObject("attrs");
        }
        return json;
    }

    public String getJSonStr()
    {
        return getJSon().toString();
    }

    public JsonObject getSimpleJson(){
        return null;
    }
}
