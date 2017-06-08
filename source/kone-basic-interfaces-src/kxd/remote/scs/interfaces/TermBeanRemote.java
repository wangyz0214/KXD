/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseTermMoveItem;
import kxd.remote.scs.beans.device.EditedTerm;
import kxd.remote.scs.beans.device.QueryedTerm;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface TermBeanRemote {

	/**
	 * 查询终端
	 * 
	 * @param extTables
	 *            是否只查询配置了广告的终端
	 * @param extWheres
	 *            TODO
	 * @param queryCount
	 *            是否返回记录总条数
	 * @param loginUserId
	 *            登录用户
	 * @param orgId
	 *            机构ID
	 * @param manufId
	 *            厂商ID
	 * @param typeId
	 *            型号ID
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
	public QueryResult<QueryedTerm> queryTerm(String extTables,
			String extWheres, boolean queryCount, long loginUserId,
			Integer orgId, Integer manufId, Integer typeId, Integer filter,
			String filterContent, int startRecord, int maxResult);

	/**
	 * 查询终端
	 * 
	 * @param extTables
	 *            是否只查询配置了广告的终端
	 * @param extWheres
	 *            TODO
	 * @param queryCount
	 *            是否返回记录总条数
	 * @param loginUserId
	 *            登录用户
	 * @param orgId
	 *            机构ID
	 * @param manufId
	 *            厂商ID
	 * @param typeId
	 *            型号ID
	 * @param bankTermNo
	 *            银联终端编码
	 * @param manufNo
	 *            出厂编码
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
	public QueryResult<QueryedTerm> queryTerm(String extTables,
			String extWheres, boolean queryCount, long loginUserId,
			Integer orgId, Integer manufId, Integer typeId, String bankTermNo,
			String manufNo, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加终端
	 */
	public int add(long loginUserId, EditedTerm term);

	/**
	 * 编辑终端
	 */
	public void edit(long loginUserId, EditedTerm term);

	/**
	 * 终端迁移
	 * 
	 * @param loginUserId
	 * @param termId
	 *            要迁移的终端
	 * @param orgId
	 *            迁移的机构
	 */
	public void move(long loginUserId, int termId, int orgId);

	/**
	 * 删除终端
	 */
	public void delete(long loginUserId, Integer[] term);

	/**
	 * 查找终端
	 * 
	 * @param manufId
	 */
	public EditedTerm find(int term);

	public List<BaseTermMoveItem> getTermMoveList(long loginUserId,
			Integer termId);

	/**
	 * 查询终端
	 * 
	 * @param manufTermCode
	 * @return
	 */
	public EditedTerm findTerm(String manufTermCode);
}
