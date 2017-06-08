package kxd.engine.scs.invoice;

import java.io.IOException;
import java.io.Serializable;

import kxd.engine.scs.invoice.template.InvoiceTemplate;
import kxd.util.Streamable;
import kxd.util.stream.Stream;

public class InvoiceConfig implements Streamable, Serializable {
	private static final long serialVersionUID = 1L;
	private String configCode;
	private int orgId;
	private int type;
	private int templateId;
	private int tax;
	private int away;
	private int alertCount;
	private boolean logged;// 是否记录发票明细
	private String extdata0;
	private String extdata1;
	InvoiceTemplate template;
	private boolean isNull;

	public InvoiceConfig() {
		super();
	}

	public InvoiceConfig(boolean isNull) {
		super();
		this.isNull = isNull;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		isNull = stream.readBoolean(3000);
		if (isNull)
			return;
		configCode = stream.readPacketByteString(3000);
		orgId = stream.readInt(false, 3000);
		type = stream.readInt(false, 3000);
		templateId = stream.readInt(false, 3000);
		tax = stream.readInt(false, 3000);
		away = stream.readInt(false, 3000);
		alertCount = stream.readInt(false, 3000);
		logged = stream.readBoolean(3000);
		extdata0 = stream.readPacketShortString(false, 3000);
		extdata1 = stream.readPacketShortString(false, 3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeBoolean(isNull, 3000);
		if (!isNull) {
			stream.writePacketByteString(configCode, 3000);
			stream.writeInt(orgId, false, 3000);
			stream.writeInt(type, false, 3000);
			stream.writeInt(templateId, false, 3000);
			stream.writeInt(tax, false, 3000);
			stream.writeInt(away, false, 3000);
			stream.writeInt(alertCount, false, 3000);
			stream.writeBoolean(logged, 3000);
			stream.writePacketShortString(extdata0, false, 3000);
			stream.writePacketShortString(extdata1, false, 3000);
		}
	}

	public InvoiceTemplate getTemplate() throws Exception {
		if (template == null) {
			template = InvoiceHelper.getTemplate(templateId);
		}
		return template;
	}

	public String getConfigCode() {
		return configCode;
	}

	public void setConfigCode(String configCode) {
		this.configCode = configCode;
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public int getTax() {
		return tax;
	}

	public void setTax(int tax) {
		this.tax = tax;
	}

	public int getAway() {
		return away;
	}

	public void setAway(int away) {
		this.away = away;
	}

	public int getAlertCount() {
		return alertCount;
	}

	public void setAlertCount(int alertCount) {
		this.alertCount = alertCount;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	public String getExtdata0() {
		return extdata0;
	}

	public void setExtdata0(String extdata0) {
		this.extdata0 = extdata0;
	}

	public String getExtdata1() {
		return extdata1;
	}

	public void setExtdata1(String extdata1) {
		this.extdata1 = extdata1;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	public void setTemplate(InvoiceTemplate template) {
		this.template = template;
	}

}
