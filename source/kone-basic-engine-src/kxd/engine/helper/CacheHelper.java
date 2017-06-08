package kxd.engine.helper;

import java.util.concurrent.ConcurrentHashMap;

import kxd.engine.cache.CacheConfigParams;
import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.CachedMap;
import kxd.engine.cache.CachedTermMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.ShortData;
import kxd.engine.cache.beans.ShortStringData;
import kxd.engine.cache.beans.sts.CachedAlarmCategory;
import kxd.engine.cache.beans.sts.CachedApp;
import kxd.engine.cache.beans.sts.CachedAppCategory;
import kxd.engine.cache.beans.sts.CachedBankTerm;
import kxd.engine.cache.beans.sts.CachedBusiness;
import kxd.engine.cache.beans.sts.CachedBusinessCategory;
import kxd.engine.cache.beans.sts.CachedCommInterface;
import kxd.engine.cache.beans.sts.CachedDevice;
import kxd.engine.cache.beans.sts.CachedDeviceDriver;
import kxd.engine.cache.beans.sts.CachedDeviceType;
import kxd.engine.cache.beans.sts.CachedDeviceTypeDriver;
import kxd.engine.cache.beans.sts.CachedFileCategory;
import kxd.engine.cache.beans.sts.CachedFileContentType;
import kxd.engine.cache.beans.sts.CachedFileHost;
import kxd.engine.cache.beans.sts.CachedFileItem;
import kxd.engine.cache.beans.sts.CachedFileOwner;
import kxd.engine.cache.beans.sts.CachedFileUser;
import kxd.engine.cache.beans.sts.CachedManuf;
import kxd.engine.cache.beans.sts.CachedOrg;
import kxd.engine.cache.beans.sts.CachedOrgCode;
import kxd.engine.cache.beans.sts.CachedPayItem;
import kxd.engine.cache.beans.sts.CachedPayWay;
import kxd.engine.cache.beans.sts.CachedPrintType;
import kxd.engine.cache.beans.sts.CachedTermByCode;
import kxd.engine.cache.beans.sts.CachedTermConfig;
import kxd.engine.cache.beans.sts.CachedTermType;
import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.cache.beans.sts.CachedTradeCodeHashMap;
import kxd.util.memcached.Cacheable;
import kxd.util.memcached.MemCachedClient;

public class CacheHelper {
	// private static Logger logger = Logger.getLogger(CacheHelper.class);
	static public MemCachedClient termMc = Cacheable.memCachedClientMap
			.get("terminal");
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Integer, CachedAlarmCategory> alarmCategoryMap = new CachedHashMap<Integer, CachedAlarmCategory>(
			3600, CachedAlarmCategory.getParams(),
			CachedAlarmCategory.KEY_PREFIX, CachedAlarmCategory.class,
			IntegerData.class);
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Integer, CachedApp> appMap = new CachedHashMap<Integer, CachedApp>(
			3600, CachedApp.getParams(), CachedApp.KEY_PREFIX, CachedApp.class,
			IntegerData.class);
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Integer, CachedAppCategory> appCategoryMap = new CachedHashMap<Integer, CachedAppCategory>(
			3600, CachedAppCategory.getParams(), CachedAppCategory.KEY_PREFIX,
			CachedAppCategory.class, IntegerData.class);
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Short, CachedCommInterface> commInterfaceMap = new CachedHashMap<Short, CachedCommInterface>(
			3600, CachedCommInterface.getParams(),
			CachedCommInterface.KEY_PREFIX, CachedCommInterface.class,
			ShortData.class);
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Integer, CachedBusinessCategory> businessCategoryMap = new CachedHashMap<Integer, CachedBusinessCategory>(
			3600, CachedBusinessCategory.getParams(),
			CachedBusinessCategory.KEY_PREFIX, CachedBusinessCategory.class,
			IntegerData.class);
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Integer, CachedBusiness> businessMap = new CachedHashMap<Integer, CachedBusiness>(
			3600, CachedBusiness.getParams(), CachedBusiness.KEY_PREFIX,
			CachedBusiness.class, IntegerData.class);
	/**
	 * 每隔1分钟自动更新一次本地缓存，调用时要注意性能影响，不需要重启服务器更新缓存
	 */
	static public CachedHashMap<Integer, CachedDevice> deviceMap = new CachedHashMap<Integer, CachedDevice>(
			60, CachedDevice.getParams(), CachedDevice.KEY_PREFIX,
			CachedDevice.class, IntegerData.class);
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Integer, CachedDeviceType> deviceTypeMap = new CachedHashMap<Integer, CachedDeviceType>(
			3600, CachedDeviceType.getParams(), CachedDeviceType.KEY_PREFIX,
			CachedDeviceType.class, IntegerData.class);
	/**
	 * 每隔1分钟自动更新一次本地缓存，调用时要注意性能影响，不需要重启服务器更新缓存
	 */
	static public CachedHashMap<Integer, CachedDeviceDriver> deviceDriverMap = new CachedHashMap<Integer, CachedDeviceDriver>(
			60, CachedDeviceDriver.getParams(), CachedDeviceDriver.KEY_PREFIX,
			CachedDeviceDriver.class, IntegerData.class);
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Integer, CachedDeviceTypeDriver> deviceTypeDriverMap = new CachedHashMap<Integer, CachedDeviceTypeDriver>(
			3600, CachedDeviceTypeDriver.getParams(),
			CachedDeviceTypeDriver.KEY_PREFIX, CachedDeviceTypeDriver.class,
			IntegerData.class);
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Short, CachedFileCategory> fileCategoryMap = new CachedHashMap<Short, CachedFileCategory>(
			3600, CachedFileCategory.getParams(),
			CachedFileCategory.KEY_PREFIX, CachedFileCategory.class,
			ShortData.class);
	/**
	 * 每隔10小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，更新应该重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<String, CachedFileContentType> fileContentTypeMap = new CachedHashMap<String, CachedFileContentType>(
			36000, CachedFileContentType.getParams(),
			CachedFileContentType.KEY_PREFIX, CachedFileContentType.class,
			ShortStringData.class);
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Short, CachedFileHost> fileHostMap = new CachedHashMap<Short, CachedFileHost>(
			3600, CachedFileHost.getParams(), CachedFileHost.KEY_PREFIX,
			CachedFileHost.class, ShortData.class);
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Short, CachedFileOwner> fileOwnerMap = new CachedHashMap<Short, CachedFileOwner>(
			3600, CachedFileOwner.getParams(), CachedFileOwner.KEY_PREFIX,
			CachedFileOwner.class, ShortData.class);
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<String, CachedFileUser> fileUserMap = new CachedHashMap<String, CachedFileUser>(
			3600, CachedFileUser.getParams(), CachedFileUser.KEY_PREFIX,
			CachedFileUser.class, ShortStringData.class);
	/**
	 * 每隔1小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Integer, CachedManuf> manufMap = new CachedHashMap<Integer, CachedManuf>(
			3600, CachedManuf.getParams(), CachedManuf.KEY_PREFIX,
			CachedManuf.class, IntegerData.class);
	/**
	 * 每隔10小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Short, CachedPrintType> printTypeMap = new CachedHashMap<Short, CachedPrintType>(
			36000, CachedPrintType.getParams(), CachedPrintType.KEY_PREFIX,
			CachedPrintType.class, ShortData.class);
	/**
	 * 每隔1分钟自动更新一次本地缓存，调用时要注意性能影响，不需要重启服务器更新缓存
	 */
	static public CachedHashMap<Integer, CachedTermType> termTypeMap = new CachedHashMap<Integer, CachedTermType>(
			60, CachedTermType.getParams(), CachedTermType.KEY_PREFIX,
			CachedTermType.class, IntegerData.class);
	/**
	 * 每隔10小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedTradeCodeHashMap tradeCodeMap = new CachedTradeCodeHashMap(
			CachedTradeCode.getParams(), CachedTradeCode.KEY_PREFIX,
			CachedTradeCode.class, IntegerData.class);
	/**
	 * 每隔10小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Short, CachedPayWay> payWayMap = new CachedHashMap<Short, CachedPayWay>(
			36000, CachedPayWay.getParams(), CachedPayWay.KEY_PREFIX,
			CachedPayWay.class, ShortData.class);
	/**
	 * 每隔10小时自动更新一次本地缓存，调用时可以放心调用，不会影响性能，但更新应该尽量晚上操作后重启服务器的手动更新本地缓存
	 */
	static public CachedHashMap<Short, CachedPayItem> payItemMap = new CachedHashMap<Short, CachedPayItem>(
			36000, CachedPayItem.getParams(), CachedPayItem.KEY_PREFIX,
			CachedPayItem.class, ShortData.class);
	/**
	 * 文件缓存映射
	 */
	static ConcurrentHashMap<Short, CachedMap<String, CachedFileItem>> fileItemsMap = new ConcurrentHashMap<Short, CachedMap<String, CachedFileItem>>();

	static public CachedMap<Integer, CachedOrg> orgMap = new CachedMap<Integer, CachedOrg>(
			CachedOrg.getParams(), CachedOrg.KEY_PREFIX, CachedOrg.class);
	static public CachedTermMap termMap = new CachedTermMap(
			CachedTermConfig.getParams(), CachedTermConfig.KEY_PREFIX,
			CachedTermConfig.class);
	static public CachedMap<Integer, CachedBankTerm> bankTermMap = new CachedMap<Integer, CachedBankTerm>(
			CachedBankTerm.getParams(), CachedBankTerm.KEY_PREFIX,
			CachedBankTerm.class);
	static public CachedMap<String, CachedOrgCode> orgCodeMap = new CachedMap<String, CachedOrgCode>(
			CachedOrgCode.getParams(), CachedOrgCode.KEY_PREFIX,
			CachedOrgCode.class);
	static public CachedMap<String, CachedTermByCode> termCodeMap = new CachedMap<String, CachedTermByCode>(
			CachedTermByCode.getParams(), CachedTermByCode.KEY_PREFIX,
			CachedTermByCode.class);
	static private CacheConfigParams fileServiceParams = new CacheConfigParams(
			"db", "kxd-ejb-termCacheBean",
			Cacheable.memCachedClientMap.get("fileservice"));

	synchronized static private CachedMap<String, CachedFileItem> getCachedFileMap(
			CachedFileCategory fileCategory) {
		CachedMap<String, CachedFileItem> map = fileItemsMap.get(fileCategory
				.getId());
		if (map == null) {
			switch (fileCategory.getId()) {
			case 0: // 本地缓存
			case 1:
			case 2:
				map = new CachedHashMap<String, CachedFileItem>(180,
						fileServiceParams, "$FILEITEM$."
								+ fileCategory.getIdString(),
						CachedFileItem.class, ShortStringData.class);
				map.setExternalParam(fileCategory.getId());
				break;
			default:
				map = new CachedMap<String, CachedFileItem>(fileServiceParams,
						"$FILEITEM$." + fileCategory.getIdString(),
						CachedFileItem.class);
				map.setExternalParam(fileCategory.getId());
			}
		}
		fileItemsMap.put(fileCategory.getId(), map);
		return map;
	}

	static public CachedMap<String, CachedFileItem> getFileItemMap(
			CachedFileCategory fileCategory) {
		CachedMap<String, CachedFileItem> map = fileItemsMap.get(fileCategory
				.getId());
		if (map == null) {
			return getCachedFileMap(fileCategory);
		} else
			return map;
	}
}
