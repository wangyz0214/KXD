package kxd.engine.cache.beans.sts;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.engine.cache.beans.CachedObject;
import kxd.engine.cache.ejb.beans.EjbCachedOrgNode;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.remote.scs.util.AppException;
import kxd.util.IdableObject;
import kxd.util.TreeNode;
import kxd.util.TreeNodeData;
import kxd.util.stream.Stream;

/**
 * 缓存机构数据
 * 
 * @author zhaom
 * 
 */
public class CachedOrg extends CachedIntegerObject implements
		TreeNodeData<Integer> {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.org";
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
	private Integer parentOrgId;
	/**
	 * 深度
	 */
	private int depth;
	private ArrayList<Integer> parents = new ArrayList<Integer>();
	private ArrayList<Integer> children = new ArrayList<Integer>();
	// private ArrayList<Integer> terms = new ArrayList<Integer>();
	private int orgType;
	private String parentPath;
	private String extField0;
	private String extField1;
	private String extField2;
	private String extField3;
	private String extField4;
	private String standardAreaCode;
	private String simpleName;
	// 运维配置
	private final ConcurrentHashMap<String, Object> maintConfigMap = new ConcurrentHashMap<String, Object>();

	public CachedOrg() {
		super();
	}

	public CachedOrg(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedOrg(Integer id) {
		super(id);
	}

	public List<Integer> getParents() {
		return parents;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setOrgCode(stream.readPacketByteString(3000));
		setOrgName(stream.readPacketByteString(3000));
		setOrgFullName(stream.readPacketByteString(3000));
		setAddress(stream.readPacketByteString(3000));
		setTelphone(stream.readPacketByteString(3000));
		setContacter(stream.readPacketByteString(3000));
		setEmail(stream.readPacketByteString(3000));
		setSerialNumber(stream.readInt(false, 3000));
		setDepth(stream.readInt(false, 3000));
		int count = stream.readShort(false, 3000);
		parents.clear();
		for (int i = 0; i < count; i++) {
			parents.add(stream.readInt(false, 3000));
		}
		orgType = stream.readByte(3000);
		parentPath = stream.readPacketByteString(3000);
		extField0 = stream.readPacketByteString(3000);
		extField1 = stream.readPacketByteString(3000);
		extField2 = stream.readPacketByteString(3000);
		extField3 = stream.readPacketByteString(3000);
		extField4 = stream.readPacketByteString(3000);
		standardAreaCode = stream.readPacketByteString(3000);
		simpleName = stream.readPacketByteString(3000);
		count = stream.readShort(false, 3000);
		for (int i = 0; i < count; i++)
			children.add(stream.readInt(false, 3000));
		// count = stream.readShort(false, 3000);
		// for (int i = 0; i < count; i++)
		// terms.add(stream.readInt(false, 3000));
		maintConfigMap.clear();
		count = stream.readInt(false, 3000);
		for (int i = 0; i < count; i++) {
			String key = stream.readPacketByteString(3000);
			try {
				@SuppressWarnings("unchecked")
				CachedObject<Serializable> o = (CachedObject<Serializable>) CachedTermConfig.maintConfigClassMap
						.get(key).newInstance();
				o.readData(stream);
				maintConfigMap.put(key, o);
			} catch (InstantiationException e) {
				throw new IOException("read(key=" + key + ") error:", e);
			} catch (IllegalAccessException e) {
				throw new IOException("read(key=" + key + ") error:", e);
			}
		}
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getOrgCode(), 3000);
		stream.writePacketByteString(getOrgName(), 3000);
		stream.writePacketByteString(getOrgFullName(), 3000);
		stream.writePacketByteString(getAddress(), 3000);
		stream.writePacketByteString(getTelphone(), 3000);
		stream.writePacketByteString(getContacter(), 3000);
		stream.writePacketByteString(getEmail(), 3000);
		stream.writeInt(getSerialNumber(), false, 3000);
		stream.writeInt(depth, false, 3000);
		stream.writeShort((short) parents.size(), false, 3000);
		for (Integer id : parents) {
			stream.writeInt(id, false, 3000);
		}
		stream.writeByte((byte) orgType, 3000);
		stream.writePacketByteString(parentPath, 3000);
		stream.writePacketByteString(extField0, 3000);
		stream.writePacketByteString(extField1, 3000);
		stream.writePacketByteString(extField2, 3000);
		stream.writePacketByteString(extField3, 3000);
		stream.writePacketByteString(extField4, 3000);
		stream.writePacketByteString(standardAreaCode, 3000);
		stream.writePacketByteString(simpleName, 3000);
		stream.writeShort((short) children.size(), false, 3000);
		for (Integer id : children)
			stream.writeInt(id, false, 3000);
		// stream.writeShort((short) terms.size(), false, 3000);
		// for (Integer id : terms)
		// stream.writeInt(id, false, 3000);
		stream.writeInt(maintConfigMap.size(), false, 3000);
		Enumeration<String> keys = maintConfigMap.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			stream.writePacketByteString(key, 3000);
			((CachedObject<?>) maintConfigMap.get(key)).writeData(stream);
		}
	}

	public Integer getParentOrgId() {
		if (parentOrgId == null) {
			if (parents.size() > 0)
				parentOrgId = parents.get(parents.size() - 1);
		}
		return parentOrgId;
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

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedOrg) {
			CachedOrg o = (CachedOrg) src;
			setAddress(o.address);
			setContacter(o.contacter);
			setEmail(o.email);
			setSerialNumber(o.serialNumber);
			setOrgCode(o.orgCode);
			setOrgFullName(o.orgFullName);
			setOrgName(o.orgName);
			setTelphone(o.telphone);
			setDepth(o.depth);
			orgType = o.orgType;
			parentPath = o.parentPath;
			parents.clear();
			parents.addAll(o.parents);
			extField0 = o.extField0;
			extField1 = o.extField1;
			extField2 = o.extField2;
			extField3 = o.extField3;
			extField4 = o.extField4;
			standardAreaCode = o.standardAreaCode;
			simpleName = o.simpleName;
			children.clear();
			children.addAll(o.children);
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedOrg();
	}

	@Override
	public String getText() {
		return orgName;
	}

	@Override
	public void setText(String text) {
		orgName = text;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getOrgType() {
		return orgType;
	}

	public void setOrgType(int orgType) {
		this.orgType = orgType;
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

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
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
			o.setSimpleName(simpleName);
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

	public String getStandardAreaCode() {
		return standardAreaCode;
	}

	public void setStandardAreaCode(String standardAreaCode) {
		this.standardAreaCode = standardAreaCode;
	}

	CachedOrg parentOrg;

	public CachedOrg getParentOrg() {
		if (parentOrg == null && getParentOrgId() != null) {
			parentOrg = CacheHelper.orgMap.get(getParentOrgId());
		}
		return parentOrg;
	}

	public ArrayList<Integer> getChildren() {
		return children;
	}

	// public ArrayList<Integer> getTerms() {
	// return terms;
	//

	List<CachedOrg> childrenOrg;
	static Comparator<CachedOrg> orgComparator = new Comparator<CachedOrg>() {
		@Override
		public int compare(CachedOrg o1, CachedOrg o2) {
			return o1.serialNumber - o2.serialNumber;
		}
	};

	public List<CachedOrg> getChildrenOrgList() {
		try {
			if (childrenOrg == null) {
				Map<Integer, CachedOrg> map = CacheHelper.orgMap
						.getCacheValues(children);
				childrenOrg = new ArrayList<CachedOrg>();
				childrenOrg.addAll(map.values());
				Collections.sort(childrenOrg, orgComparator);
			}
			return childrenOrg;
		} catch (NamingException e) {
			throw new AppException(e);
		}
	}

	// List<CachedTermConfig> termList;

	//
	// public List<CachedTermConfig> getTermList() {
	// try {
	// if (childrenOrg == null) {
	// Map<Integer, CachedTermConfig> map = CacheHelper.termMap
	// .getCacheValues(terms);
	// termList = new ArrayList<CachedTermConfig>();
	// for (Integer id : children)
	// termList.add(map.get(id));
	// }
	// return termList;
	// } catch (NamingException e) {
	// throw new AppException(e);
	// }
	// }

	public boolean isHasChildren() {
		return getChildren().isEmpty();
	}

	public QueryedOrg toQueryedOrg() {
		QueryedOrg o = new QueryedOrg();
		if (getParentOrgId() != null)
			o.setParentOrg(new BaseOrg(getParentOrgId()));
		o.setId(getId());
		o.setOrgName(getOrgName());
		o.setOrgFullName(getOrgFullName());
		o.setDepth(getDepth());
		o.setHasChildren(!getChildren().isEmpty());
		o.setOrgType(getOrgType());
		o.setStandardAreaCode(getStandardAreaCode());
		o.setSimpleName(simpleName);
		return o;
	}

	public List<QueryedOrg> getOrgChildren(Integer selected, boolean includeSelf) {
		CachedOrg selectOrg = null;
		ArrayList<QueryedOrg> c = new ArrayList<QueryedOrg>();
		if (includeSelf) {
			c.add(toQueryedOrg());
		}
		for (CachedOrg o : getChildrenOrgList()) {
			QueryedOrg qo = o.toQueryedOrg();
			c.add(qo);
			if (selected != null && o.getId().equals(selected)) {
				selectOrg = o;
				qo.setSelected(true);
			}
		}
		if (selected != null && selectOrg == null) {
			selectOrg = CacheHelper.orgMap.get(selected);
		}
		if (selectOrg != null && !selectOrg.getParentOrgId().equals(getId())
				&& selectOrg.getParents().contains(getId())) {
			ArrayList<CachedOrg> ls = new ArrayList<CachedOrg>();
			CachedOrg porg = selectOrg.getParentOrg();
			while (porg != null && porg.getId() != null) {
				ls.add(0, porg);
				if (porg.getParentOrgId().equals(getId()))
					break;
				porg = porg.getParentOrg();
			}
			for (CachedOrg o : ls) {
				Iterator<?> it = o.getChildrenOrgList().iterator();
				int first = c.indexOf(o.toQueryedOrg());
				if (first < 0)
					first = 0;
				while (it.hasNext()) {
					QueryedOrg qo = ((CachedOrg) it.next()).toQueryedOrg();
					if (selectOrg.getId().equals(qo.getId()))
						qo.setSelected(true);
					first++;
					c.add(first, qo);
				}
			}
			int index = c.indexOf(selectOrg.toQueryedOrg());
			if (index > -1)
				c.get(index).setSelected(true);
		} else if (selectOrg != null && c.size() > 0)
			c.get(0).setSelected(true);
		return c;
	}

	public ConcurrentHashMap<String, Object> getMaintConfigMap() {
		return maintConfigMap;
	}
}
