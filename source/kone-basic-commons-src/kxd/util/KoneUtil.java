package kxd.util;

import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

public class KoneUtil {
	static Logger logger = Logger.getLogger(KoneUtil.class);
	static public final int APP_SERVER_WEBLOGIC = 0;
	static public final int APP_SERVER_JBOSS = 1;
	static public final int APP_SERVER_WEBSPHERE = 2;
	static public final int APP_SERVER_UNKNOWN = 9999;
	static private int appServerType = -1;

	// static {
	// try {
	// logger = Logger.getLogger(KoneUtil.class);
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// while (!Thread.interrupted()) {
	// Runtime t = Runtime.getRuntime();
	// try {
	// FileOutputStream stream = new FileOutputStream(
	// "java-mem.log", true);
	// try {
	// logger.info("memory[free:"
	// + StringUnit.formatMemory(t
	// .freeMemory())
	// + " total:"
	// + StringUnit.formatMemory(t
	// .totalMemory())
	// + " max:"
	// + StringUnit.formatMemory(t.maxMemory())
	// + "]");
	// stream.write((new DateTime()
	// .format("yyyy-MM-dd HH:mm:ss:\t")
	// + "\tfree:"
	// + StringUnit.formatMemory(t
	// .freeMemory())
	// + "\ttotal:"
	// + StringUnit.formatMemory(t
	// .totalMemory())
	// + "\tmax:"
	// + StringUnit.formatMemory(t.maxMemory()) + "\r\n")
	// .getBytes());
	// } catch (IOException e) {
	// } finally {
	// try {
	// stream.close();
	// } catch (IOException e) {
	// }
	// }
	// } catch (Throwable e) {
	// }
	// try {
	// Thread.sleep(60000);
	// } catch (InterruptedException e) {
	// break;
	// }
	// }
	// }
	// }).start();
	// } catch (Throwable e) {
	//
	// }
	// }
	public static void main(String[] args){
		System.out.println(new Date());
	}
	synchronized static public int getAppServerType() {
		if (appServerType == -1) {
			if (System.getProperty("weblogic.home") != null
					|| System.getProperty("weblogic.Name") != null)
				appServerType = APP_SERVER_WEBLOGIC;
			else if (System.getProperty("jboss.server.home.url") != null
					|| System.getProperty("jboss.server.base.dir") != null)
				appServerType = APP_SERVER_JBOSS;
			else if (System.getProperty("was.install.root") != null)
				appServerType = APP_SERVER_WEBSPHERE;
			else
				appServerType = APP_SERVER_UNKNOWN;
		}
		return appServerType;
	}

	/**
	 * 获取配置文件路径<br>
	 * 对于JBoss，可以直接将配置文件放在 deploy/koneconfig目录下，
	 * 对于其他应用服务器，则应配置环境变量KONE_HOME，并配置文件庆在KONE_HOME/koneconfig目录 下
	 * 
	 * @return 获取配置文件路径
	 */
	static String configPath = null;
	static String koneHome = null;

	synchronized static public String getKoneHome() {
		if (koneHome != null)
			return koneHome;
		Properties ps = System.getProperties();
		for (Object p : ps.keySet()) {
			logger.info(p + "=" + ps.getProperty(p.toString()));
		}
		String path = System.getenv("KONE_HOME");
		if (path != null && !path.isEmpty()) {
			logger.info("Environment variable (KONE_HOME) is " + path);
			koneHome = path;
		} else {
			switch (getAppServerType()) {
			case APP_SERVER_JBOSS:
				koneHome = System.getProperty("jboss.server.home.url")
						+ "deploy";
				logger.warn("Environment variable (KONE_HOME) is "
						+ "not configured,use jboss home");
				break;
			default:
				koneHome = System.getProperty("user.dir");
				logger.warn("Environment variable (KONE_HOME) is "
						+ "not configured,use user.dir");
			}
		}
		return koneHome;
	}

	synchronized static public String getConfigPath() {
		if (configPath != null)
			return configPath;
		configPath = getKoneHome() + "/kone-config/";
		switch (getAppServerType()) {
		case APP_SERVER_JBOSS:
			configPath += "jboss/";
			break;
		case APP_SERVER_WEBLOGIC:
			configPath += "weblogic/";
			break;
		case APP_SERVER_WEBSPHERE:
			configPath += "websphere/";
			break;
		case APP_SERVER_UNKNOWN:
			configPath += "unknown/";
			break;
		}
		configPath = configPath.replace("\\", "/");
		configPath = configPath.replace("//", "/");
		logger.info("config path:[" + configPath + "]");
		return configPath;
	}
}
