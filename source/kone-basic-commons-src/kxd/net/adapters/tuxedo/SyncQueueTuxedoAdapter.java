package kxd.net.adapters.tuxedo;

import java.io.Serializable;
import java.util.Date;

import kxd.net.NetResponse;
import kxd.net.adapters.NetAdapterException;
import kxd.net.adapters.NetAdapterResult;
import kxd.net.adapters.QueueRequest;
import kxd.net.adapters.SyncQueueAdapter;
import kxd.net.adapters.tuxedo.atmi.Tuxedo;

/**
 * 基于TCP套接字协议的同步队列通信适配器
 * 
 * @author zhaom
 * @since 4.1
 * @param <K>
 * @param <S>
 * @param <R>
 */
abstract public class SyncQueueTuxedoAdapter<K extends Serializable, S extends QueueRequest, R extends NetResponse>
		extends SyncQueueAdapter<K, S, R> {
	protected Tuxedo tuxedo;

	/**
	 * 构造器
	 * 
	 * @param params
	 *            同步TCP适配器参数
	 */
	public SyncQueueTuxedoAdapter(TuxedoAdapterParams<K, S, R> params) {
		super(params);
	}

	protected void connect(TuxedoAdapterParams<K, S, R> params)
			throws NetAdapterException {
		if (tuxedo == null) {
			getLogger().debug(
					"connect to host[" + params.getConfig().getWsnAddr()
							+ "]...");
			try {
				tuxedo = new Tuxedo(params.getConfig());
			} catch (Throwable e) {
				getLogger().error(
						"connect to host[" + params.getConfig().getWsnAddr()
								+ "] error:", e);
				throw new NetAdapterException(NetAdapterResult.NOTARRIVALED, e);
			}
			getLogger()
					.debug("host [" + params.getConfig().getWsnAddr()
							+ "] connected.");
		}
	}

	protected void disconnect() {
		if (tuxedo != null) {
			tuxedo.dispose();
			tuxedo = null;
			TuxedoAdapterParams<K, S, R> params = (TuxedoAdapterParams<K, S, R>) getParams();
			getLogger().debug(
					"tuxedo [" + params.getConfig().getWsnAddr()
							+ "] disconnected.");
		}
	}

	/**
	 * 执行一次通信
	 * 
	 * @param id
	 *            包ID，除了用于记录日志外，通常没有其他用处
	 * @param tuxedo
	 *            tuxedo通信对象
	 * @param req
	 *            通信请求
	 * @return 通信返回包
	 * @throws NetAdapterException
	 */
	protected abstract R doExecute(K id, Tuxedo tuxedo, S req)
			throws NetAdapterException;

	/**
	 * 保持连接的通信
	 * 
	 * @param tuxedo
	 *            tuxedo通信对象
	 */
	protected abstract void doKeepAlive(Tuxedo tuxedo)
			throws NetAdapterException;

	@Override
	protected R doExecute(K id, S req) throws NetAdapterException {
		TuxedoAdapterParams<K, S, R> p = (TuxedoAdapterParams<K, S, R>) getParams();
		connect(p);
		try {
			req.setSendTime(new Date());
			R r = doExecute(id, tuxedo, req);
			r.setRecvTime(new Date());
			return r;
		} catch (NetAdapterException e) {
			switch (e.getResult()) {
			case TIMEOUT:
				disconnect();
				break;
			}
			throw e;
		} catch (Throwable e) { // 意外异常按操作超时抛出异常
			disconnect();
			throw new NetAdapterException(NetAdapterResult.TIMEOUT, e);
		}
	}

	@Override
	protected void keepAlive() {
		getLogger().debug("keep alive.");
		try {
			if (tuxedo != null) {
				doKeepAlive(tuxedo);
			}
		} catch (NetAdapterException e) {
			switch (e.getResult()) {
			case TIMEOUT:
				disconnect();
				break;
			}
		} catch (Throwable e) { // 意外异常按操作超时抛出异常
			disconnect();
		}
	}
}
