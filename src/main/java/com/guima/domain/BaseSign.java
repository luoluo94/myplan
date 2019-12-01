package com.guima.domain;

import com.guima.base.model.BaseModule;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSign<M extends BaseSign<M>> extends BaseModule<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}

	public java.lang.String getId() {
		return get("id");
	}

	public void setDescriber(java.lang.String describer) {
		set("describer", describer);
	}

	public java.lang.String getDescriber() {
		return get("describer");
	}

	public void setCreator(java.lang.String creator) {
		set("creator", creator);
	}

	public java.lang.String getCreator() {
		return get("creator");
	}

	public void setIsDeleted(java.lang.Integer isDeleted) {
		set("is_deleted", isDeleted);
	}

	public java.lang.Integer getIsDeleted() {
		return get("is_deleted");
	}

	public void setPhotoUrl(java.lang.String photoUrl) {
		set("photo_url", photoUrl);
	}

	public java.lang.String getPhotoUrl() {
		return get("photo_url");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	public void setPrivacy(java.lang.String privacy) {
		set("privacy", privacy);
	}

	public java.lang.String getPrivacy() {
		return get("privacy");
	}

	public void setPlanId(java.lang.String planId) {
		set("plan_id", planId);
	}

	public java.lang.String getPlanId() {
		return get("plan_id");
	}

	public void setPlanDetailId(java.lang.String planDetailId) {
		set("plan_detail_id", planDetailId);
	}

	public java.lang.String getPlanDetailId() {
		return get("plan_detail_id");
	}

}
