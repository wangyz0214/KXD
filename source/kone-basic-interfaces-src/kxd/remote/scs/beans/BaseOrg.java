/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

/**
 * 
 * @author Administrator
 */
public class BaseOrg extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String orgName;
	private String orgFullName;

	@Override
	public String getText() {
		return orgName;
	}

	@Override
	public void setText(String text) {
		orgName = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "name: " + orgName + ";");
		logger.debug(prefix + "fullname: " + orgFullName + ";");
	}

	public BaseOrg() {
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseOrg))
			return;
		BaseOrg d = (BaseOrg) src;
		orgName = d.orgName;
		orgFullName = d.orgFullName;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseOrg();
	}

	public BaseOrg(Integer orgId) {
		super(orgId);
	}

	public BaseOrg(Integer orgId, String orgName) {
		super(orgId);
		this.orgName = orgName;
	}

	public String getOrgFullName() {
		return orgFullName;
	}

	public void setOrgFullName(String orgFullName) {
		this.orgFullName = orgFullName;
	}

	public Integer getOrgId() {
		return getId();
	}

	public void setOrgId(Integer orgId) {
		this.setId(orgId);
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	@Override
	protected String toDisplayLabel() {
		return orgName;
	}

	@Override
	public String toString() {
		return orgName + "(" + getId() + ")";
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
