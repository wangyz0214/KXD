/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 终端状态 0 - 交易成功；1 - 交易失败；2 - 交易超时；
 * 
 * @author 赵明
 */
public enum TradeResult {

	SUCCESS {

		@Override
		public String toString() {
			return "交易成功";
		}
	},
	FAILURE {

		@Override
		public String toString() {
			return "交易失败";
		}
	},
	TIMEOUT {

		@Override
		public String toString() {
			return "交易超时";
		}
	};

	static public TradeResult valueOf(int v) {
		switch (v) {
		case 0:
			return SUCCESS;
		case 1:
			return FAILURE;
		default:
			return TIMEOUT;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public TradeResult valueOfIntString(Object str) {
		if (str == null)
			return SUCCESS;
		return valueOf(Integer.valueOf(str.toString()));
	}

}