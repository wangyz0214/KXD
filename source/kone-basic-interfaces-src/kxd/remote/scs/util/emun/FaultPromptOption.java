/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 终端状态 0 - 提示用户是否继续,1 - 提示用户设备故障，不能继续
 * 
 * @author 赵明
 */
public enum FaultPromptOption {

	CONFIRM {

		@Override
		public String toString() {
			return "提示用户是否继续";
		}
	},
	FAULT {

		@Override
		public String toString() {
			return "提示用户设备故障，不能继续";
		}
	};
	static public FaultPromptOption valueOf(int v) {
		switch (v) {
		case 0:
			return CONFIRM;
		default:
			return FAULT;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public FaultPromptOption valueOfIntString(Object str) {
		if (str == null)
			return CONFIRM;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
