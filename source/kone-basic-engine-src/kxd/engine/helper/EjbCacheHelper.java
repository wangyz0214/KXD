package kxd.engine.helper;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import kxd.engine.cache.ejb.beans.EjbCachedOrgNode;
import kxd.engine.cache.ejb.interfaces.EjbCacheServiceBeanRemote;
import kxd.remote.scs.util.AppException;

/**
 * EJB缓存帮助类
 * 
 * @author zhaom
 * 
 */
public class EjbCacheHelper {
	/**
	 * 缓存机构结点，不要直接使用此变量，请使用getCachedOrgNode()
	 */
	static public EjbCachedOrgNode cachedOrgNode;

	/**
	 * 获取缓存机构树对象，仅用于EJB容器
	 * 
	 * @return
	 */
	public static synchronized EjbCachedOrgNode getCachedOrgNode() {
		if (cachedOrgNode == null) { // 触发本地的EJB缓存
			try {
				InitialContext context = new InitialContext();
				try {
					EjbCacheServiceBeanRemote bean = (EjbCacheServiceBeanRemote) context
							.lookup("kxd-ejb-ejbCacheServiceBean");
					bean.initCacheOnly();
				} finally {
					context.close();
				}
			} catch (NamingException e) {
				throw new AppException(e);
			}
		}
		return cachedOrgNode;
	}

	/**
	 * 获取缓存机构结点，仅限于EJB容器
	 * 
	 * @param orgId
	 *            机构ID
	 * @return
	 */
	public static EjbCachedOrgNode getCachedOrgNode(int orgId) {
		return getCachedOrgNode().getGlobal(orgId);
	}

	/**
	 * 设备缓存机构树对象，仅用于EJB容器
	 * 
	 * @return
	 */
	public static synchronized void setCachedOrgNode(
			EjbCachedOrgNode cachedOrgNode) {
		EjbCacheHelper.cachedOrgNode = cachedOrgNode;
	}

}
