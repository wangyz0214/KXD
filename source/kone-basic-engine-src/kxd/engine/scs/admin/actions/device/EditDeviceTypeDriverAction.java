package kxd.engine.scs.admin.actions.device;

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
import kxd.remote.scs.beans.BaseDeviceTypeDriver;
import kxd.remote.scs.interfaces.DeviceTypeDriverBeanRemote;
import kxd.util.KoneException;
import kxd.util.fileupload.FileUploadRequestWrapper;

import org.apache.commons.fileupload.FileItem;

public class EditDeviceTypeDriverAction extends EditAction {
	private BaseDeviceTypeDriver driver;
	boolean added;
	String oldFilename;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEdit(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			getDriver().setId(request.getParameterIntDef("id", null));
			getDriver().setDeviceTypeDriverDesp(request.getParameter("desp"));
			oldFilename = request.getParameterDef("filename", null);
			added = getDriver().getId() == null;
		} else
			getDriver(request.getParameterIntDef("id", null));
	}

	private void addOrEdit(HttpRequest request) throws Throwable {
		Collection<FileItem> files = ((FileUploadRequestWrapper) request
				.getRequest()).getFirstFileItemList();
		FileItem fileItem = null;
		if (files != null && files.size() > 0) {
			fileItem = (FileItem) files.iterator().next();
		}
		if (added
				&& (fileItem == null || fileItem.getName() == null || fileItem
						.getName().trim().isEmpty())) {
			throw new KoneException("必须提供上传的文件");
		}
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceTypeDriverBean",
					DeviceTypeDriverBeanRemote.class);
			if (fileItem != null && fileItem.getName() != null
					&& fileItem.getName().trim().length() > 0) {
				String fileName = fileItem.getName();
				fileName = fileName.replace("\\", "/");
				fileName = fileName.replace("//", "/").toLowerCase();
				int index = fileName.lastIndexOf("/");
				if (index >= 0)
					fileName = fileName.substring(index + 1);
				FileUploadProcessor processor = new FileUploadProcessor(
						AdminTradeExecutor.fileUserId,
						AdminTradeExecutor.filePwd, (short) 2);
				if (added) {
					getDriver().setDriverFile(fileName);
					getDriver().setId(
							bean.add(
									((AdminSessionObject) session)
											.getLoginUser().getUserId(),
									getDriver()).getKey());
					processor.uploadFile(((AdminSessionObject) session)
							.getLoginUser().getUserId(), fileName, fileItem
							.get());
				} else {
					if (oldFilename == null || oldFilename.trim().length() == 0)
						throw new KoneException("必须提供旧文件");
					getDriver().setDriverFile(fileName);
					bean.edit(((AdminSessionObject) session).getLoginUser()
							.getUserId(), getDriver());
					processor.updateFile(((AdminSessionObject) session)
							.getLoginUser().getUserId(), oldFilename, fileName,
							fileItem.get());
				}
			} else
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), getDriver());
		} finally {
			if (context != null)
				context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + driver.getIdString() + "',desp:'"
				+ driver.getDeviceTypeDriverDesp() + "',columns:[";
		script += "'" + driver.getIdString() + "',";
		script += "'" + driver.getDeviceTypeDriverDesp() + "',";
		if (driver.getDriverFile() == null)
			script += "null";
		else
			script += "'" + driver.getDriverFile() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	private void getDriver(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceTypeDriverBean",
					DeviceTypeDriverBeanRemote.class);
			driver = bean.find(id);
			oldFilename = driver.getDriverFile();
		} finally {
			context.close();
		}
	}

	public BaseDeviceTypeDriver getDriver() {
		if (driver == null)
			driver = new BaseDeviceTypeDriver();
		return driver;
	}

	public void setDriver(BaseDeviceTypeDriver driver) {
		this.driver = driver;
	}

	@Override
	public int getEditRight() {
		return UserRight.DEVICETYPEDRIVER_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.DEVICETYPEDRIVER;
	}

	public String getOldFilename() {
		return oldFilename;
	}

	public void setOldFilename(String oldFilename) {
		this.oldFilename = oldFilename;
	}
}
