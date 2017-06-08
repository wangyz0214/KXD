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
public class BaseManuf extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String manufName;

	@Override
	public String getText() {
		return manufName;
	}

	@Override
	public void setText(String text) {
		manufName = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "name: " + manufName + ";");
	}

	public BaseManuf() {
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseManuf))
			return;
		BaseManuf d = (BaseManuf) src;
		manufName = d.manufName;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseManuf();
	}

	public BaseManuf(Integer manufId) {
		super(manufId);
	}

	public BaseManuf(Integer manufId, String manufName) {
		super(manufId);
		this.manufName = manufName;
	}

	public Integer getManufId() {
		return getId();
	}

	public void setManufId(Integer manufId) {
		this.setId(manufId);
	}

	public String getManufName() {
		return manufName;
	}

	public void setManufName(String manufName) {
		this.manufName = manufName;
	}

	@Override
	protected String toDisplayLabel() {
		return manufName;
	}

	@Override
	public String toString() {
		return manufName + "(" + getId() + ")";
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
