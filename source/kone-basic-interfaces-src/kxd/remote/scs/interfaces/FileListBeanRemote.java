/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseFileItem;

/**
 * 
 * @author 赵明
 */
@Remote
public interface FileListBeanRemote {

	/**
	 * 添加文件项目
	 */
	public void add(long loginUserId, BaseFileItem fileItem);

	/**
	 * 编辑文件项目
	 */
	public BaseFileItem edit(long loginUserId, String oldItemId,
			BaseFileItem fileItem);

	/**
	 * 更新文件访问状况
	 */
	public void updateVisit(String itemId, int visitTimes);

	/**
	 * 删除文件项目
	 */
	public BaseFileItem delete(long loginUserId, String itemId);

	/**
	 * 查找文件项目
	 * 
	 */
	public BaseFileItem find(String itemId);
}
