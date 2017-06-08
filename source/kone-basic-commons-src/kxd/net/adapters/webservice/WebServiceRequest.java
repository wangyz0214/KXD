package kxd.net.adapters.webservice;

import java.util.Date;

import kxd.net.NetRequest;

/**
 * WebService请求包接口
 * 
 * @author zhaom
 * 
 */
public class WebServiceRequest implements NetRequest {
	private static final long serialVersionUID = 1L;
	private String operation;
	private Date sendTime, connectedTime;
	private Object[] params;

	public WebServiceRequest() {

	}

	/**
	 * 创建一个WebService请求
	 * 
	 * @param operation
	 *            WebService操作
	 * @param params
	 *            请求参数
	 */
	public WebServiceRequest(String operation, Object[] params) {
		this.operation = operation;
		this.params = params;
	}

	/**
	 * 获取请求包的操作，对应于WebService中的一个操作
	 * 
	 * @return
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * 获取该操作的参数
	 * 
	 * @return
	 */
	public Object[] getParams() {
		return params;
	}

	@Override
	public void setSendTime(Date value) {
		sendTime = value;
	}

	@Override
	public Date getSendTime() {
		return sendTime;
	}

	public Date getConnectedTime() {
		return connectedTime;
	}

	public void setConnectedTime(Date connectedTime) {
		this.connectedTime = connectedTime;
	}
}
