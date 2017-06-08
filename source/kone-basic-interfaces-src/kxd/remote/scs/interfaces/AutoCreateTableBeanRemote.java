/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import javax.ejb.Remote;

/**
 * 
 * @author 赵明
 */
@Remote
public interface AutoCreateTableBeanRemote {
	/**
	 * 打开自动创建表的定时器
	 */
	public void startAutoCreateTimer();
}
