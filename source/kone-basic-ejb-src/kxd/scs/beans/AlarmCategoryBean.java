package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseAlarmCategory;
import kxd.remote.scs.interfaces.AlarmCategoryBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.device.AlarmCategoryDao;

@Stateless(name = "kxd-ejb-alarmCategoryBean", mappedName = "kxd-ejb-alarmCategoryBean")
public class AlarmCategoryBean extends BaseBean implements
		AlarmCategoryBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseAlarmCategory> getAlarmCategoryList(long loginUserId,
			String keyword) {
		return AlarmCategoryDao.getAlarmCategoryList(getDao(), loginUserId,
				keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseAlarmCategory find(int alarmCategoryId) {
		return AlarmCategoryDao.find(getDao(), alarmCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void add(long loginUserId, BaseAlarmCategory alarmCategory) {
		AlarmCategoryDao.add(getDao(), loginUserId, alarmCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] alarmCategoryId) {
		AlarmCategoryDao.delete(getDao(), loginUserId, alarmCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BaseAlarmCategory alarmCategory) {
		AlarmCategoryDao.edit(getDao(), loginUserId, alarmCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseAlarmCategory> queryAlarmCategory(
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return AlarmCategoryDao.queryAlarmCategory(getDao(), queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}
}
