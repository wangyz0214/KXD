/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.beans;

import kxd.util.ListItem;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

/**
 * 
 * @author 赵明
 */
public class BaseRole extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String roleName;

	@Override
	public String getText() {
		return roleName;
	}

	@Override
	public void setText(String text) {
		roleName = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + roleName + ";");
	}

	public BaseRole() {
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseRole))
			return;
		BaseRole d = (BaseRole) src;
		roleName = d.roleName;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseRole();
	}

	public BaseRole(Integer roleId) {
		super(roleId);
	}

	public BaseRole(Integer roleId, String roleName) {
		super(roleId);
		this.roleName = roleName;
	}

	public Integer getRoleId() {
		return getId();
	}

	public void setRoleId(Integer roleId) {
		this.setId(roleId);
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Override
	protected String toDisplayLabel() {
		return roleName;
	}

	@Override
	public String toString() {
		return roleName + "(" + getId() + ")";
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
			setId(Integer.parseInt(id));
	}
}
