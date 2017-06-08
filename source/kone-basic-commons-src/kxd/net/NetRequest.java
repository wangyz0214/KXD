package kxd.net;

import java.io.Serializable;
import java.util.Date;

/**
 * 请求包
 * 
 * @author zhaom
 * 
 */
public interface NetRequest extends Serializable {
	/**
	 * 设置请求发出的时间
	 */
	public void setSendTime(Date value);

	/**
	 * 获取请求发出的时间
	 * 
	 */
	public Date getSendTime();
}
