/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 对账状态 0-“未对账”、1-“已对账下发”、2-“已对账未下发”、3-“对账后被返销”、 4-“对账后被补销”。
 * 
 * @author 赵明
 */
public enum RecStatus {

	NOT_REC {

		@Override
		public String toString() {
			return "未对账";
		}
	},
	REC_SENDED {

		@Override
		public String toString() {
			return "已对账下发";
		}
	},
	REC_NOT_SENDED {

		@Override
		public String toString() {
			return "已对账未下发";
		}
	},
	REC_RESOLD {

		@Override
		public String toString() {
			return "已对账被返销";
		}
	},
	REC_REDO {

		@Override
		public String toString() {
			return "已对账被补销";
		}
	};
	static public RecStatus valueOf(int v) {
		switch (v) {
		case 0:
			return NOT_REC;
		case 1:
			return REC_SENDED;
		case 2:
			return REC_NOT_SENDED;
		case 3:
			return REC_RESOLD;
		default:
			return REC_REDO;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public RecStatus valueOfIntString(Object str) {
		if (str == null)
			return NOT_REC;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
