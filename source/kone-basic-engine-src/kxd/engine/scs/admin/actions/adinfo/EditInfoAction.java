package kxd.engine.scs.admin.actions.adinfo;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedFileCategory;
import kxd.engine.cache.beans.sts.CachedFileHost;
import kxd.engine.fileservice.FileDownloadProcessor;
import kxd.engine.fileservice.FileUploadProcessor;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.AdminTradeExecutor;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseInfoCategory;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.adinfo.EditedInfo;
import kxd.remote.scs.interfaces.InfoBeanRemote;
import kxd.util.DataUnit;

public class EditInfoAction extends EditAction {
	private EditedInfo info;
	boolean added;
	String infoText;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditInfo(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		added = request.getParameterBooleanDef("added", false);
		info = getInfo();
		info.setId(request.getParameterLongDef("id", null));
		BaseOrg b = new BaseOrg(request.getParameterIntDef("orgid", null));
		info.setOrg(b);
		b.setOrgName(request.getParameterDef("orgdesp", null));
		if (!isFormSubmit) {
			added = info.getInfoId() == null;
			if (added) {
				LoopNamingContext context = new LoopNamingContext("db");
				try {
					InfoBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB, "kxd-ejb-infoBean",
							InfoBeanRemote.class);
					info.setId(bean.getNextInfoId());
				} finally {
					if (context != null)
						context.close();
				}
			} else {
				LoopNamingContext context = new LoopNamingContext("db");
				try {
					InfoBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB, "kxd-ejb-infoBean",
							InfoBeanRemote.class);
					info = bean.find(info.getId());
				} finally {
					if (context != null)
						context.close();
				}
				FileDownloadProcessor processor = new FileDownloadProcessor(
						AdminTradeExecutor.fileUserId,
						AdminTradeExecutor.filePwd, (short) 6);
				infoText = new String(processor.downloadFile(0,
						info.getFileName(), 0, 0).getValue())
						.replace("$_contextpath_", request.getRequest()
								.getContextPath());
				if (infoText
						.startsWith("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"))
					infoText = infoText
							.substring("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
									.length());
			}
		}
	}

	private String urlRoot = null;

	public String getUrlRoot() {
		if (urlRoot != null)
			return urlRoot;
		CachedFileCategory f = CacheHelper.fileCategoryMap.get((short) 6);
		CachedFileHost fh = CacheHelper.fileHostMap.get(f.getFileHost());
		String root = fh.getRealHttpUrlRoot();
		if (root != null)
			root = root.trim();
		if (root != null && !root.isEmpty())
			return root + "/6/";
		else
			return "/fileService?filecategory=6&filename=";
	}

	private void addOrEditInfo(HttpRequest request) throws Throwable {
		infoText = request.getParameter("webeditor");
		if (!infoText
				.startsWith("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"))
			infoText = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
					+ infoText;

		info.setTitle(request.getParameter("title"));
		info.setSummary(request.getParameter("webeditor_summary"));
		info.setInfoCategory(new BaseInfoCategory(request
				.getParameterShort("category")));
		info.getInfoCategory().setInfoCategoryDesp(
				request.getParameter("category_desp"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-infoBean", InfoBeanRemote.class);

			String fileName = info.getIdString() + ".html";
			FileUploadProcessor processor = new FileUploadProcessor(
					AdminTradeExecutor.fileUserId, AdminTradeExecutor.filePwd,
					(short) 6);
			String path = getInfo().getOrg().getIdString() + "/"
					+ getInfo().getInfoCategory().getIdString() + "/";
			String root = processor.getFileHost().getRealHttpUrlRoot();
			if (root != null)
				root = root.trim();
			if (root != null && !root.isEmpty())
				getInfo().setUrl(root + "/6/" + path + fileName);
			else
				getInfo().setUrl(
						"/fileService?filecategory=6&filename=" + path
								+ fileName);
			if (added) {
				getInfo().setFileName(fileName);
				getInfo().setPublishTime(new Date());
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), getInfo());
				processor.uploadFile(((AdminSessionObject) session)
						.getLoginUser().getUserId(), path + fileName, infoText
						.getBytes());
			} else {
				info.setPublishTime(new Date());
				getInfo().setFileName(fileName);
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), getInfo());
				request.getRequest().getContextPath();
				processor.updateFile(((AdminSessionObject) session)
						.getLoginUser().getUserId(), path + fileName, path
						+ fileName, infoText.getBytes());
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + info.getIdString() + "',desp:'"
				+ info.getTitle() + "',columns:[";
		script += "'<a href=\"#\" onclick=\"ajax_table.showEditDialog(arguments[0]);return false;\">"
				+ "编辑</a> <a href=\"#\" onclick=\"ajax_table.deleteSelected(arguments[0]);return false;\">"
				+ "删除</a> <a href=\"#\" onclick=\"onlineReq(arguments[0]);return false;\">申请上线</a>',";
		script += "'" + info.getTitle() + "',";
		script += "'"
				+ DataUnit.formatDateTime(info.getPublishTime(), "MM-dd HH:mm")
				+ "',";
		script += "'" + info.getOrg().getOrgName() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedInfo getInfo() {
		if (info == null) {
			info = new EditedInfo();
		}
		return info;
	}

	public void setInfo(EditedInfo info) {
		this.info = info;
	}

	@Override
	public int getEditRight() {
		return UserRight.INFO_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.INFO;
	}

	public String getInfoText() {
		return infoText;
	}

	public void setInfoText(String infoText) {
		this.infoText = infoText;
	}

}
