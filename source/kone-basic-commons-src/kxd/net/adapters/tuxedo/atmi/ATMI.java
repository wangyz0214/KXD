package kxd.net.adapters.tuxedo.atmi;

/**
 * Tuxedo的Jndi封闭包(64位版本)
 * 
 * @author zhaom
 * 
 */
public class ATMI {
	/**
	 * 设置环境变量
	 * 
	 * @param cfg
	 *            byte[] 环境变量字串，格式：变量名=内容
	 * @return 0：设置成功；其他值：设置失败
	 */
	public static native int tuxputenv(byte[] cfg);

	/**
	 * 获取环境变量的内容
	 * 
	 * @param cfg
	 *            byte[] 变量名
	 * @return byte[] 非null：变量内容；null：找不到变量
	 */
	public static native byte[] tuxgetenv(byte[] cfg);

	/**
	 * 分配tpinit()要用到的缓冲区
	 * 
	 * @return int >0: 分配的缓冲区句柄；0：分配失败
	 */
	public static native long allocInit();

	/**
	 * 获取缓冲区的长度
	 * 
	 * @param int bufHandle
	 * @return
	 */
	public static native int getBufferSize(long bufHandle);

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
	public static native void setInitParams(long intiBufhandle, byte[] usrname,
			byte[] cltname, byte[] passwd, byte[] grpname, int flags,
			byte[] data);

	/**
	 * 检查验证类型
	 * 
	 * @return int 验证类型
	 */
	public static native int tpchkauth();

	/**
	 * 建立tuxedo连接
	 * 
	 * @param intiBufhandle
	 *            int
	 * @return >=0 成功； -1
	 *         执行失败,此时可执行tperrno()返回见错误码常量描述[TPEINVAL,TPENOENT,TPEPERM
	 *         ,TPEPROTO,TPESYSTEM,TPEOS]
	 */
	public static native int tpinit(long intiBufhandle);

	/**
	 * 断开tuxedo连接
	 * 
	 * @return >=0 成功； -1
	 *         执行失败,此时可执行tperrno()返回见错误码常量描述[TPEPROTO,TPESYSTEM,TPEOS]
	 */
	public static native int tpterm();

	/**
	 * 释放分配的缓冲区
	 * 
	 * @param bufhandle
	 *            int 分配好的缓冲区对象句柄
	 */
	public static native void tpfree(long bufhandle);

	/**
	 * 获取当前的上下文句柄
	 * 
	 * @param flags
	 *            保留为0
	 * @return >0 句柄；<=0时，见TPSINGLECONTEXT,TPNULLCONTEXT,TPINVALIDCONTEXT
	 */
	public static native int tpgetctxt(int flags);

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
	public static native int tpsetctxt(int context, int flags);

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
	public static native long tpalloc(byte[] dataType, byte[] subType, int size);

	/**
	 * 初始化FML缓冲区
	 * 
	 * @param bufhandle
	 *            int FML缓冲区句柄
	 * @return >= 成功；-1 失败 ,此时可执行tperrno()返回见错误码常量描述[FALIGNERR,FNOTFLD,FNOSPACE]
	 */
	public static native int Finit(long bufhandle);

	/**
	 * 初始化FML32缓冲区
	 * 
	 * @param bufhandle
	 *            int FML缓冲区句柄
	 * @return >=0 成功；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[FALIGNERR,FNOTFLD,FNOSPACE]
	 */
	public static native int Finit32(long bufhandle);

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
	public static native int tpcall(byte[] service, long sendBuf, int sendLen,
			long recvBuf, int flags);

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
	public static native int tpacall(byte[] service, long sendBuf, int sendLen,
			int flags);

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
	public static native int tpgetrply(int acallHandle, long recvBuf, int flags);

	/**
	 * 取消一次异常call
	 * 
	 * @param acallHandle
	 * @return >=0 成功；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPETRAN,TPEBADDESC,TPEPROTO
	 *         ,TPESYSTEM,TPEOS]
	 */
	public static native int tpcancel(int acallHandle);

	/**
	 * 获取请求优先级
	 * 
	 * @return >=0 优先级；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPENOENT,TPEPROTO,TPESYSTEM,TPEOS]
	 */
	public static native int tpgprio();

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
	public static native int tpsprio(int prio, int flags);

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
	public static native int tpconnect(byte[] service, long sendBuf,
			int sendLen, int flags);

	/**
	 * 断开一次会话连接
	 * 
	 * @param conHandle
	 *            int 会话描述符
	 * @return >=0 成功；-1 失败
	 *         ,此时可执行tperrno()返回见错误码常量描述[TPEBADDESC,TPETIME,TPEPROTO
	 *         ,TPESYSTEM,TPEOS]
	 */
	public static native int tpdiscon(int conHandle);

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
	public static native int tpsend(int conHandle, long sendBuf, int sendLen,
			int flags, byte[] event);

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
	public static native int tprecv(int conHandle, long recvBuf, int flags,
			byte[] event);

	/**
	 * 获取错误信息
	 * 
	 * @param errno
	 *            int 错误代码
	 * @return 错误信息
	 */
	public static native byte[] tpstrerror(int errno);

	/**
	 * 获取详细错误信息代码
	 * 
	 * @param errno
	 *            flags 保留为0
	 * @return 错误信息代码 TPED_CLIENTDISCONNECTED ... <0 错误，可通过tperrno获取错误码：TPEINVAL
	 */
	public static native int tperrordetail(int flags);

	/**
	 * 获取详细错误信息
	 * 
	 * @param errno
	 *            int 详细错误代码
	 * @param flags
	 *            0
	 * @return 错误信息
	 */
	public static native byte[] tpstrerrordetail(int errno, int flags);

	/**
	 * 获取错误代码
	 * 
	 * @return 错误码
	 */
	public static native int tperrno();

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
	public static native int Sget(long bufHandle, int srcoffset, int length,
			byte[] data, int dstoffset);

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
	public static native int Sset(long bufHandle, int dstoffset, byte[] data,
			int srcoffset, int length);

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
	public static native int Fget(long bufHandle, int row, int col,
			byte[] data, int size);

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
	public static native int Fget32(long bufHandle, int row, int col,
			byte[] data, int size);

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
	public static native boolean Fset(long bufHandle, int row, int col,
			byte[] data);

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
	public static native boolean Fset32(long bufHandle, int row, int col,
			byte[] data);

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
	public static native boolean Fadd(long bufHandle, int row, byte[] data);

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
	public static native boolean Fadd32(long bufHandle, int row, byte[] data);

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
	public static native int Fnext(long bufHandle, int[] rowcol, byte[] data,
			int size);

	public static native int Foccur32(long bufHandle, int row);

	public static native int Foccur(long bufHandle, int row);

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
	public static native int Fnext32(long bufHandle, int[] rowcol, byte[] data,
			int size);

	/**
	 * 打印FML数据项
	 * 
	 * @param bufHandle
	 *            int FML缓冲区句柄
	 * @return >=0 成功；-1 失败 此时可通过 Ferror() 获取错误码
	 */
	public static native int Fprint(long bufHandle);

	/**
	 * 打印FML32数据项
	 * 
	 * @param bufHandle
	 *            int FML缓冲区句柄
	 * @return >=0 成功；-1 失败 此时可通过 Ferror() 获取错误码
	 */
	public static native int Fprint32(long bufHandle);

	/**
	 * 获取FML错误信息
	 * 
	 * @return
	 */
	public static native int Ferror();

	public static String getErrorString() {
		int err = tperrno();
		return new String(tpstrerror(err));
	}

	public static String getErrorDetailString() {
		return new String(tpstrerrordetail(tperrordetail(0), 0));
	}

	static {
		try {
			System.loadLibrary("k_tuxedo");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
