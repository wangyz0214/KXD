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
public class BaseThirdOrg extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String thirdOrgDesp;

	@Override
	public String getText() {
		return thirdOrgDesp;
	}

	@Override
	public void setText(String text) {
		thirdOrgDesp = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId());
		logger.debug(prefix + "desp: " + thirdOrgDesp);
	}

	public BaseThirdOrg() {
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseThirdOrg))
			return;
		BaseThirdOrg d = (BaseThirdOrg) src;
		thirdOrgDesp = d.thirdOrgDesp;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseThirdOrg();
	}

	public BaseThirdOrg(Integer id) {
		super(id);
	}

	public BaseThirdOrg(Integer id, String thirdOrgDesp) {
		super(id);
		this.thirdOrgDesp = thirdOrgDesp;
	}

	public Integer getThirdOrgId() {
		return getId();
	}

	public void setThirdOrgId(Integer id) {
		this.setId(id);
	}

	public String getThirdOrgDesp() {
		return thirdOrgDesp;
	}

	public void setThirdOrgDesp(String thirdOrgDesp) {
		this.thirdOrgDesp = thirdOrgDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return thirdOrgDesp;
	}

	@Override
	public String toString() {
		return thirdOrgDesp + "(" + getId() + ")";
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
