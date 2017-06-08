package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseAppFile;
import kxd.remote.scs.beans.appdeploy.EditedApp;
import kxd.remote.scs.interfaces.AppBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.appdeploy.AppDao;

import org.apache.log4j.Logger;

@Stateless(name = "kxd-ejb-appBean", mappedName = "kxd-ejb-appBean")
public class AppBean extends BaseBean implements AppBeanRemote {
	static Logger logger = Logger.getLogger(AppBean.class);

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseApp> getAppList(long loginUserId, String keyword) {
		return AppDao.getAppList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedApp find(int appId) {
		return AppDao.find(getDao(), appId);
	}
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedApp find(String appCode) {
		return AppDao.find(getDao(), appCode);
	}	

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedApp app) {
		return AppDao.add(getDao(), loginUserId, app);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] appId) {
		AppDao.delete(getDao(), loginUserId, appId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedApp app) {
		AppDao.edit(getDao(), loginUserId, app);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedApp> queryApp(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return AppDao.queryApp(getDao(), queryCount, loginUserId, filter,
				filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void addAppFile(long loginUserId, BaseAppFile o) {
		AppDao.addAppFile(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String[] deleteAppFile(long loginUserId, int[] id) {
		return AppDao.deleteAppFile(getDao(), loginUserId, id);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String editAppFile(long loginUserId, BaseAppFile o) {
		return AppDao.editAppFile(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseAppFile> getAppFileList(long loginUserId, int appId) {
		return AppDao.getAppFileList(getDao(), loginUserId, appId);
	}
}
