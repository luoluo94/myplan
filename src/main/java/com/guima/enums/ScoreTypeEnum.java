package com.guima.enums;

/**
 * Created by Ran on 2019/8/30.
 */
public enum ScoreTypeEnum {

    CREATE_PLAN("新建计划",1),
    FINISH_PLAN("完成计划",20),
    CREATE_PUBLIC_PLAN("新建公开计划",5),
    LIKE("朋友点赞",1),
    COMMENT("朋友评论",2),
    BROWSE("朋友浏览",1);

    private String desc;
    private Integer score;

    ScoreTypeEnum(String desc, Integer score){
        this.desc=desc;
        this.score=score;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
