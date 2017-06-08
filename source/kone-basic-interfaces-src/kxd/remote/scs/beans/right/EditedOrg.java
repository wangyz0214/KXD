package kxd.remote.scs.beans.right;

import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class EditedOrg extends QueryedOrg {
	private static final long serialVersionUID = 1L;
	private String orgCode;
	private String address;
	private String telphone;
	private String contacter;
	private String email;
	private int serialNumber;
	private String parentPath;
	private String extField0;
	private String extField1;
	private String extField2;
	private String extField3;
	private String extField4;
	private String children, terms;

	public EditedOrg() {
		super();
	}

	public EditedOrg(Integer orgId, String orgName) {
		super(orgId, orgName);
	}

	public EditedOrg(Integer orgId) {
		super(orgId);
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "orgCode: " + orgCode + ";");
		logger.debug(prefix + "address: " + address + ";");
		logger.debug(prefix + "telphone: " + telphone + ";");
		logger.debug(prefix + "email: " + email + ";");
		logger.debug(prefix + "contacter: " + contacter + ";");
		logger.debug(prefix + "serialnumber: " + serialNumber + ";");
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getContacter() {
		return contacter;
	}

	public void setContacter(String contacter) {
		this.contacter = contacter;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
		if (!(src instanceof EditedOrg))
			return;
		EditedOrg d = (EditedOrg) src;
		address = d.address;
		contacter = d.contacter;
		email = d.email;
		telphone = d.telphone;
		orgCode = d.orgCode;
		serialNumber = d.serialNumber;
		parentPath = d.parentPath;
		extField0 = d.extField0;
		extField1 = d.extField1;
		extField2 = d.extField2;
		extField3 = d.extField3;
		extField4 = d.extField4;
		children = d.children;
		terms = d.terms;
	}

	public IdableObject<Integer> createObject() {
		return new EditedOrg();
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
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

	public String getChildren() {
		return children;
	}

	public void setChildren(String children) {
		this.children = children;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

}
