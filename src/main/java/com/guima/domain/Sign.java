package com.guima.domain;

import com.guima.kits.Constant;

import java.util.Date;
import java.util.UUID;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Sign extends BaseSign<Sign> {
	public static final Sign dao = new Sign();

	public Sign() {
	}


	public void init(String creator,String describer,
					 String privacy, String photoUrl,String planId,String planDetailId){
		setCreator(creator);
		setDescriber(describer);
		setIsDeleted(Constant.IS_DELETED_NO);
		setPhotoUrl(photoUrl);
		setPrivacy(privacy);
		setCreateTime(new Date());
		setPlanId(planId);
		setPlanDetailId(planDetailId);
	}
}
