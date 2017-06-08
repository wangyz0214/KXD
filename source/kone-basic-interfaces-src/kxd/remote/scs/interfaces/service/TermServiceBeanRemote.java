package kxd.remote.scs.interfaces.service;

import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.ejb.Remote;

/**
 * 
 * @author 赵明
 */
@Remote
public interface TermServiceBeanRemote {

	/**
	 * 获取主密钥
	 */
	public byte[] getMainKey();

	/**
	 * 获取当前工作密钥
	 * 
	 * @param termId
	 */
	public String getWorkKey(int termId);

	/**
	 * 终端激活
	 * 
	 * @param manufNo
	 *            出厂编号
	 * @param guid
	 *            终端标识
	 * @return 终端ID
	 */
	public int termActivate(String manufNo, String guid);

	/**
	 * 终端登录
	 * 
	 * @param termId
	 *            终端ID
	 * @param guid
	 *            终端GUID
	 * @return 返回终端的属性 orgid,orgname,orgfullname,address,appid,bankkey,opentime,
	 *         closetime
	 */
	public Hashtable<String, Object> termLogin(Object cachedTerm, String ip,
			String loginurl, String guid);

	/**
	 * 银联终端签到
	 * 
	 * @param bankTermId
	 *            银联终端ID
	 * @param signinTime
	 *            签到时间
	 * @param workKey
	 *            工作密钥，16进制字符串
	 * @param macKey
	 *            mac密钥，16进制字符串
	 * @param merchantAccount
	 *            商户号
	 * @param batch
	 *            批次号
	 * @param extField
	 *            扩展字段
	 */
	public void bankTermSignin(int bankTermId, Date signinTime, String workKey,
			String macKey, String merchantAccount, String batch, String extField);

	public void termPauseResume(Collection<Integer> terms, boolean pause);

	/**
	 * 提交纸币被人为修改日志
	 * 
	 * @param termId
	 *            被修改的终端ID
	 * @param day
	 *            修改日期
	 * @param detailModified
	 *            详细日志被修改
	 * @param traceModified
	 *            跟踪日志被修改
	 */
	public void submitCashModify(int termId, int day, boolean detailModified,
			boolean traceModified);

	/**
	 * 提交终端最新的文件属性日志
	 * 
	 * @param termId
	 * @param detailModified
	 * @param traceModified
	 */
	public void submitTermFilesAttr(int termId, String filesAttr);

	/**
	 * 返回机构下的终端ID列表
	 * 
	 * @param orgId
	 *            机构ID
	 * @return 终端ID列表
	 */
	public List<Integer> getOrgTerms(int orgId);

	/**
	 * 返回机构下的机构ID列表
	 * 
	 * @param orgId
	 *            机构ID
	 * @return 机构ID列表
	 */
	public List<Integer> getOrgChildren(int orgId);
}
