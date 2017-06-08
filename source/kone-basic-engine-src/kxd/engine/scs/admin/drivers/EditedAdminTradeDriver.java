package kxd.engine.scs.admin.drivers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import jxl.common.Logger;
import kxd.engine.cache.beans.sts.CachedOrg;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.fileservice.FileDownloadProcessor;
import kxd.engine.fileservice.FileUploadProcessor;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.AdminTradeDriver;
import kxd.engine.scs.admin.AdminTradeExecutor;
import kxd.engine.scs.admin.IntegerItem;
import kxd.engine.ui.core.FacesError;
import kxd.engine.ui.tags.website.FuncTreeNode;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseAdCategory;
import kxd.remote.scs.beans.BaseAlarmCategory;
import kxd.remote.scs.beans.BaseAlarmCode;
import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseAppCategory;
import kxd.remote.scs.beans.BaseAppFile;
import kxd.remote.scs.beans.BaseBankTerm;
import kxd.remote.scs.beans.BaseBusiness;
import kxd.remote.scs.beans.BaseBusinessCategory;
import kxd.remote.scs.beans.BaseCommInterface;
import kxd.remote.scs.beans.BaseDevice;
import kxd.remote.scs.beans.BaseDeviceDriver;
import kxd.remote.scs.beans.BaseDeviceDriverFile;
import kxd.remote.scs.beans.BaseDeviceType;
import kxd.remote.scs.beans.BaseDeviceTypeDriver;
import kxd.remote.scs.beans.BaseDisabledPrintUser;
import kxd.remote.scs.beans.BaseFileCategory;
import kxd.remote.scs.beans.BaseFileHost;
import kxd.remote.scs.beans.BaseFileOwner;
import kxd.remote.scs.beans.BaseFileUser;
import kxd.remote.scs.beans.BaseInfoCategory;
import kxd.remote.scs.beans.BaseManuf;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseOrgAd;
import kxd.remote.scs.beans.BaseOrgBusinessOpenClose;
import kxd.remote.scs.beans.BasePayItem;
import kxd.remote.scs.beans.BasePayWay;
import kxd.remote.scs.beans.BasePrintAdCategory;
import kxd.remote.scs.beans.BasePrintType;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.BaseTerm;
import kxd.remote.scs.beans.BaseTermAd;
import kxd.remote.scs.beans.BaseTermBusinessOpenClose;
import kxd.remote.scs.beans.BaseTermMoveItem;
import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.beans.BaseTradeCode;
import kxd.remote.scs.beans.BaseUser;
import kxd.remote.scs.beans.adinfo.EditedInfo;
import kxd.remote.scs.beans.adinfo.EditedPrintAd;
import kxd.remote.scs.beans.appdeploy.EditedApp;
import kxd.remote.scs.beans.appdeploy.EditedAppCategory;
import kxd.remote.scs.beans.appdeploy.EditedBusiness;
import kxd.remote.scs.beans.appdeploy.EditedPageElement;
import kxd.remote.scs.beans.appdeploy.EditedPayItem;
import kxd.remote.scs.beans.appdeploy.EditedTradeCode;
import kxd.remote.scs.beans.device.EditedBankTerm;
import kxd.remote.scs.beans.device.EditedDevice;
import kxd.remote.scs.beans.device.EditedDeviceType;
import kxd.remote.scs.beans.device.EditedTerm;
import kxd.remote.scs.beans.device.EditedTermType;
import kxd.remote.scs.beans.device.EditedTermTypeDevice;
import kxd.remote.scs.beans.invoice.EditedInvoiceConfig;
import kxd.remote.scs.beans.invoice.EditedInvoiceTemplate;
import kxd.remote.scs.beans.right.EditedFunction;
import kxd.remote.scs.beans.right.EditedManuf;
import kxd.remote.scs.beans.right.EditedOrg;
import kxd.remote.scs.beans.right.EditedUser;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.beans.right.QueryedUser;
import kxd.remote.scs.interfaces.AdCategoryBeanRemote;
import kxd.remote.scs.interfaces.AlarmCategoryBeanRemote;
import kxd.remote.scs.interfaces.AppBeanRemote;
import kxd.remote.scs.interfaces.AppCategoryBeanRemote;
import kxd.remote.scs.interfaces.BankTermBeanRemote;
import kxd.remote.scs.interfaces.BusinessBeanRemote;
import kxd.remote.scs.interfaces.BusinessCategoryBeanRemote;
import kxd.remote.scs.interfaces.CommInterfaceBeanRemote;
import kxd.remote.scs.interfaces.DeviceBeanRemote;
import kxd.remote.scs.interfaces.DeviceDriverBeanRemote;
import kxd.remote.scs.interfaces.DeviceTypeBeanRemote;
import kxd.remote.scs.interfaces.DeviceTypeDriverBeanRemote;
import kxd.remote.scs.interfaces.DisabledPrintUserBeanRemote;
import kxd.remote.scs.interfaces.FileCategoryBeanRemote;
import kxd.remote.scs.interfaces.FileHostBeanRemote;
import kxd.remote.scs.interfaces.FileOwnerBeanRemote;
import kxd.remote.scs.interfaces.FileUserBeanRemote;
import kxd.remote.scs.interfaces.InfoBeanRemote;
import kxd.remote.scs.interfaces.InfoCategoryBeanRemote;
import kxd.remote.scs.interfaces.ManufBeanRemote;
import kxd.remote.scs.interfaces.OrgAdBeanRemote;
import kxd.remote.scs.interfaces.OrgBeanRemote;
import kxd.remote.scs.interfaces.OrgBusinessOpenCloseRemote;
import kxd.remote.scs.interfaces.PageElementBeanRemote;
import kxd.remote.scs.interfaces.PayItemBeanRemote;
import kxd.remote.scs.interfaces.PayWayBeanRemote;
import kxd.remote.scs.interfaces.PrintAdBeanRemote;
import kxd.remote.scs.interfaces.PrintAdCategoryBeanRemote;
import kxd.remote.scs.interfaces.PrintTypeBeanRemote;
import kxd.remote.scs.interfaces.RoleBeanRemote;
import kxd.remote.scs.interfaces.TermAdBeanRemote;
import kxd.remote.scs.interfaces.TermBeanRemote;
import kxd.remote.scs.interfaces.TermBusinessOpenCloseRemote;
import kxd.remote.scs.interfaces.TermTypeBeanRemote;
import kxd.remote.scs.interfaces.TradeCodeBeanRemote;
import kxd.remote.scs.interfaces.UserBeanRemote;
import kxd.remote.scs.interfaces.invoice.InvoiceConfigBeanRemote;
import kxd.remote.scs.interfaces.invoice.InvoiceTemplateBeanRemote;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.AdPriority;
import kxd.remote.scs.util.emun.AdStoreType;
import kxd.remote.scs.util.emun.AlarmLevel;
import kxd.remote.scs.util.emun.BusinessOpenCloseMode;
import kxd.remote.scs.util.emun.CashFlag;
import kxd.remote.scs.util.emun.FaultPromptOption;
import kxd.remote.scs.util.emun.FileCachedType;
import kxd.remote.scs.util.emun.FileVisitRight;
import kxd.remote.scs.util.emun.FixType;
import kxd.remote.scs.util.emun.ManageScope;
import kxd.remote.scs.util.emun.PayWayType;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.remote.scs.util.emun.UserGroup;
import kxd.util.DataSecurity;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.KoneException;
import kxd.util.StringUnit;
import kxd.util.TreeNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 管理平台2.0编辑类交易驱动
 * 
 * @author liyy
 * 
 */

public class EditedAdminTradeDriver implements AdminTradeDriver {

	Logger logger = Logger.getLogger(EditedAdminTradeDriver.class);

	@Override
	public void execute(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		String cmdgroup = request.getParameterDef("cmdgroup", "");
		String cmd = request.getParameter("cmd");
		// 更新数据，包括增加与修改数据
		if (cmdgroup.equals("update")) {
			executeUpdate(session, cmd, request, response, xmlDoc, content,
					result);
			// 数据显示
		} else if (cmdgroup.equals("view")) {
			executeView(session, cmd, request, response, xmlDoc, content,
					result);
		}
	}

	public void executeUpdate(AdminSessionObject session, String cmd,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		if (cmd.equals("appcategory")) {
			this.updateAppCategory(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("app")) {
			this.updateApp(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("appfiles")) {
			this.updateAppFiles(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("businesscategory")) {
			this.updateBusinessCategory(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("filecategory")) {
			this.updateFileCategory(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("fileowner")) {
			this.updateFileOwner(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("filehost")) {
			this.updateFileHost(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("fileuser")) {
			this.updateFileUser(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("printtype")) {
			this.updatePrintType(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("comminterface")) {
			this.updateComminterface(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("alarmcategory")) {
			this.updateAlarmcategory(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("devicetypedriver")) {
			this.updateDevicetypedriver(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("devicedriver")) {
			this.updateDevicedriver(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("payway")) {
			this.updatePayWay(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("payitem")) {
			this.updatePayItem(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("disabledprintuser")) {
			this.updateDisabledPrintUser(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("printtimes")) {
			this.updatePrintTimes(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("manuf")) {
			this.updateManuf(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("bankterm")) {
			this.updateBankTerm(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("business")) {
			this.updateBusiness(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("tradecode")) {
			this.updateTradeCode(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("pageelement")) {
			this.updatePageElement(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("infocategory")) {
			this.updateInfoCategory(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("adcategory")) {
			this.updateAdCategory(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("printadcategory")) {
			this.updatePrintAdCategory(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("device")) {
			this.updateDevice(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("devicetype")) {
			this.updateDeviceType(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("devicetypealarms")) {
			this.updateDeviceTypeAlarm(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("invoicetemplate")) {
			this.updateInvoiceTemplate(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("invoiceconfig")) {
			this.updateInvoiceConfig(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("termtype")) {
			this.updateTermType(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("termbusinessopenclose")) {
			this.updateTermBusinessOpen(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("orgbusinessopenclose")) {
			this.updateOrgBusinessOpen(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("term")) {
			this.updateTerm(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("user")) {
			this.updateUser(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("userroles")) {
			this.updateUserRoles(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("role")) {
			this.updateRole(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("rolefunc")) {
			this.updateRoleFunc(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("printad")) {
			this.updatePrintAd(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("orgad")) {
			this.updateOrgAd(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("termad")) {
			this.updateTermAd(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("info")) {
			this.updateInfo(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("audit")) {
			// this.updateAudit(session, request, response, xmlDoc, content,
			// result);
		} else if (cmd.equals("org")) {
			this.updateOrg(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("editmyinfo")) {
			this.updateEditMyInfo(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("modifypwd")) {
			this.updateModifyPwd(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("addtermtypedevice")) {
			addTermTypeDevice(session, request, xmlDoc, content);
		} else if (cmd.equals("edittermtypedevice")) {
			editTermTypeDevice(session, request, xmlDoc, content);
		} else if (cmd.equals("devicedriverfile")) {
			updateDeviceDriverFile(session, request, xmlDoc, content);
		} else if (cmd.equals("termmove")) {
			updateTermMove(session, request, response, xmlDoc, content, result);
		}
	}

	protected void appendPermissionElement(Element content, Document document,
			boolean listabled, boolean queryabled, boolean viewabled,
			boolean addabled, boolean editabled, boolean deleteabled) {
		Element el = document.createElement("permission");
		el.setAttribute("Queryabled", Boolean.toString(queryabled));
		el.setAttribute("Listabled", Boolean.toString(listabled));
		el.setAttribute("Addabled", Boolean.toString(addabled));
		el.setAttribute("Editabled", Boolean.toString(editabled));
		el.setAttribute("Viewabled", Boolean.toString(viewabled));
		el.setAttribute("Deletabled", Boolean.toString(deleteabled));

		content.appendChild(el);
	}

	// 应用类别
	public void updateAppCategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		EditedAppCategory appCategory = new EditedAppCategory();
		appCategory.setId(request.getParameterIntDef("id", null));
		appCategory.setAppCategoryCode(request.getParameterDef("code", null));
		appCategory.setAppCategoryDesp(request.getParameterDef("desp", null));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appCategoryBean", AppCategoryBeanRemote.class);
			if (appCategory.getId() == null) {
				appCategory.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), appCategory));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), appCategory);
			}
		} finally {
			context.close();
		}
	}

	// 应用
	public void updateApp(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedApp app = new EditedApp();
		app.setId(request.getParameterIntDef("id", null));
		BaseAppCategory b = new BaseAppCategory(
				request.getParameterInt("appcategoryid"));
		b.setAppCategoryDesp(request.getParameter("appcategoryid_desp"));
		app.setAppCategory(b);
		app.setAppCode(request.getParameter("code"));
		app.setAppDesp(request.getParameter("desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appBean", AppBeanRemote.class);
			if (app.getId() == null) {
				app.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), app));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), app);
			}
		} finally {
			context.close();
		}
	}

	// 应用文件
	public void updateAppFiles(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		BaseAppFile appFile = new BaseAppFile();
		try {
			AppBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appBean", AppBeanRemote.class);
			String fileName = request.getParameterDef("fileName", null);
			boolean added = request.getParameterBoolean("added");
			fileName = fileName.replace("\\", "/");
			fileName = fileName.replace("//", "/").toLowerCase();
			int index = fileName.lastIndexOf("/");
			if (index >= 0)
				fileName = fileName.substring(index + 1);
			appFile.setAppFileId(request.getParameterIntDef("id", null));
			BaseApp b = new BaseApp(request.getParameterInt("appid"));
			// b.setAppDesp(request.getParameter("appdesp"));
			appFile.setApp(b);

			if (added) {
				appFile.setAppFilename(fileName);
				bean.addAppFile(((AdminSessionObject) session).getLoginUser()
						.getUserId(), appFile);
			} else {
				appFile.setAppFilename(fileName);
				bean.editAppFile(((AdminSessionObject) session).getLoginUser()
						.getUserId(), appFile);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	// 业务类别
	public void updateBusinessCategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseBusinessCategory businessCategory = new BaseBusinessCategory();
		businessCategory.setId(request.getParameterIntDef("id", null));
		businessCategory.setBusinessCategoryDesp(request.getParameter("desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BusinessCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-businessCategoryBean",
					BusinessCategoryBeanRemote.class);
			if (businessCategory.getId() == null) {
				businessCategory.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), businessCategory));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), businessCategory);
			}
		} finally {
			context.close();
		}
	}

	// 文件分类
	public void updateFileCategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseFileCategory fileCategory = new BaseFileCategory();
		boolean added;
		added = request.getParameterBoolean("add");
		fileCategory.setId(request.getParameterShort("id"));
		fileCategory.setFileCategoryDesp(request.getParameter("desp"));
		fileCategory.setCachedType(FileCachedType.valueOf(request
				.getParameterInt("cachedtype")));
		fileCategory.setFileHost(request.getParameterShort("filehost"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-fileCategoryBean",
					FileCategoryBeanRemote.class);
			if (added) {
				fileCategory.setId((short) bean.add(
						((AdminSessionObject) session).getLoginUser()
								.getUserId(), fileCategory));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), fileCategory);
			}
		} finally {
			context.close();
		}
	}

	// 文件属主
	public void updateFileOwner(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseFileOwner fileOwner = new BaseFileOwner();
		boolean added;
		added = request.getParameterBoolean("add");
		fileOwner.setId(request.getParameterShort("id"));
		fileOwner.setFileOwnerDesp(request.getParameter("desp"));
		fileOwner.setVisitRight(FileVisitRight.valueOf(request
				.getParameterInt("visitright")));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileOwnerBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileOwnerBean", FileOwnerBeanRemote.class);
			if (added) {
				fileOwner.setId((short) bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), fileOwner));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), fileOwner);
			}
		} finally {
			context.close();
		}
	}

	// 文件主机
	public void updateFileHost(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		BaseFileHost fileHost = new BaseFileHost();
		boolean added;
		added = request.getParameterBoolean("add");
		fileHost.setId(request.getParameterShort("id"));
		fileHost.setHostDesp(request.getParameter("desp"));
		fileHost.setFileRootDir(request.getParameter("rootdir"));
		fileHost.setRealHttpUrlRoot(request.getParameterDef("realhttpurlroot",
				null));
		fileHost.setHttpUrlPrefix(request
				.getParameterDef("httpurlprefix", null));
		fileHost.setFtpHost(request.getParameterDef("ftphost", null));
		fileHost.setFtpUser(request.getParameterDef("ftpuser", null));
		if (added)
			fileHost.setFtpPasswd(request.getParameter("ftppasswd"));
		else
			fileHost.setFtpPasswd(request.getParameterDef("ftppasswd", null));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileHostBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileHostBean", FileHostBeanRemote.class);
			if (added) {
				fileHost.setId((short) bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), fileHost));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), fileHost);
			}
		} finally {
			context.close();
		}
	}

	// 文件用户
	public void updateFileUser(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		BaseFileUser fileUser = new BaseFileUser();
		boolean added = request.getParameterBoolean("add");
		fileUser.setId(request.getParameter("id"));
		String userpwd = request.getParameterDef("userpwd", null);
		if (userpwd != null && !userpwd.isEmpty())
			fileUser.setFileUserPwd(DataSecurity.md5(userpwd));
		fileUser.setFileOwner(new BaseFileOwner(request
				.getParameterShort("fileownerid")));
		fileUser.getFileOwner().setFileOwnerDesp(
				request.getParameter("fileowner_desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileUserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileUserBean", FileUserBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), fileUser);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), fileUser);
			}
		} finally {
			context.close();
		}
	}

	// 打印类型
	public void updatePrintType(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BasePrintType printType = new BasePrintType();
		printType.setPrintType(request.getParameterShortDef("id", null));
		printType.setPrintTypeDesp(request.getParameter("desp"));
		boolean add = request.getParameterBoolean("add");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printTypeBean", PrintTypeBeanRemote.class);
			if (add) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), printType);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), printType);
			}
		} finally {
			context.close();
		}
	}

	// 接口管理
	public void updateComminterface(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseCommInterface commInterface = new BaseCommInterface();
		commInterface.setId(request.getParameterShortDef("id", null));
		commInterface.setType(request.getParameterIntDef("type", 0));
		commInterface.setDesp(request.getParameter("desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			CommInterfaceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-commInterfaceBean",
					CommInterfaceBeanRemote.class);
			if (commInterface.getId() == null) {
				commInterface.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), commInterface));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), commInterface);
			}
		} finally {
			context.close();
		}
	}

	// 告警分类管理
	public void updateAlarmcategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseAlarmCategory alarmCategory = new BaseAlarmCategory();
		boolean added = request.getParameterBoolean("add");
		alarmCategory.setId(request.getParameterIntDef("id", null));
		alarmCategory.setAlarmCategoryDesp(request.getParameter("desp"));
		alarmCategory.setAlarmLevel(AlarmLevel.valueOf(request
				.getParameterInt("alarmlevel")));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AlarmCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-alarmCategoryBean",
					AlarmCategoryBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), alarmCategory);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), alarmCategory);
			}
		} finally {
			context.close();
		}
	}

	// / 模块驱动管理
	public void updateDevicetypedriver(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseDeviceTypeDriver driver = new BaseDeviceTypeDriver();
		driver.setId(request.getParameterIntDef("id", null));
		driver.setDeviceTypeDriverDesp(request.getParameter("desp"));
		boolean added = driver.getId() == null;
		String oldFilename = request.getParameterDef("filename", null);
		String uploadfilename = request.getParameterDef("uploadfilename", null);
		if (added
				&& (uploadfilename == null || uploadfilename.trim().isEmpty())) {
			throw new KoneException("必须提供上传的文件");
		}
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceTypeDriverBean",
					DeviceTypeDriverBeanRemote.class);
			if ((uploadfilename != null && !uploadfilename.trim().isEmpty())) {
				if (added) {
					driver.setDriverFile(uploadfilename);
					driver.setId(bean.add(
							((AdminSessionObject) session).getLoginUser()
									.getUserId(), driver).getKey());
				} else {
					if (oldFilename == null || oldFilename.trim().length() == 0)
						throw new KoneException("必须提供旧文件");
					driver.setDriverFile(uploadfilename);
					bean.edit(((AdminSessionObject) session).getLoginUser()
							.getUserId(), driver);
				}
			} else
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), driver);
		} finally {
			context.close();
		}
	}

	// 模块管理
	public void updateDeviceType(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		EditedDeviceType deviceType = new EditedDeviceType();
		deviceType.setId(request.getParameterIntDef("id", null));
		BaseDeviceTypeDriver b = new BaseDeviceTypeDriver(
				request.getParameterInt("driverid"));
		b.setDeviceTypeDriverDesp(request.getParameter("driverid_desp"));
		deviceType.setDriver(b);
		deviceType.setDeviceTypeDesp(request.getParameter("desp"));
		deviceType.setAlarmNotifyOption(request
				.getParameterShort("alarmnotifyoption"));
		deviceType.setAlarmSendForm(request
				.getParameterBoolean("alarmsendform"));
		deviceType.setDeviceTypeCode(request.getParameter("code"));
		deviceType.setFaultSendForm(request
				.getParameterBoolean("faultsendform"));
		deviceType.setFaultNotifyOption(request
				.getParameterShort("faultnotifyoption"));
		deviceType.setFaultPromptOption(FaultPromptOption.valueOf(request
				.getParameterInt("faultpromptoption")));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceTypeBean", DeviceTypeBeanRemote.class);
			if (deviceType.getId() == null) {
				deviceType.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), deviceType));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), deviceType);
			}

			// 获取告警代码
			DeviceTypeBeanRemote alarmBean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceTypeBean",
					DeviceTypeBeanRemote.class);
			BaseAlarmCode alarmCode;
			boolean alarmadd;
			Integer alarmCount = request.getParameterIntDef("alarmcount", null);
			if (alarmCount != null && alarmCount > 0)
				for (int i = 0; i < alarmCount; i++) {
					Integer alarmcode = request.getParameterIntDef("alarmcode"
							+ i, null);
					if (alarmcode != null) {
						alarmCode = bean.findAlarmCode(deviceType.getId(),
								alarmcode);
						alarmadd = true;
					} else {
						alarmCode = new BaseAlarmCode();
						alarmCode.setDeviceType(deviceType.getId());
						alarmadd = false;
					}
					alarmCode.setAlarmCategory(new BaseAlarmCategory(request
							.getParameterInt("alarmcategory")));
					alarmCode
							.setAlarmCode(request.getParameterInt("alarmcode"));
					alarmCode.setAlarmDesp(request.getParameter("alarmdesp"));

					if (alarmadd) {
						alarmBean.addAlarmCode(((AdminSessionObject) session)
								.getLoginUser().getUserId(), alarmCode);
					} else {
						alarmBean.editAlarmCode(((AdminSessionObject) session)
								.getLoginUser().getUserId(), alarmCode);
					}
				}
		} finally {
			context.close();
		}
	}

	// 模块告警管理
	public void updateDeviceTypeAlarm(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseAlarmCode alarmCode = new BaseAlarmCode();
		Integer devicetype = request.getParameterIntDef("devicetype", null);
		Integer alarmcode = request.getParameterIntDef("alarmcode", null);
		boolean added = request.getParameterBooleanDef("add", true);
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceTypeBean", DeviceTypeBeanRemote.class);
			if (!added)
				alarmCode = bean.findAlarmCode(devicetype, alarmcode);
			else
				alarmCode.setDeviceType(request.getParameterInt("devicetype"));

			alarmCode.setDeviceType(request.getParameterInt("devicetype"));
			alarmCode.setAlarmCategory(new BaseAlarmCategory(request
					.getParameterInt("alarmcategory")));
			alarmCode.setAlarmCode(request.getParameterInt("alarmcode"));
			alarmCode.setAlarmDesp(request.getParameter("alarmdesp"));
			if (added) {
				bean.addAlarmCode(((AdminSessionObject) session).getLoginUser()
						.getUserId(), alarmCode);
			} else {
				bean.editAlarmCode(((AdminSessionObject) session)
						.getLoginUser().getUserId(), alarmCode);
			}
		} finally {
			context.close();
		}
	}

	// 设备驱动管理
	public void updateDevicedriver(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseDeviceDriver driver = new BaseDeviceDriver();
		driver.setId(request.getParameterIntDef("id", null));
		driver.setDeviceDriverDesp(request.getParameter("desp"));
		boolean added = driver.getId() == null;
		String oldFilename = request.getParameterDef("filename", null);
		String uploadfilename = request.getParameterDef("uploadfilename", null);
		if (added
				&& (uploadfilename == null || uploadfilename.trim().isEmpty())) {
			throw new KoneException("必须提供上传的文件");
		}
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceDriverBean",
					DeviceDriverBeanRemote.class);
			if ((uploadfilename != null && !uploadfilename.trim().isEmpty())) {
				if (added) {
					driver.setDriverFile(uploadfilename);
					driver.setId(bean.add(
							((AdminSessionObject) session).getLoginUser()
									.getUserId(), driver).getKey());
				} else {
					if (oldFilename == null || oldFilename.trim().length() == 0)
						throw new KoneException("必须提供旧文件");
					driver.setDriverFile(uploadfilename);
					bean.edit(((AdminSessionObject) session).getLoginUser()
							.getUserId(), driver);
				}
			} else
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), driver);
		} finally {
			context.close();
		}
	}

	// / 设备管理
	public void updateDevice(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedDevice device = new EditedDevice();
		device.setId(request.getParameterIntDef("id", null));
		BaseDeviceDriver b = new BaseDeviceDriver(
				request.getParameterInt("driverid"));
		b.setDeviceDriverDesp(request.getParameter("driverid_desp"));
		device.setDriver(b);
		BaseDeviceType t = new BaseDeviceType(request.getParameterInt("typeid"));
		t.setDeviceTypeDesp(request.getParameter("typeid_desp"));
		device.setDeviceType(t);
		device.setDeviceName(request.getParameter("desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceBean", DeviceBeanRemote.class);
			if (device.getId() == null) {
				device.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), device));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), device);
			}
		} finally {
			context.close();
		}
	}

	// 收费渠道管理
	public void updatePayWay(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		BasePayWay payWay = new BasePayWay();
		boolean added = request.getParameterBoolean("add");
		payWay.setId(request.getParameterShort("id"));
		payWay.setPayWayDesp(request.getParameter("desp"));
		payWay.setNeedTrade(request.getParameterBoolean("needtrade"));
		payWay.setType(PayWayType.valueOf(request.getParameterInt("type")));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PayWayBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-payWayBean", PayWayBeanRemote.class);
			if (added) {
				payWay.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), payWay));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), payWay);
			}
		} finally {
			context.close();
		}
	}

	// 收费项目管理
	public void updatePayItem(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedPayItem payItem = new EditedPayItem();
		boolean added = request.getParameterBoolean("add");
		payItem.setId(request.getParameterShort("id"));
		payItem.setPayItemDesp(request.getParameter("desp"));
		payItem.setPrice(request.getParameterLongDef("price", (long) 0));
		payItem.setMemo(request.getParameterDef("memo", null));
		payItem.setNeedTrade(request.getParameterBoolean("needtrade"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PayItemBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-payItemBean", PayItemBeanRemote.class);
			if (added) {
				payItem.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), payItem));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), payItem);
			}
		} finally {
			context.close();
		}
	}

	// 禁止查询打印用户号码
	public void updateDisabledPrintUser(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseDisabledPrintUser disabledPrintUser = new BaseDisabledPrintUser();
		boolean added = request.getParameterBoolean("add");
		disabledPrintUser.setId(request.getParameterLongDef("id", null));
		disabledPrintUser.setUserno(request.getParameter("desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DisabledPrintUserBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-disabledPrintUserBean",
					DisabledPrintUserBeanRemote.class);
			if (added) {
				disabledPrintUser.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), disabledPrintUser));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), disabledPrintUser);
			}
		} finally {
			context.close();
		}
	}

	// 用户打印次数配置
	public void updatePrintTimes(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		int month = request.getParameterInt("month");
		String userno = request.getParameter("userno");
		int printType = request.getParameterInt("printtype");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printTypeBean", PrintTypeBeanRemote.class);
			bean.addUserPrintTimes(userno, month, printType, 1);
		} finally {
			context.close();
		}
	}

	// 厂商管理
	public void updateManuf(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedManuf manuf = new EditedManuf();
		manuf.setId(request.getParameterIntDef("id", null));
		manuf.setManufCode(request.getParameter("code"));
		manuf.setManufName(request.getParameter("desp"));
		manuf.setSerialNumber(request.getParameterIntDef("serialnumber", 0));
		manuf.setManufType(request.getParameterShortDef("manuftype", (short) 0));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			ManufBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-manufBean", ManufBeanRemote.class);
			if (manuf.getId() == null) {
				manuf.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), manuf));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), manuf);
			}
		} finally {
			context.close();
		}
	}

	// 银联终端管理
	public void updateBankTerm(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedBankTerm bankTerm = new EditedBankTerm();
		bankTerm.setId(request.getParameterIntDef("id", null));
		bankTerm.setBankTermCode(request.getParameter("code"));
		bankTerm.setBankTermDesp(request.getParameter("desp"));
		bankTerm.setWorkKey(" ");
		bankTerm.setBatch(" ");
		bankTerm.setMacKey(" ");
		bankTerm.setMerchantAccount(request.getParameter("merchantaccount"));
		bankTerm.setExtField(request.getParameter("extfield"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BankTermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-bankTermBean", BankTermBeanRemote.class);
			if (bankTerm.getId() == null) {
				bankTerm.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), bankTerm));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), bankTerm);
			}
		} finally {
			context.close();
		}
	}

	// 业务管理
	public void updateBusiness(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedBusiness business = new EditedBusiness();
		business.setId(request.getParameterIntDef("id", null));
		BaseBusinessCategory b = new BaseBusinessCategory(
				request.getParameterInt("businesscategoryid"));
		b.setBusinessCategoryDesp(request
				.getParameter("businesscategoryid_desp"));
		business.setBusinessCategory(b);
		business.setBusinessDesp(request.getParameter("desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BusinessBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-businessBean", BusinessBeanRemote.class);
			if (business.getId() == null) {
				business.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), business));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), business);
			}
		} finally {
			context.close();
		}
	}

	// 交易管理
	public void updateTradeCode(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		EditedTradeCode tradeCode = new EditedTradeCode();
		tradeCode.setId(request.getParameterIntDef("id", null));
		BaseBusiness business = new BaseBusiness(
				request.getParameterInt("businessid"),
				request.getParameter("businessid_desp"));
		tradeCode.setBusiness(business);
		Short pw = request.getParameterShortDef("payway", null);
		if (pw != null) {
			BasePayWay payWay = new BasePayWay(pw);
			payWay.setPayWayDesp(request.getParameter("payway_desp"));
			tradeCode.setPayWay(payWay);
		} else
			tradeCode.setPayWay(new BasePayWay());
		Short pi = request.getParameterShortDef("payitem", null);
		if (pi != null) {
			BasePayItem payItem = new BasePayItem(pi);
			payItem.setPayItemDesp(request.getParameter("payitem_desp"));
			tradeCode.setPayItem(payItem);
		} else
			tradeCode.setPayItem(new BasePayItem());
		Integer id = request.getParameterIntDef("strikeid", null);
		if (id != null) {
			BaseTradeCode o = new BaseTradeCode(id);
			o.setTradeCodeDesp(request.getParameter("strikeid_desp"));
			tradeCode.setStrikeTadeCode(o);
		} else
			tradeCode.setStrikeTadeCode(new BaseTradeCode());
		tradeCode.setLogged(request.getParameterBoolean("logged"));
		tradeCode.setStated(request.getParameterBoolean("stated"));
		tradeCode.setRedoEnabled(request.getParameterBoolean("redoenabled"));
		int[] v = StringUnit.splitToInt(
				request.getParameterDef("refundmode", null), ",");
		int iv = 0;
		if (v != null) {
			for (int i : v)
				iv |= i;
		}
		tradeCode.setRefundMode(iv);
		v = StringUnit.splitToInt(
				request.getParameterDef("cancelrefundmode", null), ",");
		iv = 0;
		if (v != null) {
			for (int i : v)
				iv |= i;
		}
		tradeCode.setCancelRefundMode(iv);
		tradeCode.setTradeService(request.getParameter("tradeservice"));
		tradeCode.setTradeCode(request.getParameter("code"));
		tradeCode.setTradeCodeDesp(request.getParameter("desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeCodeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-tradeCodeBean", TradeCodeBeanRemote.class);
			if (tradeCode.getId() == null) {
				tradeCode.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), tradeCode));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), tradeCode);
			}
		} finally {
			context.close();
		}
	}

	// 业务页面管理
	public void updatePageElement(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		EditedPageElement pageElement = new EditedPageElement();
		pageElement.setId(request.getParameterIntDef("id", null));
		BaseBusiness b = new BaseBusiness(request.getParameterInt("businessid"));
		b.setBusinessDesp(request.getParameter("businessid_desp"));
		pageElement.setBusiness(b);
		pageElement.setPageCode(request.getParameter("code"));
		pageElement.setPageDesp(request.getParameter("desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PageElementBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-pageElementBean", PageElementBeanRemote.class);
			if (pageElement.getId() == null) {
				pageElement.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), pageElement));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), pageElement);
			}
		} finally {
			context.close();
		}
	}

	// 打印广告分类
	public void updatePrintAdCategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BasePrintAdCategory printAdCategory = new BasePrintAdCategory();
		boolean added = request.getParameterBoolean("add");
		printAdCategory.setId(request.getParameterShortDef("id", null));
		printAdCategory.setPrintAdCategoryDesp(request.getParameter("desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-printAdCategoryBean",
					PrintAdCategoryBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), printAdCategory);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), printAdCategory);
			}
		} finally {
			context.close();
		}
	}

	// 广告分类
	public void updateAdCategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseAdCategory adCategory = new BaseAdCategory();
		boolean added = request.getParameterBoolean("add");
		adCategory.setId(request.getParameterShortDef("id", null));
		adCategory.setAdCategoryDesp(request.getParameter("desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AdCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-adCategoryBean", AdCategoryBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), adCategory);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), adCategory);
			}
		} finally {
			context.close();
		}
	}

	// 广告分类
	public void updateInfoCategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseInfoCategory infoCategory = new BaseInfoCategory();
		boolean added = request.getParameterBoolean("add");
		infoCategory.setId(request.getParameterShortDef("id", null));
		infoCategory.setInfoCategoryDesp(request.getParameter("desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-infoCategoryBean",
					InfoCategoryBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), infoCategory);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), infoCategory);
			}
		} finally {
			context.close();
		}
	}

	// 发票模板
	public void updateInvoiceTemplate(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		EditedInvoiceTemplate invoiceTemplate = new EditedInvoiceTemplate();
		invoiceTemplate.setId(request.getParameterIntDef("id", null));
		invoiceTemplate.setTemplateDesp(request.getParameter("templatedesp"));
		invoiceTemplate.setTemplateCode(request.getParameter("templatecode"));
		invoiceTemplate.setTemplateContent(request
				.getParameter("templatecontent"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceTemplateBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceTemplateBean",
					InvoiceTemplateBeanRemote.class);
			if (invoiceTemplate.getId() == null) {
				invoiceTemplate.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), invoiceTemplate));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), invoiceTemplate);
			}
		} finally {
			context.close();
		}
	}

	// 发票配置
	public void updateInvoiceConfig(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		EditedInvoiceConfig invoiceConfig = new EditedInvoiceConfig();
		invoiceConfig.setId(request.getParameterIntDef("id", null));
		BaseOrg b = new BaseOrg(request.getParameterIntDef("orgid", null));
		b.setOrgName(request.getParameterDef("orgid_desp", null));
		invoiceConfig.setOrg(b);
		invoiceConfig.setConfigDesp(request.getParameter("desp"));
		invoiceConfig.setConfigCode(request.getParameter("code"));
		invoiceConfig.setInvoiceType(request.getParameterInt("invoicetype"));
		invoiceConfig.setInvoiceTemplate(new EditedInvoiceTemplate(request
				.getParameterInt("templateid")));
		invoiceConfig.setTaxFlag(request.getParameterInt("taxflag"));
		invoiceConfig.setAwayFlag(request.getParameterInt("awayflag"));
		invoiceConfig.setAlertCount(request.getParameterInt("alertcount"));
		invoiceConfig.setLogged(request.getParameterBoolean("logged"));
		invoiceConfig.setExtdata0(request.getParameter("extdata0"));
		invoiceConfig.setExtdata1(request.getParameter("extdata1"));
		boolean added = invoiceConfig.getId() == null;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceConfigBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceConfigBean",
					InvoiceConfigBeanRemote.class);
			try {
				if (added) {
					invoiceConfig.setId(bean.add(((AdminSessionObject) session)
							.getLoginUser().getUserId(), invoiceConfig));
				} else {
					bean.edit(((AdminSessionObject) session).getLoginUser()
							.getUserId(), invoiceConfig);
				}
			} catch (Throwable e) {
				// getTemplateList();
				throw e;
			}
		} finally {
			context.close();
		}
	}

	// 终端型号
	public void updateTermType(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedTermType termType = new EditedTermType();
		termType.setId(request.getParameterIntDef("id", null));
		termType.setTypeCode(request.getParameter("code"));
		termType.setTypeDesp(request.getParameter("desp"));
		BaseApp app = new BaseApp(request.getParameterInt("appid"),
				request.getParameter("appid_desp"));
		termType.setApp(app);
		BaseManuf manuf = new BaseManuf(request.getParameterInt("manufid"));
		termType.setManuf(manuf);
		termType.setCashFlag(CashFlag.valueOf(request
				.getParameterInt("cashflag")));
		termType.setFixType(FixType.valueOf(request.getParameterInt("fixtype")));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			if (termType.getId() == null) {
				termType.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), termType));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), termType);
			}
		} finally {
			context.close();
		}
	}

	// 终端开停
	public void updateTermBusinessOpen(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseTermBusinessOpenClose value = new BaseTermBusinessOpenClose();
		value.setId(request.getParameterLongDef("id", null));
		value.setBusinessCategoryIds(request
				.getParameter("businesscategoryids"));
		value.setBusinessIds(request.getParameter("businessids"));
		value.setOpenTimes(request.getParameter("opentimes"));
		value.setPayWays(request.getParameter("payways"));
		value.setPayItems(request.getParameter("payitems"));
		value.setOpenMode(BusinessOpenCloseMode.valueOfIntString(request
				.getParameter("openmode")));
		value.setStartDate(DataUnit.parseDateTime(
				request.getParameter("startdate"), "yyyy-MM-dd"));
		value.setEndDate(DataUnit.parseDateTime(
				request.getParameter("enddate"), "yyyy-MM-dd"));
		value.setReason(request.getParameter("reason"));
		value.setTermId(request.getParameterInt("termid"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermBusinessOpenCloseRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBusinessOpenCloseBean",
					TermBusinessOpenCloseRemote.class);
			if (value.getId() == null) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), value);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), value);
			}
		} finally {
			context.close();
		}
	}

	// 机构开停
	public void updateOrgBusinessOpen(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		BaseOrgBusinessOpenClose value = new BaseOrgBusinessOpenClose();
		value.setId(request.getParameterLongDef("id", null));
		value.setBusinessCategoryIds(request
				.getParameter("businesscategoryids"));
		value.setBusinessIds(request.getParameter("businessids"));
		value.setOpenTimes(request.getParameter("opentimes"));
		value.setPayWays(request.getParameter("payways"));
		value.setPayItems(request.getParameter("payitems"));
		value.setOpenMode(BusinessOpenCloseMode.valueOfIntString(request
				.getParameter("openmode")));
		value.setStartDate(DataUnit.parseDateTime(
				request.getParameter("startdate"), "yyyy-MM-dd"));
		value.setEndDate(DataUnit.parseDateTime(
				request.getParameter("enddate"), "yyyy-MM-dd"));
		value.setReason(request.getParameter("reason"));
		value.setOrgId(request.getParameterInt("orgid"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgBusinessOpenCloseRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-orgBusinessOpenCloseBean",
					OrgBusinessOpenCloseRemote.class);
			if (value.getId() == null) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), value);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), value);
			}
		} finally {
			context.close();
		}
	}

	// 终端
	public void updateTerm(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedTerm term = new EditedTerm();
		boolean activeDisabled = request.getParameterBooleanDef(
				"activedisabled", false);
		if (activeDisabled)
			term.setStatus(TermStatus.NOTACTIVE);
		term.setId(request.getParameterIntDef("id", null));
		term.setTermCode(request.getParameter("code"));
		term.setTermDesp(request.getParameter("desp"));
		term.setBankTerm(new BaseBankTerm());
		term.getBankTerm().setBankTermCode(
				request.getParameterDef("banktermcode", null));
		term.getBankTerm().setMerchantAccount(
				request.getParameterDef("merchantaccount", null));
		Integer tid = request.getParameterIntDef("typeid", null);
		if (tid != null)
			term.setTermType(new BaseTermType(tid, ""));
		term.setAddress(request.getParameterDef("address", null));
		term.setAreaCode(request.getParameterDef("areacode", null));
		term.setContacter(request.getParameterDef("contacter", null));
		term.setApp(new BaseApp(request.getParameterInt("appid"), ""));
		DateTime opentime = new DateTime(request.getParameter("opentime"),
				"HHmm");
		DateTime closetime = new DateTime(request.getParameter("closetime"),
				"HHmm");
		term.setCloseTime(closetime.format("HHmm"));
		term.setOpenTime(opentime.format("HHmm"));
		term.setDayRunTime((short) opentime.hoursBetween(closetime));
		term.setGuid(request.getParameterDef("guid", null));
		term.setManufNo(request.getParameter("manufno"));
		term.setTermType(new BaseTermType(request.getParameterInt("typeid"), ""));
		term.setSettlementType(SettlementType.valueOf(request
				.getParameterInt("settlementtype")));
		if (term.getTermId() == null) {
			Integer orgid = request.getParameterInt("orgid");
			term.setOrg(new BaseOrg(orgid, ""));
		} else
			term.setOrg(new BaseOrg());

		//
		term.setIp(request.getParameterDef("ip", null));
		term.setExtField0(request.getParameterDef("extfield0", null));
		term.setExtField1(request.getParameterDef("extfield1", null));
		term.setExtField2(request.getParameterDef("extfield2", null));
		term.setExtField3(request.getParameterDef("extfield3", null));
		term.setExtField4(request.getParameterDef("extfield4", null));

		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBean", TermBeanRemote.class);
			if (term.getId() == null) {
				term.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), term));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), term);
			}
		} finally {
			context.close();
		}
	}

	// 用户
	public void updateUser(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedUser user = new EditedUser();
		user.setId(request.getParameterLongDef("id", null));
		user.setUserCode(request.getParameter("code"));
		user.setUserName(request.getParameter("desp"));
		user.setEmail(request.getParameterDef("email", null));
		user.setManageScope(ManageScope.AREA);
		Integer tid = request.getParameterIntDef("manufid", null);
		if (tid != null)
			user.setManuf(new BaseManuf(tid, request
					.getParameter("manufid_desp")));
		else
			user.setManuf(new BaseManuf());
		user.setMobile(request.getParameterDef("mobile", null));
		user.setTelphone(request.getParameterDef("telphone", null));
		user.setUserGroup(UserGroup.valueOf(request
				.getParameterInt("usergroup")));
		tid = request.getParameterIntDef("roleid", null);
		if (tid != null)
			user.setRole(new BaseRole(tid, request.getParameter("roleid_desp")));
		else
			user.setRole(new BaseRole());
		String userPwd = request.getParameterDef("userpwd", null);
		if (userPwd != null && userPwd.trim().length() > 0)
			userPwd = DataSecurity.md5(userPwd.getBytes());
		else
			userPwd = null;
		if (user.getUserId() == null) {
			Integer orgid = request.getParameterInt("orgid");
			user.setOrg(new BaseOrg(orgid, request.getParameter("orgid_desp")));
		}
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			if (user.getId() == null) {
				user.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), user, userPwd));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), user, userPwd);
			}
		} finally {
			context.close();
		}
	}

	// 用户角色
	public void updateUserRoles(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		String oldRoles = request.getParameter("oldroles");
		String roles = request.getParameter("roles");
		Long userid = request.getParameterLong("id");
		BaseUser user = new BaseUser();
		user.setUserId(userid);
		try {
			if (oldRoles.endsWith(","))
				oldRoles = oldRoles.substring(0, oldRoles.length() - 1);
			if (roles.endsWith(","))
				roles = roles.substring(0, roles.length() - 1);
			String[] os = StringUnit.split(oldRoles, ",");
			String[] ns = StringUnit.split(roles, ",");
			ArrayList<Integer> olist = new ArrayList<Integer>();
			ArrayList<Integer> nlist = new ArrayList<Integer>();
			for (int i = 0; i < os.length; i++)
				olist.add(Integer.valueOf(os[i]));
			for (int i = 0; i < ns.length; i++)
				nlist.add(Integer.valueOf(ns[i]));
			for (int i = 0; i < olist.size(); i++) {
				Integer l = olist.get(i);
				if (nlist.contains(l)) {
					olist.remove(i);
					nlist.remove(l);
					i--;
				}
			}
			if (nlist.size() > 0 || olist.size() > 0) {
				UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-userBean", UserBeanRemote.class);
				bean.setUserManagedRoles(((AdminSessionObject) session)
						.getLoginUser().getUserId(), user.getUserId(), nlist,
						olist);
			}
		} finally {
			context.close();
		}
	}

	// 角色
	public void updateRole(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		BaseRole role = new BaseRole();
		role.setId(request.getParameterIntDef("id", null));
		role.setRoleName(request.getParameter("desp"));
		boolean added = role.getId() == null;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			RoleBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-roleBean", RoleBeanRemote.class);
			if (added) {
				role.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), role));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), role);
			}
		} finally {
			context.close();
		}
	}

	// 角色权限
	public void updateRoleFunc(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		String oldFuncs, funcs;
		oldFuncs = request.getParameter("oldfuncs");
		funcs = request.getParameter("newfuncs");
		BaseRole role = new BaseRole();
		role.setId(request.getParameterIntDef("id", null));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			if (oldFuncs.endsWith(","))
				oldFuncs = oldFuncs.substring(0, oldFuncs.length() - 1);
			if (funcs.endsWith(","))
				funcs = funcs.substring(0, funcs.length() - 1);
			String[] os = StringUnit.split(oldFuncs, ",");
			String[] ns = StringUnit.split(funcs, ",");
			ArrayList<Integer> olist = new ArrayList<Integer>();
			ArrayList<Integer> nlist = new ArrayList<Integer>();
			for (int i = 0; i < os.length; i++)
				olist.add(Integer.valueOf(os[i]));
			for (int i = 0; i < ns.length; i++)
				nlist.add(Integer.valueOf(ns[i]));
			for (int i = 0; i < olist.size(); i++) {
				Integer l = olist.get(i);
				if (nlist.contains(l)) {
					olist.remove(i);
					nlist.remove(l);
					i--;
				}
			}
			if (nlist.size() > 0 || olist.size() > 0) {
				RoleBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-roleBean", RoleBeanRemote.class);
				bean.setFunction(((AdminSessionObject) session).getLoginUser()
						.getUserId(), role.getRoleId(), nlist, olist);
			}
		} finally {
			context.close();
		}
	}

	// 打印广告管理
	public void updatePrintAd(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedPrintAd printAd = new EditedPrintAd();
		printAd.setId(request.getParameterIntDef("id", null));
		BaseOrg b = new BaseOrg(request.getParameterIntDef("orgid", null));
		printAd.setOrg(b);
		printAd.setAdCategory(new BasePrintAdCategory(request
				.getParameterShort("category")));
		printAd.setContent(request.getParameter("content"));
		boolean added = printAd.getId() == null;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printAdBean", PrintAdBeanRemote.class);
			if (added) {
				printAd.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getId(), printAd));
			} else
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), printAd);

		} finally {
			context.close();
		}
	}

	// 机构广告管理
	public void updateOrgAd(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		BaseOrgAd orgAd = new BaseOrgAd();
		orgAd.setAdDesp(request.getParameter("addesp"));
		orgAd.setOrg(new BaseOrg(request.getParameterInt("orgid"), ""));
		orgAd.setAdCategory(new BaseAdCategory(request
				.getParameterShort("category")));
		String startDate = request.getParameter("startdate");
		String endDate = request.getParameter("enddate");
		orgAd.setStartDate(new DateTime(startDate, "yyyy-MM-dd").getTime());
		orgAd.setEndDate(new DateTime(endDate + " 23:59:59",
				"yyyy-MM-dd HH:mm:ss").getTime());
		orgAd.setDuration(request.getParameterInt("duration"));
		orgAd.setPriority(AdPriority.valueOfIntString(request
				.getParameter("priority")));
		orgAd.setStoreType(AdStoreType.valueOfIntString(request
				.getParameter("storetype")));
		if (orgAd.getStoreType().equals(AdStoreType.LOCAL)) {
			orgAd.setFileName(request.getParameter("localfile"));
			orgAd.setUploadComplete(true);
		}
		boolean added = orgAd.getId() == null;

		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), orgAd);
			} else
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), orgAd);
		} finally {
			context.close();
		}
	}

	// 终端广告管理
	public void updateTermAd(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		BaseTermAd termAd = new BaseTermAd();
		termAd.setAdDesp(request.getParameter("addesp"));
		termAd.setTerm(new BaseTerm(request.getParameterInt("termid")));
		termAd.setAdCategory(new BaseAdCategory(request
				.getParameterShort("category")));
		String startDate = request.getParameter("startdate");
		String endDate = request.getParameter("enddate");
		termAd.setStartDate(new DateTime(startDate, "yyyy-MM-dd").getTime());
		termAd.setEndDate(new DateTime(endDate + " 23:59:59",
				"yyyy-MM-dd HH:mm:ss").getTime());
		termAd.setDuration(request.getParameterInt("duration"));
		termAd.setPriority(AdPriority.valueOfIntString(request
				.getParameter("priority")));
		termAd.setStoreType(AdStoreType.valueOfIntString(request
				.getParameter("storetype")));
		if (termAd.getStoreType().equals(AdStoreType.LOCAL)) {
			termAd.setFileName(request.getParameter("localfile"));
			termAd.setUploadComplete(true);
		}
		boolean added = termAd.getId() == null;

		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termAdBean", TermAdBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), termAd);
			} else
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), termAd);

		} finally {
			context.close();
		}
	}

	// 信息管理
	public void updateInfo(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedInfo info = new EditedInfo();
		BaseOrg b = new BaseOrg(request.getParameterIntDef("orgid", null));
		info.setOrg(b);
		b.setOrgName(request.getParameterDef("orgdesp", null));
		info.setId(request.getParameterLongDef("id", null));
		String infoText = "";

		boolean added = info.getInfoId() == null;
		if (added) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-infoBean", InfoBeanRemote.class);
				info.setId(bean.getNextInfoId());
			} finally {
				if (context != null)
					context.close();
			}
		} else {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-infoBean", InfoBeanRemote.class);
				info = bean.find(info.getId());
			} finally {
				if (context != null)
					context.close();
			}
			FileDownloadProcessor processor = new FileDownloadProcessor(
					AdminTradeExecutor.fileUserId, AdminTradeExecutor.filePwd,
					(short) 6);
			infoText = new String(processor.downloadFile(0, info.getFileName(),
					0, 0).getValue()).replace("$_contextpath_", request
					.getRequest().getContextPath());
			if (infoText
					.startsWith("<meta http-equiv=\"Content-Type\" content=\"textml; charset=utf-8\" />"))
				infoText = infoText
						.substring("<meta http-equiv=\"Content-Type\" content=\"textml; charset=utf-8\" />"
								.length());
		}

		infoText = request.getParameter("webeditor");
		if (!infoText
				.startsWith("<meta http-equiv=\"Content-Type\" content=\"textml; charset=utf-8\" />"))
			infoText = "<meta http-equiv=\"Content-Type\" content=\"textml; charset=utf-8\" />"
					+ infoText;

		info.setTitle(request.getParameter("title"));
		info.setSummary(request.getParameter("webeditor_summary"));
		info.setInfoCategory(new BaseInfoCategory(request
				.getParameterShort("category")));

		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-infoBean", InfoBeanRemote.class);

			String fileName = info.getIdString() + ".html";
			FileUploadProcessor processor = new FileUploadProcessor(
					AdminTradeExecutor.fileUserId, AdminTradeExecutor.filePwd,
					(short) 6);
			String path = info.getOrg().getIdString() + "/"
					+ info.getInfoCategory().getIdString() + "/";
			String root = processor.getFileHost().getRealHttpUrlRoot();
			if (root != null)
				root = root.trim();
			if (root != null && !root.isEmpty())
				info.setUrl(root + "/6/" + path + fileName);
			else
				info.setUrl("/fileService?filecategory=6&filename=" + path
						+ fileName);
			if (added) {
				info.setFileName(fileName);
				info.setPublishTime(new Date());
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), info);
				processor.uploadFile(((AdminSessionObject) session)
						.getLoginUser().getUserId(), path + fileName, infoText
						.getBytes());
			} else {
				info.setPublishTime(new Date());
				info.setFileName(fileName);
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), info);
				request.getRequest().getContextPath();
				processor.updateFile(((AdminSessionObject) session)
						.getLoginUser().getUserId(), path + fileName, path
						+ fileName, infoText.getBytes());
			}
		} finally {
			context.close();
		}
	}

	// 机构管理
	public void updateOrg(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedOrg org = new EditedOrg();
		org.setId(request.getParameterIntDef("id", null));
		boolean added = org.getId() == null;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgBean", OrgBeanRemote.class);
			if (!added) {
				org = bean.find(org.getOrgId());
			}
			org.setOrgName(request.getParameter("desp"));
			org.setOrgFullName(request.getParameter("fullname"));
			if (org.getOrgCode() == null)
				org.setOrgCode(request.getParameter("code"));
			org.setEmail(request.getParameterDef("email", null));
			org.setAddress(request.getParameterDef("address", null));
			org.setContacter(request.getParameterDef("contacter", null));
			org.setTelphone(request.getParameterDef("telphone", null));
			org.setExtField0(request.getParameterDef("extfield0", null));
			org.setExtField1(request.getParameterDef("extfield1", null));
			org.setExtField2(request.getParameterDef("extfield2", null));
			org.setExtField3(request.getParameterDef("extfield3", null));
			org.setExtField4(request.getParameterDef("extfield4", null));
			org.setSerialNumber(request.getParameterIntDef("serialnumber", 0));
			org.setOrgType(request.getParameterInt("orgtype"));
			org.setStandardAreaCode(request.getParameterDef("standardareacode",
					null));
			org.setParentOrg(new BaseOrg(request.getParameterIntDef("parentid",
					null)));
			org.setSimpleName(request.getParameterDef("simplename", null));
			if (org.getOrgName().contains("\"")
					|| org.getOrgName().contains("'"))
				throw new AppException("机构名称中不能包含”或'字符");
			if (added) {
				org.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), org));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), org);
			}
		} finally {
			context.close();
		}
	}

	// 修改我的资料
	public void updateEditMyInfo(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			String username = request.getParameter("username");
			String mobile = request.getParameter("mobile");
			String telphone = request.getParameter("telphone");
			String email = request.getParameter("email");

			UserBeanRemote userBean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			QueryedUser user = new QueryedUser();
			user.setId(((AdminSessionObject) session).getLoginUser()
					.getUserId());
			user.setEmail(email);
			user.setTelphone(telphone);
			user.setMobile(mobile);
			user.setUserName(username);
			userBean.editInfo(user);
		} finally {
			context.close();
		}
	}

	// 修改密码
	public void updateModifyPwd(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			String oldpwd = request.getParameter("oldpwd");
			String password = request.getParameter("password");
			if (oldpwd == null || oldpwd.trim().length() == 0) {
				throw new FacesError("error", "未提供[旧密码]");
			}
			if (password == null || password.trim().length() == 0) {
				throw new FacesError("error", "未提供[新密码]");
			}
			UserBeanRemote userBean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			password = DataSecurity.md5(password.getBytes());
			oldpwd = DataSecurity.md5(oldpwd.getBytes());
			userBean.modifyPwd(((AdminSessionObject) session).getLoginUser()
					.getUserId(), oldpwd, password);
		} finally {
			context.close();
		}
	}

	public void editTermTypeDevice(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NumberFormatException, NoSuchFieldException {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			EditedTermTypeDevice typeDevice = new EditedTermTypeDevice();
			typeDevice.setTermType(new BaseTermType(request
					.getParameterInt("id")));
			typeDevice.setDevice(new BaseDevice(Integer.valueOf(request
					.getParameter("deviceid"))));
			typeDevice.setExtConfig(request.getParameter("form:extconfig"));
			typeDevice.setPort(Integer.valueOf(request
					.getParameter("form:port")));
			bean.editTermTypeDevice(((AdminSessionObject) session)
					.getLoginUser().getUserId(), typeDevice);
		} finally {
			if (context != null)
				context.close();
		}
	}

	public void addTermTypeDevice(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NumberFormatException, NoSuchFieldException {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			EditedTermTypeDevice typeDevice = new EditedTermTypeDevice();
			typeDevice.setTermType(new BaseTermType(Integer.valueOf(request
					.getParameter("id"))));
			typeDevice.setDevice(new BaseDevice(Integer.valueOf(request
					.getParameter("form:deviceid"))));
			typeDevice.setExtConfig(request.getParameter("form:extconfig"));
			typeDevice.setPort(Integer.valueOf(request
					.getParameter("form:port")));
			bean.addTermTypeDevice(((AdminSessionObject) session)
					.getLoginUser().getUserId(), typeDevice);
		} finally {
			if (context != null)
				context.close();
		}
	}

	// 设备驱动文件
	public void updateDeviceDriverFile(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NumberFormatException, NoSuchFieldException {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BaseDeviceDriverFile deviceDriverFile = new BaseDeviceDriverFile();
			String fileName = request.getParameter("devicedriverfile");
			fileName = fileName.replace("\\", "/");
			fileName = fileName.replace("//", "/").toLowerCase();
			int index = fileName.lastIndexOf("/");
			if (index >= 0)
				fileName = fileName.substring(index + 1);
			fileName = request.getParameterDef("path", "") + "/" + fileName;
			fileName = fileName.replace("\\", "/");
			fileName = fileName.replace("//", "/").toLowerCase();
			if (fileName.startsWith("/"))
				fileName = fileName.substring(1);

			deviceDriverFile.setFileName(fileName);
			deviceDriverFile.setId(request.getParameterIntDef("id", null));
			deviceDriverFile.setDeviceDriverId(request
					.getParameterInt("devicedriverid"));

			DeviceDriverBeanRemote fileBean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceDriverBean",
					DeviceDriverBeanRemote.class);

			fileBean.addDeviceDriverFile(((AdminSessionObject) session)
					.getLoginUser().getUserId(), deviceDriverFile);
		} finally {
			if (context != null)
				context.close();
		}
	}

	// 终端迁移
	public void updateTermMove(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer orgId = request.getParameterIntDef("orgId", null);
		Integer termId = request.getParameterIntDef("termId", null);
		try {
			TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBean", TermBeanRemote.class);
			bean.move(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					termId, orgId);
		} finally {
			context.close();
		}
	}

	// *******************************************以下部分为查看功能驱动**********************************************

	public void executeView(AdminSessionObject session, String cmd,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		// 导航菜单
		if (cmd.equals("navigatemenu")) {
			this.queryNavigatorMenu(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("submenu")) {
			this.querySubMenu(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("appcategory")) {
			this.viewAppCategory(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("app")) {
			this.viewApp(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("businesscategory")) {
			this.viewBusinessCategory(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("filecategory")) {
			this.viewFileCategory(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("fileowner")) {
			this.viewFileOwner(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("filehost")) {
			this.viewFileHost(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("fileuser")) {
			this.viewFileUser(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("printtype")) {
			this.viewPrintType(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("comminterface")) {
			this.viewComminterface(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("alarmcategory")) {
			this.viewAlarmcategory(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("devicetypedriver")) {
			this.viewDevicetypedriver(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("devicetype")) {
			this.viewDevicetype(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("devicetypealarms")) {
			this.viewDevicetypeAlarm(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("devicedriver")) {
			this.viewDevicedriver(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("device")) {
			this.viewDevice(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("payway")) {
			this.viewPayWay(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("payitem")) {
			this.viewPayItem(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("disabledprintuser")) {
			this.viewDisabledPrintUser(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("manuf")) {
			this.viewManuf(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("bankterm")) {
			this.viewBankTerm(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("business")) {
			this.viewBusiness(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("tradecode")) {
			this.viewTradeCode(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("pageelement")) {
			this.viewPageElement(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("termtype")) {
			this.viewTermType(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("printadcategory")) {
			this.viewPrintAdCategory(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("adcategory")) {
			this.viewAdCategory(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("infocategory")) {
			this.viewInfoCategory(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("invoicetemplate")) {
			this.viewInvoiceTemplate(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("invoiceconfig")) {
			this.viewInvoiceConfig(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("termbusinessopenclose")) {
			this.viewTermBusinessOpen(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("orgbusinessopenclose")) {
			this.viewOrgBusinessOpen(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("term")) {
			this.viewTerm(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("role")) {
			this.viewRole(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("rolefunc")) {
			this.viewRoleFunc(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("user")) {
			this.viewUser(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("userroles")) {
			this.viewUserRoles(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("loginuser")) {
			viewLoginUser(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("printad")) {
			this.viewPrintAd(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("orgad")) {
			this.viewOrgAd(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("termad")) {
			this.viewTermAd(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("info")) {
			this.viewInfo(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("org")) {
			this.viewOrg(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("editmyinfo")) {
			this.viewEditMyInfo(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("myuserrights")) {
			this.viewMyUserRights(session, request, response, xmlDoc, content,
					result);
			// } else if (cmd.equals("getallfunc")) {
			// this.getAllFunc(session, request, response, xmlDoc, content,
			// result);
		} else if (cmd.equals("termtypedevice")) {
			this.viewTermTypeDevice(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("devicedriverfile")) {
			this.viewDeviceDriverFile(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("termmovehistory")) {
			TermMoveHistory(session, request, response, xmlDoc, content, result);
		}

	}

	// 获取导航菜单数据
	public void queryNavigatorMenu(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		// LoopNamingContext context = new LoopNamingContext("db");
		try {
			List<EditedFunction> naviList = new ArrayList<EditedFunction>();
			naviList.add(new EditedFunction(10, "我的首页"));
			naviList.add(new EditedFunction(18, "应用管理"));
			naviList.add(new EditedFunction(19, "驱动管理"));
			naviList.add(new EditedFunction(20, "文件服务管理"));
			naviList.add(new EditedFunction(21, "系统配置管理"));
			naviList.add(new EditedFunction(11, "监控系统"));
			naviList.add(new EditedFunction(12, "权限管理"));
			naviList.add(new EditedFunction(13, "终端管理"));
			naviList.add(new EditedFunction(14, "报表系统"));
			naviList.add(new EditedFunction(15, "日常运营"));
			naviList.add(new EditedFunction(16, "信息发布管理"));
			naviList.add(new EditedFunction(17, "流程工单系统"));
			naviList.add(new EditedFunction(27, "发货工单系统"));
			naviList.add(new EditedFunction(22, "交易明细管理"));
			// naviList.add(new EditedFunction(23, "系统管理"));
			naviList.add(new EditedFunction(24, "我的帐户"));
			// naviList.add(new EditedFunction(25, "帮助系统"));
			// naviList.add(new EditedFunction(26, "凯欣达介绍"));
			for (EditedFunction func : naviList) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", func.getIdString());
				el.setAttribute("name", func.getFuncDesp());
				content.appendChild(el);
			}
		} finally {
			// context.close();
		}
	}

	private Element getXmlNode(Document xmlDoc, String key, String funcid,
			String funcname, String funcicon, String pageclass, String pack,
			String assembly) {
		Element node = xmlDoc.createElement(key);
		node.setAttribute("funcid", funcid);
		node.setAttribute("funcname", funcname);
		node.setAttribute("funcicon", funcicon);
		node.setAttribute("pageclass", pageclass);
		node.setAttribute("package", pack);
		node.setAttribute("assembly", assembly);

		return node;
	}

	// 查看业务类别
	public void viewAppBusinessCategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		int id = request.getParameterIntDef("id", null);
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BaseBusinessCategory businessCategory;
			BusinessCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-businessCategoryBean",
					BusinessCategoryBeanRemote.class);
			businessCategory = bean.find(id);
			if (businessCategory != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", businessCategory.getIdString());
				el.setAttribute("desp",
						businessCategory.getBusinessCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看应用
	public void viewApp(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		int id = request.getParameterIntDef("id", null);
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			EditedApp app;
			AppBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appBean", AppBeanRemote.class);
			if (request.getParameter("appcode") != null)
				app = bean.find(request.getParameter("appcode"));
			else
				app = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (app != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", app.getIdString());
				el.setAttribute("appCode", app.getAppCode());
				el.setAttribute("appDesp", app.getAppDesp());
				el.setAttribute("appcategoryid", app.getAppCategory()
						.getIdString());
				el.setAttribute("appcategorydesc", app.getAppCategory()
						.getAppCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看应用类别
	public void viewAppCategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		int id = request.getParameterIntDef("id", null);
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			EditedAppCategory appCategory;
			AppCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appCategoryBean", AppCategoryBeanRemote.class);
			appCategory = bean.find(id);
			if (appCategory != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", appCategory.getIdString());
				el.setAttribute("code", appCategory.getAppCategoryCode());
				el.setAttribute("desp", appCategory.getAppCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看业务类型
	public void viewBusinessCategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		int id = request.getParameterIntDef("id", null);
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			BaseBusinessCategory businessCategory;
			BusinessCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-businessCategoryBean",
					BusinessCategoryBeanRemote.class);
			businessCategory = bean.find(id);
			if (businessCategory != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", businessCategory.getIdString());
				el.setAttribute("desp",
						businessCategory.getBusinessCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看文件类别
	public void viewFileCategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		short id = request.getParameterShort("id");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			BaseFileCategory fileCategory;
			FileCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-fileCategoryBean",
					FileCategoryBeanRemote.class);
			fileCategory = bean.find(id);
			if (fileCategory != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", fileCategory.getIdString());
				el.setAttribute("desp", fileCategory.getFileCategoryDesp());
				el.setAttribute("cachedtypeid",
						String.valueOf(fileCategory.getCachedType().getValue()));
				el.setAttribute("cachedtype", fileCategory.getCachedType()
						.toString());
				el.setAttribute("filehost",
						String.valueOf(fileCategory.getFileHost()));
				el.setAttribute("filehostdesp",
						CacheHelper.fileHostMap.get(fileCategory.getFileHost())
								.getHostDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看文件属主
	public void viewFileOwner(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		short id = request.getParameterShort("id");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			BaseFileOwner fileOwner;
			FileOwnerBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileOwnerBean", FileOwnerBeanRemote.class);
			fileOwner = bean.find(id);
			if (fileOwner != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", fileOwner.getIdString());
				el.setAttribute("desp", fileOwner.getFileOwnerDesp());
				el.setAttribute("visitrightid",
						String.valueOf(fileOwner.getVisitRight().getValue()));
				el.setAttribute("visitright", fileOwner.getVisitRight()
						.toString());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看文件主机
	public void viewFileHost(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		short id = request.getParameterShort("id");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			BaseFileHost fileHost;
			FileHostBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileHostBean", FileHostBeanRemote.class);
			fileHost = bean.find(id);
			if (fileHost != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", fileHost.getIdString());
				el.setAttribute("desp", fileHost.getHostDesp());
				el.setAttribute("rootdir", fileHost.getFileRootDir());

				el.setAttribute("realhttpurlroot",
						fileHost.getRealHttpUrlRoot());
				el.setAttribute("httpurlprefix", fileHost.getHttpUrlPrefix());
				el.setAttribute("ftphost", fileHost.getFtpHost());
				el.setAttribute("ftpuser", fileHost.getFtpUser());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看文件用户
	public void viewFileUser(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		String id = request.getParameterDef("id", null);
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			FileUserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileUserBean", FileUserBeanRemote.class);
			BaseFileUser fileUser = bean.find(id);
			if (fileUser != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", fileUser.getId());
				el.setAttribute("fileownerid", fileUser.getFileOwner()
						.getIdString());
				el.setAttribute("fileowner", fileUser.getFileOwner()
						.getFileOwnerDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看打印类型
	public void viewPrintType(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BasePrintType printType = null;
			PrintTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printTypeBean", PrintTypeBeanRemote.class);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			Short printTypeId = request.getParameterShortDef("id", null);
			printType = bean.find(printTypeId);
			if (printType != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", printType.getIdString());
				el.setAttribute("desp", printType.getPrintTypeDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看接口信息
	public void viewComminterface(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Short id = request.getParameterShortDef("id", null);
		BaseCommInterface commInterface = null;
		try {
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, false);
			CommInterfaceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-commInterfaceBean",
					CommInterfaceBeanRemote.class);
			commInterface = bean.find(id);
			if (commInterface != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(commInterface.getId()));
				el.setAttribute("type",
						Integer.toString(commInterface.getType()));
				el.setAttribute("desp", commInterface.getDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看告警分类
	public void viewAlarmcategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Short id = request.getParameterShortDef("id", null);
		try {
			BaseAlarmCategory alarmCategory;
			AlarmCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-alarmCategoryBean",
					AlarmCategoryBeanRemote.class);
			alarmCategory = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (alarmCategory != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(alarmCategory.getId()));
				el.setAttribute("desp", alarmCategory.getAlarmCategoryDesp());
				el.setAttribute("alarmlevelid", String.valueOf(alarmCategory
						.getAlarmLevel().getValue()));
				el.setAttribute("alarmlevel", alarmCategory.getAlarmLevel()
						.toString());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看模块驱动
	public void viewDevicetypedriver(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		try {
			BaseDeviceTypeDriver driver = null;
			DeviceTypeDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceTypeDriverBean",
					DeviceTypeDriverBeanRemote.class);
			driver = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (driver != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Integer.toString(driver.getId()));
				el.setAttribute("desp", driver.getDeviceTypeDriverDesp());
				el.setAttribute("filename", driver.getDriverFileDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看模块
	public void viewDevicetype(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		try {
			EditedDeviceType deviceType = null;
			DeviceTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceTypeBean", DeviceTypeBeanRemote.class);
			deviceType = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (deviceType != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Integer.toString(deviceType.getId()));
				el.setAttribute("code", deviceType.getDeviceTypeCode());
				el.setAttribute("desp", deviceType.getDeviceTypeDesp());
				el.setAttribute("driverid", deviceType.getDriver()
						.getIdString());
				el.setAttribute("driverdesp", deviceType.getDriver()
						.getDeviceTypeDriverDesp());
				el.setAttribute("faultpromptoptionid", String
						.valueOf(deviceType.getFaultPromptOption().getValue()));
				el.setAttribute("faultpromptoptiondesp", deviceType
						.getFaultPromptOption().toString());

				el.setAttribute("alarmnotifyoptionid",
						String.valueOf(deviceType.getAlarmNotifyOption()));
				el.setAttribute("faultnotifyoptionid",
						String.valueOf(deviceType.getFaultNotifyOption()));

				el.setAttribute("alarmsendform",
						String.valueOf(deviceType.isAlarmSendForm()));
				el.setAttribute("faultsendform",
						String.valueOf(deviceType.isFaultSendForm()));
				el.setAttribute("alarmsendformdesp",
						deviceType.isAlarmSendForm() ? "是" : "否");
				el.setAttribute("faultsendformdesp",
						deviceType.isFaultSendForm() ? "是" : "否");

				content.appendChild(el);
			}

			Iterator<?> it = bean.getAlarmCodeList(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					Integer.valueOf(id)).iterator();
			while (it.hasNext()) {
				BaseAlarmCode t = (BaseAlarmCode) it.next();
				Element e = xmlDoc.createElement("alarmcode");
				content.appendChild(e);
				e.setAttribute("id", Integer.toString(t.getAlarmCode()));
				e.setAttribute("code", String.valueOf(t.getAlarmCode()));
				e.setAttribute("desp", t.getAlarmDesp());
				e.setAttribute("alarmcategoryid",
						Integer.toString(t.getAlarmCategory().getId()));
				e.setAttribute("alarmcategorydesp", t.getAlarmCategory()
						.getAlarmCategoryDesp());
			}
		} finally {
			context.close();
		}
	}

	// 查看模块告警
	public void viewDevicetypeAlarm(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer devicetype = request.getParameterIntDef("devicetype", null);
		Integer alarmcode = request.getParameterIntDef("alarmcode", null);
		BaseAlarmCode alarmCode;
		try {
			DeviceTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceTypeBean", DeviceTypeBeanRemote.class);
			alarmCode = bean.findAlarmCode(devicetype, alarmcode);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (alarmCode != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("devicetype",
						String.valueOf(alarmCode.getDeviceType()));
				el.setAttribute("alarmcode",
						String.valueOf(alarmCode.getAlarmCode()));
				el.setAttribute("alarmcategoryid", alarmCode.getAlarmCategory()
						.getIdString());
				el.setAttribute("alarmcategorydesp", alarmCode
						.getAlarmCategory().getAlarmCategoryDesp());
				el.setAttribute("alarmdesp", alarmCode.getAlarmDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看设备驱动
	public void viewDevicedriver(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BaseDeviceDriver driver = null;
			DeviceDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceDriverBean",
					DeviceDriverBeanRemote.class);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			Short id = request.getParameterShortDef("id", null);
			driver = bean.find(id);
			if (driver != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Integer.toString(driver.getId()));
				el.setAttribute("desp", driver.getDeviceDriverDesp());
				el.setAttribute("filename", driver.getDriverFileDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看设备
	public void viewDevice(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		Integer id = request.getParameterIntDef("id", null);
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			EditedDevice device = null;
			DeviceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceBean", DeviceBeanRemote.class);
			device = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (device != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Integer.toString(device.getId()));
				el.setAttribute("desp", device.getDeviceName());
				el.setAttribute("type", device.getDeviceType().getIdString());
				el.setAttribute("typedesp", device.getDeviceType()
						.getDeviceTypeDesp());
				el.setAttribute("driverid", device.getDriver().getIdString());
				el.setAttribute("driverdesp", device.getDriver()
						.getDeviceDriverDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看收费渠道
	public void viewPayWay(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		try {
			BasePayWay payWay;
			PayWayBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-payWayBean", PayWayBeanRemote.class);
			payWay = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (payWay != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(payWay.getId()));
				el.setAttribute("desp", payWay.getPayWayDesp());
				el.setAttribute("needtradeid",
						String.valueOf(payWay.isNeedTrade()));
				el.setAttribute("needtrade", payWay.isNeedTrade() ? "是" : "否");
				el.setAttribute("typeid",
						String.valueOf(payWay.getType().getValue()));
				el.setAttribute("type", payWay.getType().toString());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看收费项目
	public void viewPayItem(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		try {
			EditedPayItem payItem;
			PayItemBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-payItemBean", PayItemBeanRemote.class);
			payItem = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (payItem != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(payItem.getId()));
				el.setAttribute("desp", payItem.getPayItemDesp());
				el.setAttribute("price", Long.toString(payItem.getPrice()));
				el.setAttribute("memo", payItem.getMemo());
				el.setAttribute("needtrade", payItem.isNeedTrade() ? "是" : "否");
				el.setAttribute("needtradeid",
						String.valueOf(payItem.isNeedTrade()));
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看禁止查询打印用户号码
	public void viewDisabledPrintUser(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		BaseDisabledPrintUser disabledPrintUser;
		try {
			DisabledPrintUserBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-disabledPrintUserBean",
					DisabledPrintUserBeanRemote.class);
			disabledPrintUser = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (disabledPrintUser != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(disabledPrintUser.getId()));
				el.setAttribute("desp", disabledPrintUser.getUserno());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看厂商
	public void viewManuf(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedManuf manuf;
		try {
			ManufBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-manufBean", ManufBeanRemote.class);
			manuf = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (manuf != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Integer.toString(manuf.getId()));
				el.setAttribute("code", manuf.getManufCode());
				el.setAttribute("desp", manuf.getManufName());
				el.setAttribute("serialnumber",
						String.valueOf(manuf.getSerialNumber()));
				el.setAttribute("manuftype",
						String.valueOf(manuf.getManufType()));
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看银联终端
	public void viewBankTerm(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedBankTerm bankTerm;
		try {
			BankTermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-bankTermBean", BankTermBeanRemote.class);
			bankTerm = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (bankTerm != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Integer.toString(bankTerm.getId()));
				el.setAttribute("code", bankTerm.getBankTermCode());
				el.setAttribute("desp", bankTerm.getBankTermDesp());
				el.setAttribute("merchantaccount",
						bankTerm.getMerchantAccount());
				el.setAttribute("extfield", bankTerm.getExtField());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看业务
	public void viewBusiness(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedBusiness business;
		try {
			BusinessBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-businessBean", BusinessBeanRemote.class);
			business = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (business != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(business.getId()));
				el.setAttribute("desp", business.getBusinessDesp());
				el.setAttribute("categoryid", business.getBusinessCategory()
						.getIdString());
				el.setAttribute("category", business.getBusinessCategory()
						.getBusinessCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看交易
	public void viewTradeCode(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedTradeCode tradeCode;
		try {
			TradeCodeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-tradeCodeBean", TradeCodeBeanRemote.class);
			tradeCode = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (tradeCode != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(tradeCode.getId()));
				el.setAttribute("desp", tradeCode.getTradeCodeDesp());
				el.setAttribute("service", tradeCode.getTradeService());
				el.setAttribute("tradecode", tradeCode.getTradeCode());
				el.setAttribute("statedid",
						String.valueOf(tradeCode.isStated()));
				el.setAttribute("loggedid",
						String.valueOf(tradeCode.isLogged()));
				el.setAttribute("stated", tradeCode.isStated() ? "是" : "否");
				el.setAttribute("logged", tradeCode.isLogged() ? "是" : "否");
				if (tradeCode.getPayWay() != null) {
					el.setAttribute("paywayid", tradeCode.getPayWay()
							.getIdString());
					el.setAttribute("payway", tradeCode.getPayWay()
							.getPayWayDesp());
				} else {
					el.setAttribute("paywayid", "");
					el.setAttribute("payway", "");
				}
				if (tradeCode.getPayItem() != null) {
					el.setAttribute("payitemid", tradeCode.getPayItem()
							.getIdString());
					el.setAttribute("payitem", tradeCode.getPayItem()
							.getPayItemDesp());
				} else {
					el.setAttribute("payitemid", "");
					el.setAttribute("payitem", "");
				}

				el.setAttribute("businessid", tradeCode.getBusiness()
						.getIdString());
				el.setAttribute("businessid_desp", tradeCode.getBusiness()
						.getBusinessDesp());
				el.setAttribute("strikeid", tradeCode.getStrikeTadeCode()
						.getIdString());
				el.setAttribute("strikeid_desp", tradeCode.getStrikeTadeCode()
						.getTradeCodeDesp());
				el.setAttribute("redoenabled",
						String.valueOf(tradeCode.isRedoEnabled()));
				el.setAttribute("redoenableddesp",
						tradeCode.isRedoEnabled() ? "是" : "否");
				el.setAttribute("refundmode",
						String.valueOf(tradeCode.getRefundMode()));
				el.setAttribute("cancelrefundmode",
						String.valueOf(tradeCode.getCancelRefundMode()));

				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看业务页面
	public void viewPageElement(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedPageElement pageElement;
		try {
			PageElementBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-pageElementBean", PageElementBeanRemote.class);
			pageElement = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (pageElement != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(pageElement.getId()));
				el.setAttribute("code", pageElement.getPageCode());
				el.setAttribute("desp", pageElement.getPageDesp());
				el.setAttribute("businessid", pageElement.getBusiness()
						.getIdString());
				el.setAttribute("business", pageElement.getBusiness()
						.getBusinessDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看终端型号
	public void viewTermType(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedTermType termType;
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			termType = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);

			bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			QueryResult<EditedTermType> r = bean.queryTermType(false,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, "", 0, 9999);
			Iterator<EditedTermType> it = r.getResultList().iterator();

			if (termType != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Integer.toString(termType.getId()));
				el.setAttribute("code", termType.getTypeCode());
				el.setAttribute("desp", termType.getTypeDesp());
				el.setAttribute("manufid", termType.getManuf().getIdString());
				el.setAttribute("manufdesp", termType.getManuf().getManufName());
				el.setAttribute("appid",
						String.valueOf(termType.getApp().getAppId()));
				el.setAttribute("appdesc", termType.getApp().getAppDesp());
				el.setAttribute("cashflagid",
						String.valueOf(termType.getCashFlag().getValue()));
				el.setAttribute("cashflag", termType.getCashFlag().toString());
				el.setAttribute("fixtypeid",
						String.valueOf(termType.getFixType().getValue()));
				el.setAttribute("fixtype", termType.getFixType().toString());

				content.appendChild(el);

				while (it.hasNext()) {
					EditedTermType t = it.next();
					Element e = xmlDoc.createElement("device");
					content.appendChild(e);
					e.setAttribute("id", t.getIdString());
					e.setAttribute("name", t.getTypeDesp());
					// e.setAttribute("port", t.getDevice().getDeviceName());
					// e.setAttribute("name", t.getDevice().getDeviceName());
				}
			}
		} finally {
			context.close();
		}
	}

	// 查看终端型号所司设备
	public void viewTermTypeDevice(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer deviceid = request.getParameterIntDef("deviceid", null);
		Integer typeid = request.getParameterInt("typeid");
		EditedTermTypeDevice termTypeDevice;
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			termTypeDevice = bean.findDevice(((AdminSessionObject) session)
					.getLoginUser().getUserId(), typeid, deviceid);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);

			if (termTypeDevice != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("termtypeid", termTypeDevice.getTermType()
						.getIdString());
				el.setAttribute("termtypedesp", termTypeDevice.getTermType()
						.getTypeDesp());
				el.setAttribute("deviceid", termTypeDevice.getDevice()
						.getIdString());
				el.setAttribute("devicedesp", termTypeDevice.getDevice()
						.getDeviceName());
				el.setAttribute("port",
						String.valueOf(termTypeDevice.getPort()));
				el.setAttribute("portdesp", "");
				if (!session.getComPortItems().isEmpty())
					for (int i = 0; i < session.getComPortItems().size(); i++) {
						IntegerItem item = session.getComPortItems().get(i);
						if (item.getId() == termTypeDevice.getPort()) {
							el.setAttribute("portdesp", item.getText());
							break;
						}
					}
				el.setAttribute("extConfig", termTypeDevice.getExtConfig());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看打印广告分类
	public void viewPrintAdCategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		short id = request.getParameterShort("id");
		BasePrintAdCategory printAdCategory;
		try {
			PrintAdCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-printAdCategoryBean",
					PrintAdCategoryBeanRemote.class);
			printAdCategory = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);

			if (printAdCategory != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(printAdCategory.getId()));
				el.setAttribute("desp",
						printAdCategory.getPrintAdCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看广告分类
	public void viewAdCategory(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		short id = request.getParameterShort("id");
		BaseAdCategory adCategory;
		try {
			AdCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-adCategoryBean", AdCategoryBeanRemote.class);
			adCategory = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);

			if (adCategory != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(adCategory.getId()));
				el.setAttribute("desp", adCategory.getAdCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看信息分类
	public void viewInfoCategory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		short id = request.getParameterShort("id");
		BaseInfoCategory infoCategory;
		try {
			InfoCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-infoCategoryBean",
					InfoCategoryBeanRemote.class);
			infoCategory = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);

			if (infoCategory != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(infoCategory.getId()));
				el.setAttribute("desp", infoCategory.getInfoCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看发票模板
	public void viewInvoiceTemplate(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedInvoiceTemplate invoiceTemplate;
		try {
			InvoiceTemplateBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceTemplateBean",
					InvoiceTemplateBeanRemote.class);
			invoiceTemplate = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);

			if (invoiceTemplate != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", invoiceTemplate.getIdString());
				el.setAttribute("desp", invoiceTemplate.getTemplateDesp());
				el.setAttribute("code", invoiceTemplate.getTemplateCode());
				el.setAttribute("templatecontent",
						invoiceTemplate.getTemplateContent());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看发票模板
	public void viewInvoiceConfig(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedInvoiceConfig invoiceConfig;
		try {
			InvoiceConfigBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceConfigBean",
					InvoiceConfigBeanRemote.class);
			invoiceConfig = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			Map<String, String> typeMap = AdminSessionObject
					.getParamConfig(100);
			Map<String, String> taxMap = AdminSessionObject.getParamConfig(101);
			Map<String, String> awayMap = AdminSessionObject
					.getParamConfig(102);
			if (invoiceConfig != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", invoiceConfig.getIdString());
				el.setAttribute("desp", invoiceConfig.getConfigDesp());
				el.setAttribute("configcode", invoiceConfig.getConfigCode());
				el.setAttribute("orgid", invoiceConfig.getOrg().getOrgId() + "");
				el.setAttribute("orgname", invoiceConfig.getOrg().getOrgName());
				el.setAttribute("invoicetypeid",
						String.valueOf(invoiceConfig.getInvoiceType()));
				el.setAttribute("invoicetype", typeMap.get(Integer
						.toString(invoiceConfig.getInvoiceType())));
				el.setAttribute("templateid", invoiceConfig
						.getInvoiceTemplate().getIdString());
				el.setAttribute("templatedesp", invoiceConfig
						.getInvoiceTemplate().getTemplateDesp() + "");
				el.setAttribute("taxflagid",
						Integer.toString(invoiceConfig.getTaxFlag()));
				el.setAttribute("taxflag", taxMap.get(Integer
						.toString(invoiceConfig.getTaxFlag())));
				el.setAttribute("awayflagid",
						Integer.toString(invoiceConfig.getAwayFlag()));
				el.setAttribute("awayflag", awayMap.get(Integer
						.toString(invoiceConfig.getAwayFlag())));
				el.setAttribute("logged", invoiceConfig.isLogged() + "");
				el.setAttribute("loggeddesp", invoiceConfig.isLogged() ? "是"
						: "否");
				el.setAttribute("alertcount", invoiceConfig.getAlertCount()
						+ "");
				el.setAttribute("extdata0", invoiceConfig.getExtdata0());
				el.setAttribute("extdata1", invoiceConfig.getExtdata1());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看终端开停
	public void viewTermBusinessOpen(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Long id = request.getParameterLongDef("id", null);
		BaseTermBusinessOpenClose value;
		try {
			TermBusinessOpenCloseRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBusinessOpenCloseBean",
					TermBusinessOpenCloseRemote.class);
			value = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (value != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(value.getId()));
				el.setAttribute("termid", String.valueOf(value.getTermId()));
				el.setAttribute("businesscategoryids",
						value.getBusinessCategoryIds());
				el.setAttribute("businessids", value.getBusinessIds());
				el.setAttribute("payways", value.getPayWays());
				el.setAttribute("payitems", value.getPayItems());
				el.setAttribute("startdate", DataUnit.formatDateTime(
						value.getStartDate(), "yyyy-MM-dd"));
				el.setAttribute("enddate", DataUnit.formatDateTime(
						value.getEndDate(), "yyyy-MM-dd"));
				el.setAttribute("time", value.getOpenTimes());
				el.setAttribute("openmodeid",
						String.valueOf(value.getOpenMode().getValue()));
				el.setAttribute("openmode", value.getOpenMode().toString());
				el.setAttribute("reason", value.getReason());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看机构开停
	public void viewOrgBusinessOpen(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Long id = request.getParameterLongDef("id", null);
		BaseOrgBusinessOpenClose value;
		try {
			OrgBusinessOpenCloseRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-orgBusinessOpenCloseBean",
					OrgBusinessOpenCloseRemote.class);
			value = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (value != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(value.getId()));
				el.setAttribute("orgid", String.valueOf(value.getOrgId()));
				el.setAttribute("businesscategoryids",
						value.getBusinessCategoryIds());
				el.setAttribute("businessids", value.getBusinessIds());
				el.setAttribute("payways", value.getPayWays());
				el.setAttribute("payitems", value.getPayItems());
				el.setAttribute("startdate", DataUnit.formatDateTime(
						value.getStartDate(), "yyyy-MM-dd"));
				el.setAttribute("enddate", DataUnit.formatDateTime(
						value.getEndDate(), "yyyy-MM-dd"));
				el.setAttribute("time", value.getOpenTimes());
				el.setAttribute("openmodeid",
						String.valueOf(value.getOpenMode().getValue()));
				el.setAttribute("openmode", value.getOpenMode().toString());
				el.setAttribute("reason", value.getReason());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看终端
	public void viewTerm(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedTerm term;
		try {
			TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBean", TermBeanRemote.class);
			term = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (term != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(term.getId()));
				el.setAttribute("typeid", term.getTermType().getIdString());
				el.setAttribute("typedesp", term.getTermType()
						.getDisplayLabel());
				// 应用
				el.setAttribute("appid", term.getApp().getIdString());
				el.setAttribute("appdesp", term.getApp().getAppDesp());
				// 结算类型
				el.setAttribute("settlementtypeid",
						String.valueOf(term.getSettlementType().getValue()));
				el.setAttribute("settlementtypedesp", term.getSettlementType()
						.toString());
				// 所属机构
				el.setAttribute("orgid", term.getOrg().getIdString());
				el.setAttribute("orgdesp", "");
				CachedOrg org = CacheHelper.orgMap
						.get(term.getOrg().getOrgId());
				if (org != null)
					el.setAttribute("orgdesp", org.getOrgFullName());

				el.setAttribute("termcode", term.getTermCode());
				el.setAttribute("termdesp", term.getTermDesp());
				// 出厂编码
				el.setAttribute("manufno", term.getManufNo());
				// 开关机时间
				el.setAttribute("opentime", term.getOpenTime());
				el.setAttribute("closetime", term.getCloseTime());
				// 区号
				el.setAttribute("areacode", term.getAreaCode());
				// 银联终端编码
				el.setAttribute("banktermcode", term.getBankTerm()
						.getBankTermCode());
				el.setAttribute("merchantaccount", term.getBankTerm()
						.getMerchantAccount());
				// 联系人
				el.setAttribute("contacter", term.getContacter());
				el.setAttribute("address", term.getAddress());
				// GUID
				el.setAttribute("guid", term.getGuid());

				el.setAttribute("ip", term.getIp());

				el.setAttribute("extfield0", term.getExtField0());
				el.setAttribute("extfield1", term.getExtField1());
				el.setAttribute("extfield2", term.getExtField2());
				el.setAttribute("extfield3", term.getExtField3());
				el.setAttribute("extfield4", term.getExtField4());

				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看角色
	public void viewRole(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		BaseRole role;
		try {
			RoleBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-roleBean", RoleBeanRemote.class);
			role = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (role != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", role.getIdString());
				el.setAttribute("rolename", role.getRoleName());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看登录用户信息
	public void viewLoginUser(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		try {
			LoginUser user = session.getLoginUser();
			if (user != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", user.getIdString());
				el.setAttribute("usercode", user.getUserCode());
				el.setAttribute("username", user.getUserName());
				el.setAttribute("orgid", user.getOrg().getIdString());
				el.setAttribute("orgname", user.getOrg().getOrgName());
				el.setAttribute(
						"orgdepth",
						String.valueOf(CacheHelper.orgMap.get(
								user.getOrg().getOrgId()).getDepth()));
				el.setAttribute("manufid", user.getManuf().getIdString());
				el.setAttribute("manufname", user.getManuf().getManufName());
				content.appendChild(el);
			}
		} finally {
		}
	}

	// 查看用户
	public void viewUser(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedUser user;
		try {
			UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			user = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (user != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", Long.toString(user.getUserId()));
				el.setAttribute("code", user.getUserCode());
				el.setAttribute("desp", user.getUserName());
				el.setAttribute("rightvalue",
						Integer.toString(user.getUserGroup().getValue()));
				el.setAttribute("rightvaluedesp", user.getUserGroup()
						.toString());
				if (user.getUserGroup().isCustomer()) {
					if (user.getRole().getId() != null) {
						el.setAttribute("rightid", user.getRole().getIdString());
						el.setAttribute("right", user.getRole().getRoleName());
					} else {
						el.setAttribute("rightid",
								String.valueOf(user.getUserGroup().getValue()));
						el.setAttribute("right", user.getUserGroup()
								.getString());
					}
				} else {
					el.setAttribute("rightid",
							String.valueOf(user.getUserGroup().getValue()));
					el.setAttribute("right", user.getUserGroup().toString());
				}
				if (user.getManuf().getId() != null) {
					el.setAttribute("manufid", user.getManuf().getIdString());
					el.setAttribute("manuf", user.getManuf().getManufName());
				} else {
					el.setAttribute("manufid", "");
					el.setAttribute("manuf", "");
				}
				if (user.getOrg().getId() != null) {
					el.setAttribute("orgid", user.getOrg().getIdString());
					el.setAttribute("org", user.getOrg().getOrgName());
				} else {
					el.setAttribute("orgid", "");
					el.setAttribute("org", "");
				}

				el.setAttribute("email", user.getEmail());
				el.setAttribute("telphone", user.getTelphone());
				el.setAttribute("mobile", user.getMobile());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看用户角色
	public void viewUserRoles(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		BaseUser user = new BaseUser();
		try {
			UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			Long userid = request.getParameterLong("id");
			user.setUserId(userid);
			List<BaseRole> roleList = (List<BaseRole>) bean
					.getUserManagedRoles(((AdminSessionObject) session)
							.getLoginUser().getUserId(), userid, "");
			Iterator<BaseRole> it = roleList.iterator();
			while (it.hasNext()) {
				BaseRole role = it.next();
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", role.getIdString());
				el.setAttribute("name", role.getRoleName());
				el.setAttribute("checked", role.isChecked() ? "true" : "false");
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	// 查看打印广告
	public void viewPrintAd(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedPrintAd printAd;
		try {
			PrintAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printAdBean", PrintAdBeanRemote.class);
			printAd = (EditedPrintAd) bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (printAd != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", printAd.getIdString());
				el.setAttribute("orgid", printAd.getOrg().getIdString());
				el.setAttribute("orgdesp", printAd.getOrg().getOrgName());
				el.setAttribute("adcategoryid", printAd.getAdCategory()
						.getIdString());
				el.setAttribute("adcategorydesp", printAd.getAdCategory()
						.getPrintAdCategoryDesp());
				el.setAttribute("auditstatusid",
						String.valueOf(printAd.getAuditStatus().getValue()));
				el.setAttribute("auditstatusdesp", printAd.getAuditStatus()
						.toString());
				el.setAttribute("content", printAd.getContent());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看机构广告
	public void viewOrgAd(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		BaseOrgAd orgAd;
		try {
			OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
			orgAd = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (orgAd != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", orgAd.getIdString());
				el.setAttribute("adcategoryid", orgAd.getAdCategory()
						.getIdString());
				el.setAttribute("adcategorydesp", orgAd.getAdCategory()
						.getAdCategoryDesp());

				el.setAttribute("addesp", orgAd.getAdDesp());
				el.setAttribute("startdate",
						new DateTime(orgAd.getStartDate()).format("yyyy-MM-dd"));
				el.setAttribute("enddate",
						new DateTime(orgAd.getEndDate()).format("yyyy-MM-dd"));
				el.setAttribute("filename", orgAd.getFileName());
				el.setAttribute("playtimes",
						String.valueOf(orgAd.getPlayTimes()));
				el.setAttribute("duration", String.valueOf(orgAd.getDuration()));

				el.setAttribute("auditstatusid",
						String.valueOf(orgAd.getAuditStatus().getValue()));
				el.setAttribute("auditstatusdesp", orgAd.getAuditStatus()
						.toString());
				el.setAttribute("url", orgAd.getUrl());
				el.setAttribute("storetypeid",
						String.valueOf(orgAd.getStoreType().getValue()));
				el.setAttribute("storetypedesp",
						String.valueOf(orgAd.getStoreType().toString()));
				el.setAttribute("priorityid",
						String.valueOf(orgAd.getPriority().getValue()));
				el.setAttribute("prioritydesp", orgAd.getPriority().toString());
				el.setAttribute("uploadCompleteid",
						String.valueOf(orgAd.isUploadComplete()));

				el.setAttribute("uploadCompletedesp",
						orgAd.isUploadComplete() ? "是" : "否");

				el.setAttribute("orgid", orgAd.getOrg().getIdString());
				el.setAttribute("orgdesp", orgAd.getOrg().getOrgName());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看终端广告
	public void viewTermAd(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		BaseTermAd termAd;
		try {
			TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termAdBean", TermAdBeanRemote.class);
			termAd = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (termAd != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", termAd.getIdString());
				el.setAttribute("adcategoryid", termAd.getAdCategory()
						.getIdString());
				el.setAttribute("adcategorydesp", termAd.getAdCategory()
						.getAdCategoryDesp());

				el.setAttribute("addesp", termAd.getAdDesp());
				el.setAttribute("startdate",
						new DateTime(termAd.getStartDate())
								.format("yyyy-MM-dd"));
				el.setAttribute("enddate",
						new DateTime(termAd.getEndDate()).format("yyyy-MM-dd"));
				el.setAttribute("filename", termAd.getFileName());
				el.setAttribute("playtimes",
						String.valueOf(termAd.getPlayTimes()));
				el.setAttribute("duration",
						String.valueOf(termAd.getDuration()));

				el.setAttribute("auditstatusid",
						String.valueOf(termAd.getAuditStatus().getValue()));
				el.setAttribute("auditstatusdesp", termAd.getAuditStatus()
						.toString());
				el.setAttribute("url", termAd.getUrl());
				el.setAttribute("storetypeid",
						String.valueOf(termAd.getStoreType().getValue()));
				el.setAttribute("storetypedesp",
						String.valueOf(termAd.getStoreType().toString()));
				el.setAttribute("priorityid",
						String.valueOf(termAd.getPriority().getValue()));
				el.setAttribute("prioritydesp", termAd.getPriority().toString());
				el.setAttribute("uploadCompleteid",
						String.valueOf(termAd.isUploadComplete()));

				el.setAttribute("uploadCompletedesp",
						termAd.isUploadComplete() ? "是" : "否");

				el.setAttribute("termid", termAd.getTerm().getIdString());
				el.setAttribute("termdesp", termAd.getTerm().getTermDesp());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看信息
	public void viewInfo(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedInfo info;
		try {
			InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-infoBean", InfoBeanRemote.class);
			info = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (info != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", info.getIdString());
				el.setAttribute("orgid", info.getOrg().getIdString());
				el.setAttribute("orgdesp", info.getOrg().getOrgName());
				el.setAttribute("infocategoryid", info.getInfoCategory()
						.getIdString());
				el.setAttribute("infocategorydesp", info.getInfoCategory()
						.getInfoCategoryDesp());
				el.setAttribute("publishtime",
						new DateTime(info.getPublishTime())
								.format("yyyy-MM-dd"));
				el.setAttribute("title", info.getTitle());
				el.setAttribute("auditstatusid",
						String.valueOf(info.getAuditStatus().getValue()));
				el.setAttribute("auditstatusdesp", info.getAuditStatus()
						.toString());

				el.setAttribute("url", info.getUrl());
				el.setAttribute("fileName", info.getFileName());
				el.setAttribute("summary", info.getSummary());
				el.setAttribute("startdate",
						new DateTime(info.getStartDate()).format("yyyy-MM-dd"));
				el.setAttribute("enddate",
						new DateTime(info.getStartDate()).format("yyyy-MM-dd"));

				FileDownloadProcessor processor = new FileDownloadProcessor(
						AdminTradeExecutor.fileUserId,
						AdminTradeExecutor.filePwd, (short) 6);
				String infoText = new String(processor.downloadFile(0,
						info.getFileName(), 0, 0).getValue())
						.replace("$_contextpath_", request.getRequest()
								.getContextPath());
				if (infoText
						.startsWith("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"))
					infoText = infoText
							.substring("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
									.length());

				el.setAttribute("infoText", infoText);
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看机构
	public void viewOrg(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		EditedOrg org;
		try {
			OrgBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgBean", OrgBeanRemote.class);
			org = bean.find(id);
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (org != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", org.getIdString());
				el.setAttribute("orgtypeid", String.valueOf(org.getOrgType()));
				el.setAttribute("orgtypename", AdminSessionObject
						.getParamConfig(1)
						.get(String.valueOf(org.getOrgType())));
				el.setAttribute("orgcode", org.getOrgCode());
				el.setAttribute("orgname", org.getOrgName());
				el.setAttribute("simplename", org.getSimpleName());
				el.setAttribute("orgfullname", org.getOrgFullName());
				el.setAttribute("standardareacode", org.getStandardAreaCode());
				el.setAttribute("contacter", org.getContacter());
				el.setAttribute("telphone", org.getTelphone());
				el.setAttribute("address", org.getAddress());
				el.setAttribute("email", org.getEmail());
				el.setAttribute("serialnumber",
						String.valueOf(org.getSerialNumber()));

				el.setAttribute("extfield0", org.getExtField0());
				// el.setAttribute("extfield0_desp",
				// AdminSessionObject.getParamConfig(200).get(String.valueOf(org.getExtField0())));
				el.setAttribute("extfield1", org.getExtField1());
				// el.setAttribute("extfield1_desp",
				// AdminSessionObject.getParamConfig(201).get(String.valueOf(org.getExtField1())));
				el.setAttribute("extfield2", org.getExtField2());
				// el.setAttribute("extfield2_desp",
				// AdminSessionObject.getParamConfig(203).get(String.valueOf(org.getExtField2())));
				el.setAttribute("extfield3", org.getExtField3());
				el.setAttribute("extfield4", org.getExtField4());
				el.setAttribute("parentid", org.getParentOrg().getIdString());
				el.setAttribute("parentname", "");
				if (!org.getParentOrg().getIdString().trim().equals("")) {
					CachedOrg parentorg = CacheHelper.orgMap.get(Integer
							.parseInt(org.getParentOrg().getIdString()));
					if (parentorg != null)
						el.setAttribute("parentname", parentorg.getOrgName());
				}
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看我的信息
	public void viewEditMyInfo(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		QueryedUser user;
		try {
			UserBeanRemote userBean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			user = userBean.find(((AdminSessionObject) session).getLoginUser()
					.getId());
			this.appendPermissionElement(content, xmlDoc, true, true, true,
					true, true, true);
			if (user != null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", user.getIdString());
				el.setAttribute("usercode", user.getUserCode());
				el.setAttribute("username", user.getUserName());
				el.setAttribute("mobile", user.getMobile());
				el.setAttribute("telphone", user.getTelphone());
				el.setAttribute("email", user.getEmail());
				el.setAttribute("orgname", user.getOrg().getOrgName());
				el.setAttribute("manufname", user.getManuf().getManufName());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 查看设备驱动文件
	public void viewDeviceDriverFile(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceDriverBean",
					DeviceDriverBeanRemote.class);
			BaseDeviceDriverFile driver = bean.findDriverFile(request
					.getParameterLong("id"));
			if (driver != null) {
				Element e = xmlDoc.createElement("record");
				e.setAttribute("id", driver.getIdString());
				e.setAttribute("devicedriverid",
						String.valueOf(driver.getDeviceDriverId()));
				e.setAttribute("filename", driver.getFileName());
				content.appendChild(e);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	// 查看我的权限
	public void viewMyUserRights(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		EditedFunction node;
		try {
			FuncTreeNode funcTree = session.getLoginUserFuncTree();
			if (funcTree != null) {
				node = funcTree.getFunction();
				if (node != null) {
					Element el = xmlDoc.createElement("node");
					el.setAttribute("id", node.getIdString());
					el.setAttribute("name", node.getText());
					content.appendChild(el);
				}
				Iterator<TreeNode<Integer>> it = funcTree.getChildren()
						.iterator();
				while (it.hasNext()) {
					TreeNode<Integer> t = (TreeNode<Integer>) it.next();
					Element el = xmlDoc.createElement("node");
					el.setAttribute("id", t.getIdString());
					el.setAttribute("name", t.getText());
					el.setAttribute("parentid", "0");
					// if(t.getParent()!=null&&t.getParent().getIdString()!=null)
					// el.setAttribute("parentid", t.getParent().getIdString());
					el.setAttribute("depth", String.valueOf(t.getDepth()));
					content.appendChild(el);
					Iterator<TreeNode<Integer>> its = t.getChildren()
							.iterator();
					while (its.hasNext()) {
						TreeNode<Integer> tn = (TreeNode<Integer>) its.next();
						el = xmlDoc.createElement("node");
						el.setAttribute("id", tn.getIdString());
						el.setAttribute("name", tn.getText());
						el.setAttribute("parentid", "");
						if (tn.getParent() != null
								&& tn.getParent().getIdString() != null)
							el.setAttribute("parentid", tn.getParent()
									.getIdString());
						el.setAttribute("depth", String.valueOf(tn.getDepth()));
						content.appendChild(el);

					}
				}
			}
		} finally {

		}
	}

	// 获取所有功能权限
	public void viewRoleFunc(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		EditedFunction node;
		Integer id = request.getParameterInt("id");
		LoopNamingContext context = new LoopNamingContext("db");
		FuncTreeNode funcTree;
		try {
			RoleBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-roleBean", RoleBeanRemote.class);
			Iterator<Integer> it = bean.getFunction(id).iterator();
			funcTree = ((AdminSessionObject) session).getCurCustomFuncTree();

			while (it.hasNext()) {
				TreeNode<Integer> n = funcTree.find(it.next(), true);
				if (n != null)
					n.setChecked(true);
			}

			if (funcTree != null) {
				node = funcTree.getFunction();
				if (node != null) {
					Element el = xmlDoc.createElement("node");
					el.setAttribute("id", node.getIdString());
					el.setAttribute("name", node.getText());
					content.appendChild(el);
				}
				Iterator<TreeNode<Integer>> itn = funcTree.getChildren()
						.iterator();
				while (itn.hasNext()) {
					TreeNode<Integer> t = (TreeNode<Integer>) itn.next();
					Element el = xmlDoc.createElement("node");
					el.setAttribute("id", t.getIdString());
					el.setAttribute("name", t.getText());
					el.setAttribute("parentid", "0");
					el.setAttribute("depth", String.valueOf(t.getDepth()));
					el.setAttribute("checked", "false");
					if (t.isChecked())
						el.setAttribute("checked", "true");
					content.appendChild(el);
					Iterator<TreeNode<Integer>> its = t.getChildren()
							.iterator();
					while (its.hasNext()) {
						TreeNode<Integer> tn = (TreeNode<Integer>) its.next();
						el = xmlDoc.createElement("node");
						el.setAttribute("id", tn.getIdString());
						el.setAttribute("name", tn.getText());
						el.setAttribute("parentid", "");
						if (tn.getParent() != null
								&& tn.getParent().getIdString() != null)
							el.setAttribute("parentid", tn.getParent()
									.getIdString());
						el.setAttribute("depth", String.valueOf(tn.getDepth()));
						el.setAttribute("checked", "false");
						if (t.isChecked())
							el.setAttribute("checked", "true");
						content.appendChild(el);
					}
				}
			}
		} finally {

		}
	}

	// 查看终端迁移历史记录
	public void TermMoveHistory(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer termId = request.getParameterIntDef("termId", null);
		try {
			CachedTerm term = CacheHelper.termMap.getTerm(termId);
			TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBean", TermBeanRemote.class);
			List<BaseTermMoveItem> list = bean.getTermMoveList(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					termId);
			if (list.size() > 0) {
				BaseTermMoveItem item = list.get(0);
				String orgdesp = item.getOrg().getOrgFullName();
				for (int i = 1; i < list.size(); i++) {
					Element e = xmlDoc.createElement("record");
					BaseTermMoveItem item1 = list.get(i);
					String s = new DateTime(item.getMoveTime())
							.format("yyyy-MM-dd: ");
					s += orgdesp + "  ->  ";
					orgdesp = item1.getOrg().getOrgFullName();
					s += orgdesp;
					item = item1;
					e.setAttribute("desp", s);
					content.appendChild(e);
				}
				Element e = xmlDoc.createElement("record");
				String s = new DateTime(item.getMoveTime())
						.format("yyyy-MM-dd: ");
				s += orgdesp + "  ->  ";
				s += term.getOrg().getOrgFullName();
				e.setAttribute("desp", s);
				content.appendChild(e);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	// 获取功能菜单数据
	public void querySubMenu(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		int funcId = request.getParameterIntDef("funcId", 0);
		try {
			Element root, recordRoot, el;

			// 终端系统
			if (funcId == 11) {
				root = this.getXmlNode(xmlDoc, "function", "11010", "监控管理",
						"", "", "",
						"");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);

				el = this.getXmlNode(xmlDoc, "record", "11011", "终端监控",
						"select.png", "Kxd.MonitorAnalysis.MainPage",
						"monitors", "main");
				recordRoot.appendChild(el);

				el = this.getXmlNode(xmlDoc, "record", "11012", "业务监控", "",
						"Kxd.MonitorAnalysis.MainPage", "monitors", "main");
				recordRoot.appendChild(el);

				el = this.getXmlNode(xmlDoc, "record", "11013", "接口监控", "",
						"Kxd.MonitorAnalysis.MainPage", "monitors", "main");
				recordRoot.appendChild(el);

				content.appendChild(root);

			}

			// 权限管理
			if (funcId == 12) {
				this.createSubMenu(content, xmlDoc, "1201", "厂商", "Right.Manuf");
				this.createUnicomSubMenu(content, xmlDoc, "1202", "机构网点",
						"Right.Org");
				this.createSubMenu(content, xmlDoc, "1203", "角色", "Right.Role");
				this.createSubMenu(content, xmlDoc, "1204", "用户", "Right.User");

			}

			// 终端管理
			if (funcId == 13) {
				this.createSubMenu(content, xmlDoc, "1301", "银联终端",
						"Term.BankTerm");
				this.createSubMenu(content, xmlDoc, "1302", "终端", "Term.Term");
			}

			// 报表系统
			if (funcId == 14) {
				root = this.getXmlNode(xmlDoc, "function", "14100", "交易类报表",
						"", "", "default", "");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				createUnicomReportMenu(recordRoot, xmlDoc, "14111", "现金营业款日报",
						"Report.CashPay");
				createUnicomReportMenu(recordRoot, xmlDoc, "14112", "银行卡营业款日报",
						"Report.TermChinaUnionpay");
				createUnicomReportMenu(recordRoot, xmlDoc, "14113", "营业应收款日报",
						"Report.TradeAccount");
				createUnicomReportMenu(recordRoot, xmlDoc, "14114", "交易量日报",
						"Report.TradeVolume");
				content.appendChild(root);

				root = this.getXmlNode(xmlDoc, "function", "14200", "报表管理", "",
						"", "default", "");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				createReportMenu(recordRoot, xmlDoc, "14201", "登录用户报表",
						"Report.LoginUserReport");
				createReportMenu(recordRoot, xmlDoc, "14202", "访问用户报表",
						"Report.VisitUsers");
				createReportMenu(recordRoot, xmlDoc, "14213", "业务量分析报表",
						"Report.Transactions");
				createReportMenu(recordRoot, xmlDoc, "14214", "应收款报表",
						"Report.Receivable");
				createReportMenu(recordRoot, xmlDoc, "14215", "业务点击率报表",
						"Report.BusinessVisit");
				createReportMenu(recordRoot, xmlDoc, "14216", "设备开机率报表",
						"Report.UseRate");
				createReportMenu(recordRoot, xmlDoc, "14217", "设备故障报表",
						"Report.Fault");
				createReportMenu(recordRoot, xmlDoc, "14218", "打印统计报表",
						"Report.Print");

				createUnicomReportMenu(recordRoot, xmlDoc, "14219", "营业日报表",
						"Report.Trade");
				createUnicomReportMenu(recordRoot, xmlDoc, "14220", "明细清单",
						"Report.DetailList");

				createUnicomReportMenu(recordRoot, xmlDoc, "14223",
						"电子渠道业务营业应收款报表 ", "Report.TradeArea");
				createUnicomReportMenu(recordRoot, xmlDoc, "14224",
						"终端错误交易统计计表 ", "Report.Failtics");
				createUnicomReportMenu(recordRoot, xmlDoc, "14226", "现金跨地市报表",
						"Report.CashCross");
				createUnicomReportMenu(recordRoot, xmlDoc, "14228", "交易额考核表",
						"Report.TradeCheck");
				createUnicomReportMenu(recordRoot, xmlDoc, "14229", "运营通报报表",
						"Report.OperateReport");
				createUnicomReportMenu(recordRoot, xmlDoc, "14230", "无交易量终端查询",
						"Report.QueryTradeTerm");
				createUnicomReportMenu(recordRoot, xmlDoc, "14231", "增值业务统计报表",
						"Report.Vas");
				createUnicomReportMenu(recordRoot, xmlDoc, "14232", "发票打印明细查询",
						"Report.Invoice");
				createUnicomReportMenu(recordRoot, xmlDoc, "14233", "发票统计报表",
						"Report.Statistic");
				createUnicomReportMenu(recordRoot, xmlDoc, "14234", "营业厅明细报表",
						"Report.OrgDetail");
				createUnicomReportMenu(recordRoot, xmlDoc, "14235", "选号报表统计",
						"Report.SelNum");
				content.appendChild(root);

				root = this.getXmlNode(xmlDoc, "function", "14300", "已停用报表",
						"", "", "default", "");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				createUnicomReportMenu(recordRoot, xmlDoc, "14301", "营业款报表",
						"Report.BusinessTurnOver");
				createUnicomReportMenu(recordRoot, xmlDoc, "14302",
						"现金日报表(10.9…", "Report.CashDay");
				createUnicomReportMenu(recordRoot, xmlDoc, "14303", "营业应收款日报",
						"Report.TradeReceivable");
				content.appendChild(root);
			}

			// 日常运维
			if (funcId == 15) {
				this.createSubMenu(content, xmlDoc, "1501", "禁止查询打印用户号码",
						"Maint.DisabledPrintUser");

				root = this.getXmlNode(xmlDoc, "function", "15020", "用户打印次数配置",
						"", "", "default", "");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this.getXmlNode(xmlDoc, "record", "15021", "增加用户打印次数",
						"create.png",
						"Kxd.Admin.App.Pages.Maint.PrintTimes.Edit", "default",
						"");
				recordRoot.appendChild(el);
				content.appendChild(root);

				this.createSubMenu(content, xmlDoc, "1503", "机构业务开停",
						"Maint.OrgBusinessOpenClose");
				// this.createSubMenu(content, xmlDoc, "1504", "终端业务开停",
				// "Maint.TermBusinessOpenClose");
				root = this.getXmlNode(xmlDoc, "function", "15040", "终端业务开停",
						"", "", "default", "");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this.getXmlNode(xmlDoc, "record", "15041", "查看终端业务开停配置",
						"select.png",
						"Kxd.Admin.App.Pages.Term.BusinessOpenCloseTerm.List",
						"default", "");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "15042", "新增终端业务开停配置",
						"create.png",
						"Kxd.Admin.App.Pages.Term.BusinessOpenCloseTerm.List",
						"default", "");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "15043", "修改终端业务开停配置",
						"update.png",
						"Kxd.Admin.App.Pages.Term.BusinessOpenCloseTerm.List",
						"default", "");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "15044", "删除终端业务开停配置",
						"delete.png",
						"Kxd.Admin.App.Pages.Term.BusinessOpenCloseTerm.List",
						"default", "");
				recordRoot.appendChild(el);
				content.appendChild(root);

				this.createUnicomSubMenu(content, xmlDoc, "1507", "交费金额范围配置",
						"MaintConfig.PayRangeConfig");

				this.createUnicomSubMenu(content, xmlDoc, "1508", "缴费区域配置",
						"MaintConfig.CashPayConfig");

				this.createUnicomSubMenu(content, xmlDoc, "1509", "业务关闭时间配置",
						"MaintConfig.TradeTimeConfig");

				this.createUnicomSubMenu(content, xmlDoc, "1510", "交费通知短信配置",
						"MaintConfig.SendMesConfig");

				this.createUnicomSubMenu(content, xmlDoc, "1511", "查询打印业务配置",
						"MaintConfig.PrintConfig");

				// this.createUnicomSubMenu(content, xmlDoc, "1512",
				// "终端业务交易时间配置",
				// "Term.TradeTimeTerm");
				root = this.getXmlNode(xmlDoc, "function", "15120",
						"终端业务交易时间配置管理", "", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this.getXmlNode(xmlDoc, "record", "15121", "查询终端业务交易时间配置",
						"select.png",
						"Kxd.Admin.Unicom.App.Pages.Term.TradeTimeTerm.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "15122", "增加终端业务交易时间配置",
						"create.png",
						"Kxd.Admin.Unicom.App.Pages.Term.TradeTimeTerm.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "15123", "修改终端业务交易时间配置",
						"update.png",
						"Kxd.Admin.Unicom.App.Pages.Term.TradeTimeTerm.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "15124", "删除业务交易时间配置",
						"delete.png",
						"Kxd.Admin.Unicom.App.Pages.Term.TradeTimeTerm.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				this.createUnicomSubMenu(content, xmlDoc, "1513", "信用卡业务配置",
						"MaintConfig.CreditCardConfig");

				// this.createUnicomSubMenu(content, xmlDoc, "1514", "终端打印业务配置",
				// "MaintConfig.PrintConfigTerm");
				root = this.getXmlNode(xmlDoc, "function", "15140",
						"终端打印业务配置管理", "", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this.getXmlNode(xmlDoc, "record", "15141", "查询终端打印业务配置",
						"select.png",
						"Kxd.Admin.Unicom.App.Pages.Term.PrintConfigTerm.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "15142", "增加终端打印业务配置",
						"create.png",
						"Kxd.Admin.Unicom.App.Pages.Term.PrintConfigTerm.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "15143", "修改终端打印业务配置",
						"update.png",
						"Kxd.Admin.Unicom.App.Pages.Term.PrintConfigTerm.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "15144", "删除终端打印业务配置",
						"delete.png",
						"Kxd.Admin.Unicom.App.Pages.Term.PrintConfigTerm.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				this.createUnicomSubMenu(content, xmlDoc, "1515", "终端打印发票配置",
						"Maint.TermPrintInvoiceConfig");

				this.createUnicomSubMenu(content, xmlDoc, "1516", "公告提示信息",
						"Maint.UpGrade");

				// this.createUnicomSubMenu(content, xmlDoc, "1517", "手工对帐更新套餐",
				// "Maint.OptionUpdate");

				root = this.getXmlNode(xmlDoc, "function", "15170",
						"手工对帐更新套餐管理", "", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this.getXmlNode(xmlDoc, "record", "15171", "查询手工对帐更新套餐",
						"select.png",
						"Kxd.Admin.Unicom.App.Pages.Maint.OptionUpdate.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "15172", "增加手工对帐更新套餐",
						"create.png",
						"Kxd.Admin.Unicom.App.Pages.Maint.OptionUpdate.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "15173", "修改手工对帐更新套餐",
						"update.png",
						"Kxd.Admin.Unicom.App.Pages.Maint.OptionUpdate.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "15174", "删除手工对帐更新套餐",
						"delete.png",
						"Kxd.Admin.Unicom.App.Pages.Maint.OptionUpdate.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);
			}

			// 信息发布
			if (funcId == 16) {
				this.createSubMenu(content, xmlDoc, "1601", "打印广告分类",
						"Publish.PrintAdCategory");
				this.createSubMenu(content, xmlDoc, "1602", "广告分类",
						"Publish.AdCategory");
				this.createSubMenu(content, xmlDoc, "1603", "信息分类",
						"Publish.InfoCategory");

				this.createSubMenu(content, xmlDoc, "1604", "打印广告",
						"Publish.PrintAd");
				this.createSubMenu(content, xmlDoc, "1605", "机构广告",
						"Publish.OrgAd");
				// this.createSubMenu(content, xmlDoc, "1606", "终端广告",
				// "Publish.TermAd");
				root = this.getXmlNode(xmlDoc, "function", "16060", "终端广告", "",
						"", "default", "");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this.getXmlNode(xmlDoc, "record", "16061", "查看终端广告",
						"select.png", "Kxd.Admin.App.Pages.Term.AdTerm.List",
						"default", "");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "16062", "新增终端广告",
						"create.png", "Kxd.Admin.App.Pages.Term.AdTerm.List",
						"default", "");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "16063", "修改终端广告",
						"update.png", "Kxd.Admin.App.Pages.Term.AdTerm.List",
						"default", "");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "16064", "删除终端广告",
						"delete.png", "Kxd.Admin.App.Pages.Term.AdTerm.List",
						"default", "");
				recordRoot.appendChild(el);
				content.appendChild(root);
				this.createSubMenu(content, xmlDoc, "1607", "信息",
						"Publish.Info");
				root = this.getXmlNode(xmlDoc, "function", "16080", "广告与信息审核",
						"", "", "default", "");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this.getXmlNode(xmlDoc, "record", "16081", "查看广告与信息审核",
						"select.png", "Kxd.Admin.App.Pages.Publish.Audit.List",
						"default", "");
				recordRoot.appendChild(el);
				content.appendChild(root);

				root = this.getXmlNode(xmlDoc, "function", "15180", "产品上架管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"15181",
								"查询产品上架",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.Maint.VasProductReport.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				root = this.getXmlNode(xmlDoc, "function", "15190", "产品上架排序管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this.getXmlNode(xmlDoc, "record", "15191", "查询产品上架排序",
						"select.png",
						"Kxd.Admin.Unicom.App.Pages.Maint.VasProductSort.List",
						"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);
			}

			// 流程工单
			if (funcId == 17) {
				this.createUnicomSubMenu(content, xmlDoc, "1701", "申请工单",
						"ProcessWorkorder.Submit");

				// this.createUnicomSubMenu(content, xmlDoc, "1702", "分派工单",
				// "ProcessWorkorder.Forward");
				root = this.getXmlNode(xmlDoc, "function", "17020", "分派工单管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"17021",
								"查询分派工单",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.ProcessWorkorder.Forward.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"17023",
								"修改分派工单",
								"update.png",
								"Kxd.Admin.Unicom.App.Pages.ProcessWorkorder.Forward.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				// this.createUnicomSubMenu(content, xmlDoc, "1703", "受理工单",
				// "ProcessWorkorder.Accept");
				root = this.getXmlNode(xmlDoc, "function", "17030", "受理工单管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"17031",
								"查询受理工单",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.ProcessWorkorder.Accept.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"17033",
								"修改受理工单",
								"update.png",
								"Kxd.Admin.Unicom.App.Pages.ProcessWorkorder.Accept.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				// this.createUnicomSubMenu(content, xmlDoc, "1704", "处理工单",
				// "ProcessWorkorder.Handle");
				root = this.getXmlNode(xmlDoc, "function", "17040", "处理工单管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"17041",
								"查询处理工单",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.ProcessWorkorder.Handle.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"17043",
								"修改处理工单",
								"update.png",
								"Kxd.Admin.Unicom.App.Pages.ProcessWorkorder.Handle.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				// this.createUnicomSubMenu(content, xmlDoc, "1705", "验收工单",
				// "ProcessWorkorder.Acceptance");
				root = this.getXmlNode(xmlDoc, "function", "17050", "验收工单管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"17051",
								"查询验收工单",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.ProcessWorkorder.Acceptance.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"17053",
								"修改验收工单",
								"update.png",
								"Kxd.Admin.Unicom.App.Pages.ProcessWorkorder.Acceptance.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				// this.createUnicomSubMenu(content, xmlDoc, "1706", "工单查询 ",
				// "ProcessWorkorder.Query");
				root = this.getXmlNode(xmlDoc, "function", "17060", "工单查询管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"17061",
								"查询工单",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.ProcessWorkorder.Query.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);
			}

			// 应用管理
			if (funcId == 18) {
				this.createSubMenu(content, xmlDoc, "1801", "应用类别",
						"AppDeploy.AppCategory");
				this.createSubMenu(content, xmlDoc, "1802", "应用",
						"AppDeploy.App");
				this.createSubMenu(content, xmlDoc, "1803", "业务分类",
						"AppDeploy.BusinessCategory");
				this.createSubMenu(content, xmlDoc, "1804", "收费渠道",
						"AppDeploy.PayWay");
				this.createSubMenu(content, xmlDoc, "1805", "收费项目",
						"AppDeploy.PayItem");

				root = this.getXmlNode(xmlDoc, "function", "18070", "接口管理", "",
						"", "default", "");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this.getXmlNode(xmlDoc, "record", "18071", "查询接口",
						"select.png",
						"Kxd.Admin.App.Pages.AppDeploy.Comminterface.List",
						"default", "");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "18072", "增加接口",
						"create.png",
						"Kxd.Admin.App.Pages.AppDeploy.Comminterface.Edit",
						"default", "");
				recordRoot.appendChild(el);
				el = this.getXmlNode(xmlDoc, "record", "18073", "修改接口",
						"update.png",
						"Kxd.Admin.App.Pages.AppDeploy.Comminterface.List",
						"default", "");
				recordRoot.appendChild(el);
				content.appendChild(root);

				this.createSubMenu(content, xmlDoc, "1808", "业务管理",
						"AppDeploy.Business");
				this.createSubMenu(content, xmlDoc, "1809", "交易",
						"AppDeploy.TradeCode");
				this.createSubMenu(content, xmlDoc, "1810", "业务页面管理",
						"AppDeploy.PageElement");
			}

			// 驱动管理
			if (funcId == 19) {
				this.createSubMenu(content, xmlDoc, "1901", "告警分类",
						"Driver.AlarmCategory");
				this.createSubMenu(content, xmlDoc, "1902", "模块驱动",
						"Driver.DeviceTypeDriver");
				this.createSubMenu(content, xmlDoc, "1903", "模块",
						"Driver.DeviceType");
				this.createSubMenu(content, xmlDoc, "1904", "设备驱动",
						"Driver.DeviceDriver");
				this.createSubMenu(content, xmlDoc, "1905", "设备",
						"Driver.Device");
				this.createSubMenu(content, xmlDoc, "1906", "终端型号",
						"Driver.TermType");
			}

			// 文件服务
			if (funcId == 20) {
				this.createSubMenu(content, xmlDoc, "2001", "文件分类",
						"FileService.FileCategory");
				this.createSubMenu(content, xmlDoc, "2002", "文件主机",
						"FileService.FileHost");
				this.createSubMenu(content, xmlDoc, "2003", "文件属主",
						"FileService.FileOwner");
				this.createSubMenu(content, xmlDoc, "2004", "文件用户",
						"FileService.FileUser");
			}

			// 交易明细
			if (funcId == 22) {
				root = this.getXmlNode(xmlDoc, "function", "22010", "交易明细", "",
						"", "default", "");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				// createReportMenu(recordRoot, xmlDoc, "22011", "交易处理(标准)",
				// "TradeManage.Trade");
				// createReportMenu(recordRoot, xmlDoc, "22012", "退款明细处理(标准)",
				// "TradeManage.Refundlog");
				createUnicomReportMenu(recordRoot, xmlDoc, "22013", "交易处理",
						"TradeManage.Trade");
				createUnicomReportMenu(recordRoot, xmlDoc, "22014", "退款明细处理",
						"TradeManage.Refundlog");
				createUnicomReportMenu(recordRoot, xmlDoc, "22015", "应返销退款明细",
						"TradeManage.MustReturnMoneyDetail");
				createUnicomReportMenu(recordRoot, xmlDoc, "22016",
						"山东交易推荐人交易明细查询", "TradeManage.UnicomExttrade");
				createUnicomReportMenu(recordRoot, xmlDoc, "22017", "抽奖人明细清单",
						"TradeManage.VasPrizes");
				createUnicomReportMenu(recordRoot, xmlDoc, "22018", "增值业务明细查询",
						"TradeManage.Vas");
				createUnicomReportMenu(recordRoot, xmlDoc, "22019", "售卡信息查询",
						"Maint.ShowCardInfo.List");
				createUnicomReportMenu(recordRoot, xmlDoc, "22020", "导出银联退款文件",
						"Maint.ExportRefund.List");
				content.appendChild(root);
			}

			// **************************************************************************************************************************
			// 系统配置管理
			if (funcId == 21) {
				// this.createSubMenu(content, xmlDoc, "2101", "广告文件类型", "");
				// this.createUnicomSubMenu(content, xmlDoc, "2102", "信用卡发卡行",
				// "SystemConfig.IsSuerCardConfig");
				this.createSubMenu(content, xmlDoc, "2103", "打印类型",
						"SystemConfig.PrintType");
				this.createUnicomSubMenu(content, xmlDoc, "2104", "公告类型",
						"SystemConfig.NoticeType");
				this.createSubMenu(content, xmlDoc, "2105", "发票模板配置",
						"SystemConfig.InvoiceTemplate");
				this.createSubMenu(content, xmlDoc, "2106", "发票配置",
						"SystemConfig.InvoiceConfig");
			}

			// 我的帐户
			if (funcId == 24) {
				root = this.getXmlNode(xmlDoc, "function", "24010", "我的帐户", "",
						"", "default", "");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this.getXmlNode(xmlDoc, "record", "24011", "修改密码",
						"update.png",
						"Kxd.Admin.App.Pages.MyAccount.ModifyPwd", "default",
						"");
				recordRoot.appendChild(el);

				el = this.getXmlNode(xmlDoc, "record", "24012", "修改我的资料",
						"update.png",
						"Kxd.Admin.App.Pages.MyAccount.EditMyInfo", "default",
						"");
				recordRoot.appendChild(el);

				el = this.getXmlNode(xmlDoc, "record", "24013", "查看我的权限",
						"select.png",
						"Kxd.Admin.App.Pages.MyAccount.MyUserRights",
						"default", "");
				recordRoot.appendChild(el);

				content.appendChild(root);

			}

			// 发货工单
			if (funcId == 27) {
				// this.createUnicomSubMenu(content, xmlDoc, "2701", "工单发货查询",
				// "TermDeployManage.ReceiverSearch");
				root = this.getXmlNode(xmlDoc, "function", "27010", "工单发货查询管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27011",
								"查询工单发货查询",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.ReceiverSearch.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				// this.createUnicomSubMenu(content, xmlDoc, "2702", "工单安装查询",
				// "TermDeployManage.InstallSearch");
				root = this.getXmlNode(xmlDoc, "function", "27020", "工单安装查询管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27021",
								"查询工单安装查询",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.InstallSearch.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				this.createUnicomSubMenu(content, xmlDoc, "2703", "合同管理",
						"TermDeployManage.Contract");
				this.createUnicomSubMenu(content, xmlDoc, "2704", "申请发货",
						"TermDeployManage.ApplyTermOrder");
				// this.createUnicomSubMenu(content, xmlDoc, "2705", "提交发货单",
				// "TermDeployManage.SubmitTermOrder");
				root = this.getXmlNode(xmlDoc, "function", "27050", "提交发货单管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27051",
								"查询提交发货单",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.SubmitTermOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27053",
								"修改提交发货单",
								"update.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.SubmitTermOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				// this.createUnicomSubMenu(content, xmlDoc, "2706", "收货确认",
				// "TermDeployManage.ReceiverTermOrder");
				root = this.getXmlNode(xmlDoc, "function", "27060", "收货确认管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27061",
								"查询收货确认",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.ReceiverTermOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27063",
								"修改收货确认",
								"update.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.ReceiverTermOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				// this.createUnicomSubMenu(content, xmlDoc, "2707", "申请安装",
				// "TermDeployManage.ApplyInstallOrder");
				root = this.getXmlNode(xmlDoc, "function", "27070", "申请安装管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27071",
								"查询申请安装",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.ApplyInstallOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27072",
								"增加申请安装",
								"create.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.ApplyInstallOrder.Add",
								"unicom", "app");
				recordRoot.appendChild(el);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27073",
								"修改申请安装",
								"update.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.ApplyInstallOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27074",
								"删除申请安装",
								"delete.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.ApplyInstallOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				// this.createUnicomSubMenu(content, xmlDoc, "2708", "安装信息注册",
				// "TermDeployManage.RegeditInstallOrder");
				root = this.getXmlNode(xmlDoc, "function", "27080", "安装信息注册管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27081",
								"查询安装信息注册",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.RegeditInstallOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27083",
								"修改安装信息注册",
								"update.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.RegeditInstallOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				// this.createUnicomSubMenu(content, xmlDoc, "2709", "终端安装",
				// "TermDeployManage.TermInstallOrder");
				root = this.getXmlNode(xmlDoc, "function", "27090", "终端安装管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27091",
								"查询终端安装",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.TermInstallOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27093",
								"修改终端安装",
								"update.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.TermInstallOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);

				// this.createUnicomSubMenu(content, xmlDoc, "2710", "安装确认",
				// "TermDeployManage.TermInstallConfirmOrder");
				root = this.getXmlNode(xmlDoc, "function", "27100", "安装确认管理",
						"", "", "unicom", "app");
				recordRoot = xmlDoc.createElement("records");
				root.appendChild(recordRoot);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27101",
								"查询安装确认",
								"select.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.TermInstallConfirmOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				el = this
						.getXmlNode(
								xmlDoc,
								"record",
								"27103",
								"修改安装确认",
								"update.png",
								"Kxd.Admin.Unicom.App.Pages.TermDeployManage.TermInstallConfirmOrder.List",
								"unicom", "app");
				recordRoot.appendChild(el);
				content.appendChild(root);
			}

		} finally {
			// context.close();
		}
	}

	private void createSubMenu(Element content, Document xmlDoc, String id,
			String Name, String pageClass) {
		pageClass = "Kxd.Admin.App.Pages." + pageClass;
		Element root, recordRoot, el;
		root = this.getXmlNode(xmlDoc, "function", id + "0", Name + "管理", "",
				"", "default", "");
		recordRoot = xmlDoc.createElement("records");
		root.appendChild(recordRoot);
		el = this.getXmlNode(xmlDoc, "record", id + "1", "查询" + Name,
				"select.png", pageClass + ".List", "default", "");
		recordRoot.appendChild(el);
		el = this.getXmlNode(xmlDoc, "record", id + "2", "增加" + Name,
				"create.png", pageClass + ".Edit", "default", "");
		recordRoot.appendChild(el);
		el = this.getXmlNode(xmlDoc, "record", id + "3", "修改" + Name,
				"update.png", pageClass + ".List", "default", "");
		recordRoot.appendChild(el);
		el = this.getXmlNode(xmlDoc, "record", id + "4", "删除" + Name,
				"delete.png", pageClass + ".List", "default", "");
		recordRoot.appendChild(el);
		content.appendChild(root);
	}

	private void createReportMenu(Element content, Document xmlDoc, String id,
			String Name, String pageClass) {
		pageClass = "Kxd.Admin.App.Pages." + pageClass;
		Element el;
		el = this.getXmlNode(xmlDoc, "record", id, Name, "report.png",
				pageClass, "default", "");
		content.appendChild(el);
	}

	private void createUnicomReportMenu(Element content, Document xmlDoc,
			String id, String Name, String pageClass) {
		pageClass = "Kxd.Admin.Unicom.App.Pages." + pageClass;
		Element el;
		el = this.getXmlNode(xmlDoc, "record", id, Name, "report.png",
				pageClass, "unicom", "app");
		content.appendChild(el);
	}

	private void createUnicomSubMenu(Element content, Document xmlDoc,
			String id, String Name, String pageClass) {
		pageClass = "Kxd.Admin.Unicom.App.Pages." + pageClass;
		Element root, recordRoot, el;
		root = this.getXmlNode(xmlDoc, "function", id + "0", Name + "管理", "",
				"", "unicom", "app");
		recordRoot = xmlDoc.createElement("records");
		root.appendChild(recordRoot);
		el = this.getXmlNode(xmlDoc, "record", id + "1", "查询" + Name,
				"select.png", pageClass + ".List", "unicom", "app");
		recordRoot.appendChild(el);
		el = this.getXmlNode(xmlDoc, "record", id + "2", "增加" + Name,
				"create.png", pageClass + ".Edit", "unicom", "app");
		recordRoot.appendChild(el);
		el = this.getXmlNode(xmlDoc, "record", id + "3", "修改" + Name,
				"update.png", pageClass + ".List", "unicom", "app");
		recordRoot.appendChild(el);
		el = this.getXmlNode(xmlDoc, "record", id + "4", "删除" + Name,
				"delete.png", pageClass + ".List", "unicom", "app");
		recordRoot.appendChild(el);
		content.appendChild(root);
	}

}