package kxd.remote.scs.util.emun;

/**
 *日志文件类型分类
 * 
 * @author 刘祯曌
 * 
 * @time：2010-3-25
 */
public enum FileType {
	BOOT {

		@Override
		public String toString() {
			return "boot";
		}
	},
	CHECK {

		@Override
		public String toString() {
			return "check";
		}
	},
	KONE4 {

		@Override
		public String toString() {
			return "KONE4";
		}
	},
	PAY {

		@Override
		public String toString() {
			return "pay";
		}
	},
	QUERY {

		@Override
		public String toString() {
			return "query";
		}
	},
	SERVER {

		@Override
		public String toString() {
			return "server";
		}
	},
	UMS {

		@Override
		public String toString() {
			return "ums";
		}
	},
	UNIONPAY {

		@Override
		public String toString() {
			return "unionpay";
		}
	};

	static public FileType valueOf(int v) {
		switch (v) {
		case 0:
			return BOOT;
		case 1:
			return CHECK;
		case 2:
			return KONE4;
		case 3:
			return PAY;
		case 4:
			return QUERY;
		case 5:
			return SERVER;
		case 6:
			return UMS;
		case 7:
			return UNIONPAY;
		default:
			return PAY;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public FileType valueOfIntString(Object str) {
		if (str == null)
			return PAY;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
