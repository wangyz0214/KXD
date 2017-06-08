package kxd.remote.scs.beans.right;

import kxd.remote.scs.beans.BaseManuf;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class EditedManuf extends BaseManuf {
	private static final long serialVersionUID = 1L;
	private String manufCode;
	private int serialNumber;
	private short manufType;

	public EditedManuf() {
		super();
	}

	public EditedManuf(Integer manufId, String manufName) {
		super(manufId, manufName);
	}

	public EditedManuf(Integer manufId) {
		super(manufId);
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "manufCode: " + manufCode + ";");
		logger.debug(prefix + "serialNumber: " + serialNumber + ";");
	}

	public String getManufCode() {
		return manufCode;
	}

	public void setManufCode(String manufCode) {
		this.manufCode = manufCode;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedManuf))
			return;
		EditedManuf d = (EditedManuf) src;
		manufCode = d.manufCode;
		serialNumber = d.serialNumber;
		manufType = d.manufType;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedManuf();
	}

	@Override
	public String toString() {
		return this.getManufName() + "(" + getManufCode() + ")";
	}

	public short getManufType() {
		return manufType;
	}

	public void setManufType(short manufType) {
		this.manufType = manufType;
	}

}
