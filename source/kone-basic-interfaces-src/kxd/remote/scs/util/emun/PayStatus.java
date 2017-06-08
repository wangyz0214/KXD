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
public enum PayStatus {
	NOT_PAY {

		@Override
		public String toString() {
			return "未支付";
		}
	},
	PAY_SUCCESS {

		@Override
		public String toString() {
			return "支付成功";
		}
	},
	PAY_SUCCESS_RECONCIL {

		@Override
		public String toString() {
			return "对账置为支付成功";
		}
	},
	PAY_SUCCESS_SET {

		@Override
		public String toString() {
			return "手工置为支付成功";
		}
	},
	PAY_SUCCESS_QUERY {

		@Override
		public String toString() {
			return "查询置为支付成功";
		}
	},
	PAY_TIMEOUT {

		@Override
		public String toString() {
			return "支付超时";
		}
	},
	PAY_FAILURE {
		@Override
		public String toString() {
			return "支付失败";
		}
	},
	PAY_FAILURE_RESOLD {
		@Override
		public String toString() {
			return "冲正置为支付失败";
		}
	},
	PAY_FAILURE_RECONCIL {
		@Override
		public String toString() {
			return "对账置为支付失败";
		}
	},
	PAY_FAILURE_REFULD {
		@Override
		public String toString() {
			return "退款置为支付失败";
		}
	},
	PAY_FAILURE_SET {
		@Override
		public String toString() {
			return "手动置为支付失败";
		}
	};

	static public PayStatus valueOf(int v) {
		switch (v) {
		case 0:
			return NOT_PAY;
		case 10:
			return PAY_SUCCESS;
		case 11:
			return PAY_SUCCESS_RECONCIL;
		case 12:
			return PAY_SUCCESS_SET;
		case 13:
			return PAY_SUCCESS_QUERY;
		case 20:
			return PAY_TIMEOUT;
		case 30:
			return PAY_FAILURE;
		case 31:
			return PAY_FAILURE_RESOLD;
		case 32:
			return PAY_FAILURE_RECONCIL;
		case 33:
			return PAY_FAILURE_REFULD;
		case 34:
			return PAY_FAILURE_SET;
		default:
			return NOT_PAY;
		}
	}

	public int getValue() {
		switch (this) {
		case NOT_PAY:
			return 0;
		case PAY_SUCCESS:
			return 10;
		case PAY_SUCCESS_RECONCIL:
			return 11;
		case PAY_SUCCESS_SET:
			return 12;
		case PAY_SUCCESS_QUERY:
			return 13;
		case PAY_TIMEOUT:
			return 20;
		case PAY_FAILURE:
			return 30;
		case PAY_FAILURE_RESOLD:
			return 31;
		case PAY_FAILURE_RECONCIL:
			return 32;
		case PAY_FAILURE_REFULD:
			return 33;
		case PAY_FAILURE_SET:
			return 34;
		default:
			return 0;
		}
	}

	static public PayStatus valueOfIntString(Object str) {
		if (str == null)
			return NOT_PAY;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
