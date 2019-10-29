package com.guima.base.model;

import com.guima.kits.Kit;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

import java.util.UUID;

/**
 * Created by Ran on 2018/6/5.
 */
public class BaseModule<M extends BaseModule<M>> extends Model<M> implements IBean {

    public void setId(java.lang.String id) {
        set("id", id);
    }

    public java.lang.String getId() {
        return get("id");
    }

    public void setStatus(java.lang.String status) {
        set("status", status);
    }

    public java.lang.String getStatus() {
        return get("status");
    }

    public boolean saveOrUpdate(){
        String id=get("id");
        if(Kit.isNull(id)){
            return save();
        }else{
            return super.update();
        }
    }

    @Override
    public boolean save(){
        set("id", UUID.randomUUID().toString());
        return super.save();
    }

    public boolean superSave(){
        return super.save();
    }

}
