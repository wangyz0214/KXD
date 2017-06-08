/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 终端状态 0 - 未评价; 1 - 满意，2 - 一般，3 - 不满意
 * 
 * @author 赵明
 */
public enum UserEvaluation {

	NO_EVALUATION {

		@Override
		public String toString() {
			return "未评价";
		}
	},
	SATISFIED {

		@Override
		public String toString() {
			return "满意";
		}
	},
	TOLERABLE {

		@Override
		public String toString() {
			return "一般";
		}
	},
	DIS_SATISFIED {

		@Override
		public String toString() {
			return "不满意";
		}
	};
	static public UserEvaluation valueOf(int v) {
		switch (v) {
		case 0:
			return NO_EVALUATION;
		case 1:
			return SATISFIED;
		case 2:
			return TOLERABLE;
		default:
			return DIS_SATISFIED;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public UserEvaluation valueOfIntString(Object str) {
		if (str == null)
			return NO_EVALUATION;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
