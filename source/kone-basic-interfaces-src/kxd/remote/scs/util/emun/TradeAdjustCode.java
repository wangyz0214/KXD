/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 终端状态 0 - 未处理； 1 - 已冲正； 2 - 已退款；
 * 
 * @author 赵明
 */
public enum TradeAdjustCode {

	UNADJUST {

		@Override
		public String toString() {
			return "未调整";
		}
	},
	ELIMINATED {

		@Override
		public String toString() {
			return "已冲正";
		}
	},
	REFUND {

		@Override
		public String toString() {
			return "已退款";
		}
	};
	static public TradeAdjustCode valueOf(int v) {
		switch (v) {
		case 0:
			return UNADJUST;
		case 1:
			return ELIMINATED;
		default:
			return REFUND;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public TradeAdjustCode valueOfIntString(Object str) {
		if (str == null)
			return UNADJUST;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
