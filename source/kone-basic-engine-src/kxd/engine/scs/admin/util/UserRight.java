package kxd.engine.scs.admin.util;

public class UserRight {
	public static final int EXIT = 10000;// 退出
	public static final int MY = 10001;// 我的
	public static final int PROFILE = 10002;// 个人资料
	public static final int MODIFYPWD = 10003; // 修改密码
	public static final int ADDCOMMONUSE = 10004; // 添加我最常用功能

	public static final int TERMMONITOR = 10099;// 终端监控

	public static final int APPDEPLOY = 11000;// 部署应用
	public static final int APPCATEGORY = 11010;// 应用分类管理
	public static final int APPCATEGORY_EDIT = 11011;// 编辑应用分类
	public static final int APP = 11020;// 应用管理
	public static final int APP_EDIT = 11021;// 编辑分类
	public static final int PAYWAY = 11030;// 收费渠道管理
	public static final int PAYWAY_EDIT = 11031;// 编辑收费渠道
	public static final int PAYITEM = 11040;// 收费项目管理
	public static final int PAYITEM_EDIT = 11041;// 编辑收费项目
	public static final int BUSINESSCATEGORY = 11050;// 业务分类管理
	public static final int BUSINESSCATEGORY_EDIT = 11050;// 编辑业务分类
	public static final int BUSINESS = 11060;// 业务管理
	public static final int BUSINESS_EDIT = 11060;// 编辑分类
	public static final int TRADECODE = 11070;// 交易管理
	public static final int TRADECODE_EDIT = 11070;// 编辑交易
	public static final int PAGEELEMENT = 11080;// 业务页面管理
	public static final int PAGEELEMENT_EDIT = 11081;// 编辑业务页面
	public static final int COMMINTERFACE = 11090;// 接口管理
	public static final int COMMINTERFACE_EDIT = 11091;// 编辑接口信息

	public static final int RIGHT = 12000;// 权限管理
	public static final int ROLE = 12010;// 角色管理
	public static final int ROLE_EDIT = 12011;// 编辑角色
	public static final int ORG = 12020;// 机构网点管理
	public static final int ORG_EDIT = 12021;// 编辑机构网点
	public static final int MANUF = 12030;// 厂商管理
	public static final int MANUF_EDIT = 12031;// 编辑厂商
	public static final int USER = 12040;// 用户管理
	public static final int USER_EDIT = 12041;// 编辑用户

	public static final int DEVICEMGR = 13000;// 驱动管理
	public static final int ALARMCATEGORY = 13001;// 告警分类管理
	public static final int ALARMCATEGORY_EDIT = 13002;// 编辑告警分类
	public static final int DEVICETYPEDRIVER = 13010;// 模块驱动管理
	public static final int DEVICETYPEDRIVER_EDIT = 13011;// 编辑模块驱动
	public static final int DEVICETYPE = 13020;// 模块管理
	public static final int DEVICETYPE_EDIT = 13021;// 编辑模块
	public static final int DEVICEDRIVER = 13030;// 硬件管理
	public static final int DEVICEDRIVER_EDIT = 13031;// 编辑硬件
	public static final int DEVICE = 13040;// 硬件管理
	public static final int DEVICE_EDIT = 13041;// 编辑硬件

	public static final int TERMMGR = 14000;// 终端管理
	public static final int TERMTYPE = 14020;// 型号管理
	public static final int TERMTYPE_EDIT = 14021;// 编辑型号
	public static final int BANKTERM = 14010;// 终端管理
	public static final int BANKTERM_EDIT = 14011;// 编辑终端
	public static final int TERM = 14030;// 终端管理
	public static final int TERM_EDIT = 14031;// 编辑终端
	public static final int TERM_EDITGUID = 14032;// 编辑终端的GUID

	public static final int SETTLEMENT = 15000;// 结算系统
	public static final int TRADEPROC = 15010;// 交易处理
	public static final int RETURN_MONEY_TODAY = 15011;// 退款(当日)
	public static final int RETURN_MONEY_NEXTDAY = 15012;// 退款(隔日)
	public static final int RETURN_MONEY_NEXTMONTH = 15013;// 退款(隔月)
	public static final int UNDO_RETURN_MONEY_TODAY = 15014;// 取消退款(操作当日)
	public static final int UNDO_RETURN_MONEY_NEXTDAY = 15015;// 取消退款(操作隔日)
	public static final int UNDO_RETURN_MONEY_NEXTMONTH = 15016;// 取消退款(操作隔月)
	public static final int SUCCESS_TRADE_RETURN = 15020;// 成功交易退款
	public static final int UNIONPAY_TRADE_RETURN = 15030;// 银联退款
	public static final int REFUND_LOG = 15040;// 退款明细查询

	public static final int REPORT = 16000;// 报表系统
	public static final int LOGINUSERS_REPORT = 16010;// 登录用户报表
	public static final int VISITUSERS_REPORT = 16011;// 访问用户报表
	public static final int TRANSACTION_REPORT = 16012;// 业务量分析报表
	public static final int RECEIVABLE_REPORT = 16013;// 营业款账报表
	public static final int BUSINESSVISIT_REPORT = 16014;// 业务点击率报表
	public static final int USERATE_REPORT = 16015;// 设备使用率报表
	public static final int FAULT_REPORT = 16016;// 设备故障报表
	public static final int PRINT_REPORT = 16017;// 打印统计报表

	public static final int FILE = 17000;// 文件服务
	public static final int FILECATEGORY = 17010;// 文件分类管理
	public static final int FILECATEGORY_EDIT = 17011;// 编辑文件分类
	public static final int FILEHOST = 17020;// 文件主机管理
	public static final int FILEHOST_EDIT = 17021;// 编辑文件主机
	public static final int FILEOWNER = 17030;// 文件属主管理
	public static final int FILEOWNER_EDIT = 17031;// 编辑文件属主
	public static final int FILEUSER = 17040;// 文件用户管理
	public static final int FILEUSER_EDIT = 17041;// 编辑文件用户

	public static final int ADINFO = 18000;// 广告与信息发布
	public static final int PRINTADCATEGORY = 18010;// 打印广告分类管理
	public static final int PRINTADCATEGORY_EDIT = 18011;// 编辑打印广告分类
	public static final int PRINTAD = 18020;// 打印广告管理
	public static final int PRINTAD_EDIT = 18021;// 编辑打印广告
	public static final int ADCATEGORY = 18030;// 广告分类管理
	public static final int ADCATEGORY_EDIT = 18031;// 编辑广告分类
	public static final int ORGAD = 18040;// 机构广告管理
	public static final int ORGAD_EDIT = 18041;// 编辑机构广告
	public static final int TERMAD = 18050;// 终端广告管理
	public static final int TERMAD_EDIT = 18051;// 编辑终端广告
	public static final int INFOCATEGORY = 18060;// 信息分类管理
	public static final int INFOCATEGORY_EDIT = 18061;// 编辑信息分类
	public static final int INFO = 18070;// 信息管理
	public static final int INFO_EDIT = 18071;// 编辑信息
	public static final int AUDIT = 18200;// 广告与信息审核
	public static final int AUDIT_PUB = 18201;// 发布审核
	public static final int AUDIT_DEL = 18202;// 删除审核

	public static final int ORGBUSINESSOPENCLOSE = 19010;// 业务停开
	public static final int ORGBUSINESSOPENCLOSE_EDIT = 19011;// 业务停开
	public static final int TERMBUSINESSOPENCLOSE = 19020;// 业务停开
	public static final int TERMBUSINESSOPENCLOSE_EDIT = 19021;// 业务停开
	public static final int INVOICE_CONFIG = 19030;// 发票配置
	public static final int INVOICE_CONFIG_EDIT = 19031;// 编辑发票配置

	public static final int SYSTEM = 38000;// 系统管理
	public static final int PRINTTYPE = 38100;// 打印类型管理
	public static final int PRINTTYPE_EDIT = 38101;// 编辑打印类型
	public static final int DISABLEPRINTACCOUNT = 38200;// 编辑禁止查询打印的号码
	public static final int DISABLEPRINTACCOUNT_EDIT = 38201;// 编辑禁止查询打印的号码
	public static final int INVOICE_TEMPLATE = 38300;// 发票模板管理
	public static final int INVOICE_TEMPLATE_EDIT = 38301;// 编辑发票模板

	public static final int OTHER = 58000;// 系统管理
	public static final int ADDACCOUNTPRINTTIMES = 58100;// 添加一个号码的打印次数
}
