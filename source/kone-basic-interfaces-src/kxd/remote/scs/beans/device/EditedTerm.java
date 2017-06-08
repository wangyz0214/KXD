package kxd.remote.scs.beans.device;

import kxd.util.IdableObject;

import org.apache.log4j.Logger;

public class EditedTerm extends QueryedTerm {
	private static final long serialVersionUID = 1L;
	private String manufNo;
	private String address;
	private String contacter;
	private String ip;
	private String areaCode;
	private short dayRunTime;
	private String openTime;
	private String closeTime;
	private String guid;
	private String extField0;
	private String extField1;
	private String extField2;
	private String extField3;
	private String extField4;
	
	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "manufNo: " + manufNo + ";");
		logger.debug(prefix + "address: " + address + ";");
		logger.debug(prefix + "contacter: " + contacter + ";");
		logger.debug(prefix + "ip: " + ip + ";");
		logger.debug(prefix + "dayRunTime: " + dayRunTime + ";");
		logger.debug(prefix + "openTime: " + openTime + ";");
		logger.debug(prefix + "closeTime: " + closeTime + ";");
		logger.debug(prefix + "guid: " + guid + ";");
		logger.debug(prefix + "areacode: " + areaCode + ";");
	}

	public EditedTerm() {
		super();
	}

	public EditedTerm(Integer id) {
		super(id);
	}

	public EditedTerm(Integer id, String termCode, String termDesp) {
		super(id, termCode, termDesp);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedTerm))
			return;
		EditedTerm d = (EditedTerm) src;
		manufNo = d.manufNo;
		address = d.address;
		contacter = d.contacter;
		ip = d.ip;
		areaCode = d.areaCode;
		dayRunTime = d.dayRunTime;
		openTime = d.openTime;
		closeTime = d.closeTime;
		guid = d.guid;
		extField0 = d.extField0;
		extField1 = d.extField1;
		extField2 = d.extField2;
		extField3 = d.extField3;
		extField4 = d.extField4;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedTerm();
	}

	public String getManufNo() {
		return manufNo;
	}

	public void setManufNo(String manufNo) {
		this.manufNo = manufNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContacter() {
		return contacter;
	}

	public void setContacter(String contacter) {
		this.contacter = contacter;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String bankKey) {
		this.areaCode = bankKey;
	}

	public short getDayRunTime() {
		return dayRunTime;
	}

	public void setDayRunTime(short dayRunTime) {
		this.dayRunTime = dayRunTime;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getExtField0() {
		return extField0;
	}

	public void setExtField0(String extField0) {
		this.extField0 = extField0;
	}

	public String getExtField1() {
		return extField1;
	}

	public void setExtField1(String extField1) {
		this.extField1 = extField1;
	}

	public String getExtField2() {
		return extField2;
	}

	public void setExtField2(String extField2) {
		this.extField2 = extField2;
	}

	public String getExtField3() {
		return extField3;
	}

	public void setExtField3(String extField3) {
		this.extField3 = extField3;
	}

	public String getExtField4() {
		return extField4;
	}

	public void setExtField4(String extField4) {
		this.extField4 = extField4;
	}
}
