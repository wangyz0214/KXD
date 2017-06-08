/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.beans.right;

import kxd.remote.scs.beans.BaseManuf;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.BaseUser;
import kxd.remote.scs.util.emun.ManageScope;
import kxd.util.IdableObject;

import org.apache.log4j.Logger;

/**
 * 
 * @author Administrator
 */
public class EditedUser extends BaseUser {

	private static final long serialVersionUID = 1L;
	private BaseManuf manuf;
	private BaseOrg org;
	private BaseRole role;
	private String userCode;
	private String telphone;
	private String mobile;
	private String email;
	private ManageScope manageScope;
	private String userPwd;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "userCode: " + userCode + ";");
		logger.debug(prefix + "manageScope: " + manageScope + ";");
		logger.debug(prefix + "telphone: " + telphone + ";");
		logger.debug(prefix + "mobile: " + mobile + ";");
		logger.debug(prefix + "email: " + email + ";");
		logger.debug(prefix + "org: ");
		org.debug(logger, prefix + "  ");
		logger.debug(prefix + "manuf: ");
		if (manuf != null)
			manuf.debug(logger, prefix + "  ");
		else
			logger.debug(prefix + "  {null}");
		logger.debug(prefix + "role: ");
		if (role != null)
			role.debug(logger, prefix + "  ");
		else
			logger.debug(prefix + "  {null}");
	}

	public EditedUser(long userId, String userName) {
		super(userId, userName);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedUser))
			return;
		EditedUser d = (EditedUser) src;
		email = d.email;
		manageScope = d.manageScope;
		manuf = d.manuf;
		mobile = d.mobile;
		org = d.org;
		role = d.role;
		telphone = d.telphone;
		userCode = d.userCode;
		userPwd = d.userPwd;
	}

	@Override
	public IdableObject<Long> createObject() {
		return new EditedUser();
	}

	public EditedUser(long userId) {
		super(userId);
	}

	public EditedUser() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BaseManuf getManuf() {
		return manuf;
	}

	public void setManuf(BaseManuf manuf) {
		this.manuf = manuf;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public BaseOrg getOrg() {
		return org;
	}

	public void setOrg(BaseOrg org) {
		this.org = org;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public ManageScope getManageScope() {
		return manageScope;
	}

	public void setManageScope(ManageScope manageScope) {
		this.manageScope = manageScope;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public BaseRole getRole() {
		return role;
	}

	public void setRole(BaseRole role) {
		this.role = role;
	}

	@Override
	protected String toDisplayLabel() {
		String ret = "[" + getUserGroup().toString() + "] "
				+ super.toDisplayLabel() + "(" + this.getUserCode() + ","
				+ org.getDisplayLabel();
		if (manuf != null)
			ret += "," + manuf.getDisplayLabel();
		ret += ")";
		return ret;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

}
