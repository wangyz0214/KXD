package kxd.engine.fileservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminTradeExecutor;
import kxd.remote.scs.beans.BaseFileItem;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.util.StringUnit;
import kxd.util.stream.Stream;

import org.apache.log4j.Logger;

/**
 * 支持断点上传的Servlet
 * 
 * @author zhaom
 * 
 */
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger(FileUploadServlet.class);
	/**
	 * 文件上载的管理器，可外部重定向，实现个性化的管理配置
	 */
	static public Class<FileUploadManager> fileUploadManagerClazz = FileUploadManager.class;

	public FileUploadServlet() {

		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Enumeration<?> en = request.getHeaderNames();
		while (en.hasMoreElements()) {
			String n = (String) en.nextElement();
			System.out.println(n + ": " + request.getHeader(n));
		}
		try {
			LoginUser user = (LoginUser) request.getSession().getAttribute(
					"loginUser");
			if (user == null || !user.isLogined()) {
				response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
						"尚未登录");
				return;
			}
			long userId = user.getId();
			Stream stream = new Stream(request.getInputStream(), null);
			int len = stream.readInt(false, 3000);
			for (int i = 0; i < len; i++) {
				String n = stream.readPacketIntString(false, 3000);
				String v = stream.readPacketIntString(false, 3000);
				request.setAttribute(n, v);
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			while ((len = request.getInputStream().read(data)) > 0) {
				out.write(data, 0, len);
			}
			FileUploadProcessor processor = new FileUploadProcessor(
					AdminTradeExecutor.fileUserId, AdminTradeExecutor.filePwd,
					Short.valueOf(request.getParameter("filecategory")));
			FileUploadManager manager = fileUploadManagerClazz.newInstance();
			BaseFileItem item = new BaseFileItem();
			String fileName = request.getParameter("uploadfilename").replace(
					"\\", "/");
			int index = fileName.lastIndexOf("/");
			if (index >= 0)
				fileName = fileName.substring(index + 1);
			if (request.getParameter("savepath") != null) {
				item.setSavePath(request.getParameter("savepath"));
			}
			item.setOriginalFileName(fileName);
			item.setFileCategoryId(processor.fileCategory.getId());
			item.setFileOwnerId(processor.userOwner.getId());
			item.setFileHostId(processor.getFileHost().getId());
			item.setMd5(request.getHeader("md5-digest"));

			manager.setSavePath(processor, request, item); // 重新设置存储路径
			int startPos = request.getIntHeader("startPos");
			boolean complete = request.getIntHeader("uploadcompleted") != 0;
			String resp = null;
			processor.appendFile(out.toByteArray(), userId, startPos, complete,
					item);
			if (startPos <= 0)
				resp = manager.addOrEdit(userId, complete, processor, request,
						item);
			else if (complete)
				resp = manager.uploadComplete(userId, processor, request, item);

			response.setContentType("text/plain");
			out.close();
			out = new ByteArrayOutputStream();
			if (resp != null)
				out.write(("{\"success\":true,\"data\":" + resp + "}")
						.getBytes());
			else
				out.write(("{\"success\":true}").getBytes());
			byte[] b = out.toByteArray();
			response.setContentLength(b.length);
			response.getOutputStream().write(b);
			stream = null;
			b = null;
			logger.debug("上载文件[" + request.getParameter("filecategory") + ","
					+ request.getParameter("uploadfilename") + "]成功");
		} catch (Throwable e) {
			logger.debug("上载失败", e);
			response.setContentType("text/plain");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(("{\"success\":\"false\",\"message\":\""
					+ StringUnit.getExceptionMessage(e).replace("\"", "'") + "\"}")
					.getBytes());
			byte[] b = out.toByteArray();
			response.setContentLength(b.length);
			response.getOutputStream().write(b);
			b = null;
		}
	}
}
