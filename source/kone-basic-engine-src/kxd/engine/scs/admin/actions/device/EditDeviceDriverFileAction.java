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
import kxd.remote.scs.beans.BaseDeviceDriverFile;
import kxd.remote.scs.interfaces.DeviceDriverBeanRemote;
import kxd.util.KoneException;
import kxd.util.fileupload.FileUploadRequestWrapper;

import org.apache.commons.fileupload.FileItem;

public class EditDeviceDriverFileAction extends EditAction {
	private BaseDeviceDriverFile deviceDriverFile;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditDeviceDriverFile(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		deviceDriverFile = getDeviceDriverFile();
		deviceDriverFile.setId(request.getParameterIntDef("id", null));
		deviceDriverFile.setDeviceDriverId(request
				.getParameterInt("devicedriverid"));
	}

	private void addOrEditDeviceDriverFile(HttpRequest request)
			throws Throwable {
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
			DeviceDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceDriverBean",
					DeviceDriverBeanRemote.class);
			String fileName = fileItem.getName();
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
			FileUploadProcessor processor = new FileUploadProcessor(
					AdminTradeExecutor.fileUserId, AdminTradeExecutor.filePwd,
					(short) 3);
			getDeviceDriverFile().setFileName(fileName);
			bean.addDeviceDriverFile(((AdminSessionObject) session)
					.getLoginUser().getUserId(), getDeviceDriverFile());
			processor.uploadFile(((AdminSessionObject) session).getLoginUser()
					.getUserId(), fileName, fileItem.get());
		} finally {
			if (context != null)
				context.close();
		}
	}

	public String getSuccessScript() {
		return "";
	}

	public BaseDeviceDriverFile getDeviceDriverFile() {
		if (deviceDriverFile == null) {
			deviceDriverFile = new BaseDeviceDriverFile();
		}
		return deviceDriverFile;
	}

	public void setDeviceDriverFile(BaseDeviceDriverFile deviceDriverFile) {
		this.deviceDriverFile = deviceDriverFile;
	}

	@Override
	public int getEditRight() {
		return UserRight.DEVICEDRIVER_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.DEVICEDRIVER;
	}
}
