/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 退款状态 0 - 未退款；1 - 正在退款；2 - 退款成功；3 - 退款失败
 * 
 * @author 赵明
 */
public enum RefundStatus {

	NOT_REFUND {

		@Override
		public String toString() {
			return "未退款";
		}
	},
	REFUNDING {

		@Override
		public String toString() {
			return "正在退款";
		}
	},
	REFUND_SUCCESS {

		@Override
		public String toString() {
			return "退款成功";
		}
	},
	REFUND_FAILURE {

		@Override
		public String toString() {
			return "退款失败";
		}
	};
	static public RefundStatus valueOf(int v) {
		switch (v) {
		case 0:
			return NOT_REFUND;
		case 1:
			return REFUNDING;
		case 2:
			return REFUND_SUCCESS;
		case 3:
			return REFUND_FAILURE;
		default:
			return REFUND_FAILURE;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public RefundStatus valueOfIntString(Object str) {
		if (str == null)
			return NOT_REFUND;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
