package kxd.engine.scs.admin.actions.device;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedFileContentType;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.remote.scs.util.AppException;
import kxd.util.DataUnit;

public class EditDownServerLogAction extends EditAction {

	String termids, dayTime;
	InputStream in;
	OutputStream out;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (termids != null) {
			downServerLog(response);
		}
		return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		termids = request.getParameterDef("termids", null);
		dayTime = request.getParameterDef("dayTime", null);
	}

	private void downServerLog(HttpServletResponse response) throws Exception {
		if (termids != null) {
			String extName = "zip";
			String contenType = "text/plain";// 默认类型
			if (!extName.isEmpty()) {
				CachedFileContentType fc = CacheHelper.fileContentTypeMap
						.get(extName);
				if (fc != null)
					contenType = fc.getContentType();
				if (contenType == null || contenType.isEmpty())
					contenType = "text/plain";// 默认类型
			}
			File f = new File("/mnt/nfs1/upload/" + termids + "/log/" + dayTime
					+ ".zip");
			if (f.exists()) {
				int size = (int) f.length();
				response.setContentLength(size);// 设置文件长度
				response.setContentType(contenType);// 设置文件内容
				response.setHeader("Content-Disposition",
						"attachment;filename=" + dayTime + ".zip");
				in = new FileInputStream(f);
				byte[] data = new byte[size];
				int count = 0;
				while (count < data.length) {
					count += in.read(data, count, data.length - count);
				}
				out = response.getOutputStream();
				out.write(data);
			} else
				throw new AppException("指定的文件在远程服务器上不存在，请先上传");
		}
		out.flush();
		out.close();
		in.close();
	}

	public String getDayTime() {
		if(dayTime==null)
			dayTime= DataUnit.formatDateTime(new Date(), "yyyy-MM-dd");
		return dayTime; 
	}

	@Override
	public int getEditRight() {
		return UserRight.TERMMONITOR;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TERMMONITOR;
	}

}
