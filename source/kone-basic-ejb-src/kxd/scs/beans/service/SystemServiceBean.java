package kxd.scs.beans.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.interfaces.service.SystemServiceBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.publish.InfoDao;
import kxd.scs.dao.publish.OrgAdDao;
import kxd.scs.dao.publish.PrintAdDao;
import kxd.scs.dao.publish.TermAdDao;

import org.apache.log4j.Logger;

@Stateless(name = "kxd-ejb-systemServiceBean", mappedName = "kxd-ejb-systemServiceBean")
public class SystemServiceBean extends BaseBean implements
		SystemServiceBeanRemote {

	static Logger logger = Logger.getLogger(SystemServiceBean.class);

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<?> getParamsConfig() {
		return getDao()
				.query("select paramcategoryid,paramvalue,paramdesp from paramconfig order by paramcategoryid,paramvalue");
	}

	/**
	 * 获取广告列表
	 * 
	 * @param option
	 *            操作：0 - 打印广告；1 - 机构广告；2 - 终端广告；3 - 信息
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<?> queryAuditInfo(int option, boolean queryCount,
			long loginUserId, int orgId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		if (option == 0) {
			return PrintAdDao.queryNeedAuditPrintAd(getDao(), orgId,
					queryCount, loginUserId, filter, filterContent,
					firstResult, maxResults);
		} else if (option == 1) {
			return OrgAdDao
					.queryNeedAuditAd(getDao(), orgId, queryCount, loginUserId,
							filter, filterContent, firstResult, maxResults);
		} else if (option == 2) {
			return TermAdDao
					.queryNeedAuditAd(getDao(), orgId, queryCount, loginUserId,
							filter, filterContent, firstResult, maxResults);
		} else if (option == 3) {
			return InfoDao
					.queryNeedAuditInfo(getDao(), orgId, queryCount,
							loginUserId, filter, filterContent, firstResult,
							maxResults);
		} else
			return null;
	}
}
