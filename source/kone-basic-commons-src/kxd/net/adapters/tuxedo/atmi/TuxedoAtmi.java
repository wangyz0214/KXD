package kxd.net.adapters.tuxedo.atmi;

/**
 * Tuxedo的Jndi封闭包
 * 
 * @author zhaom
 * 
 */
public class TuxedoAtmi {
	static private boolean tuxedo32 = false;
	public static void main(String[] args){
		//System.out.println(tuxedo32);
	}
	public static boolean isTuxedo32() {
		return tuxedo32;
	}

	public static void setTuxedo32(boolean tuxedo32) {
		TuxedoAtmi.tuxedo32 = tuxedo32;
	}

	static {
		String str = System.getProperty("os.arch", null);
		tuxedo32 = str == null ? false : str.contains("86");
	}
	static public final int TPU_SIG = 0x00000001;/* signal based notification */
	static public final int TPU_DIP = 0x00000002;/* dip-in based notification */
	static public final int TPU_IGN = 0x00000004;/* ignore unsolicited messages */
	static public final int TPSA_FASTPATH = 0x00000008;
	/**
	 * System access == fastpath
	 */
	static public final int TPSA_PROTECTED = 0x00000010;
	/**
	 * System access == protected
	 */
	static public final int TPMULTICONTEXTS = 0x00000020;/* Enable MULTI context */
	static public final int TPU_THREAD = 0x00000040;/* thread based notification */

	/**
	 * 错误的上下文
	 */
	static public final int TPINVALIDCONTEXT = -1;
	/**
	 * 空的上下文
	 */
	static public final int TPNULLCONTEXT = -2;
	/**
	 * 单上下文
	 */
	static public final int TPSINGLECONTEXT = 0;

	/**
	 * 无验证
	 */
	static public final int TPNOAUTH = 0;
	/**
	 * 系统验证
	 */
	static public final int TPSYSAUTH = 1;
	/**
	 * 系统和应用验证
	 */
	static public final int TPAPPAUTH = 2;

	/**
	 * 最小的错误码
	 */
	static public final int TPMINVAL = 0;
	static public final int TPEABORT = 1;
	static public final int TPEBADDESC = 2;
	static public final int TPEBLOCK = 3;
	/**
	 * 参数错误
	 */
	static public final int TPEINVAL = 4;
	static public final int TPELIMIT = 5;
	/**
	 * The client cannot join the application because of space limitations.
	 */
	static public final int TPENOENT = 6;
	/**
	 * An operating system error has occurred.
	 */
	static public final int TPEOS = 7;
	/**
	 * The client cannot join the application because it does not have
	 * permission to do so or because it has not supplied the correct
	 * application password. Permission may be denied based on an invalid
	 * application password, failure to pass application-specific
	 * authentication, or use of restricted names. tpurcode() may be set by an
	 * application-specific authentication server to explain why the client
	 * cannot join the application.
	 */
	static public final int TPEPERM = 8;
	/**
	 * tpinit() has been called improperly. For example: (a) the caller is a
	 * server; (b) the TPMULTICONTEXTS flag has been specified in single-context
	 * mode; or (c) the TPMULTICONTEXTS flag has not been specified in
	 * multicontext mode.
	 */
	static public final int TPEPROTO = 9;
	static public final int TPESVCERR = 10;
	static public final int TPESVCFAIL = 11;
	/**
	 * A BEA Tuxedo system error has occurred. The exact nature of the error is
	 * written to a log file.
	 */
	static public final int TPESYSTEM = 12;
	static public final int TPETIME = 13;
	static public final int TPETRAN = 14;
	static public final int TPGOTSIG = 15;
	static public final int TPERMERR = 16;
	static public final int TPEITYPE = 17;
	static public final int TPEOTYPE = 18;
	static public final int TPERELEASE = 19;
	static public final int TPEHAZARD = 20;
	static public final int TPEHEURISTIC = 21;
	static public final int TPEEVENT = 22;
	static public final int TPEMATCH = 23;
	static public final int TPEDIAGNOSTIC = 24;
	static public final int TPEMIB = 25;
	/**
	 * 最大的错误码
	 */
	static public final int TPMAXVAL = 26;

	static public final int FMINVAL = 0;/* bottom of error message codes */
	static public final int FALIGNERR = 1;/* fielded buffer not aligned */
	static public final int FNOTFLD = 2;/* buffer not fielded */
	static public final int FNOSPACE = 3;/* no space in fielded buffer */
	static public final int FNOTPRES = 4;/* field not present */
	static public final int FBADFLD = 5;/* unknown field number or type */
	static public final int FTYPERR = 6;/* illegal field type */
	static public final int FEUNIX = 7;/* unix system call error */
	static public final int FBADNAME = 8;/* unknown field name */
	static public final int FMALLOC = 9;/* malloc failed */
	static public final int FSYNTAX = 10;/* bad syntax in boolean expression */
	static public final int FFTOPEN = 11;/* cannot find or open field table */
	static public final int FFTSYNTAX = 12;/* syntax error in field table */
	static public final int FEINVAL = 13;/* invalid argument to function */
	static public final int FBADTBL = 14;/*
										 * destructive concurrent access to
										 * field table
										 */
	static public final int FBADVIEW = 15;/* cannot find or get view */
	static public final int FVFSYNTAX = 16;/* bad viewfile */
	static public final int FVFOPEN = 17;/* cannot find or open viewfile */
	static public final int FBADACM = 18;/* ACM contains negative value */
	static public final int FNOCNAME = 19;/* cname not found */
	static public final int FEBADOP = 20;/* operation invalid for field type */
	static public final int FMAXVAL = 21;/* top of error message codes */

	static public final int TPNOBLOCK = 0x00000001;/* non-blocking send/rcv */
	static public final int TPSIGRSTRT = 0x00000002;/* restart rcv on interrupt */
	static public final int TPNOREPLY = 0x00000004;/* no reply expected */
	static public final int TPNOTRAN = 0x00000008;/* not sent in transaction mode */
	static public final int TPTRAN = 0x00000010;/* sent in transaction mode */
	static public final int TPNOTIME = 0x00000020;/* no timeout */
	static public final int TPABSOLUTE = 0x00000040;/*
													 * absolute value on
													 * tmsetprio
													 */
	static public final int TPGETANY = 0x00000080;/* get any valid reply */
	static public final int TPNOCHANGE = 0x00000100;/*
													 * force incoming buffer to
													 * match
													 */
	static public final int RESERVED_BIT1 = 0x00000200;/*
														 * reserved for future
														 * use
														 */
	static public final int TPCONV = 0x00000400;/* conversational service */
	static public final int TPSENDONLY = 0x00000800;/* send-only mode */
	static public final int TPRECVONLY = 0x00001000;/* recv-only mode */
	static public final int TPACK = 0x00002000;/* */

	static public final int TPEV_DISCONIMM = 0x0001;
	static public final int TPEV_SVCERR = 0x0002;
	static public final int TPEV_SVCFAIL = 0x0004;
	static public final int TPEV_SVCSUCC = 0x0008;
	static public final int TPEV_SENDONLY = 0x0020;

	static public final int TPED_MINVAL = 0; /* minimum error message */
	static public final int TPED_SVCTIMEOUT = 1;
	static public final int TPED_TERM = 2;
	static public final int TPED_NOUNSOLHANDLER = 3;
	static public final int TPED_NOCLIENT = 4;
	static public final int TPED_DOMAINUNREACHABLE = 5;
	static public final int TPED_CLIENTDISCONNECTED = 6;
	static public final int TPED_PERM = 7;
	static public final int TPED_OTS_INTERNAL = 8;
	static public final int TPED_INVALID_CERTIFICATE = 9;
	static public final int TPED_INVALID_SIGNATURE = 10;
	static public final int TPED_DECRYPTION_FAILURE = 11;
	static public final int TPED_INVALIDCONTEXT = 12;
	static public final int TPED_MAXVAL = 13; /* maximum error message */

	/**
	 * 设置环境变量
	 * 
	 * @param cfg
	 *            byte[] 环境变量字串，格式：变量名=内容
	 * @return 0：设置成功；其他值：设置失败
	 */
	public static int tuxputenv(byte[] cfg) {
		if (tuxedo32)
			return ATMI32.tuxputenv(cfg);
		else
			return ATMI.tuxputenv(cfg);
	}

	/**
	 * 获取环境变量的内容
	 * 
	 * @param cfg
	 *            byte[] 变量名
	 * @return byte[] 非null：变量内容；null：找不到变量
	 */
	public static byte[] tuxgetenv(byte[] cfg) {
		if (tuxedo32)
			return ATMI32.tuxgetenv(cfg);
		else
			return ATMI.tuxgetenv(cfg);
	}

	/**
	 * 分配tpinit()要用到的缓冲区
	 * 
	 * @return int >0: 分配的缓冲区句柄；0：分配失败
	 */
	public static long allocInit() {
		if (tuxedo32)
			return ATMI32.allocInit();
		else
			return ATMI.allocInit();
	}

	/**
	 * 获取缓冲区的长度
	 * 
	 * @param int bufHandle
	 * @return
	 */
	public static int getBufferSize(long bufHandle) {
		if (tuxedo32)
			return ATMI32.getBufferSize((int) bufHandle);
		else
			return ATMI.getBufferSize(bufHandle);
	}

	/**
	 * 设置tpinit()缓冲区的参数
	 * 
	 * @param intiBufhandle
	 *            int 用allocInit()分配的缓冲区句柄
	 * @param usrname
	 *            byte[] 用户
	 * @param cltname
	 * @param passwd
	 * @param grpname
	 * @param flags
	 *            int 标志
	 * @param data
	 */
	public static void setInitParams(long intiBufhandle, byte[] usrname,
			byte[] cltname, byte[] passwd, byte[] grpname, int flags,
			byte[] data) {
		if (tuxedo32)
			ATMI32.setInitParams((int) intiBufhandle, usrname, cltname, passwd,
					grpname, flags, data);
		else
			ATMI.setInitParams(intiBufhandle, usrname, cltname, passwd,
					grpname, flags, data);
	}

	/**
	 * 检查验证类型
	 * 
	 * @return int 验证类型
	 */
	public static int tpchkauth() {
		if (tuxedo32)
			return ATMI32.tpchkauth();
		else
			return ATMI.tpchkauth();
	}

	/**
	 * 建立tuxedo连接
	 * 
	 * @param intiBufhandle
	 *            int
	 * @return >=0 成功； -1
	 *         执行失败,此时可执行tperrno()返回见错误码常量描述[TPEINVAL,TPENOENT,TPEPERM
	 *         ,TPEPROTO,TPESYSTEM,TPEOS]
	 */
	public static int tpinit(long intiBufhandle) {
		if (tuxedo32)
			return ATMI32.tpinit((int) intiBufhandle);
		else
			return ATMI.tpinit(intiBufhandle);
	}

	/**
	 * 断开tuxedo连接
	 * 
	 * @return >=0 成功； -1
	 *         执行失败,此时可执行tperrno()返回见错误码常量描述[TPEPROTO,TPESYSTEM,TPEOS]
	 */
	public static int tpterm() {
		if (tuxedo32)
			return ATMI32.tpterm();
		else
			return ATMI.tpterm();
	}

	/**
	 * 释放分配的缓冲区
	 * 
	 * @param bufhandle
	 *            int 分配好的缓冲区对象句柄
	 */
	public static void tpfree(long bufhandle) {
		if (tuxedo32)
			ATMI32.tpfree((int) bufhandle);
		else
			ATMI.tpfree(bufhandle);
	}

	/**
	 * 获取当前的上下文句柄
	 * 
	 * @param flags
	 *            保留为0
	 * @return >0 句柄；<=0时，见TPSINGLECONTEXT,TPNULLCONTEXT,TPINVALIDCONTEXT
	 */
	public static int tpgetctxt(int flags) {
		if (tuxedo32)
			return ATMI32.tpgetctxt(flags);
		else
			return ATMI.tpgetctxt(flags);
	}

	/**
	 * 设置当前的上下文句柄
	 * 
	 * @param context
	 *            int 先前保存的句柄或TPNULLCONTEXT
	 * @param flags
	 *            保留为0
	 * @return >=0 成功； -1
	 *         执行失败,此时可执行tperrno()返回见错误码常量描述[TPEINVAL,TPENOENT,TPEPROTO
	 *         ,TPESYSTEM,TPEOS]
	 */
	public static int tpsetctxt(int context, int flags) {
		if (tuxedo32)
			return ATMI32.tpsetctxt(context, flags);
		else
			return ATMI.tpsetctxt(context, flags);
	}

	/**
	 * 分配缓冲区
	 * 
	 * @param dataType
	 *            byte[] 缓冲区数据类型
	 * @param subType
	 *            byte[] 缓冲区数据子类型，通常为null
	 * @param size
	 *            int 缓冲区长度
	 * @return >0 分配的缓冲区句柄；其他值，分配缓冲区失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPEINVAL,TPENOENT
	 *         ,TPEPROTO,TPESYSTEM,TPEOS]
	 */
	public static long tpalloc(byte[] dataType, byte[] subType, int size) {
		if (tuxedo32)
			return ATMI32.tpalloc(dataType, subType, size);
		else
			return ATMI.tpalloc(dataType, subType, size);
	}

	/**
	 * 初始化FML缓冲区
	 * 
	 * @param bufhandle
	 *            int FML缓冲区句柄
	 * @return >= 成功；-1 失败 ,此时可执行tperrno()返回见错误码常量描述[FALIGNERR,FNOTFLD,FNOSPACE]
	 */
	public static int Finit(long bufhandle) {
		if (tuxedo32)
			return ATMI32.Finit((int) bufhandle);
		else
			return ATMI.Finit(bufhandle);
	}

	/**
	 * 初始化FML32缓冲区
	 * 
	 * @param bufhandle
	 *            int FML缓冲区句柄
	 * @return >=0 成功；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[FALIGNERR,FNOTFLD,FNOSPACE]
	 */
	public static int Finit32(long bufhandle) {
		if (tuxedo32)
			return ATMI32.Finit32((int) bufhandle);
		else
			return ATMI.Finit32(bufhandle);
	}

	/**
	 * 同步执行一次tpcall
	 * 
	 * @param service
	 * @param sendBuf
	 * @param sendLen
	 * @param recvBuf
	 * @param flags
	 *            int 标志[TPNOTRAN,TPNOCHANGE,TPNOBLOCK,TPNOTIME,TPSIGRSTRT]
	 * @return >=0 成功；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPEINVAL,TPENOENT,TPEITYPE,TPEOTYPE,
	 *         TPETRAN,TPETIME,TPESVCFAIL,TPESVCERR,TPEBLOCK,TPGOTSIG,TPEPROTO,
	 *         TPESYSTEM,TPEOS]
	 */
	public static int tpcall(byte[] service, long sendBuf, int sendLen,
			long recvBuf, int flags) {
		if (tuxedo32)
			return ATMI32.tpcall(service, (int) sendBuf, sendLen,
					(int) recvBuf, flags);
		else
			return ATMI.tpcall(service, sendBuf, sendLen, recvBuf, flags);
	}

	/**
	 * 异步执行一次tpcall
	 * 
	 * @param service
	 * @param sendBuf
	 * @param sendLen
	 * @param flags
	 *            int 标志[TPNOTRAN,TPNOCHANGE,TPNOBLOCK,TPNOTIME,TPSIGRSTRT]
	 * @return >0 acall描述符；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPEINVAL,TPENOENT,TPEITYPE,TPEOTYPE,
	 *         TPETRAN,TPETIME,TPESVCFAIL,TPESVCERR,TPEBLOCK,TPGOTSIG,TPEPROTO,
	 *         TPESYSTEM,TPEOS]
	 */
	public static int tpacall(byte[] service, long sendBuf, int sendLen,
			int flags) {
		if (tuxedo32)
			return ATMI32.tpacall(service, (int) sendBuf, sendLen, flags);
		else
			return ATMI.tpacall(service, sendBuf, sendLen, flags);
	}

	/**
	 * 取得异步的响应
	 * 
	 * @param acallHandle
	 *            acall描述符
	 * @param recvBuf
	 * @param flags
	 *            int 标志[TPGETANY,TPNOCHANGE,TPNOBLOCK,TPNOTIME,TPSIGRSTRT]
	 * @return >=0 成功；-1 失败 ,此时可执行tperrno()返回见错误码常量描述[TPEINVAL,TPEOTYPE,
	 *         TPEBADDESC
	 *         ,TPETIME,TPESVCFAIL,TPESVCERR,TPEBLOCK,TPGOTSIG,TPEPROTO
	 *         ,TPESYSTEM,TPEOS]
	 */
	public static int tpgetrply(int acallHandle, long recvBuf, int flags) {
		if (tuxedo32)
			return ATMI32.tpgetrply(acallHandle, (int) recvBuf, flags);
		else
			return ATMI.tpgetrply(acallHandle, recvBuf, flags);
	}

	/**
	 * 取消一次异常call
	 * 
	 * @param acallHandle
	 * @return >=0 成功；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPETRAN,TPEBADDESC,TPEPROTO
	 *         ,TPESYSTEM,TPEOS]
	 */
	public static int tpcancel(int acallHandle) {
		if (tuxedo32)
			return ATMI32.tpcancel(acallHandle);
		else
			return ATMI.tpcancel(acallHandle);
	}

	/**
	 * 获取请求优先级
	 * 
	 * @return >=0 优先级；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPENOENT,TPEPROTO,TPESYSTEM,TPEOS]
	 */
	public static int tpgprio() {
		if (tuxedo32)
			return ATMI32.tpgprio();
		else
			return ATMI.tpgprio();
	}

	/**
	 * 设置请求优先级
	 * 
	 * @param prio
	 *            int 优先级
	 * @param flags
	 *            int TPABSOLUTE
	 * @return >=0 成功；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPEINVAL,TPEPROTO,TPESYSTEM,TPEOS]
	 */
	public static int tpsprio(int prio, int flags) {
		if (tuxedo32)
			return ATMI32.tpsprio(prio, flags);
		else
			return ATMI.tpsprio(prio, flags);
	}

	/**
	 * 连接一次会话
	 * 
	 * @param service
	 * @param sendBuf
	 * @param sendLen
	 * @param flags
	 *            int
	 *            [TPNOTRAN,TPSENDONLY,TPRECVONLY,TPNOBLOCK,TPNOTIME,TPSIGRSTRT]
	 * @return >0 会话描述符；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPEINVAL,TPENOENT,TPEITYPE
	 *         ,TPELIMIT,TPETRAN,
	 *         TPETIME,TPEBLOCK,TPGOTSIG,TPEPROTO,TPESYSTEM,TPEOS]
	 */
	public static int tpconnect(byte[] service, long sendBuf, int sendLen,
			int flags) {
		if (tuxedo32)
			return ATMI32.tpconnect(service, (int) sendBuf, sendLen, flags);
		else
			return ATMI.tpconnect(service, sendBuf, sendLen, flags);
	}

	/**
	 * 断开一次会话连接
	 * 
	 * @param conHandle
	 *            int 会话描述符
	 * @return >=0 成功；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPEBADDESC,TPETIME,TPEPROTO
	 *         ,TPESYSTEM,TPEOS]
	 */
	public static int tpdiscon(int conHandle) {
		if (tuxedo32)
			return ATMI32.tpdiscon(conHandle);
		else
			return ATMI.tpdiscon(conHandle);
	}

	/**
	 * 发送数据
	 * 
	 * @param conHandle
	 *            int 会话描述符
	 * @param sendBuf
	 * @param sendLen
	 * @param flags
	 *            int
	 *            [TPNOTRAN,TPSENDONLY,TPRECVONLY,TPNOBLOCK,TPNOTIME,TPSIGRSTRT]
	 * @param event
	 *            byte[] [TPEV_DISCONIMM,TPEV_SVCERR,TPEV_SVCFAIL]
	 * @return >=0 成功；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPEINVAL,TPENOENT,TPEITYPE,
	 *         TPELIMIT,TPETRAN,
	 *         TPETIME,TPEBLOCK,TPGOTSIG,TPEPROTO,TPESYSTEM,TPEOS]
	 */
	public static int tpsend(int conHandle, long sendBuf, int sendLen,
			int flags, byte[] event) {
		if (tuxedo32)
			return ATMI32.tpsend(conHandle, (int) sendBuf, sendLen, flags,
					event);
		else
			return ATMI.tpsend(conHandle, sendBuf, sendLen, flags, event);
	}

	/**
	 * 接收数据
	 * 
	 * @param conHandle
	 *            int 会话描述符
	 * @param recvBuf
	 * @param flags
	 *            int [TPNOCHANGE,TPNOBLOCK,TPNOTIME,TPSIGRSTRT]
	 * @param event
	 *            int
	 *            [TPEV_DISCONIMM,TPEV_SENDONLY,TPEV_SVCERR,TPEV_SVCFAIL,TPEV_SVCSUCC
	 *            ]
	 * @return >=0 成功；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPEINVAL,TPENOENT,TPEITYPE,
	 *         TPELIMIT,TPETRAN,
	 *         TPETIME,TPEBLOCK,TPGOTSIG,TPEPROTO,TPESYSTEM,TPEOS]
	 */
	public static int tprecv(int conHandle, long recvBuf, int flags,
			byte[] event) {
		if (tuxedo32)
			return ATMI32.tprecv(conHandle, (int) recvBuf, flags, event);
		else
			return ATMI.tprecv(conHandle, recvBuf, flags, event);
	}

	/**
	 * 获取错误信息
	 * 
	 * @param errno
	 *            int 错误代码
	 * @return 错误信息
	 */
	public static byte[] tpstrerror(int errno) {
		if (tuxedo32)
			return ATMI32.tpstrerror(errno);
		else
			return ATMI.tpstrerror(errno);
	}

	/**
	 * 获取详细错误信息代码
	 * 
	 * @param errno
	 *            flags 保留为0
	 * @return 错误信息代码 TPED_CLIENTDISCONNECTED ... <0 错误，可通过tperrno获取错误码：TPEINVAL
	 */
	public static int tperrordetail(int flags) {
		if (tuxedo32)
			return ATMI32.tperrordetail(flags);
		else
			return ATMI.tperrordetail(flags);
	}

	/**
	 * 获取详细错误信息
	 * 
	 * @param errno
	 *            int 详细错误代码
	 * @param flags
	 *            0
	 * @return 错误信息
	 */
	public static byte[] tpstrerrordetail(int errno, int flags) {
		if (tuxedo32)
			return ATMI32.tpstrerrordetail(errno, flags);
		else
			return ATMI.tpstrerrordetail(errno, flags);
	}

	/**
	 * 获取错误代码
	 * 
	 * @return 错误码
	 */
	public static int tperrno() {
		if (tuxedo32)
			return ATMI32.tperrno();
		else
			return ATMI.tperrno();
	}

	/**
	 * 从缓冲区中读取数据
	 * 
	 * @param bufHandle
	 *            int 缓冲区句柄
	 * @param srcoffset
	 * @param length
	 * @param data
	 * @param dstoffset
	 * @return 0 未读到数据；>0 返回读取的数据长度
	 */
	public static int Sget(long bufHandle, int srcoffset, int length,
			byte[] data, int dstoffset) {
		if (tuxedo32)
			return ATMI32.Sget((int) bufHandle, srcoffset, length, data,
					dstoffset);
		else
			return ATMI.Sget(bufHandle, srcoffset, length, data, dstoffset);
	}

	/**
	 * 向缓冲区写数据
	 * 
	 * @param bufHandle
	 *            int 缓冲区句柄
	 * @param dstoffset
	 * @param data
	 * @param srcoffset
	 * @param length
	 * @return 0 未写数据；>0 返回写入的数据长度
	 */
	public static int Sset(long bufHandle, int dstoffset, byte[] data,
			int srcoffset, int length) {
		if (tuxedo32)
			return ATMI32.Sset((int) bufHandle, dstoffset, data, srcoffset,
					length);
		else
			return ATMI.Sset(bufHandle, dstoffset, data, srcoffset, length);
	}

	/**
	 * 获取FML数据
	 * 
	 * @param bufHandle
	 *            int FML缓冲区句柄
	 * @param row
	 *            int 行
	 * @param col
	 *            int 列
	 * @param data
	 *            byte[] 数据缓冲区
	 * @param size
	 *            int 需要读出的数据长度
	 * @return >0 返回读出的数据长度;0 已到缓冲区尾；-1 错误,此时可通过 Ferror() 获取错误码
	 */
	public static int Fget(long bufHandle, int row, int col, byte[] data,
			int size) {
		if (tuxedo32)
			return ATMI32.Fget((int) bufHandle, row, col, data, size);
		else
			return ATMI.Fget(bufHandle, row, col, data, size);
	}

	/**
	 * 获取FML32数据
	 * 
	 * @param bufHandle
	 *            int FML32缓冲区句柄
	 * @param row
	 *            int 行
	 * @param col
	 *            int 列
	 * @param data
	 *            byte[] 数据缓冲区
	 * @param size
	 *            int 需要读出的数据长度
	 * @return >0 返回读出的数据长度;0 已到缓冲区尾；-1 错误,此时可通过 Ferror() 获取错误码
	 */
	public static int Fget32(long bufHandle, int row, int col, byte[] data,
			int size) {
		if (tuxedo32)
			return ATMI32.Fget32((int) bufHandle, row, col, data, size);
		else
			return ATMI.Fget32(bufHandle, row, col, data, size);
	}

	/**
	 * 设置FML数据项
	 * 
	 * @param bufHandle
	 *            int FML缓冲区句柄
	 * @param row
	 *            int 行
	 * @param col
	 *            int 列
	 * @param data
	 *            byte[] 数据缓冲区
	 * @param size
	 *            int 需要读出的数据长度
	 * @return >=0 成功；-1 错误,此时可通过 Ferror() 获取错误码
	 */
	public static boolean Fset(long bufHandle, int row, int col, byte[] data) {
		if (tuxedo32)
			return ATMI32.Fset((int) bufHandle, row, col, data);
		else
			return ATMI.Fset(bufHandle, row, col, data);
	}

	/**
	 * 设置FML32数据项
	 * 
	 * @param bufHandle
	 *            int FML32缓冲区句柄
	 * @param row
	 *            int 行
	 * @param col
	 *            int 列
	 * @param data
	 *            byte[] 数据缓冲区
	 * @param size
	 *            int 需要读出的数据长度
	 * @return >=0 成功；-1 错误,此时可通过 Ferror() 获取错误码
	 */
	public static boolean Fset32(long bufHandle, int row, int col, byte[] data) {
		if (tuxedo32)
			return ATMI32.Fset32((int) bufHandle, row, col, data);
		else
			return ATMI.Fset32(bufHandle, row, col, data);
	}

	/**
	 * 添加FML数据项
	 * 
	 * @param bufHandle
	 *            int FML缓冲区句柄
	 * @param row
	 *            int 行
	 * @param data
	 *            byte[] 数据缓冲区
	 * @param size
	 *            int 需要读出的数据长度
	 * @return >=0 成功；-1 错误,此时可通过 Ferror() 获取错误码
	 */
	public static boolean Fadd(long bufHandle, int row, byte[] data) {
		if (tuxedo32)
			return ATMI32.Fadd((int) bufHandle, row, data);
		else
			return ATMI.Fadd(bufHandle, row, data);
	}

	/**
	 * 添加FML32数据项
	 * 
	 * @param bufHandle
	 *            int FML32缓冲区句柄
	 * @param row
	 *            int 行
	 * @param data
	 *            byte[] 数据缓冲区
	 * @param size
	 *            int 需要读出的数据长度
	 * @return >=0 成功；-1 错误,此时可通过 Ferror() 获取错误码
	 */
	public static boolean Fadd32(long bufHandle, int row, byte[] data) {
		if (tuxedo32)
			return ATMI32.Fadd32((int) bufHandle, row, data);
		else
			return ATMI.Fadd32(bufHandle, row, data);
	}

	/**
	 * 找到FML数据项
	 * 
	 * @param bufHandle
	 *            int FML缓冲区句柄
	 * @param row
	 *            byte[] 行 传入当前行号，传出下一数据行号
	 * @param col
	 *            byte[] 列 传入当前列号，传出下一数据列号
	 * @param data
	 *            byte[] 数据缓冲区
	 * @param size
	 *            int 需要读出的数据长度
	 * @return >0 返回读出的数据长度;0 已到缓冲区尾；-1 错误,此时可通过 Ferror() 获取错误码
	 */
	public static int Fnext(long bufHandle, int[] rowcol, byte[] data, int size) {
		if (tuxedo32)
			return ATMI32.Fnext((int) bufHandle, rowcol, data, size);
		else
			return ATMI.Fnext(bufHandle, rowcol, data, size);
	}

	public static int Foccur32(long bufHandle, int row) {
		if (tuxedo32)
			return ATMI32.Foccur32((int) bufHandle, row);
		else
			return ATMI.Foccur32(bufHandle, row);
	}

	public static int Foccur(long bufHandle, int row) {
		if (tuxedo32)
			return ATMI32.Foccur((int) bufHandle, row);
		else
			return ATMI.Foccur(bufHandle, row);
	}

	/**
	 * 找到FML32数据项
	 * 
	 * @param bufHandle
	 *            int FML32缓冲区句柄
	 * @param row
	 *            byte[] 行 传入当前行号，传出下一数据行号
	 * @param col
	 *            byte[] 列 传入当前列号，传出下一数据列号
	 * @param data
	 *            byte[] 数据缓冲区
	 * @param size
	 *            int 需要读出的数据长度
	 * @return >0 返回读出的数据长度;0 已到缓冲区尾；-1 错误,此时可通过 Ferror() 获取错误码
	 */
	public static int Fnext32(long bufHandle, int[] rowcol, byte[] data,
			int size) {
		if (tuxedo32)
			return ATMI32.Fnext32((int) bufHandle, rowcol, data, size);
		else
			return ATMI.Fnext32(bufHandle, rowcol, data, size);
	}

	/**
	 * 打印FML数据项
	 * 
	 * @param bufHandle
	 *            int FML缓冲区句柄
	 * @return >=0 成功；-1 失败 此时可通过 Ferror() 获取错误码
	 */
	public static int Fprint(long bufHandle) {
		if (tuxedo32)
			return ATMI32.Fprint((int) bufHandle);
		else
			return ATMI.Fprint(bufHandle);
	}

	/**
	 * 打印FML32数据项
	 * 
	 * @param bufHandle
	 *            int FML缓冲区句柄
	 * @return >=0 成功；-1 失败 此时可通过 Ferror() 获取错误码
	 */
	public static int Fprint32(long bufHandle) {
		if (tuxedo32)
			return ATMI32.Fprint32((int) bufHandle);
		else
			return ATMI.Fprint32(bufHandle);
	}

	/**
	 * 获取FML错误信息
	 * 
	 * @return
	 */
	public static int Ferror() {
		if (tuxedo32)
			return ATMI32.Ferror();
		else
			return ATMI.Ferror();
	}

	public static String getErrorString() {
		int err = tperrno();
		return new String(tpstrerror(err));
	}

	public static String getErrorDetailString() {
		return new String(tpstrerrordetail(tperrordetail(0), 0));
	}

}
