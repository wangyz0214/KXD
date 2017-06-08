/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.beans.invoice;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

/**
 * 
 * @author jurstone
 */
public class BaseInvoiceConfig extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String configDesp;

	@Override
	public String getText() {
		return configDesp;
	}

	@Override
	public void setText(String text) {
		configDesp = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + configDesp + ";");
	}

	public BaseInvoiceConfig() {
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseInvoiceConfig))
			return;
		BaseInvoiceConfig d = (BaseInvoiceConfig) src;
		configDesp = d.configDesp;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseInvoiceConfig();
	}

	public BaseInvoiceConfig(Integer configId) {
		super(configId);
	}

	public BaseInvoiceConfig(Integer configId, String configDesp) {
		super(configId);
		this.configDesp = configDesp;
	}

	public Integer getConfigId() {
		return getId();
	}

	public void setConfigId(Integer configId) {
		this.setId(configId);
	}

	public String getConfigDesp() {
		return configDesp;
	}

	public void setConfigDesp(String configDesp) {
		this.configDesp = configDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return configDesp;
	}

	@Override
	public String toString() {
		return configDesp + "(" + getId() + ")";
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
