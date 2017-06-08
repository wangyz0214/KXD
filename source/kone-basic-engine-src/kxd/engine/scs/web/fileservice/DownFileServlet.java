package kxd.engine.scs.web.fileservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedFileContentType;
import kxd.engine.cache.beans.sts.CachedFileHost;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.fileservice.FileDownloadProcessor;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.TermHelper;
import kxd.util.DataSecurity;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.KeyValue;

import org.apache.log4j.Logger;

public class DownFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger(DownFileServlet.class);

	public DownFileServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		TermHelper.checkSystemStarted();
		String cmd = request.getParameter("cmd");
		if (cmd != null && cmd.equalsIgnoreCase("upload")) {
			try {
				if (request.getParameter("data") == null)
					return;
				String termid = request.getParameter("termid");
				String filename = request.getParameter("filename");
				byte[] data = DataUnit.hexToBytes(request.getParameter("data"));
				CachedFileHost fh = CacheHelper.fileHostMap.get((short) 1);
				if (fh != null) {
					File file = new File(fh.getFileRootDir() + "/upload/"
							+ termid + "/" + filename);
					file.getParentFile().mkdirs();
					FileOutputStream st = new FileOutputStream(file);
					try {
						st.write(data);
					} finally {
						st.close();
					}
				}
				response.setContentType("text/plain");
				response.setContentLength(2);
				response.getOutputStream().write("OK".getBytes());
			} catch (Throwable e) {
				response.setStatus(404);
				logger.debug("上传失败", e);
				response.setStatus(404);
			}
		} else {
			try {
				String username = null, userpwd = null, filename = null, startpos = null, endpos = null;
				username = request.getParameter("username");
				userpwd = request.getParameter("userpwd");
				if (userpwd != null)
					userpwd = DataSecurity.md5(userpwd);
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
				// if(filecategory==1){
				// response.setStatus(404); // 无法找到指定位置的资源。
				// return;
				// }
				if (username == null || username.isEmpty()) {
					username = "anonymous";
				}
				if (userpwd == null || userpwd.isEmpty()) {
					userpwd = "anonymous";
				}
				// 解决中文乱码问题
				filename = new String(filename.getBytes("iso-8859-1"), "gbk")
						.trim();
				String clientFileName = filename;

				try {
					if (startpos != null)
						istartpos = Integer.parseInt(startpos);
					if (endpos != null)
						iendpos = Integer.parseInt(endpos);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				int index = filename.indexOf(".");
				if (index != -1) {
					extName = filename.substring(index + 1);
				}
				String contenType = "application/x-msdownload";// 默认类型
				if (!extName.isEmpty()) {
					CachedFileContentType typeObj = CacheHelper.fileContentTypeMap
							.get(extName);
					if (typeObj != null)
						contenType = typeObj.getContentType();

					if (contenType == null || contenType.isEmpty())
						contenType = "application/x-msdownload";// 默认类型
				}

				if (filecategory == 1) {// 通用文件
					int termId;
					try {
						termId = Integer
								.valueOf(request.getParameter("termid"));
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
						filecategory);
				long lastModified = request.getDateHeader("If-Modified-Since");
				KeyValue<Date, byte[]> data = processor.downloadFile(
						lastModified, filename, istartpos, iendpos);
				if (data != null) {
					if (data.getValue() == null) {
						logger.debug("下载文件[" + filecategory + "," + filename
								+ "]文件不需要下载");
						response.setStatus(304);
						return;
					} else {
						response.setContentLength(data.getValue().length);// 设置文件长度
						response.setContentType(contenType);// 设置文件内容
						if (contenType
								.compareToIgnoreCase("application/x-msdownload") == 0) {
							response.setHeader("Content-Disposition",
									"attachment;filename=" + clientFileName);
						}
						response.setCharacterEncoding("utf-8");
						response.setHeader("Connection", "close");
						response.setDateHeader("Date",
								System.currentTimeMillis());
						response.setHeader("Accept-Ranges", "bytes");
						response.setDateHeader("Last-Modified", data.getKey()
								.getTime());
						// System.out.println(data.getKey());
						if (filecategory == 6 && !filename.startsWith("files/")) {
							String str = new String(data.getValue());
							str = "<html><body>"
									+ str.replace("${contextpath}",
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
				logger.debug("下载文件["
						+ filecategory
						+ ","
						+ filename
						+ ","
						+ new DateTime(data.getKey())
								.format("yyyy-MM-dd HH:mm:ss") + "]成功");
			} catch (Throwable e) {
				logger.debug("下载失败", e);
				response.setStatus(404);
			}
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
