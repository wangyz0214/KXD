package kxd.net;

import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML执行器接口。本接口用于<code>XMLTradeManager</code>执行一个XML交易。
 * 
 * @author 赵明
 * @see kxd.web.servlets#XMLTradeManager
 * @version 1.0
 * 
 */
public interface XMLExecutor {
	/**
	 * 初始化执行器，只会在WEB容器装入时调用一次
	 * 
	 * @param doc
	 *            配置文档对象，用于装入个性的配置参数
	 */
	public void init(Document doc);

	/**
	 * 清理执行器，只会有WEB容器卸载时调用一次
	 */
	public void uninit();

	/**
	 * 执行交易。
	 * <p>
	 * 实现类通过request获取交易参数，处理完成后，将结果填写到xmlDoc中返回给<code>XMLTradeManager</code>
	 * </p>
	 * 
	 * @param request
	 *            交易请求
	 * @param response
	 *            交易响应对象
	 * @param xmlDoc
	 *            交易返回的XML文档对象
	 * @param content
	 *            内容结点，具体执行器生成的全部结点应该是该结点的子结点
	 * @param result
	 *            结果结点，默认包含success,message两个属性，由系统自动设定，具体执行器可以添加其他属性
	 * @return 返回的document，可以与传入的不同
	 * @throws Throwable
	 *             操作失败时抛出
	 */
	public Document execute(HttpRequest request, HttpServletResponse response,
			Document xmlDoc, Element content, Element result) throws Throwable;
}
