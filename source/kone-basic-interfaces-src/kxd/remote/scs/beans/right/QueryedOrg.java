package kxd.remote.scs.beans.right;

import kxd.remote.scs.beans.BaseOrg;
import kxd.util.IdableObject;

import org.apache.log4j.Logger;

public class QueryedOrg extends BaseOrg {
	private static final long serialVersionUID = 1L;
	private int depth;
	private int ident;
	private boolean disabled;
	private BaseOrg parentOrg;
	private BaseOrg lastChildOrg;
	private String orgFullPath;
	private boolean hasChildren;
	private int orgType;
	private String standardAreaCode;
	private String simpleName;

	public QueryedOrg() {
		super();
	}

	public QueryedOrg(Integer orgId, String orgName) {
		super(orgId, orgName);
	}

	public QueryedOrg(Integer orgId) {
		super(orgId);
	}

	public String getOrgFullPath() {
		return orgFullPath;
	}

	public void setOrgFullPath(String orgFullPath) {
		this.orgFullPath = orgFullPath;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "parengOrg: ");
		if (parentOrg != null && parentOrg.getOrgId() != null)
			parentOrg.debug(logger, prefix + "  ");
		else
			logger.debug(prefix + "  {null}");
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "depth: " + depth + ";");
		logger.debug(prefix + "ident: " + ident + ";");
		logger.debug(prefix + "disabled: " + disabled + ";");
		// logger.debug(prefix + "hasChildren: " + hasChildren + ";");
	}

	public String getDepthBlank() {
		String ret = "";
		for (int i = 0; i < depth; i++)
			ret += "　";
		return ret;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getIdent() {
		return ident;
	}

	public void setIdent(int ident) {
		this.ident = ident;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public BaseOrg getParentOrg() {
		return parentOrg;
	}

	public void setParentOrg(BaseOrg parentOrg) {
		this.parentOrg = parentOrg;
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof QueryedOrg))
			return;
		QueryedOrg d = (QueryedOrg) src;
		depth = d.depth;
		disabled = d.disabled;
		ident = d.ident;
		parentOrg = d.parentOrg;
		orgType = d.orgType;
		standardAreaCode = d.standardAreaCode;
		simpleName = d.simpleName;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new QueryedOrg();
	}

	public BaseOrg getLastChildOrg() {
		return lastChildOrg;
	}

	public void setLastChildOrg(BaseOrg lastChildOrg) {
		this.lastChildOrg = lastChildOrg;
		hasChildren = lastChildOrg != null && lastChildOrg.getOrgId() != null;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public boolean isHasChildren() {
		return hasChildren;
	}

	public boolean isHasParent() {
		return parentOrg != null && parentOrg.getOrgId() != null;
	}

	public boolean isCanDelete() {
		return !isHasChildren() && depth > 0;
	}

	public int getOrgType() {
		return orgType;
	}

	public void setOrgType(int orgType) {
		this.orgType = orgType;
	}

	public String getStandardAreaCode() {
		return standardAreaCode;
	}

	public void setStandardAreaCode(String standardAreaCode) {
		this.standardAreaCode = standardAreaCode;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

}
