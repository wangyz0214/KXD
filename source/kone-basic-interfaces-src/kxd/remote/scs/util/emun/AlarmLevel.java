/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 终端状态 0 - 正常,1 - 告警,2 - 故障
 * 
 * @author 赵明
 */
public enum AlarmLevel {

	NORMAL {

		@Override
		public String toString() {
			return "正常";
		}
	},
	ALARM {

		@Override
		public String toString() {
			return "告警";
		}
	},
	FAULT {

		@Override
		public String toString() {
			return "故障";
		}
	};

	static public AlarmLevel valueOf(int v) {
		switch (v) {
		case 0:
			return NORMAL;
		case 1:
			return ALARM;
		default:
			return FAULT;
		}
	}

	static public AlarmLevel valueOfIntString(Object str) {
		if (str == null)
			return NORMAL;
		return valueOf(Integer.valueOf(str.toString()));
	}

	public int getValue() {
		return this.ordinal();
	}
}
