package com.guima.domain;

import com.guima.enums.ConstantEnum;
import com.guima.kits.Constant;
import com.guima.kits.NumberConstant;

import java.util.Date;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Plan extends BasePlan<Plan> {

	public static final Plan dao = new Plan();


	public void init(String title,String creator ,Date endDate,
			String privacy,Date startDate){
		this.setTitle(title);
		setCreator(creator);
		setEndDate(endDate);
		setIsDeleted(Constant.IS_DELETED_NO);
		setPrivacy(privacy);
		setStartDate(startDate);
		setStatus(ConstantEnum.STATUS_ONGOING.getValue());
		setParticipantNum(NumberConstant.ONE);
		setFinishNum(NumberConstant.ZERO);
		setUnFinishNum(NumberConstant.ZERO);
		setIsOfficial(NumberConstant.ZERO);
	}
}
