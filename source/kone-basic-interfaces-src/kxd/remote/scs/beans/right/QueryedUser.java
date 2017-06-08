/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.beans.right;

import java.util.Date;

import kxd.util.DataUnit;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

/**
 * 
 * @author Administrator
 */
public class QueryedUser extends EditedUser {

	private static final long serialVersionUID = 1L;
	private Date regTime;
	private Date lastInlineTime;
	private long loginCount;
	private String headImage;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger
				.debug( prefix
						+ "regTime: "
						+ DataUnit.formatDateTime(regTime,
								"yyyy-MM-dd HH:mm:ss") + ";");
		logger
				.debug( prefix
						+ "lastInlineTime: "
						+ DataUnit.formatDateTime(regTime,
								"yyyy-MM-dd HH:mm:ss") + ";");
		logger.debug( prefix + "loginCount: " + loginCount + ";");
		logger.debug( prefix + "headImage: " + headImage + ";");
	}

	public QueryedUser(long userId, String userName) {
		super(userId, userName);
	}

	public QueryedUser(long userId) {
		super(userId);
	}

	public QueryedUser() {
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof QueryedUser))
			return;
		QueryedUser d = (QueryedUser) src;
		regTime = d.regTime;
		lastInlineTime = d.lastInlineTime;
		loginCount = d.loginCount;
		headImage = d.headImage;
	}

	@Override
	public IdableObject<Long> createObject() {
		return new QueryedUser();
	}

	public String getHeadImage() {
		if (headImage == null)
			headImage = "noavatar.gif";
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public Date getLastInlineTime() {
		return lastInlineTime;
	}

	public void setLastInlineTime(Date lastInlineTime) {
		this.lastInlineTime = lastInlineTime;
	}

	public long getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(long loginCount) {
		this.loginCount = loginCount;
	}

	public Date getRegTime() {
		return regTime;
	}

	public void setRegTime(Date regTime) {
		this.regTime = regTime;
	}
}
