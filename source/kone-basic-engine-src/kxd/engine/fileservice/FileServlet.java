package kxd.engine.fileservice;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedFileContentType;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.util.KeyValue;

import org.apache.log4j.Logger;

public class FileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger(FileServlet.class);

	public FileServlet() {

		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			LoginUser user = (LoginUser) request.getSession().getAttribute(
					"loginUser");
			if (user == null || !user.isLogined()) {
				response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
						"尚未登录");
				return;
			}
			long lastModified = request.getDateHeader("If-Modified-Since");
			String username = "system", userpwd = "", filename = null, startpos = null, endpos = null;
			filename = request.getParameter("filename");
			short filecategory = Short.valueOf(request
					.getParameter("filecategory"));
			startpos = request.getParameter("startpos");
			endpos = request.getParameter("endpos");

			int istartpos = 0, iendpos = 0;

			String extName = "";
			if (filename == null || filename.isEmpty()) {
				response.setStatus(404); // 无法找到指定位置的资源。
				return;
			}
			// 解决中文乱码问题
			String clientFileName = filename;
			filename = new String(filename.getBytes("ISO-8859-1"), "gb2312");
			try {
				if (startpos != null)
					istartpos = Integer.parseInt(startpos);
				if (endpos != null)
					iendpos = Integer.parseInt(endpos);
			} catch (Exception e) {
				response.setStatus(404);// 无法找到指定位置的资源。
				return;
			}
			int index = filename.indexOf(".");
			if (index != -1) {
				extName = filename.substring(index + 1);
			}
			String contenType = "application/x-msdownload";// 默认类型
			if (!extName.isEmpty()) {
				CachedFileContentType fc = CacheHelper.fileContentTypeMap
						.get(extName);
				if (fc != null)
					contenType = fc.getContentType();
				if (contenType == null || contenType.isEmpty())
					contenType = "application/x-msdownload";// 默认类型
			}
			if (filecategory == 1) {// 通用文件
				int termId;
				try {
					termId = Integer.valueOf(request.getParameter("termid"));
				} catch (Throwable e) {
					response.setStatus(404);// 无法找到指定位置的资源。
					return;
				}
				CachedTerm term = CacheHelper.termMap.getTerm(termId);
				if (term == null) {
					response.setStatus(404);// 无法找到指定位置的资源。
					return;
				}
				filename = Integer.toString(term.getApp().getId()) + "/"
						+ filename;
			}
			FileDownloadProcessor processor = new FileDownloadProcessor(
					username, userpwd, filecategory);
			KeyValue<Date, byte[]> data = processor.downloadFile(lastModified,
					filename, istartpos, iendpos);
			if (data != null) {
				if (data.getValue() == null) {// 不需要下载
					response.setStatus(304);
					return;
				} else {
					response.setContentType(contenType);// 设置文件内容
					if (contenType
							.compareToIgnoreCase("application/x-msdownload") == 0) {
						response.setHeader("Content-Disposition",
								"attachment;filename=" + clientFileName);
					}
					response.setCharacterEncoding("utf-8");
					response.setHeader("Connection", "close");
					response.setDateHeader("Date", System.currentTimeMillis());
					response.setHeader("Accept-Ranges", "bytes");
					response.setDateHeader("Last-Modified", data.getKey()
							.getTime());
					if (filecategory == 6 && !filename.startsWith("files/")) {
						String str = new String(data.getValue());
						str = "<html><body>"
								+ str.replace("$_contextpath_",
										request.getContextPath())
								+ "</body></html>";
						data.setValue(str.getBytes());
					}
					response.setContentLength(data.getValue().length);// 设置文件长度
					response.getOutputStream().write(data.getValue());

					response.getOutputStream().flush();
					response.getOutputStream().close();
				}
			} else {
				response.setStatus(404);// 无法找到指定位置的资源。
			}
			logger.debug("下载文件[" + filecategory + "," + filename + "]成功");
		} catch (Throwable e) {
			logger.debug("下载失败", e);
			response.setStatus(404);
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
