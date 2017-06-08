/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.beans.right;

import java.util.Collection;

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
public class LoginUser extends BaseUser {

	private static final long serialVersionUID = 1L;
	Collection<Integer> funcCollection;
	ManageScope manageScope;
	private BaseOrg org;
	private BaseManuf manuf;
	private String userCode;
	private String userPwd;
	private BaseRole role;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "userCode: " + userCode + ";");
		logger.debug(prefix + "manageScope: " + manageScope + ";");
		logger.debug(prefix + "org: ");
		org.debug(logger, prefix + "  ");
		logger.debug(prefix + "manuf: ");
		if (manuf != null)
			manuf.debug(logger, prefix + "  ");
		else
			logger.debug(prefix + "  {null}");
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public LoginUser(long userId, String userName) {
		super(userId, userName);
	}

	public LoginUser(long userId) {
		super(userId);
	}

	public LoginUser() {
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof LoginUser))
			return;
		LoginUser d = (LoginUser) src;
		manageScope = d.manageScope;
		org = d.org;
		manuf = d.manuf;
		userCode = d.userCode;
		userPwd = d.userPwd;
		role = d.role;
	}

	@Override
	public IdableObject<Long> createObject() {
		return new LoginUser();
	}

	public Collection<Integer> getFuncCollection() {
		return funcCollection;
	}

	public void setFuncCollection(Collection<Integer> funcCollection) {
		this.funcCollection = funcCollection;
	}

	public boolean isLogined() {
		return getUserId() != null;
	}

	public ManageScope getManageScope() {
		return manageScope;
	}

	public void setManageScope(ManageScope manageScope) {
		this.manageScope = manageScope;
	}

	public BaseOrg getOrg() {
		return org;
	}

	public void setOrg(BaseOrg org) {
		this.org = org;
	}

	public BaseManuf getManuf() {
		return manuf;
	}

	public void setManuf(BaseManuf manuf) {
		this.manuf = manuf;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public BaseRole getRole() {
		return role;
	}

	public void setRole(BaseRole role) {
		this.role = role;
	}
}
