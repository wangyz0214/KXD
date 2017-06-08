package kxd.remote.scs.beans.device;

import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseBankTerm;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseTerm;
import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.remote.scs.util.emun.TermRunStatus;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.util.IdableObject;

import org.apache.log4j.Logger;

public class QueryedTerm extends BaseTerm {
	private static final long serialVersionUID = 1L;
	private BaseTermType termType;
	private BaseApp app;
	private BaseOrg org;
	private BaseBankTerm bankTerm;
	private TermStatus status;
	private TermRunStatus runStatus;
	private AlarmStatus alarmStatus;
	private boolean online;
	private SettlementType settlementType;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "status: " + status + ";");
		logger.debug(prefix + "online: " + online + ";");
		logger.debug(prefix + "runStatus: " + runStatus + ";");
		logger.debug(prefix + "alarmStatus: " + alarmStatus + ";");
		logger.debug(prefix + "termType: ");
		termType.debug(logger, prefix + "  ");
		logger.debug(prefix + "app: ");
		app.debug(logger, prefix + "  ");
		logger.debug(prefix + "org: ");
		org.debug(logger, prefix + "  ");
		if (bankTerm != null)
			bankTerm.debug(logger, prefix + "  ");
		else
			logger.debug(prefix + "  {null}");
	}

	public QueryedTerm() {
		super();
	}

	public QueryedTerm(Integer id) {
		super(id);
	}

	public QueryedTerm(Integer id, String termCode, String termDesp) {
		super(id, termCode, termDesp);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof QueryedTerm))
			return;
		QueryedTerm d = (QueryedTerm) src;
		termType = d.termType;
		app = d.app;
		org = d.org;
		status = d.status;
		bankTerm = d.bankTerm;
		runStatus = d.runStatus;
		settlementType = d.settlementType;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new QueryedTerm();
	}

	public BaseTermType getTermType() {
		return termType;
	}

	public void setTermType(BaseTermType termType) {
		this.termType = termType;
	}

	public BaseApp getApp() {
		return app;
	}

	public void setApp(BaseApp app) {
		this.app = app;
	}

	public BaseOrg getOrg() {
		return org;
	}

	public void setOrg(BaseOrg org) {
		this.org = org;
	}

	public TermStatus getStatus() {
		return status;
	}

	public void setStatus(TermStatus status) {
		this.status = status;
	}

	public TermRunStatus getRunStatus() {
		return runStatus;
	}

	public int getRunStatusValue() {
		return runStatus.ordinal();
	}

	public void setRunStatus(TermRunStatus runStatus) {
		this.runStatus = runStatus;
	}

	@Override
	protected String toDisplayLabel() {
		if (termType != null && org != null)
			return super.toDisplayLabel() + "(" + termType.getDisplayLabel()
					+ "," + org.getDisplayLabel() + ")";
		else
			return super.toDisplayLabel();
	}

	public AlarmStatus getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(AlarmStatus alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public BaseBankTerm getBankTerm() {
		return bankTerm;
	}

	public void setBankTerm(BaseBankTerm bankTerm) {
		this.bankTerm = bankTerm;
	}

	public SettlementType getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(SettlementType settlementType) {
		this.settlementType = settlementType;
	}
}
