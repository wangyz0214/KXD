package kxd.remote.scs.beans.appdeploy;

import kxd.remote.scs.beans.BaseThirdOrg;
import kxd.remote.scs.util.emun.ThirdOrgType;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class EditedThirdOrg extends BaseThirdOrg {
	private static final long serialVersionUID = 1L;
	private String thirdOrgCode;
	private String shortName;
	private ThirdOrgType thirdOrgType;
	private String contacter;
	private String telphone;
	private String businessNo;
	private String termNo;
	private String extField;
	private String extField2;
	private String extField3;
	private String extField4;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug( prefix + "thirdOrgCode: " + thirdOrgCode + ";");
		logger.debug( prefix + "shortName: " + shortName + ";");
		logger.debug( prefix + "thirdOrgType: " + thirdOrgType + ";");
		logger.debug( prefix + "contacter: " + contacter + ";");
		logger.debug( prefix + "telphone: " + telphone + ";");
		logger.debug( prefix + "businessNo: " + businessNo + ";");
		logger.debug( prefix + "termNo: " + termNo + ";");
		logger.debug( prefix + "extField: " + extField + ";");
		logger.debug( prefix + "extField2: " + extField2 + ";");
		logger.debug( prefix + "extField3: " + extField3 + ";");
		logger.debug( prefix + "extField4: " + extField4 + ";");
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedThirdOrg();
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedThirdOrg))
			return;
		EditedThirdOrg d = (EditedThirdOrg) src;
		thirdOrgCode = d.thirdOrgCode;
		shortName = d.shortName;
		thirdOrgType = d.thirdOrgType;
		contacter = d.contacter;
		telphone = d.telphone;
		businessNo = d.businessNo;
		termNo = d.termNo;
		extField = d.extField;
		extField2 = d.extField2;
		extField3 = d.extField3;
		extField4 = d.extField4;
	}

	public String getThirdOrgCode() {
		return thirdOrgCode;
	}

	public void setThirdOrgCode(String thirdOrgCode) {
		this.thirdOrgCode = thirdOrgCode;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public ThirdOrgType getThirdOrgType() {
		return thirdOrgType;
	}

	public void setThirdOrgType(ThirdOrgType thirdOrgType) {
		this.thirdOrgType = thirdOrgType;
	}

	public String getContacter() {
		return contacter;
	}

	public void setContacter(String contacter) {
		this.contacter = contacter;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public String getTermNo() {
		return termNo;
	}

	public void setTermNo(String termNo) {
		this.termNo = termNo;
	}

	public String getExtField() {
		return extField;
	}

	public void setExtField(String extField) {
		this.extField = extField;
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
