/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 渠道类型
 * 
 * @author 赵明
 */
public enum PayWayType {

	CASH {

		@Override
		public String toString() {
			return "现金";
		}
	},
	UNIONPAY {

		@Override
		public String toString() {
			return "银联";
		}
	},
	OTHER {

		@Override
		public String toString() {
			return "其他";
		}
	};
	static public PayWayType valueOf(int v) {
		switch (v) {
		case 0:
			return CASH;
		case 1:
			return UNIONPAY;
		default:
			return OTHER;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public PayWayType valueOfIntString(Object str) {
		if (str == null)
			return OTHER;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
