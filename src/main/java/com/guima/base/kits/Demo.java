package com.guima.base.kits;

import com.jfinal.plugin.activerecord.Db;

/**
 * Created by Ran on 2018/7/9.
 */
public class Demo {

//    public Page<Record> getMyCreatedParty(String userId, int pageNum, int pageSize){
//        StringBuffer sql=new StringBuffer();
//        String select =" select a.id,a.`name` as activity_name,u.`name` as creater_name,a.hold_time,a.`status`," +
//                SqlKit.getActivityStatus("a");
//        sql.append(" from activity a")
//                .append(" join user u on a.creater_id=u.id")
//                .append(" where a.creater_id=?")
//                .append(" and a.`status` in(?,?)")
//                .append(" order by a.hold_time desc");
//
//        List<Object> params=new ArrayList<>();
//        params.add(userId);
//        params.add(Constant.ACTIVE);
//        params.add(Constant.END);
//        return Db.paginate(pageNum,pageSize,select,sql.toString(),params.toArray());
//    }

//    public Long getFriendsNum(String activityId){
//        StringBuffer sql=new StringBuffer();
//        sql.append(" select count(*) as num from activity_users a")
//                .append(" where a.activity_id=?");
//        return Db.queryLong(sql.toString(),new String[]{activityId});
//    }
}
