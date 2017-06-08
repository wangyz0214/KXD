/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseAppFile;
import kxd.remote.scs.beans.appdeploy.EditedApp;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface AppBeanRemote {

	/**
	 * 查询应用
	 * 
	 * @param queryCount
	 *            是否返回记录总条数
	 * @param loginUserId
	 *            登录用户
	 * @param filter
	 *            过滤操作
	 * @param filterContent
	 *            过滤内容
	 * @param startRecord
	 *            起始记录
	 * @param maxResult
	 *            最大的返回记录条数
	 * @return
	 */
	public QueryResult<EditedApp> queryApp(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加应用
	 */
	public int add(long loginUserId, EditedApp app);

	/**
	 * 编辑应用
	 */
	public void edit(long loginUserId, EditedApp app);

	/**
	 * 删除应用
	 */
	public void delete(long loginUserId, Integer[] app);

	/**
	 * 查找应用
	 * 
	 */
	public EditedApp find(int app);
	
	/**
	 * 查找应用
	 * @param appCode
	 * @return
	 */
	public EditedApp find(String appCode);

	/**
	 * 获取应用列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseApp> getAppList(long loginUserId, String keyword);

	/**
	 * 获取应用文件列表
	 * 
	 * @param loginUserId
	 * @param appId
	 */
	public List<BaseAppFile> getAppFileList(long loginUserId, int appId);

	public void addAppFile(long loginUserId, BaseAppFile o);

	/**
	 * 返回旧文件
	 * 
	 * @param loginUserId
	 * @param o
	 * @return
	 */
	public String editAppFile(long loginUserId, BaseAppFile o);

	public String[] deleteAppFile(long loginUserId, int[] id);
}
