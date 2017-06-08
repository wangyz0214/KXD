package kxd.engine.scs.web.fileservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.fileservice.FileProcessor;
import kxd.engine.fileservice.FileProperty;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.TermHelper;
import kxd.net.HttpRequest;
import kxd.net.XMLExecutor;
import kxd.util.DataSecurity;
import kxd.util.StringUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLFileExecutor implements XMLExecutor {

	@Override
	public Document execute(HttpRequest request, HttpServletResponse response,
			Document xmlDoc, Element content, Element result) throws Throwable {
		TermHelper.checkSystemStarted();
		String command = null;
		command = request.getParameter("command");// 命令字
		if (command == null || command.isEmpty()) {
			result.setAttribute("code", "01");
			throw new IOException("下载文件失败，command参数为空");

		} else if (command.compareToIgnoreCase("FileProperty") == 0) {// 获取一个文件的属性
			String username = null, userpwd = null, filename = null;
			short fileCategory;

			int termId = request.getParameterInt("termid");
			username = request.getParameter("username");
			userpwd = request.getParameter("userpwd");
			filename = request.getParameter("filename");
			fileCategory = request.getParameterShort("filecategory");
			if (filename == null || filename.isEmpty()) {
				result.setAttribute("code", "01");
				throw new IOException("获取文件属性失败，请求参数错误");
			}
			if (username == null || username.isEmpty()) {
				username = "anonymous";
			}
			if (userpwd == null || userpwd.isEmpty()) {
				userpwd = "anonymous";

			}
			// 解决中文乱码问题
			filename = new String(request.getParameter("filename").getBytes(
					"iso-8859-1"), "gbk");
			filename = filename.trim();
			String clientFileName = filename;
			if (fileCategory == 1) {// 通用文件，需要转换下文件名
				CachedTerm term = CacheHelper.termMap.getTerm(termId);
				if (term == null) {
					result.setAttribute("code", "01");
					throw new IOException("获取文件属性失败，终端ID[" + termId + "]不存在");
				}
				filename = Integer.toString(term.getAppId()) + "/" + filename;
			}
			try {
				userpwd = DataSecurity.md5(userpwd);
				FileProcessor processor = new FileProcessor(username, userpwd,
						fileCategory);
				FileProperty fp = processor.getFileProperty(filename, false);

				Element filenode = xmlDoc.createElement("file");
				filenode.setAttribute("filename", clientFileName);
				filenode.setAttribute("filesize", Long.toString(fp.getSize()));
				filenode.setAttribute("lastmodify", fp.getLastModified());
				filenode.setAttribute("filecategory",
						Integer.toString(fp.getFileCategory()));
				content.appendChild(filenode);
			} catch (Throwable e) {
				result.setAttribute("code", "01");
				throw new IOException("获取文件属性失败:" + e.getMessage());
			}

		} else if (command.compareToIgnoreCase("FilesProperty") == 0) {// 获取多个文件的属性
			String username = null, userpwd = null, filename = null;
			int termId = request.getParameterInt("termid");
			username = request.getParameter("username");
			userpwd = request.getParameter("userpwd");
			userpwd = DataSecurity.md5(userpwd);
			filename = request.getParameter("filename");
			if (filename == null || filename.isEmpty()) {
				result.setAttribute("code", "01");
				throw new IOException("获取文件属性失败，请求参数错误");
			}
			if (username == null || username.isEmpty()) {
				username = "anonymous";
			}
			if (userpwd == null || userpwd.isEmpty()) {
				userpwd = "anonymous";

			}
			// 解析出文件数目
			// 文件参数格式：filename1,category1|filename2,category2...
			String fileNames = new String(request.getParameter("filename")
					.getBytes("iso-8859-1"), "gbk");
			List<String> files = new ArrayList<String>();
			StringUnit.split(fileNames.trim(), "|", files);
			CachedTerm term = CacheHelper.termMap.getTerm(termId);
			for (int i = 0; i < files.size(); i++) {
				List<String> nameAndCategory = new ArrayList<String>();
				StringUnit.split(files.get(i), ",", nameAndCategory);
				String clientFileName = nameAndCategory.get(0).trim();
				short fileCategory = Short.valueOf(nameAndCategory.get(1)
						.trim());
				if (fileCategory == 1) {// 属于通用文件
					if (term == null) {
						result.setAttribute("code", "01");
						throw new IOException("获取文件属性失败，终端[id=" + termId
								+ "]不存在");
					}
					filename = Integer.toString(term.getApp().getId()) + "/"
							+ clientFileName;
				} else
					filename = clientFileName;
				FileProcessor processor = new FileProcessor(username, userpwd,
						fileCategory);
				try {
					FileProperty fp = processor
							.getFileProperty(filename, false);
					Element filenode = xmlDoc.createElement("file");
					filenode.setAttribute("filename", clientFileName);
					filenode.setAttribute("filesize",
							Long.toString(fp.getSize()));
					filenode.setAttribute("lastmodify", fp.getLastModified());
					filenode.setAttribute("filecategory",
							Integer.toString(fp.getFileCategory()));
					content.appendChild(filenode);
				} catch (Throwable e) {
				}
			}
		} else {
			result.setAttribute("code", "01");
			throw new IOException("无效的文件下载命令：" + command);

		}
		return null;
	}

	@Override
	public void init(Document doc) {
	}

	@Override
	public void uninit() {
	}

}
