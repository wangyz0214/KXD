/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.engine.cache.ejb.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;
import kxd.util.TreeNode;
import kxd.util.TreeNodeData;

import org.apache.log4j.Logger;

/**
 * 
 * @author Administrator
 */
public class EjbCachedOrg extends ListItem<Integer> implements
		TreeNodeData<Integer> {
	private static final long serialVersionUID = 1L;
	private String orgName;
	/**
	 * 机构编码
	 */
	private String orgCode;
	/**
	 * 机构全称
	 */
	private String orgFullName;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 电话
	 */
	private String telphone;
	/**
	 * 联系人
	 */
	private String contacter;
	/**
	 * 邮件
	 */
	private String email;
	/**
	 * 排序序号
	 */
	private int serialNumber;
	private int orgType;
	private int depth;
	private String standardAreaCode;
	private String simpleName;

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

	public EjbCachedOrg() {
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EjbCachedOrg))
			return;
		EjbCachedOrg d = (EjbCachedOrg) src;
		orgName = d.orgName;
		orgFullName = d.orgFullName;
		address = d.address;
		contacter = d.contacter;
		email = d.email;
		telphone = d.telphone;
		orgCode = d.orgCode;
		serialNumber = d.serialNumber;
		orgType = d.orgType;
		depth = d.depth;
		standardAreaCode = d.standardAreaCode;
		simpleName = d.simpleName;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EjbCachedOrg();
	}

	public EjbCachedOrg(Integer orgId) {
		super(orgId);
	}

	public EjbCachedOrg(Integer orgId, String orgName) {
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

	public int getOrgType() {
		return orgType;
	}

	public void setOrgType(int orgType) {
		this.orgType = orgType;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getStandardAreaCode() {
		return standardAreaCode;
	}

	public void setStandardAreaCode(String standardAreaCode) {
		this.standardAreaCode = standardAreaCode;
	}

	@Override
	public void copyDataToTreeNode(TreeNode<?> node) {
		if (node instanceof EjbCachedOrgNode) {
			EjbCachedOrgNode o = (EjbCachedOrgNode) node;
			o.setAddress(getAddress());
			o.setContacter(getContacter());
			o.setEmail(getEmail());
			o.setSerialNumber(getSerialNumber());
			o.setOrgCode(getOrgCode());
			o.setOrgFullName(getOrgFullName());
			o.setOrgName(getOrgName());
			o.setTelphone(getTelphone());
			o.setId(getId());
			o.setOrgType(getOrgType());
			o.setDepth(getDepth());
			o.setStandardAreaCode(getStandardAreaCode());
			o.setSimpleName(getSimpleName());
		} else
			throw new UnsupportedOperationException();
	}

	@Override
	public void copyDataFromTreeNode(TreeNode<?> node) {
		if (node instanceof EjbCachedOrgNode) {
			EjbCachedOrgNode o = (EjbCachedOrgNode) node;
			setAddress(o.getAddress());
			setContacter(o.getContacter());
			setEmail(o.getEmail());
			setSerialNumber(o.getSerialNumber());
			setOrgCode(o.getOrgCode());
			setOrgFullName(o.getOrgFullName());
			setOrgName(o.getOrgName());
			setTelphone(o.getTelphone());
			setId(o.getId());
			setOrgType(o.getOrgType());
			setDepth(o.getDepth());
			setStandardAreaCode(o.getStandardAreaCode());
			setSimpleName(o.getSimpleName());
		} else
			throw new UnsupportedOperationException();
	}
}
