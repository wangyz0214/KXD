package kxd.engine.scs.admin.actions.adinfo;

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
import kxd.remote.scs.beans.BaseInfoFile;
import kxd.remote.scs.interfaces.InfoBeanRemote;
import kxd.util.KoneException;
import kxd.util.fileupload.FileUploadRequestWrapper;

import org.apache.commons.fileupload.FileItem;

public class EditInfoFileAction extends EditAction {
	private BaseInfoFile infoFile;
	private boolean submitted;

	public boolean isSubmitted() {
		return submitted;
	}

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			submitted = true;
			addOrEditInfo(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		infoFile = getInfoFile();
		infoFile.setInfoId(request.getParameterLong("infoid"));
		infoFile.setFileType(request.getParameterInt("filetype"));
	}

	private void addOrEditInfo(HttpRequest request) throws Throwable {
		Collection<FileItem> files = ((FileUploadRequestWrapper) request
				.getRequest()).getFirstFileItemList();
		FileItem fileItem = null;
		if (files != null && files.size() > 0) {
			fileItem = (FileItem) files.iterator().next();
		}
		if ((fileItem == null || fileItem.getName() == null || fileItem
				.getName().trim().isEmpty())) {
			throw new KoneException("必须提供上传的文件");
		}
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-infoBean", InfoBeanRemote.class);
			if ((fileItem != null && fileItem.getName() != null && !fileItem
					.getName().trim().isEmpty())) {
				BaseInfoFile f = getInfoFile();
				String fileName = fileItem.getName();
				fileName = fileName.replace("\\", "/");
				fileName = fileName.replace("//", "/").toLowerCase();
				int index = fileName.lastIndexOf("/");
				if (index >= 0)
					fileName = fileName.substring(index + 1);
				f.setFileName(fileName);
				fileName = bean.addFile(((AdminSessionObject) session)
						.getLoginUser().getUserId(), getInfoFile());
				FileUploadProcessor processor = new FileUploadProcessor(
						AdminTradeExecutor.fileUserId,
						AdminTradeExecutor.filePwd, (short) 6);
				String path = "files/" + getInfoFile().getInfoId() + "/"
						+ getInfoFile().getFileType() + "/";
				getInfoFile().setFileName(path + fileName);
				String root = processor.getFileHost().getRealHttpUrlRoot();
				if (root != null)
					root = root.trim();
				if (root != null && !root.isEmpty())
					getInfoFile().setUrl(root + "/6/" + path + fileName);
				else
					getInfoFile().setUrl(
							"/fileService?filecategory=6&filename=" + path
									+ fileName);
				processor.uploadFile(((AdminSessionObject) session)
						.getLoginUser().getUserId(), path + fileName, fileItem
						.get());
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	public String getSuccessScript() {
		return "";
	}

	public BaseInfoFile getInfoFile() {
		if (infoFile == null) {
			infoFile = new BaseInfoFile();
		}
		return infoFile;
	}

	@Override
	public int getEditRight() {
		return UserRight.INFO_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.INFO;
	}
}
