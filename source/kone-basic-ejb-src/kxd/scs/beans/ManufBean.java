package kxd.scs.beans;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.right.EditedManuf;
import kxd.remote.scs.interfaces.ManufBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.right.ManufDao;

@Stateless(name = "kxd-ejb-manufBean", mappedName = "kxd-ejb-manufBean")
public class ManufBean extends BaseBean implements ManufBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedManuf find(int manufId) {
		return ManufDao.find(getDao(), manufId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedManuf manuf) {
		return ManufDao.add(getDao(), loginUserId, manuf);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] manufId) {
		ManufDao.delete(getDao(), loginUserId, manufId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedManuf manuf) {
		ManufDao.edit(getDao(), loginUserId, manuf);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedManuf> queryManuf(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return ManufDao.queryManuf(getDao(), queryCount, loginUserId, filter,
				filterContent, firstResult, maxResults);
	}
}
