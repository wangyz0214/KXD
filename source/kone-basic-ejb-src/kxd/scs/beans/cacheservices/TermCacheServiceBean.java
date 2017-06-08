package kxd.scs.beans.cacheservices;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.cache.beans.sts.CachedFileCategory;
import kxd.engine.cache.interfaces.CacheServiceBeanRemote;
import kxd.engine.dao.BaseBean;
import kxd.engine.helper.CacheHelper;
import kxd.scs.dao.appdeploy.AppCategoryDao;
import kxd.scs.dao.appdeploy.AppDao;
import kxd.scs.dao.appdeploy.BusinessCategoryDao;
import kxd.scs.dao.appdeploy.BusinessDao;
import kxd.scs.dao.appdeploy.CommInterfaceDao;
import kxd.scs.dao.appdeploy.PayItemDao;
import kxd.scs.dao.appdeploy.PayWayDao;
import kxd.scs.dao.appdeploy.TradeCodeDao;
import kxd.scs.dao.cache.OrgCodeDao;
import kxd.scs.dao.device.AlarmCategoryDao;
import kxd.scs.dao.device.DeviceDao;
import kxd.scs.dao.device.DeviceDriverDao;
import kxd.scs.dao.device.DeviceTypeDao;
import kxd.scs.dao.device.DeviceTypeDriverDao;
import kxd.scs.dao.fileservice.FileCategoryDao;
import kxd.scs.dao.fileservice.FileContentTypeDao;
import kxd.scs.dao.fileservice.FileHostDao;
import kxd.scs.dao.fileservice.FileListDao;
import kxd.scs.dao.fileservice.FileOwnerDao;
import kxd.scs.dao.fileservice.FileUserDao;
import kxd.scs.dao.right.ManufDao;
import kxd.scs.dao.right.OrgDao;
import kxd.scs.dao.system.converters.PrintTypeDao;
import kxd.scs.dao.term.BankTermDao;
import kxd.scs.dao.term.TermDao;
import kxd.scs.dao.term.TermTypeDao;
import kxd.util.DateTime;
import kxd.util.KeyValue;

import org.apache.log4j.Logger;

@Stateless(name = "kxd-ejb-termCacheBean", mappedName = "kxd-ejb-termCacheBean")
public class TermCacheServiceBean extends BaseBean implements
		CacheServiceBeanRemote {
	static Logger logger = Logger.getLogger(TermCacheServiceBean.class);

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public KeyValue<byte[], List<?>> getKeys(String className, Object extParam) {
		if (className.equals("CachedAlarmCategory")) {
			return AlarmCategoryDao.getCachedAlarmCategoryHashMapKeys(getDao());
		} else if (className.equals("CachedApp")) {
			return AppDao.getCachedAppHashMapKeys(getDao());
		} else if (className.equals("CachedDevice")) {
			return DeviceDao.getCachedDeviceHashMapKeys(getDao());
		} else if (className.equals("CachedDeviceDriver")) {
			return DeviceDriverDao.getCachedDeviceDriverHashMapKeys(getDao());
		} else if (className.equals("CachedDeviceType")) {
			return DeviceTypeDao.getCachedDeviceTypeHashMapKeys(getDao());
		} else if (className.equals("CachedDeviceTypeDriver")) {
			return DeviceTypeDriverDao
					.getCachedDeviceTypeDriverHashMapKeys(getDao());
		} else if (className.equals("CachedFileCategory")) {
			return FileCategoryDao.getCachedFileCategoryHashMapKeys(getDao());
		} else if (className.equals("CachedFileContentType")) {
			return FileContentTypeDao
					.getCachedFileContentTypeHashMapKeys(getDao());
		} else if (className.equals("CachedFileHost")) {
			return FileHostDao.getCachedFileHostHashMapKeys(getDao());
		} else if (className.equals("CachedFileOwner")) {
			return FileOwnerDao.getCachedFileOwnerHashMapKeys(getDao());
		} else if (className.equals("CachedFileUser")) {
			return FileUserDao.getCachedFileUserHashMapKeys(getDao());
		} else if (className.equals("CachedManuf")) {
			return ManufDao.getCachedManufHashMapKeys(getDao());
		} else if (className.equals("CachedTermType")) {
			return TermTypeDao.getCachedTermTypeHashMapKeys(getDao());
		} else if (className.equals("CachedTradeCode")) {
			return TradeCodeDao.getCachedTradeCodeHashMapKeys(getDao());
		} else if (className.equals("CachedPrintType")) {
			return PrintTypeDao.getCachedPrintTypeHashMapKeys(getDao());
		} else if (className.equals("CachedPayWay")) {
			return PayWayDao.getCachedPayWayHashMapKeys(getDao());
		} else if (className.equals("CachedPayItem")) {
			return PayItemDao.getCachedPayItemHashMapKeys(getDao());
		} else if (className.equals("CachedOrgCode")) {
			return OrgCodeDao.getCachedOrgCodeHashMapKeys(getDao());
		} else if (className.equals("CachedAppCategory")) {
			return AppCategoryDao.getCachedHashMapKeys(getDao());
		} else if (className.equals("CachedBusinessCategory")) {
			return BusinessCategoryDao.getCachedHashMapKeys(getDao());
		} else if (className.equals("CachedBusiness")) {
			return BusinessDao.getCachedHashMapKeys(getDao());
		} else if (className.equals("CachedFileItem")) {
			return FileListDao.getCachedFileItemHashMapKeys(getDao(),
					(Short) extParam);
		} else if (className.equals("CachedCommInterface")) {
			return CommInterfaceDao.getCachedHashMapKeys(getDao());
		} else {
			throw new UnsupportedOperationException("未知类名[" + className + "]");
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<?> getValues(String className, List<?> keys, Object extParam) {
		if (className.equals("CachedOrg")) {
			return OrgDao.getCachedOrgsList(getDao(), keys);
		} else if (className.equals("CachedTermConfig")) {
			return TermDao.getCachedTermsList(getDao(), keys);
		} else if (className.equals("CachedAlarmCategory")) {
			return AlarmCategoryDao.getCachedAlarmCategorysList(getDao(), keys);
		} else if (className.equals("CachedApp")) {
			return AppDao.getCachedAppsList(getDao(), keys);
		} else if (className.equals("CachedBankTerm")) {
			return BankTermDao.getCachedBankTermsList(getDao(), keys);
		} else if (className.equals("CachedDevice")) {
			return DeviceDao.getCachedDevicesList(getDao(), keys);
		} else if (className.equals("CachedDeviceDriver")) {
			return DeviceDriverDao.getCachedDeviceDriversList(getDao(), keys);
		} else if (className.equals("CachedDeviceType")) {
			return DeviceTypeDao.getCachedDeviceTypesList(getDao(), keys);
		} else if (className.equals("CachedDeviceTypeDriver")) {
			return DeviceTypeDriverDao.getCachedDeviceTypeDriversList(getDao(),
					keys);
		} else if (className.equals("CachedFileCategory")) {
			return FileCategoryDao.getCachedFileCategorysList(getDao(), keys);
		} else if (className.equals("CachedFileContentType")) {
			return FileContentTypeDao.getCachedFileContentTypesList(getDao(),
					keys);
		} else if (className.equals("CachedFileHost")) {
			return FileHostDao.getCachedFileHostsList(getDao(), keys);
		} else if (className.equals("CachedFileOwner")) {
			return FileOwnerDao.getCachedFileOwnersList(getDao(), keys);
		} else if (className.equals("CachedFileUser")) {
			return FileUserDao.getCachedFileUsersList(getDao(), keys);
		} else if (className.equals("CachedManuf")) {
			return ManufDao.getCachedManufsList(getDao(), keys);
		} else if (className.equals("CachedTermType")) {
			return TermTypeDao.getCachedTermTypesList(getDao(), keys);
		} else if (className.equals("CachedTradeCode")) {
			return TradeCodeDao.getCachedTradeCodesList(getDao(), keys);
		} else if (className.equals("CachedPrintType")) {
			return PrintTypeDao.getCachedPrintTypesList(getDao(), keys);
		} else if (className.equals("CachedPayWay")) {
			return PayWayDao.getCachedPayWaysList(getDao(), keys);
		} else if (className.equals("CachedPayItem")) {
			return PayItemDao.getCachedPayItemsList(getDao(), keys);
		} else if (className.equals("CachedOrgCode")) {
			return OrgCodeDao.getCachedOrgCodeList(getDao(), keys);
		} else if (className.equals("CachedAppCategory")) {
			return AppCategoryDao.getCachedList(getDao(), keys);
		} else if (className.equals("CachedBusinessCategory")) {
			return BusinessCategoryDao.getCachedList(getDao(), keys);
		} else if (className.equals("CachedBusiness")) {
			return BusinessDao.getCachedList(getDao(), keys);
		} else if (className.equals("CachedFileItem")) {
			return FileListDao.getCachedFileItemsList(getDao(), keys,
					(Short) extParam);
		} else if (className.equals("CachedCommInterface")) {
			return CommInterfaceDao.getCachedList(getDao(), keys);
		} else {
			throw new UnsupportedOperationException("未知类名[" + className + "]");
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Serializable getKeyValue(String className, Serializable id,
			Object extParam) {
		if (className.equals("CachedOrg")) {
			return OrgDao.getCachedOrg(getDao(), (Integer) id);
		} else if (className.equals("CachedTermConfig")) {
			return TermDao.getCachedTerm(getDao(), (Integer) id);
		} else if (className.equals("CachedAlarmCategory")) {
			return AlarmCategoryDao.getCachedAlarmCategory(getDao(),
					(Integer) id);
		} else if (className.equals("CachedApp")) {
			return AppDao.getCachedApp(getDao(), (Integer) id);
		} else if (className.equals("CachedBankTerm")) {
			return BankTermDao.getCachedBankTerm(getDao(), (Integer) id);
		} else if (className.equals("CachedDevice")) {
			return DeviceDao.getCachedDevice(getDao(), (Integer) id);
		} else if (className.equals("CachedDeviceDriver")) {
			return DeviceDriverDao
					.getCachedDeviceDriver(getDao(), (Integer) id);
		} else if (className.equals("CachedDeviceType")) {
			return DeviceTypeDao.getCachedDeviceType(getDao(), (Integer) id);
		} else if (className.equals("CachedDeviceTypeDriver")) {
			return DeviceTypeDriverDao.getCachedDeviceTypeDriver(getDao(),
					(Integer) id);
		} else if (className.equals("CachedFileCategory")) {
			return FileCategoryDao.getCachedFileCategory(getDao(), (Short) id);
		} else if (className.equals("CachedFileContentType")) {
			return FileContentTypeDao.getCachedFileContentType(getDao(),
					(String) id);
		} else if (className.equals("CachedFileHost")) {
			return FileHostDao.getCachedFileHost(getDao(), (Short) id);
		} else if (className.equals("CachedFileOwner")) {
			return FileOwnerDao.getCachedFileOwner(getDao(), (Short) id);
		} else if (className.equals("CachedFileUser")) {
			return FileUserDao.getCachedFileUser(getDao(), (String) id);
		} else if (className.equals("CachedPrintType")) {
			return PrintTypeDao.getCachedPrintType(getDao(), (Short) id);
		} else if (className.equals("CachedManuf")) {
			return ManufDao.getCachedManuf(getDao(), (Integer) id);
		} else if (className.equals("CachedTermType")) {
			return TermTypeDao.getCachedTermType(getDao(), (Integer) id);
		} else if (className.equals("CachedTradeCode")) {
			return TradeCodeDao.getCachedTradeCode(getDao(), (Integer) id);
		} else if (className.equals("CachedPayWay")) {
			return PayWayDao.getCachedPayWay(getDao(), (Integer) id);
		} else if (className.equals("CachedPayItem")) {
			return PayItemDao.getCachedPayItem(getDao(), (Integer) id);
		} else if (className.equals("CachedOrgCode")) {
			return OrgCodeDao.getCachedOrgCode(getDao(), (String) id);
		} else if (className.equals("CachedAppCategory")) {
			return AppCategoryDao.getCached(getDao(), (Integer) id);
		} else if (className.equals("CachedBusinessCategory")) {
			return BusinessCategoryDao.getCached(getDao(), (Integer) id);
		} else if (className.equals("CachedBusiness")) {
			return BusinessDao.getCached(getDao(), (Integer) id);
		} else if (className.equals("CachedFileItem")) {
			return FileListDao.getCachedFileItem(getDao(), (String) id,
					(Short) extParam);
		} else if (className.equals("CachedCommInterface")) {
			return CommInterfaceDao.getCached(getDao(), (Short) id);
		} else {
			throw new UnsupportedOperationException("未知类名[" + className + "]");
		}
	}

	static AtomicBoolean cacheLoaded = new AtomicBoolean(false);
	static ReentrantLock lock = new ReentrantLock();

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public boolean loadInitCache() {
		// return true;
		if (!lock.tryLock())
			return cacheLoaded.get();
		try {
			if (!cacheLoaded.get()) {
				logger.info("开始重建缓存...");
				Date t = new Date();
				try {
					CacheHelper.alarmCategoryMap.refresh(true);
					CacheHelper.appMap.refresh(true);
					CacheHelper.deviceMap.refresh(true);
					CacheHelper.deviceDriverMap.refresh(true);
					CacheHelper.deviceTypeMap.refresh(true);
					CacheHelper.deviceTypeDriverMap.refresh(true);
					CacheHelper.fileCategoryMap.refresh(true);
					CacheHelper.fileContentTypeMap.refresh(true);
					CacheHelper.fileHostMap.refresh(true);
					CacheHelper.fileOwnerMap.refresh(true);
					CacheHelper.fileUserMap.refresh(true);
					CacheHelper.manufMap.refresh(true);
					CacheHelper.termTypeMap.refresh(true);
					CacheHelper.tradeCodeMap.refresh(true);
					CacheHelper.printTypeMap.refresh(true);
					CacheHelper.payWayMap.refresh(true);
					CacheHelper.payItemMap.refresh(true);
					OrgCodeDao.buildCache(getDao());
					CacheHelper.appCategoryMap.refresh(true);
					CacheHelper.businessCategoryMap.refresh(true);
					CacheHelper.businessMap.refresh(true);
					CacheHelper.commInterfaceMap.refresh(true);
					for (CachedFileCategory o : CacheHelper.fileCategoryMap
							.values())
						if (o.getId() < 4)
							FileListDao.buildCache(getDao(), (Short) o.getId());
					OrgDao.buildCache(getDao());
					TermDao.buildCache(getDao());
					logger.info("缓存重建完毕，耗时："
							+ DateTime.secondsBetween(t, new Date()));
				} catch (Throwable e) {
					logger.error("缓存重建失败：", e);
				} finally {
					cacheLoaded.set(true);
				}
			}
			return cacheLoaded.get();
		} finally {
			lock.unlock();
		}
	}
}
