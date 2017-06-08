package kxd.remote.scs.beans;

import kxd.util.ListItem;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class BaseTradeCode extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String tradeCodeDesp;
	private String tradeCode;
	private String tradeService;
	private BaseBusiness business;

	@Override
	public String getText() {
		return tradeCodeDesp;
	}

	@Override
	public void setText(String text) {
		tradeCodeDesp = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "business: ");
		business.debug(logger, prefix + "  ");
		logger.debug(prefix + "id: " + getId());
		logger.debug(prefix + "desp: " + tradeCodeDesp);
		logger.debug(prefix + "service: " + tradeService);
		logger.debug(prefix + "code: " + tradeCode);
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseTradeCode();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseTradeCode))
			return;
		BaseTradeCode d = (BaseTradeCode) src;
		tradeCodeDesp = d.tradeCodeDesp;
		tradeCode = d.tradeCode;
		tradeService = d.tradeService;
		business = d.business;
	}

	public BaseTradeCode() {
		super();
	}

	public BaseTradeCode(Integer id) {
		super(id);
	}

	public BaseTradeCode(Integer id, String desp) {
		super(id);
		this.tradeCodeDesp = desp;
	}

	public Integer getTradeCodeId() {
		return getId();
	}

	public void setTradeCodeId(Integer id) {
		setId(id);
	}

	public String getTradeCodeDesp() {
		return tradeCodeDesp;
	}

	public void setTradeCodeDesp(String tradeCodeDesp) {
		this.tradeCodeDesp = tradeCodeDesp;
	}

	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public String getTradeService() {
		return tradeService;
	}

	public void setTradeService(String tradeService) {
		this.tradeService = tradeService;
	}

	public BaseBusiness getBusiness() {
		return business;
	}

	public void setBusiness(BaseBusiness Business) {
		this.business = Business;
	}

	@Override
	protected String toDisplayLabel() {
		if (business != null)
			return business.getDisplayLabel() + "-" + tradeCodeDesp;
		else
			return tradeCodeDesp;
	}

	@Override
	public String toString() {
		return tradeCodeDesp + "(" + getId() + ")";
	}

	@Override
	public String getIdString() {
		if (getId() == null)
			return null;
		else
			return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		if (id == null || id.trim().isEmpty())
			setId(null);
		else
			setId(Integer.parseInt(id));
	}
}
