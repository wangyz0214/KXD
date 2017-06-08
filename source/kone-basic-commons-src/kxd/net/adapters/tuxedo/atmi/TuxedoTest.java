package kxd.net.adapters.tuxedo.atmi;

import java.io.File;

import kxd.util.DataUnit;
import kxd.util.KoneException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class TuxedoTest {
	static Logger logger = Logger.getLogger(TuxedoTest.class);
	static int PROVINCE_CODE = 167793639;
	static int IN_MODE_CODE = 167793100;
	static int TRADE_EPARCHY_CODE = 167794127;
	static int TRADE_CITY_CODE = 167794121;
	static int TRADE_DEPART_ID = 167794126;
	static int TRADE_STAFF_ID = 167794134;
	static int X_TRANS_CODE = 167782161;
	static int TRADE_DEPART_PASSWD = 167794435;
	static int SERIAL_NUMBER = 167793921;
	static int X_GETMODE = 33564437;
	static int CUST_NAME = 167792603;
	static int X_RESULTCODE = 33564434;
	static int X_RESULTINFO = 167782163;
	static int OPEN_DATE = 167793362;
	static int BRAND = 167792386;
	static int SCORE_VALUE = 167793906;
	static int WRITEOFF_MODE = 167794325;
	static int REMOVE_TAG = 167793739;
	static int EPARCHY_CODE = 167792808;
	static int ALL_BALANCE = 167792254;
	static int ALLROWE_FEE = 167792253;

	static public final int JSCX = 167775462; // 接收参数
	static public final int FSCX = 167775460; // 发送参数
	static public final int PARAM0 = 167775360; // 参数0
	static public final int PARAM1 = 167775361; // 参数0
	static public final int PARAM2 = 167775362; // 参数0
	static public final int PARAM3 = 167775363; // 参数0
	static public final int PARAM4 = 167775364; // 参数0
	static public final int PARAM5 = 167775365; // 参数0
	static public final int PARAM6 = 167775366; // 参数0
	static public final int PARAM7 = 167775367; // 参数0
	static public final int PARAM8 = 167775368; // 参数0
	static public final int PARAM9 = 167775369; // 参数0
	static public final int PARAM10 = 167775370; // 参数0
	static public final int PARAM11 = 167775371; // 参数0
	static public final int PARAM12 = 167775372; // 参数0
	static public final int PARAM13 = 167775373; // 参数0
	static public final int PARAM14 = 167775374; // 参数0
	static public final int RESULTCODE = 167775480; // 返回码
	static public final int RESULTERRINFO = 167775481; // 返回信息
	static public final int RESULTRECORDS = 167775414; // 返回记录数

	static public final int F_RECV = 167775414;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = new File("").getAbsolutePath();
		try {
			path = new TuxedoTest().getClass().getResource("/").getPath();
		} catch (Throwable e) {
		}
		path = path.replace("%20", " ");
		System.out.println(path);
		PropertyConfigurator.configure(path + "/log4j.properties");
		try {
			Tuxedo conn = new Tuxedo("//132.232.55.21:7311", true);
			StringBuffer input = new StringBuffer(10), output = new StringBuffer(
					10);
			input.write(0, "asddffff10");
			conn.tpcall("TOUPPER", input, output, 0);
			System.out.println(output.read());
			conn.dispose();
			conn = new Tuxedo("//132.228.96.48:3190", true);
			FMLBuffer32 input32 = new FMLBuffer32(10240), output32 = new FMLBuffer32(
					10240);
			input32.add(167772398, "CUST_NAME_QR".getBytes());
			input32.add(33554562, DataUnit.longToBytes(10003));
			input32.add(167783161, "1005".getBytes());
			input32.add(167772272, "0512".getBytes());
			input32.add(167772269, "051258597852".getBytes());
			logger.debug("============[start]=============");
			input32.print();
			logger.debug("=============[end]==============");
			conn.tpcall("UN_USER_INFO_R", input32, output32, 0);
			logger.debug("============[start]=============");
			output32.print();
			logger.debug("=============[end]==============");
			conn.dispose();
		} catch (TuxedoException e) {
			e.printStackTrace();
		}
		// TuxedoTest.test();
		// test: 10.109.2.154:28770,operatorid=aagh00,passwd=FKHAKLCCBMJDECFH
		// orgid=010199900
		// TuxedoTest.payLogin("//10.142.193.13:7310", "13808192358",
		// "010199900",
		// "aagh00");
		// TuxedoTest.pay("//10.109.2.154:28770", "13808192358","5",
		// "010199900", "aagh00","FKHAKLCCBMJDECFH");
		// TuxedoTest.pay(wsnAddr, phone, money, orgid, operatorid, passwd);
	}

	static public void payLogin(String wsnAddr, String phone, String orgid,
			String operatorid) {
		Tuxedo conn = null;
		FMLBuffer32 input = null, output = null;
		try {
			input = new FMLBuffer32(1024);
			output = new FMLBuffer32(1024);
			input.write(FSCX, 0, "1");
			input.write(JSCX, 0, "13");
			input.write(PARAM0, 0, "a" + phone + "b");
			input.print();
			// input.read(PARAM14, 0, 100);
			conn = new Tuxedo(wsnAddr, true);
			tpcall(conn, "TOUPPER", input, output);
			// System.out.println(output.read(167775363, 0, 20)); // 余额
			// input.reset();
			// output.reset();
			// input.write(FSCX, 0, "4");
			// input.write(JSCX, 0, "3");
			// input.write(PARAM0, 0, phone); // 手机号码
			// input.write(PARAM1, 0, orgid); // 机构编码
			// input.write(PARAM2, 0, operatorid); // 工号
			// conn.dispose();
			// conn = new Tuxedo("//10.105.2.104:26000", true);
			// input.write(PARAM3, 0, "1300"); // 操作码
			// tpcall(conn, "s1300BaseMsg", input, output);
			// String tmp = output.read(167775402, 0, 20).trim();// 零头
			// if (tmp.startsWith("-"))
			// tmp = tmp.substring(1);
			// else
			// tmp = "0";
			// int lt = (int) (Double.parseDouble(tmp) * 100);
			// tmp = output.read(167775378, 0, 20);// 欠费
			// int qf = (int) (Double.parseDouble(tmp) * 100);
			// qf = (qf + lt) / 100;
			// System.out.println(output.read(PARAM5, 0, 90)); // 户名
			// System.out.println(Integer.toString(qf)); // 欠费
		} catch (KoneException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (conn != null)
			conn.dispose();
		if (input != null)
			input.dispose();
		if (output != null)
			output.dispose();
	}

	static public void pay(String wsnAddr, String phone, String money,
			String orgid, String operatorid, String passwd) {
		Tuxedo conn = null;
		FMLBuffer32 input = null, output = null;
		try {
			conn = new Tuxedo(wsnAddr, true);
			input = new FMLBuffer32(1024);
			output = new FMLBuffer32(1024);
			input.write(FSCX, 0, "15");
			input.write(JSCX, 0, "3");
			input.write(PARAM0, 0, operatorid); // 工号
			input.write(PARAM1, 0, passwd); // 工号密码
			input.write(PARAM2, 0, orgid); // 机构编码
			input.write(PARAM3, 0, "1300"); // 操作码
			input.write(PARAM4, 0, "0"); // 帐户代码
			input.write(PARAM5, 0, phone);// 手机号码
			input.write(PARAM6, 0, money);// 缴费金额
			input.write(PARAM7, 0, "0");// 交费方式
			input.write(PARAM8, 0, "0");// 滞纳金优惠率
			input.write(PARAM9, 0, "0");// 补收月租优惠率
			input.write(PARAM10, 0, "0");// 银行代码
			input.write(PARAM11, 0, "0");// 支票号码
			input.write(PARAM12, 0,
					phone + DataUnit.formatCurrentDateTime("yyyyMMddHHmmss"));// 交费注释
			input.write(
					PARAM13,
					0,
					"bbb134" + phone
							+ DataUnit.formatCurrentDateTime("yyyyMMddHHmmss"));// 交费标志
			input.write(PARAM14, 0, "0");// 取整前金额
			tpcall(conn, "s1300Cfm", input, output);
			System.out.println(output.read(PARAM1, 0, 50)); // 流水号
		} catch (KoneException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (conn != null)
			conn.dispose();
		if (input != null)
			input.dispose();
		if (output != null)
			output.dispose();
	}

	static private void tpcall(Tuxedo tuxedo, String service,
			FMLBuffer32 input, FMLBuffer32 output) throws KoneException {
		try {
			if (tuxedo.tpcall(service, input, output, 0) == -1)
				throw new KoneException(TuxedoAtmi.tperrno() + ": "
						+ TuxedoAtmi.getErrorDetailString());
			output.print();
			// String retCode = output.read(RESULTCODE, 0, 20).trim();
			// if (Integer.parseInt(retCode) == 0)
			// return;
			// throw new KoneException(output.read(RESULTERRINFO, 0,
			// 200).trim());
		} catch (KoneException e) {
			throw e;
		} catch (Throwable e) {
			throw new KoneException("交易失败，错误：" + e.getMessage(), e);
		}
	}

	static public void test() {
		Tuxedo con = null;
		// FMLBuffer32 input=null,output=null;
		StringBuffer input = null, output = null;
		FMLBuffer32 bbb = null;
		byte[] buf = new byte[100];
		try {
			bbb = new FMLBuffer32(1024);
			bbb.write(PARAM0, 0, "afds");
			bbb.write(PARAM1, 0, "afds");
			bbb.write(PARAM2, 0, "afds");
			bbb.write(PARAM3, 0, "afds");
			FMLID row = new FMLID(), col = new FMLID();
			int len;
			while ((len = bbb.next(row, col, buf, 100)) > 0)
				System.out.println(new String(buf, 0, len - 1));
			input = new StringBuffer(1024);
			output = new StringBuffer(1024);
			con = new Tuxedo("//192.168.1.202:7310", true);
			TuxedoSession session = new TuxedoSession(con, "AUDITC", null,
					TuxedoAtmi.TPSENDONLY);
			for (int i = 0; i < 11; i++) {
				input.write(0, Integer.toString(i));
				int event = session.send(input, TuxedoAtmi.TPRECVONLY);
				event = session.recv(output, TuxedoAtmi.TPNOCHANGE);
				if (TuxedoAtmi.tperrno() != TuxedoAtmi.TPEEVENT
						|| (event != TuxedoAtmi.TPEV_SENDONLY && event != TuxedoAtmi.TPEV_SVCSUCC))
					break;
				System.out.println(output.read());
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (con != null)
			con.dispose();
		if (input != null)
			input.dispose();
		if (output != null)
			output.dispose();
	}
}
