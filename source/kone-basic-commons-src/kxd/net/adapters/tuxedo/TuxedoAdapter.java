package kxd.net.adapters.tuxedo;

import java.util.Date;

import kxd.net.NetRequest;
import kxd.net.NetResponse;
import kxd.net.adapters.NetAdapter;
import kxd.net.adapters.NetAdapterException;
import kxd.net.adapters.NetAdapterResult;
import kxd.net.adapters.tuxedo.atmi.Tuxedo;
import kxd.net.adapters.tuxedo.atmi.TuxedoConfig;
import kxd.net.adapters.tuxedo.atmi.TuxedoException;

/**
 * 基于Tuxedo短连接的通信器
 * 
 * @author zhaom
 * @since 4.1
 * @param <S>
 * @param <R>
 */
abstract public class TuxedoAdapter<S extends NetRequest, R extends NetResponse>
		implements NetAdapter<S, R> {
	TuxedoConfig config;
	boolean available;

	/**
	 * 短连接适配器构造器
	 * 
	 * @param address
	 *            地址
	 * @param connectTimeout
	 *            连接超时
	 */
	public TuxedoAdapter(TuxedoConfig config) {
		this.config = config;
	}

	/**
	 * 执行一次通信，继承类应该实现本函数
	 * 
	 * @param tuxedo
	 *            tuxedo通信对象
	 * @param data
	 *            发送数据
	 * @return 接收数据
	 * @throws NetAdapterException
	 *             通信失败时，抛出此异常
	 */
	abstract protected R doExecute(Tuxedo tuxedo, S data)
			throws NetAdapterException;

	/**
	 * 连接至Tuxedo
	 * 
	 * @return 返回Tuxedo对象
	 * @throws TuxedoException
	 */
	protected Tuxedo connect() throws TuxedoException {
		return new Tuxedo(config);
	}

	@Override
	public R execute(S data) throws NetAdapterException {
		Tuxedo tuxedo = null;
		NetAdapterResult r = NetAdapterResult.NOTARRIVALED;
		try {
			tuxedo = connect();
			available = true;
			r = NetAdapterResult.TIMEOUT;
			data.setSendTime(new Date());
			R ret = doExecute(tuxedo, data);
			ret.setRecvTime(new Date());
			return ret;
		} catch (NetAdapterException e) {
			throw e;
		} catch (Throwable e) {
			throw new NetAdapterException(r, e);
		} finally {
			if (tuxedo != null)
				tuxedo.dispose();
		}
	}

	@Override
	public synchronized boolean isAvailable() {
		return available;
	}

}
