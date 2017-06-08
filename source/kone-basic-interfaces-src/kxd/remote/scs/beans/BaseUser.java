/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.beans;

import kxd.remote.scs.util.emun.UserGroup;
import kxd.util.ListItem;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

/**
 * 
 * @author 赵明
 */
public class BaseUser extends ListItem<Long> {
	private static final long serialVersionUID = 1L;
	private String userName;
	private UserGroup userGroup;

	@Override
	public String getText() {
		return userName;
	}

	@Override
	public void setText(String text) {
		userName = text;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId());
		logger.debug(prefix + "name: " + userName);
		logger.debug(prefix + "userGroup: " + userGroup);
	}

	public BaseUser() {
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseUser))
			return;
		BaseUser d = (BaseUser) src;
		userName = d.userName;
		userGroup = d.userGroup;
	}

	@Override
	public IdableObject<Long> createObject() {
		return new BaseUser();
	}

	public BaseUser(Long userId) {
		super(userId);
	}

	public BaseUser(Long userId, String userName) {
		super(userId);
		this.userName = userName;
	}

	public Long getUserId() {
		return getId();
	}

	public void setUserId(Long userId) {
		this.setId(userId);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	protected String toDisplayLabel() {
		return userName;
	}

	@Override
	public String toString() {
		return userName + "(" + getId() + ")";
	}

	@Override
	public String getIdString() {
		if (getId() == null)
			return null;
		else
			return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		if (id == null || id.trim().isEmpty())
			setId(null);
		else
			setId(Long.parseLong(id));
	}
}