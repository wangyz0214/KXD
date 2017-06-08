package kxd.scs.dao.term.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseBankTerm;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.beans.device.EditedTerm;
import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.remote.scs.util.emun.TermStatus;

public class EditedTermConverter extends BaseDaoConverter<EditedTerm> implements
		SqlConverter<EditedTerm> {

	public EditedTermConverter() {
	}

	public EditedTerm doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedTerm r = new EditedTerm();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setTermCode((String) o[1]);
		r.setTermDesp((String) o[2]);

		r.setTermType(new BaseTermType(Integer.valueOf(o[3].toString()),
				(String) o[4]));
		r.setApp(new BaseApp(Integer.valueOf(o[5].toString()), (String) o[6]));
		if (o[7] == null)
			r.setBankTerm(new BaseBankTerm());
		else {
			r.setBankTerm(new BaseBankTerm(Integer.valueOf(o[7].toString()),
					(String) o[8], "", (String) o[9]));
		}
		r.setOrg(new BaseOrg(Integer.valueOf(o[10].toString())));
		r.setStatus(TermStatus.valueOfIntString(o[11]));
		r.setAlarmStatus(AlarmStatus.valueOfIntString(o[12]));
		r.setManufNo((String) o[13]);
		r.setAddress((String) o[14]);
		r.setContacter((String) o[15]);
		r.setDayRunTime(Short.valueOf(o[16].toString()));
		r.setOpenTime((String) o[17]);
		r.setCloseTime((String) o[18]);
		r.setGuid((String) o[19]);
		r.setAreaCode((String) o[20]);
		r.setExtField0((String) o[21]);
		r.setExtField1((String) o[22]);
		r.setExtField2((String) o[23]);
		r.setExtField3((String) o[24]);
		r.setExtField4((String) o[25]);
		r.setSettlementType(SettlementType.valueOfIntString(o[26]));
		return r;
	}

	private final static String DELETE_SQL = "delete from term where termid=?1";

	public SqlParams getDeleteSql(EditedTerm o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into term (termid,"
			+ "typeid,appid,orgid,banktermid,termcode,termdesp,"
			+ "manufno,address,contacter,areacode,status,dayruntime"
			+ ",opentime,closetime,guid,alarmstatus,extfield0,extfield1,"
			+ "extfield2,extfield3,extfield4,settlement_type) values(?1,?2,"
			+ "?3,?4,?5,?6,?7,?8,?9,?10,?11,?12,?13,?14,?15,?16,?17,?18,?19,?20,?21,?22,?23)";

	public SqlParams getInsertSql(EditedTerm o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getTermType().getId(), o
				.getApp().getId(), o.getOrg().getId(), o.getBankTerm().getId(),
				o.getTermCode(), o.getTermDesp(), o.getManufNo(),
				o.getAddress(), o.getContacter(), o.getAreaCode(), o
						.getStatus().getValue(), o.getDayRunTime(),
				o.getOpenTime(), o.getCloseTime(), o.getGuid(), o
						.getAlarmStatus().getValue(), o.getExtField0(),
				o.getExtField1(), o.getExtField2(), o.getExtField3(),
				o.getExtField4(), o.getSettlementType().getValue());
	}

	private final static String SEQUENCE_STRING = "seq_term";

	public String getSequenceString(EditedTerm o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update term set "
			+ "typeid=?1,appid=?2,banktermid=?3,termcode=?4,termdesp=?5,"
			+ "manufno=?6,address=?7,contacter=?8,areacode=?9,status=?10,dayruntime=?11"
			+ ",opentime=?12,closetime=?13,guid=?14,settlement_type=?15 where termid=?16";

	private final static String UPDATE_SQL1 = "update term set "
			+ "extfield0=?1,"
			+ "extfield1=?2,extfield2=?3,extfield3=?4,extfield4=?5 where termid=?6";

	public SqlParams getUpdateSql(EditedTerm o) {
		if (o.getTermCode() != null)
			return new SqlParams(UPDATE_SQL, o.getTermType().getId(), o
					.getApp().getId(), o.getBankTerm().getId(),
					o.getTermCode(), o.getTermDesp(), o.getManufNo(),
					o.getAddress(), o.getContacter(), o.getAreaCode(), o
							.getStatus().getValue(), o.getDayRunTime(),
					o.getOpenTime(), o.getCloseTime(), o.getGuid(), o
							.getSettlementType().getValue(), o.getId());
		else
			return new SqlParams(UPDATE_SQL1, o.getExtField0(),
					o.getExtField1(), o.getExtField2(), o.getExtField3(),
					o.getExtField4(), o.getId());
	}
}
