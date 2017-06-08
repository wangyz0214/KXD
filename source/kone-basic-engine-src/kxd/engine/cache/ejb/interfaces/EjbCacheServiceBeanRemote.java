package kxd.engine.cache.ejb.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.naming.NamingException;

import kxd.engine.cache.ejb.beans.EjbCachedOrg;
import kxd.remote.scs.beans.right.QueryedOrg;

@Remote
public interface EjbCacheServiceBeanRemote extends OrgChangeNotifyBeanRemote{
	public String getLastOrgModified();

	/**
	 * 初始化缓存后立即返回
	 */
	public void initCacheOnly();

	/**
	 * 从缓存中获取树列表
	 * 
	 * @param parentOrgId
	 *            父机构ID，null表示从根结点开始
	 * @param depth
	 *            查询深度，表示获取至第几级子机构
	 * @param selectedOrgId
	 *            选定的orgid，null表示没有选定的机构
	 * @param includeSelf
	 *            是否包含父机构
	 * @param keyword
	 *            关键字
	 * @return 获取的机构树列表
	 */
	public Collection<QueryedOrg> getOrgTreeItems(Integer parentOrgId,
			int depth, Integer selectedOrgId, boolean includeSelf,
			String keyword);

	public List<EjbCachedOrg> getOrgTree();

	/**
	 * 从缓存中获取机构树路径
	 * 
	 * @param orgId
	 *            机构ID
	 * @param separator
	 *            路径分隔符
	 * @return 路径字串
	 */
	public String getOrgTreePath(int orgId, String separator);

	public List<QueryedOrg> getParentOrgs(Integer orgId, boolean includeSelf)
			throws NamingException;

	/**
	 * 获取机构所属的省、市
	 * 
	 * @param orgIdList
	 *            要获取机构的ID列表
	 * @return 基于Object[]的Map，返回对应机构的数据，定义如下： <br>
	 *         object[0] - 该机构所属省名称<br>
	 *         object[1] - 该机构所属地市名称<br>
	 *         object[2] - 机构名称<br>
	 */
	public Map<?, ?> getOrgProvinceCity(List<?> orgIdList);

}
