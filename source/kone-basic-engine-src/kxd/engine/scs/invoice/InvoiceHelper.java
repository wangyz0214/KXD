package kxd.engine.scs.invoice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.scs.invoice.template.InvoiceTemplate;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.invoice.EditedInvoiceConfig;
import kxd.remote.scs.beans.invoice.EditedInvoiceTemplate;
import kxd.remote.scs.interfaces.invoice.InvoiceConfigBeanRemote;
import kxd.remote.scs.interfaces.invoice.InvoiceTemplateBeanRemote;
import kxd.util.memcached.Cacheable;
import kxd.util.memcached.MemCachedClient;

public class InvoiceHelper {
	public static MemCachedClient mc = (MemCachedClient) Cacheable.memCachedClientMap
			.get("invoice");
	private static final String CONFIG_PREFIX = "$invoice.config.";
	private static final String TEMPLATE_PREFIX = "$invoice.template.";

	/**
	 * 从数据库查询发票配置内容
	 * 
	 * @param orgId
	 *            机构ID
	 * @param invoiceType
	 *            发票类型
	 * @return 模板内容，null-表示没有配置模板
	 * @throws NamingException
	 */
	private static EditedInvoiceConfig getConfigFromDb(int orgId, int type)
			throws NamingException {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceConfigBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceConfigBean",
					InvoiceConfigBeanRemote.class);
			return bean.find(orgId, type);
		} finally {
			context.close();
		}
	}

	/**
	 * 从数据库查询模板内容
	 * 
	 * @param orgId
	 *            机构ID
	 * @param invoiceType
	 *            发票类型
	 * @return 模板内容，null-表示没有配置模板
	 * @throws NamingException
	 */
	private static String getTemplateFromDb(int templateid)
			throws NamingException {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceTemplateBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceTemplateBean",
					InvoiceTemplateBeanRemote.class);
			EditedInvoiceTemplate t = bean.find(templateid);
			if (t != null)
				return t.getTemplateContent();
			else
				return null;
		} finally {
			context.close();
		}
	}

	public static InvoiceTemplate getTemplate(int templateid) throws Exception {
		String key = TEMPLATE_PREFIX + templateid;
		InvoiceTemplate template = mc.getStreamable(key, InvoiceTemplate.class);
		if (template == null) {
			String t = getTemplateFromDb(templateid);
			if (t != null) {
				template = new InvoiceTemplate();
				template.decode(t);
				mc.setStreamable(key, template, null);
			}
		}
		return template;
	}

	/**
	 * 获取模板类
	 * 
	 * @param orgid
	 * @param invoiceType
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static InvoiceConfig getInvoiceConfig(int orgId, int invoiceType)
			throws Exception {
		String key = CONFIG_PREFIX + orgId + "_" + invoiceType;
		InvoiceConfig config = mc.getStreamable(key, InvoiceConfig.class);
		if (config == null) {
			EditedInvoiceConfig c = getConfigFromDb(orgId, invoiceType);
			if (c == null)
				config = new InvoiceConfig(true);
			else {
				config = new InvoiceConfig(false);
				config.setAlertCount(c.getAlertCount());
				config.setAway(c.getAwayFlag());
				config.setConfigCode(c.getConfigCode());
				config.setExtdata0(c.getExtdata0());
				config.setExtdata1(c.getExtdata1());
				config.setLogged(c.isLogged());
				config.setOrgId(c.getOrg().getId());
				config.setTemplateId(c.getInvoiceTemplate().getId());
				config.setType(c.getInvoiceType());
				config.setTax(c.getTaxFlag());
			}
			mc.setStreamable(key, config, null);
		}
		return config;
	}

	/**
	 * 获取模板类，从最底层机构，依次向上查找配置的模板
	 * 
	 * @param orgLevel
	 *            查询的最底层机构层级，-1-表示从营业厅开始，根机构属于0层，依次类推。如：全国为第0 层，省为第1层，地市为第2层<br>
	 *            如果指定最底层为省，就不要传-1，这样可以减少向上搜索的层次，提升效率
	 * @param term
	 *            终端对象
	 * @param invoiceType
	 *            发票类型
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static InvoiceConfig getInvoiceConfig(int orgLevel, CachedTerm term,
			int invoiceType) throws Exception {
		List<Integer> ls = new ArrayList<Integer>();
		ls.addAll(term.getOrg().getParents());
		ls.add(term.getOrgId());
		if (orgLevel == -1 || orgLevel > (ls.size() - 1))
			orgLevel = ls.size() - 1;
		InvoiceConfig c;
		for (int i = orgLevel; i >= 0; i--) {
			c = getInvoiceConfig(ls.get(i), invoiceType);
			if (!c.isNull())
				return c;
		}
		return null;
	}

	static public void templateAdded(int id) throws InterruptedException,
			IOException {
	}

	static public void templateEdited(int id) throws InterruptedException,
			IOException {
		mc.delete(TEMPLATE_PREFIX + id);
	}

	static public void templateRemoved(int id) throws InterruptedException,
			IOException {
		mc.delete(TEMPLATE_PREFIX + id);
	}

	static public void configAdded(int orgid, int invoiceType)
			throws InterruptedException, IOException {
	}

	static public void configEdited(int orgid, int invoiceType)
			throws InterruptedException, IOException {
		mc.delete(CONFIG_PREFIX + orgid + "_" + invoiceType);
	}

	static public void configRemoved(int orgid, int invoiceType)
			throws InterruptedException, IOException {
		mc.delete(CONFIG_PREFIX + orgid + "_" + invoiceType);
	}
}
