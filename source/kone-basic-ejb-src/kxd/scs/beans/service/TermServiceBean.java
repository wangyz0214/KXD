package kxd.scs.beans.service;

import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.cache.beans.sts.CachedBankTerm;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.dao.BaseBean;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.interfaces.service.TermServiceBeanRemote;
import kxd.remote.scs.util.AppException;
import kxd.scs.dao.right.OrgDao;
import kxd.scs.dao.term.BankTermDao;
import kxd.scs.dao.term.TermDao;
import kxd.util.DataSecurity;
import kxd.util.DataUnit;

import org.apache.log4j.Logger;

@Stateless(name = "kxd-ejb-termServiceBean", mappedName = "kxd-ejb-termServiceBean")
public class TermServiceBean extends BaseBean implements TermServiceBeanRemote {

	static Logger logger = Logger.getLogger(TermServiceBean.class);
	static byte[] mainKey = DataUnit.hexToBytes("B6DA1C3BE989E6E3");
	final static int LOGINUPDATE_TIMES = 60 * 1000;

	/**
	 * 终端激活
	 * 
	 * @param manufNo
	 *            出厂编号
	 * @param guid
	 *            终端标识
	 * @return 终端ID
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int termActivate(String manufNo, String guid) {
		return TermDao.termActive(getDao(), manufNo, guid);
	}

	/**
	 * 终端登录
	 * 
	 * @param termId
	 *            终端ID
	 * @param guid
	 *            终端GUID
	 * @return 返回终端的属性
	 *         devices,orgid,orgname,orgfullname,address,appid,bankkey,opentime,
	 *         closetime
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Hashtable<String, Object> termLogin(Object term, String ip,
			String loginurl, String guid) {
		if (guid == null || guid.length() != 32) {
			throw new AppException("必须提供终端的唯一标识");
		}
		try {
			CachedTerm cachedTerm = (CachedTerm) term;
			if ((System.currentTimeMillis() - cachedTerm.getLastLoginTime()) >= LOGINUPDATE_TIMES) {
				String key = DataUnit.bytesToHex(DataSecurity.generateDesKey());
				String loginKey = kxd.util.DataSecurity.md5(Double
						.toString(new Random().nextDouble()));
				cachedTerm.setLastLoginTime(System.currentTimeMillis());
				cachedTerm.setWorkKey(key);
				cachedTerm.setLoginSessionID(loginKey);
				TermDao.termLogin(getDao(), ip, loginurl, cachedTerm);
			}
			Hashtable<String, Object> ls = new Hashtable<String, Object>();
			ls.put("workkey", DataUnit.bytesToHex(DataSecurity.desEncrypt(
					mainKey, DataUnit.hexToBytes(cachedTerm.getWorkKey()))));
			ls.put("loginsessionid", DataUnit.bytesToHex(DataSecurity
					.desEncrypt(mainKey, cachedTerm.getLoginSessionID()
							.getBytes())));
			return ls;
		} catch (Throwable e) {
			throw new AppException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public byte[] getMainKey() {
		return mainKey;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getWorkKey(int termId) {
		CachedTerm term = CacheHelper.termMap.getTerm(termId);
		if (term == null) {
			throw new AppException("非法终端");
		}
		switch (term.getStatus()) {
		case DISUSE:
			throw new AppException("终端被禁止使用");
		}
		return term.getWorkKey();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void bankTermSignin(int bankTermId, Date signinTime, String workKey,
			String macKey, String merchantAccount, String batch, String extField) {
		CachedBankTerm bt = CacheHelper.bankTermMap.get(bankTermId);
		if (bt == null) {
			throw new AppException("找不到银联终端[id=" + bankTermId + "]");
		}
		bt.setBatch(batch);
		bt.setSigninTime(signinTime);
		bt.setMacKey(macKey);
		bt.setWorkKey(workKey);
		bt.setMerchantAccount(merchantAccount);
		bt.setExtField(extField);
		BankTermDao.bankTermSignin(getDao(), bt);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void termPauseResume(Collection<Integer> terms, boolean pause) {
		TermDao.termPauseResume(getDao(), terms, pause);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void submitCashModify(int termId, int day, boolean detailModified,
			boolean traceModified) {
		TermDao.submitCashModify(getDao(), termId, day, detailModified,
				traceModified);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void submitTermFilesAttr(int termId, String filesAttr) {
		TermDao.submitTermFilesAttr(getDao(), termId, filesAttr);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer> getOrgChildren(int orgId) {
		return OrgDao.getOrgChildren(getDao(), orgId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer> getOrgTerms(int orgId) {
		return OrgDao.getOrgTerms(getDao(), orgId);
	}
}
