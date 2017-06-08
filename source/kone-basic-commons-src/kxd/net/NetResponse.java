package kxd.net;

import java.io.Serializable;
import java.util.Date;

public interface NetResponse extends Serializable {
	/**
	 * 设置响应收到的时间
	 */
	public void setRecvTime(Date value);

	/**
	 * 获取响应收到的时间
	 * 
	 */
	public Date getRecvTime();
}
