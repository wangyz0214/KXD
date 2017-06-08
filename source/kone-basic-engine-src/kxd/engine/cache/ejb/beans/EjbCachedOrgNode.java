package kxd.engine.cache.ejb.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import kxd.engine.cache.beans.sts.CachedOrg;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.right.EditedOrg;
import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.remote.scs.util.AppException;
import kxd.util.IdableObject;
import kxd.util.TreeNode;
import kxd.util.TreeNodeable;

import org.apache.log4j.Logger;

/**
 * 机构缓存结点
 * 
 * @author zhaom
 * 
 */
public class EjbCachedOrgNode extends TreeNode<Integer> {
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
	static protected final ConcurrentHashMap<Integer, EjbCachedOrgNode> nodesMap = new ConcurrentHashMap<Integer, EjbCachedOrgNode>();
	private String standardAreaCode;
	private Object data;
	private String simpleName;
	
	public EjbCachedOrgNode() {
		super();
	}

	/**
	 * 全局查询机构，不管当前机构处于哪一层，均从根机构往下搜索
	 * 
	 * @param orgId
	 * @return
	 */
	public EjbCachedOrgNode getGlobal(int orgId) {
		return nodesMap.get(orgId);
	}

	public String toString() {
		return super.toString();
	}

	public static void clearNodesMap() {
		nodesMap.clear();
	}

	@Override
	public int compare(TreeNode<Integer> o2) {
		EjbCachedOrgNode n2 = (EjbCachedOrgNode) o2;
		return serialNumber - n2.serialNumber;
	}

	@Override
	public TreeNode<Integer> add(Integer id) {
		TreeNode<Integer> r = super.add(id);
		nodesMap.put(id, (EjbCachedOrgNode) r);
		return r;
	}

	@Override
	public TreeNode<Integer> add(Integer id, int index) {
		TreeNode<Integer> r = super.add(id, index);
		nodesMap.put(id, (EjbCachedOrgNode) r);
		return r;
	}

	@Override
	public void add(TreeNode<Integer> o) {
		super.add(o);
		nodesMap.put(o.getId(), (EjbCachedOrgNode) o);
	}

	public void cleanChildrenDisabledNodes() {
		throw new AppException("not implements");
	}

	public void clear() {
		throw new AppException("not implements");
	}

	public TreeNode<Integer> remove(Integer id) {
		nodesMap.remove(id);
		return super.remove(id);
	}

	public boolean remove(TreeNodeable<Integer> o) {
		nodesMap.remove(o.getId());
		return super.remove(o);
	}

	public IdableObject<Integer> createObject() {
		return new EjbCachedOrgNode();
	}

	protected String toDisplayLabel() {
		return null;
	}

	public String getText() {
		return orgName;
	}

	public void setText(String text) {
		orgName = text;
	}

	public String getIdString() {
		return Integer.toString(getId());
	}

	public void setIdString(String id) {
		if (id == null)
			setId(null);
		else
			setId(Integer.valueOf(id));
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgFullName() {
		return orgFullName;
	}

	public void setOrgFullName(String orgFullName) {
		this.orgFullName = orgFullName;
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

	public int getOrgType() {
		return orgType;
	}

	public void setOrgType(int orgType) {
		this.orgType = orgType;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
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

	public void copyData(Object src) {
		if (src instanceof EjbCachedOrgNode) {
			EjbCachedOrgNode o = (EjbCachedOrgNode) src;
			setAddress(o.address);
			setContacter(o.contacter);
			setEmail(o.email);
			setSerialNumber(o.serialNumber);
			setOrgCode(o.orgCode);
			setOrgFullName(o.orgFullName);
			setOrgName(o.orgName);
			setTelphone(o.telphone);
			setOrgType(o.orgType);
			setStandardAreaCode(o.getStandardAreaCode());
			setSimpleName(o.simpleName);
		}
	}

	public void copyDataFromCachedOrg(CachedOrg o) {
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
		setStandardAreaCode(o.getStandardAreaCode());
		setSimpleName(o.getSimpleName());
	}

	public void copyDataFromEditedOrg(EditedOrg o) {
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
		setStandardAreaCode(o.getStandardAreaCode());
		setSimpleName(o.getSimpleName());
	}

	public void copyDataFromEjbCachedOrg(EjbCachedOrg o) {
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
		setStandardAreaCode(o.getStandardAreaCode());
		setSimpleName(o.getSimpleName());
	}

	public EjbCachedOrg toEjbCachedOrg() {
		EjbCachedOrg o = new EjbCachedOrg();
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
		o.setSimpleName(simpleName);
		return o;
	}

	public QueryedOrg toQueryedOrg() {
		final QueryedOrg o = new QueryedOrg();
		toQueryedOrg(o);
		return o;
	}

	public void toQueryedOrg(QueryedOrg o) {
		if (getParent() != null)
			o.setParentOrg(new BaseOrg(getParent().getId()));
		o.setId(getId());
		o.setOrgName(getOrgName());
		o.setOrgFullName(getOrgFullName());
		o.setDepth(getDepth());
		o.setHasChildren(!isEmpty());
		o.setOrgType(getOrgType());
		o.setStandardAreaCode(getStandardAreaCode());
		o.setSimpleName(simpleName);
	}

	public EditedOrg toEditedOrg() {
		final EditedOrg o = new EditedOrg();
		if (getParent() != null)
			o.setParentOrg(new BaseOrg(getParent().getId()));
		o.setId(getId());
		o.setOrgName(getOrgName());
		o.setOrgFullName(getOrgFullName());
		o.setDepth(getDepth());
		o.setOrgCode(getOrgCode());
		o.setAddress(getAddress());
		o.setTelphone(getTelphone());
		o.setContacter(getContacter());
		o.setEmail(getEmail());
		o.setSerialNumber(getSerialNumber());
		o.setHasChildren(!isEmpty());
		o.setOrgFullPath(getPath("", 2));
		o.setOrgType(getOrgType());
		o.setStandardAreaCode(getStandardAreaCode());
		o.setSimpleName(simpleName);
		return o;
	}

	public void addChildrenTo(Collection<QueryedOrg> c, int depths) {
		inAddChildrenTo(c, getPath("", 2), depths);
	}

	private void inAddChildrenTo(Collection<QueryedOrg> c, String fullPath,
			int depths) {
		if (depths > 0) {
			depths--;
			Iterator<?> it = getChildren().iterator();
			while (it.hasNext()) {
				EjbCachedOrgNode org = (EjbCachedOrgNode) it.next();
				QueryedOrg qorg = org.toQueryedOrg();
				String path = fullPath;
				if (org.getDepth() >= 2)
					path += qorg.getOrgName();
				qorg.setOrgFullPath(path);
				c.add(qorg);
				org.inAddChildrenTo(c, path, depths);
			}
		}
	}

	public List<QueryedOrg> getOrgChildren(Integer selected, int depths,
			boolean includeSelf, String keyword) {
		EjbCachedOrgNode selectOrg = null;
		if (selected != null) {
			selectOrg = (EjbCachedOrgNode) find(selected, true);
		} else if (keyword != null && keyword.trim().length() > 0) {
			StringTokenizer st = new StringTokenizer(keyword);
			selectOrg = this;
			while (st.hasMoreElements()) {
				String e = st.nextToken();
				EjbCachedOrgNode o1 = (EjbCachedOrgNode) selectOrg.find(e);
				if (o1 == null) {
					if (selectOrg == this)
						return new ArrayList<QueryedOrg>();
					else {
						break;
					}
				} else
					selectOrg = o1;
			}
		}
		ArrayList<QueryedOrg> c = new ArrayList<QueryedOrg>();
		if (includeSelf) {
			c.add(toQueryedOrg());
			depths--;
		}
		addChildrenTo(c, depths);
		if (selectOrg != null) {
			ArrayList<EjbCachedOrgNode> ls = new ArrayList<EjbCachedOrgNode>();
			EjbCachedOrgNode porg = (EjbCachedOrgNode) selectOrg.getParent();
			int depth = this.getDepth() + depths;
			while (porg != null && porg.getDepth() >= depth
					&& porg.getId() != null) {
				ls.add(0, porg);
				porg = (EjbCachedOrgNode) porg.getParent();
			}
			for (EjbCachedOrgNode o : ls) {
				Iterator<?> it = o.getChildren().iterator();
				int first = c.indexOf(o.toQueryedOrg());
				if (first < 0)
					first = 0;
				while (it.hasNext()) {
					QueryedOrg qo = ((EjbCachedOrgNode) it.next())
							.toQueryedOrg();
					if (selectOrg.getId().equals(qo.getId()))
						qo.setSelected(true);
					first++;
					c.add(first, qo);
				}
			}
			int index = c.indexOf(selectOrg.toQueryedOrg());
			if (index > -1)
				c.get(index).setSelected(true);
		} else if (c.size() > 0)
			c.get(0).setSelected(true);
		return c;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void getAllChildrenData(List<EjbCachedOrg> ls) {
		for (TreeNode<Integer> o : getChildren()) {
			EjbCachedOrg org = ((EjbCachedOrgNode) o).toEjbCachedOrg();
			ls.add(org);
			((EjbCachedOrgNode) o).getAllChildrenData(ls);
		}
	}

	public void logTree(String prefix, Logger logger) {
		logger.debug(prefix + "-" + orgFullName + "[" + getId() + "]");
		Iterator<?> it = getChildren().iterator();
		while (it.hasNext()) {
			((EjbCachedOrgNode) it.next()).logTree(prefix + "\t", logger);
		}
	}

}
