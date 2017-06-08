package kxd.scs.dao.trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.remote.scs.transaction.AutoReTradeData;
import kxd.util.DateTime;

public class TradeDao {

	static public List<AutoReTradeData> queryNeedAutoReTradeList(Dao dao) {
		DateTime now = new DateTime();
		List<?> ls = dao
				.query("select a.termglide,a.termid,a.orgid,trademoney,"
						+ "to_char(tradetime,'yyyymmddhh24miss'),b.tradeservice,"
						+ "b.tradecode,userno,"
						+ "bankcardno,termmoney,tradeglide,bankglide,redoparams from trade_"
						+ now.format("yyyyMM")
						+ " a,tradecode b where tradetime>="
						+ "to_date('"
						+ now.format("yyyyMMdd")
						+ "','yyyymmdd') and tradetime<to_date('"
						+ now.addDays(1).format("yyyyMMdd")
						+ "','yyyymmdd') and a.tradecodeid=b.tradecodeid and paystatus>=10"
						+ " and b.redo_mode=1 "
						+ "and paystatus<20 and ( (tradestatus=0 and tradetime!=uploadtime) "
						+ "or tradestatus=20) and optioncode=0 and not redoparams is null");
		List<AutoReTradeData> r = new ArrayList<AutoReTradeData>();
		for (Object o : ls) {
			Object a[] = (Object[]) o;
			AutoReTradeData d = new AutoReTradeData();
			d.setTermGlide(new BigDecimal(a[0].toString()));
			d.setTermid(Integer.valueOf(a[1].toString()));
			if (a[2] != null) {
				d.setOrgid(Integer.valueOf(a[2].toString()));
			} else {
				d.setOrgid(-1);
			}
			d.setTradeAmount(a[3].toString());
			d.setTradeTime(a[4].toString());
			d.setTradeService(a[5].toString());
			d.setTradeCode(a[6].toString());
			d.setUserNo((String) a[7]);
			d.setBankCardNo((String) a[8]);
			d.setAmount(a[9].toString());
			d.setTradeGlide((String) a[10]);
			d.setBankGlide((String) a[11]);
			d.setRedoParams((String) a[12]);
			r.add(d);
		}
		return r;
	}
}
