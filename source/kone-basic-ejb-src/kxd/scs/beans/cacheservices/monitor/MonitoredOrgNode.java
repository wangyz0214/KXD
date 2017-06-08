package kxd.scs.beans.cacheservices.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import kxd.engine.cache.beans.sts.CachedOrg;
import kxd.remote.scs.beans.right.EditedOrg;
import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.remote.scs.util.AppException;
import kxd.util.IdableObject;
import kxd.util.TreeNode;
import kxd.util.TreeNodeable;

/**
 * 监控机构结点
 * 
 * @author zhaom
 * 
 */
public class MonitoredOrgNode extends TreeNode<Integer> {
	private static final long serialVersionUID = 1L;
	// private static Logger logger = Logger.getLogger(MonitoredOrgNode.class);
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
	static public final ConcurrentHashMap<Integer, MonitoredOrgNode> nodesMap = new ConcurrentHashMap<Integer, MonitoredOrgNode>();

	private CopyOnWriteArrayList<CachedMonitoredTerm> terms = new CopyOnWriteArrayList<CachedMonitoredTerm>();

	/**
	 * 全局查询机构，不管当前机构处于哪一层，均从根机构往下搜索
	 * 
	 * @param orgId
	 * @return
	 */
	public MonitoredOrgNode getGlobal(int orgId) {
		return nodesMap.get(orgId);
	}

	public String toString() {
		return super.toString();
	}

	@Override
	public TreeNode<Integer> add(Integer id) {
		TreeNode<Integer> r = super.add(id);
		nodesMap.put(id, (MonitoredOrgNode) r);
		return r;
	}

	@Override
	public TreeNode<Integer> add(Integer id, int index) {
		TreeNode<Integer> r = super.add(id, index);
		nodesMap.put(id, (MonitoredOrgNode) r);
		return r;
	}

	@Override
	public void add(TreeNode<Integer> o) {
		super.add(o);
		nodesMap.put(o.getId(), (MonitoredOrgNode) o);
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
		return new MonitoredOrgNode();
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

	public void copyData(Object src) {
		if (src instanceof MonitoredOrgNode) {
			MonitoredOrgNode o = (MonitoredOrgNode) src;
			setAddress(o.address);
			setContacter(o.contacter);
			setEmail(o.email);
			setSerialNumber(o.serialNumber);
			setOrgCode(o.orgCode);
			setOrgFullName(o.orgFullName);
			setOrgName(o.orgName);
			setTelphone(o.telphone);
			setOrgType(o.orgType);
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
	}

	public QueryedOrg toQueryedOrg() {
		final QueryedOrg o = new QueryedOrg();
		o.setId(getId());
		o.setOrgName(getOrgName());
		o.setOrgFullName(getOrgFullName());
		o.setDepth(getDepth());
		o.setHasChildren(!isEmpty());
		o.setOrgType(getOrgType());
		return o;
	}

	public EditedOrg toEditedOrg() {
		final EditedOrg o = new EditedOrg();
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
		return o;
	}

	public CopyOnWriteArrayList<CachedMonitoredTerm> getTerms() {
		return terms;
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
				MonitoredOrgNode org = (MonitoredOrgNode) it.next();
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
		MonitoredOrgNode selectOrg = null;
		if (selected != null) {
			selectOrg = (MonitoredOrgNode) find(selected, true);
		} else if (keyword != null && keyword.trim().length() > 0) {
			StringTokenizer st = new StringTokenizer(keyword);
			selectOrg = this;
			while (st.hasMoreElements()) {
				String e = st.nextToken();
				MonitoredOrgNode o1 = (MonitoredOrgNode) selectOrg.find(e);
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
			ArrayList<MonitoredOrgNode> ls = new ArrayList<MonitoredOrgNode>();
			MonitoredOrgNode porg = (MonitoredOrgNode) selectOrg.getParent();
			int depth = this.getDepth() + depths;
			while (porg != null && porg.getDepth() >= depth
					&& porg.getId() != null) {
				ls.add(0, porg);
				porg = (MonitoredOrgNode) porg.getParent();
			}
			for (MonitoredOrgNode o : ls) {
				Iterator<?> it = o.getChildren().iterator();
				int first = c.indexOf(o.toQueryedOrg());
				if (first < 0)
					first = 0;
				while (it.hasNext()) {
					QueryedOrg qo = ((MonitoredOrgNode) it.next())
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

	private void addTermToTerms(CachedMonitoredTerm o,
			MonitoredOrgManufTerms terms) {
		switch (o.getStatus()) {
		case DISUSE:
			terms.pauseTerms.add(o);
			break;
		case NOTACTIVE:
			terms.notActiveTerms.add(o);
			break;
		case NOTINSTALL:
			terms.notInstallTerms.add(o);
			break;
		default:
			terms.normalTerms.add(o);
			break;
		}
	}

	public void inGetMonitoredTerms(MonitoredOrgManufTerms ls, Integer manufId) {
		if (manufId == null)
			for (CachedMonitoredTerm o : terms) {
				addTermToTerms(o, ls);
			}
		else {
			int id = manufId;
			for (CachedMonitoredTerm o : terms) {
				if (o.getManufId() == id) {
					addTermToTerms(o, ls);
				}
			}
		}
		for (Object o : getChildren()) {
			MonitoredOrgNode node = (MonitoredOrgNode) o;
			node.inGetMonitoredTerms(ls, manufId);
		}
	}

	public void getMonitoredTerms(CachedMonitoredTermMap termsMap,
			MonitoredOrgManufTerms ls, Integer manufId) {
		if (getParent() == null || getParent().getId() == null) {
			Enumeration<CachedMonitoredTerm> en = termsMap.elements();
			if (manufId == null) {
				while (en.hasMoreElements()) {
					CachedMonitoredTerm o = (CachedMonitoredTerm) en
							.nextElement();
					addTermToTerms(o, ls);
				}
			} else {
				int id = manufId;
				while (en.hasMoreElements()) {
					CachedMonitoredTerm o = (CachedMonitoredTerm) en
							.nextElement();
					if (o.getManufId() == id)
						addTermToTerms(o, ls);
				}
			}
		} else {
			inGetMonitoredTerms(ls, manufId);
		}
	}
}
