package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.YesOrNoQuestion;
import com.guima.kits.Constant;
import com.guima.services.YesOrNoQuestionService;

import java.util.Random;

/**
 * Created by Ran on 2019/12/9.
 */
public class YesOrNoController extends BaseController{

    private YesOrNoQuestionService questionService;

    public YesOrNoController()
    {
        questionService=((YesOrNoQuestionService) ServiceManager.instance().getService("yesornoquestion"));
    }

    public void getQuestion(){
        if(Constant.QUESTION_TOTAL==0){
            Constant.QUESTION_TOTAL=getTotal();
        }
        int id=new Random().nextInt(Constant.QUESTION_TOTAL);
        YesOrNoQuestion question=questionService.findById((id+1)+"");
        String answerStr=question.getAnswer();
        String[] answer=answerStr.split(",");
        if(answer.length!=1){
            int answerIndex=new Random().nextInt(answer.length);
            question.setAnswer(answer[answerIndex]);
        }
        doRenderSuccess(question);
    }

    public int getTotal(){
        Long total=questionService.getTotal();
        return total.intValue();
    }

    public void setTotal(){
        Constant.QUESTION_TOTAL=0;
        doRender(true);
    }
}
