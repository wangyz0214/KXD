package kxd.engine.scs.admin.actions.appdeploy;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.fileservice.FileUploadProcessor;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.AdminTradeExecutor;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseAppFile;
import kxd.remote.scs.interfaces.AppBeanRemote;
import kxd.util.KoneException;
import kxd.util.fileupload.FileUploadRequestWrapper;

import org.apache.commons.fileupload.FileItem;

public class EditAppFileAction extends EditAction {
	private BaseAppFile appFile;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditAppFile(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		appFile = getAppFile();
		appFile.setAppFileId(request.getParameterIntDef("id", null));
		appFile.setAppFilename(request.getParameterDef("filename", null));
		BaseApp b = new BaseApp(request.getParameterInt("appid"));
		b.setAppDesp(request.getParameter("appdesp"));
		appFile.setApp(b);
		added = appFile.getId() == null;
	}

	private void addOrEditAppFile(HttpRequest request) throws Throwable {
		Collection<FileItem> files = ((FileUploadRequestWrapper) request
				.getRequest()).getFirstFileItemList();
		FileItem fileItem = null;
		if (files != null && files.size() > 0) {
			fileItem = (FileItem) files.iterator().next();
		}
		if (fileItem == null || fileItem.getName() == null
				|| fileItem.getName().trim().isEmpty()) {
			throw new KoneException("必须提供上传的文件");
		}
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appBean", AppBeanRemote.class);
			String fileName = fileItem.getName();
			fileName = fileName.replace("\\", "/");
			fileName = fileName.replace("//", "/").toLowerCase();
			int index = fileName.lastIndexOf("/");
			if (index >= 0)
				fileName = fileName.substring(index + 1);
			FileUploadProcessor processor = new FileUploadProcessor(
					AdminTradeExecutor.fileUserId, AdminTradeExecutor.filePwd,
					(short) 1);
			if (added) {
				getAppFile().setAppFilename(fileName);
				bean.addAppFile(((AdminSessionObject) session).getLoginUser()
						.getUserId(), getAppFile());
				processor.uploadFile(((AdminSessionObject) session)
						.getLoginUser().getUserId(), getAppFile().getApp()
						.getIdString() + "/" + fileName, fileItem.get());
			} else {
				getAppFile().setAppFilename(fileName);
				String oldFile = bean.editAppFile(
						((AdminSessionObject) session).getLoginUser()
								.getUserId(), getAppFile());
				processor.updateFile(((AdminSessionObject) session)
						.getLoginUser().getUserId(), oldFile, getAppFile()
						.getApp().getIdString() + "/" + fileName, fileItem
						.get());
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + appFile.getIdString() + "',desp:'"
				+ appFile.getAppFilename() + "',columns:[";
		script += "'" + appFile.getIdString() + "',";
		script += "'" + appFile.getAppFilename() + "',";
		script += "'" + appFile.getApp().getAppId() + "',";
		script += "'" + appFile.getApp().getAppDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseAppFile getAppFile() {
		if (appFile == null) {
			appFile = new BaseAppFile();
		}
		return appFile;
	}

	public void setAppFile(BaseAppFile appFile) {
		this.appFile = appFile;
	}

	@Override
	public int getEditRight() {
		return UserRight.APP_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.APP;
	}
}
