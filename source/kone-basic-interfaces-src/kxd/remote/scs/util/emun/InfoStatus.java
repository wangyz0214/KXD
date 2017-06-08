package kxd.remote.scs.util.emun;

public enum InfoStatus {

	PUBLISH {
		@Override
		public String toString() {
			return "待厂商提交终端信息";
		}
	},
	MANUFOPERATE {
		@Override
		public String toString() {
			return "待省分确认";
		}
	},
	PROVINCECONFIRM {
		@Override
		public String toString() {
			return "待总部确认";
		}
	},
	HQCONFIRM {
		@Override
		public String toString() {
			return "待提交平台信息";
		}
	},
	KXDOPERATE {
		@Override
		public String toString() {
			return "待总部确认平台信息";
		}
	},
	HQFINISH {
		@Override
		public String toString() {
			return "待省分转发银联";
		}
	},
	PROVINCETRANS {
		@Override
		public String toString() {
			return "待银联提交商户信息";
		}
	},
	UNIONPAYOPERATE {
		@Override
		public String toString() {
			return "待省分确认商户信息";
		}
	},
	FINISH {
		@Override
		public String toString() {
			return "信息发布完成";
		}
	};

	static public InfoStatus valueOf(int v) {
		switch (v) {
		case 0:
			return PUBLISH;
		case 1:
			return MANUFOPERATE;
		case 2:
			return PROVINCECONFIRM;
		case 3:
			return HQCONFIRM;
		case 4:
			return KXDOPERATE;
		case 5:
			return HQFINISH;
		case 6:
			return PROVINCETRANS;
		case 7:
			return UNIONPAYOPERATE;
		default:
			return FINISH;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	public String getString() {
		switch (this) {
		case PUBLISH:
			return PUBLISH.toString();
		case MANUFOPERATE:
			return MANUFOPERATE.toString();
		case PROVINCECONFIRM:
			return PROVINCECONFIRM.toString();
		case HQCONFIRM:
			return HQCONFIRM.toString();
		case KXDOPERATE:
			return KXDOPERATE.toString();
		case HQFINISH:
			return HQFINISH.toString();
		case PROVINCETRANS:
			return PROVINCETRANS.toString();
		case UNIONPAYOPERATE:
			return UNIONPAYOPERATE.toString();
		default:
			return FINISH.toString();
		}
	}

	static public InfoStatus valueOfIntString(Object str) {
		if (str == null)
			return PUBLISH;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
