/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 柜员操作
 * 
 * @author 赵明
 */
public enum CounterOption {

	NONE {

		@Override
		public String toString() {
			return "没有操作";
		}
	},
	RETURN {

		@Override
		public String toString() {
			return "退款";
		}
	},
	REDO {

		@Override
		public String toString() {
			return "标记为成功交易";
		}
	};
	static public CounterOption valueOf(int v) {
		switch (v) {
		case 0:
			return NONE;
		case 1:
			return RETURN;
		default:
			return REDO;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public CounterOption valueOfIntString(Object str) {
		if (str == null)
			return NONE;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
