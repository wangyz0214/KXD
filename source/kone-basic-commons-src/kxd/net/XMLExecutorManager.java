package kxd.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jxl.Workbook;
import kxd.util.KoneException;
import kxd.util.KoneUtil;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * XML交易管理器。
 * <p>
 * <code>XMLTradeManager</code>包含一个基于<code>XMLExecutor</code>
 * 的列表，做一个交易时，根据需要查找相应的<code>XMLExecutor</code>来完成具体的交易
 * </p>
 * <p>
 * <code>XMLTradeManager</code>只提供静态的交易函数，内部的执行器列表也是静态的。当WEB服务器接收到一个基于
 * <code>XMLSevlet</code>的HTTP请求时，<code>XMLServlet</code>会自动调用
 * <code>XMLTradeManager</code>的静态函数trade()来完成交易,trade()函数从列表查找相关的
 * <code>XMLExecutor</code>来执行交易。一个典型的应用流程如下：
 * <ul>
 * <li>在WEB服务器启动或在适当的时候，程序调用
 * {@link XMLExecutorManager#addExecutor(String name,Class executorClass)}
 * 向交易管理器中添加一个交易执行器的类。没有相应的执行器，是无法做交易的。</li>
 * <li>流览器发起请求，如：localhost:8080/web/xmlTrade?executor=ename&...，向服务器发起交易，
 * <code>XMLSevlet</code>接收请求并提交<code>XMLTradeManager</code>处理.
 * 其中：xmlTrade为web.xml中配置的XMLServlet的路径</li>
 * <li><code>XMLTradeManager</code>找到名称为ename的执行器类(找不到抛出异常)，实例化后执行交易后返回
 * <code>XMLSevlet</code>.</li>
 * <li><code>XMLSevlet</code>将结果输出到浏览器.</li>
 * </ul>
 * </p>
 * <p>
 * <code>XMLTradeManager</code>的返回XML格式如下： <blockquote>
 * 
 * <pre>
 *  &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 *  &lt;result&gt;
 *    &lt;result_info code=&quot;true&quot; info=&quot;操作成功&quot;/&gt;
 *    由XMLExecutor执行器的返回结果填写在此处，具体格式由具体的执行器解释 
 *  &lt;/result&gt;
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author 赵明
 * @see kxd.web.servlets#XMLExecutor
 * @version 1.0
 */
public class XMLExecutorManager {

	static ConcurrentHashMap<String, Class<?>> executors = new ConcurrentHashMap<String, Class<?>>();
	static ConcurrentHashMap<ServletContext, ArrayList<String>> webContextExecutors = new ConcurrentHashMap<ServletContext, ArrayList<String>>();
	static Logger logger = Logger.getLogger(XMLExecutorManager.class);

	/**
	 * 添加一个执行器至交易管理器中。如果出现名称重复，则只添加第一个，后面的添加无效
	 * 
	 * @param name
	 *            交易执行器名称
	 * @param executor
	 *            执行器对象
	 */
	public static void init(ServletContext context, String configFile) {
		try { // 装入交易配置
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			String path = KoneUtil.getConfigPath();
			logger.debug("[configpath:" + path + "]");
			Document doc = builder.parse(path + configFile);

			NodeList list = doc.getElementsByTagName("xmlexecutor");
			ArrayList<String> ls = new ArrayList<String>();
			webContextExecutors.put(context, ls);
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				Class<?> clazz = Class.forName(node.getAttribute("class"));
				ls.add(node.getAttribute("name"));
				executors.put(node.getAttribute("name"), clazz);
				String file = node.getAttribute("configfile");
				Document document = null;
				if (file != null && !file.trim().isEmpty()) {
					document = builder.parse(path + file);
				}
				Object o = clazz.newInstance();
				if (o instanceof XMLExecutor)
					((XMLExecutor) o).init(document);
			}
		} catch (Throwable e) {
			logger.error("init error:", e);
		}
	}

	/**
	 * 移除所有执行器
	 */
	public static void uninit(ServletContext context) {
		ArrayList<String> ls = webContextExecutors.get(context);
		if (ls != null) {
			for (String n : ls) {
				try {
					Object o = executors.remove(n).newInstance();
					if (o instanceof XMLExecutor)
						((XMLExecutor) o).uninit();
				} catch (Throwable e) {
				}
			}
		}
	}

	/**
	 * 移除一个交易执行器
	 * 
	 * @param name
	 *            交易执行器名称
	 */
	public static void removeExecutor(String name) {
		executors.remove(name);
	}

	/**
	 * 做交易。本函数从request取出[executor]参数，根据此参数找到相对应的执行器后执行交易
	 * 
	 * @param request
	 *            HTTP交易请求
	 * @param response
	 *            HTTP交易响应
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	static final void execute(HttpRequest request, HttpServletResponse response)
			throws IOException, NoSuchFieldException, InstantiationException,
			IllegalAccessException {
		String executorName = request.getParameter("executor");
		Object executor = executors.get(executorName).newInstance();
		if (executor instanceof ExcelExecutor) {
			response.setCharacterEncoding("utf-8");
			request.getRequest().setCharacterEncoding("utf-8");
			String fileName = request.getParameter("filename");
			response.setHeader("Content-Disposition", "attachment;filename="
					+ URLEncoder.encode(fileName, "utf-8"));
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			((ExcelExecutor) executor).execute(request,
					Workbook.createWorkbook(o));
			byte[] b = o.toByteArray();
			response.setContentLength(b.length);
			response.setContentType("application/x-msdownload");
			response.setHeader("Connection", "close");
			response.setDateHeader("Date", System.currentTimeMillis());
			response.setHeader("Accept-Ranges", "bytes");
			response.getOutputStream().write(b);
			response.getOutputStream().flush();
			// response.getOutputStream().close();
			return;
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			if (logger.isDebugEnabled()) {
				Enumeration<?> em = request.getParameterNames();
				StringBuffer b = new StringBuffer();
				while (em.hasMoreElements()) {
					String name = (String) em.nextElement();
					b.append(name + "=" + request.getParameter(name) + ";");
				}
				logger.debug("xmltrade:{" + b + "}");
			}

			builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement("kone");
			doc.appendChild(root);
			Element result = doc.createElement("result");
			root.appendChild(result);
			Element content = doc.createElement("content");
			root.appendChild(content);
			try {
				if (executor != null) {
					Document ndoc = ((XMLExecutor) executor).execute(request,
							response, doc, content, result);

					if (ndoc != null && ndoc != doc) {
						doc = ndoc;
						NodeList ls = doc.getElementsByTagName("result");
						if (ls.getLength() > 0) {
							result = (Element) ls.item(0);
						}
					}
					result.setAttribute("success", "true");
					result.setAttribute("message", "操作成功");
				} else {
					throw new IllegalArgumentException("无效的交易服务代码");
				}
			} catch (KoneException e) {
				if (e.getData() != null && e.getData() instanceof Document) {
					doc = (Document) e.getData();
					NodeList ls = doc.getElementsByTagName("result");
					if (ls.getLength() > 0) {
						result = (Element) ls.item(0);
					}
				}
				result.setAttribute("success", "false");
				result.setAttribute("message",
						StringUnit.getExceptionMessage(e));
			} catch (Throwable e) {
				if ((e.getCause() == null && !(e instanceof KoneException))
						|| (e.getCause() != null && !(e.getCause() instanceof KoneException))) {
					logger.error("操作失败：", e);
				}
				result.setAttribute("success", "false");
				result.setAttribute("message",
						StringUnit.getExceptionMessage(e));
			}

			response.setCharacterEncoding("utf-8");
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer transformer = tfactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult sr = new StreamResult(response.getOutputStream());
			transformer.setOutputProperty("encoding", "utf-8");
			transformer.transform(source, sr);
		} catch (ParserConfigurationException e) {
		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		}
	}
}
