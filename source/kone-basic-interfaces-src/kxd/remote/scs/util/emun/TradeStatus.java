/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 交易状态
 * 
 * @author 赵明
 */
public enum TradeStatus {
	NOT_TRADE {

		@Override
		public String toString() {
			return "未交易";
		}
	},
	TRADE_SUCCESS {

		@Override
		public String toString() {
			return "交易成功";
		}
	},
	TRADE_SUCCESS_RECONCIL {

		@Override
		public String toString() {
			return "对账置为交易成功";
		}
	},
	TRADE_SUCCESS_SET {

		@Override
		public String toString() {
			return "手工置为交易成功";
		}
	},
	TRADE_SUCCESS_REDO {

		@Override
		public String toString() {
			return "补交置为交易成功";
		}
	},
	TRADE_SUCCESS_QUERY {

		@Override
		public String toString() {
			return "查询置为交易成功";
		}
	},
	TRADE_TIMEOUT {

		@Override
		public String toString() {
			return "交易超时";
		}
	},
	TRADE_FAILURE {
		@Override
		public String toString() {
			return "交易失败";
		}
	},
	TRADE_FAILURE_RESOLD {
		@Override
		public String toString() {
			return "冲正置为交易失败";
		}
	},
	TRADE_FAILURE_RECONCIL {
		@Override
		public String toString() {
			return "对账置为交易失败";
		}
	},
	TRADE_FAILURE_REFULD {
		@Override
		public String toString() {
			return "退款置为交易失败";
		}
	},
	TRADE_FAILURE_SET {
		@Override
		public String toString() {
			return "手动置为交易失败";
		}
	},
	TRADE_FAILURE_REDO {
		@Override
		public String toString() {
			return "重做后交易失败";
		}
	};

	static public TradeStatus valueOf(int v) {
		switch (v) {
		case 0:
			return NOT_TRADE;
		case 10:
			return TRADE_SUCCESS;
		case 11:
			return TRADE_SUCCESS_RECONCIL;
		case 12:
			return TRADE_SUCCESS_SET;
		case 13:
			return TRADE_SUCCESS_REDO;
		case 14:
			return TRADE_SUCCESS_QUERY;
		case 20:
			return TRADE_TIMEOUT;
		case 30:
			return TRADE_FAILURE;
		case 31:
			return TRADE_FAILURE_RESOLD;
		case 32:
			return TRADE_FAILURE_RECONCIL;
		case 33:
			return TRADE_FAILURE_REFULD;
		case 34:
			return TRADE_FAILURE_SET;
		case 35:
			return TRADE_FAILURE_REDO;
		default:
			return NOT_TRADE;
		}
	}

	public int getValue() {
		switch (this) {
		case NOT_TRADE:
			return 0;
		case TRADE_SUCCESS:
			return 10;
		case TRADE_SUCCESS_RECONCIL:
			return 11;
		case TRADE_SUCCESS_SET:
			return 12;
		case TRADE_SUCCESS_REDO:
			return 13;
		case TRADE_SUCCESS_QUERY:
			return 14;
		case TRADE_TIMEOUT:
			return 20;
		case TRADE_FAILURE:
			return 30;
		case TRADE_FAILURE_RESOLD:
			return 31;
		case TRADE_FAILURE_RECONCIL:
			return 32;
		case TRADE_FAILURE_REFULD:
			return 33;
		case TRADE_FAILURE_SET:
			return 34;
		case TRADE_FAILURE_REDO:
			return 35;
		default:
			return 0;
		}
	}

	static public TradeStatus valueOfIntString(Object str) {
		if (str == null)
			return NOT_TRADE;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
