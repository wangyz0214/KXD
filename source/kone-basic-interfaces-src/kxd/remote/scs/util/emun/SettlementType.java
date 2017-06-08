/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 终端结算类型 0 - 销售,1 - 租赁,2 - 运营
 * 
 * @author 赵明
 */
public enum SettlementType {

	SALE {

		@Override
		public String toString() {
			return "采购";
		}
	},
	RENTAL {

		@Override
		public String toString() {
			return "租赁";
		}
	},
	COMMISSION {

		@Override
		public String toString() {
			return "运营";
		}
	};

	static public SettlementType valueOf(int v) {
		switch (v) {
		case 0:
			return SALE;
		case 1:
			return RENTAL;
		default:
			return COMMISSION;
		}
	}

	static public SettlementType valueOfIntString(Object str) {
		if (str == null)
			return SALE;
		return valueOf(Integer.valueOf(str.toString()));
	}

	public int getValue() {
		return this.ordinal();
	}
}
