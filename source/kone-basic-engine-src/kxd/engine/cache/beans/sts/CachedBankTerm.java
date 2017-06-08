package kxd.engine.cache.beans.sts;

import java.io.IOException;
import java.util.Date;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedBankTerm extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.bankterm";
	/**
	 * 终端编码
	 */
	private String bankTermCode;
	/**
	 * 终端描述
	 */
	private String bankTermDesp;
	/**
	 * 工作密钥
	 */
	private String workKey;
	/**
	 * MAC密钥
	 */
	private String macKey;
	/**
	 * 商户号
	 */
	private String merchantAccount;
	/**
	 * 批次号
	 */
	private String batch;
	/**
	 * 批次号
	 */
	private String extField;
	/**
	 * 签到时间
	 */
	private Date signinTime;

	public CachedBankTerm(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedBankTerm() {
		super();
	}

	public CachedBankTerm(int id) {
		super(id);
	}

	public String getBankTermCode() {
		return bankTermCode;
	}

	public void setBankTermCode(String bankTermCode) {
		this.bankTermCode = bankTermCode;
	}

	public String getBankTermDesp() {
		return bankTermDesp;
	}

	public void setBankTermDesp(String bankTermDesp) {
		this.bankTermDesp = bankTermDesp;
	}

	public String getWorkKey() {
		return workKey;
	}

	public void setWorkKey(String workKey) {
		this.workKey = workKey;
	}

	public String getMacKey() {
		return macKey;
	}

	public void setMacKey(String macKey) {
		this.macKey = macKey;
	}

	public String getMerchantAccount() {
		return merchantAccount;
	}

	public void setMerchantAccount(String merchantAccount) {
		this.merchantAccount = merchantAccount;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getExtField() {
		return extField;
	}

	public void setExtField(String extField) {
		this.extField = extField;
	}

	public Date getSigninTime() {
		return signinTime;
	}

	public void setSigninTime(Date signinTime) {
		this.signinTime = signinTime;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setBankTermCode(stream.readPacketByteString(3000));
		setBankTermDesp(stream.readPacketByteString(3000));
		setWorkKey(stream.readPacketByteString(3000));
		setMacKey(stream.readPacketByteString(3000));
		setMerchantAccount(stream.readPacketByteString(3000));
		setBatch(stream.readPacketByteString(3000));
		setExtField(stream.readPacketByteString(3000));
		setSigninTime(stream.readLongDateTime(3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getBankTermCode(), 3000);
		stream.writePacketByteString(getBankTermDesp(), 3000);
		stream.writePacketByteString(getWorkKey(), 3000);
		stream.writePacketByteString(getMacKey(), 3000);
		stream.writePacketByteString(getMerchantAccount(), 3000);
		stream.writePacketByteString(getBatch(), 3000);
		stream.writePacketByteString(getExtField(), 3000);
		stream.writeLongDateTime(getSigninTime(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedBankTerm) {
			CachedBankTerm o = (CachedBankTerm) src;
			setBankTermCode(o.getBankTermCode());
			setBankTermDesp(o.getBankTermDesp());
			setWorkKey(o.getWorkKey());
			setMacKey(o.getMacKey());
			setMerchantAccount(o.getMerchantAccount());
			setBatch(o.getBatch());
			setExtField(o.getExtField());
			setSigninTime(o.getSigninTime());
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedBankTerm();
	}

	@Override
	public String getText() {
		return bankTermDesp;
	}

	@Override
	public void setText(String text) {
		bankTermDesp = text;
	}

}
