package kxd.remote.scs.beans.invoice;


/**
 * 
 * @author jurstone
 * 
 */
public class EditedInvoiceTemplate extends BaseInvoiceTemplate {
	private static final long serialVersionUID = -689315914247439587L;
	private String templateCode;
	// private Clob templateContent;
	private String templateContent;

	public EditedInvoiceTemplate() {
	}

	public EditedInvoiceTemplate(Integer invoiceTemplateId) {
		super(invoiceTemplateId);
	}

	public EditedInvoiceTemplate(Integer invoiceTemplateId,
			String templateDesp, String templateContent) {
		super(invoiceTemplateId, templateDesp);
		this.templateContent = templateContent;
	}

	public String getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	/*
	 * public String getClobValue(){ String content=""; try {
	 * if(templateContent!=null){ Reader is =
	 * templateContent.getCharacterStream(); BufferedReader br=new
	 * BufferedReader(is); String s=br.readLine(); while(s!=null){ content+=s;
	 * s=br.readLine(); } } } catch (Exception e) { e.printStackTrace(); }
	 * return content; }
	 */
}
