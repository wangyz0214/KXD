package kxd.remote.scs.util.emun;

public enum OrgTermStatus {
	PROVINCECONFIRM {
		@Override
		public String toString() {
			return "待总部确认";
		}
	},
	PROVINCESUCC {
		@Override
		public String toString() {
			return "省份信息发布成功";
		}
	},
	UNIONSUCC {
		@Override
		public String toString() {
			return "银联信息发布成功";
		}
	},
	TERMSUCC {
		@Override
		public String toString() {
			return "信息发布完成";
		}
	};

	static public OrgTermStatus valueOf(int v) {
		switch (v) {
		case 0:
			return PROVINCECONFIRM;
		case 1:
			return PROVINCESUCC;
		case 2:
			return UNIONSUCC;
		case 3:
			return TERMSUCC;
		default:
			return PROVINCECONFIRM;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public OrgTermStatus valueOfIntString(Object str) {
		if (str == null)
			return PROVINCECONFIRM;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
