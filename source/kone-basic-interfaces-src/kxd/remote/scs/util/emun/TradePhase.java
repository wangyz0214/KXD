/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 交易所处的阶段，用于说明一笔交易失败所处的阶段，便于分析业务处理失败原因。 交易阶段和交易结果两者结合，可以得知交易在什么阶段由于什么原因失败
 * <ol>
 * <li>BEFORE_PAY: 这个阶段，TradeResult只可能有一个结果，就是FAILURE(交易失败)
 * <ol>
 * <li>
 * FAILURE: 表示交易支付请求尚未发起就失败了，具体可能原因可能是交易的数据准备抛异常或支付交易的连接不成功</li>
 * </ol>
 * </li>
 * <li>PAY: 这个阶段，TradeResult可能有二个结果，就是FAILURE(交易失败),TIMEOUT(交易超时)
 * <ol>
 * <li>
 * FAILURE: 表示可以确定支付是失败的，这种情况支付必定已经发起至银联，只是银联返回了支付失败信息，此种情况不需要冲正</li>
 * <li>
 * TIMEOUT:
 * 表示支付超时（可能是发送，接收数据超时或发送接收数据出错，全部属于超时，即只要数据已经开始发出，只要未成功接收到返回结果是成功或失败，就属于超时），
 * 这种情况支付可能银联收到支付请求也可能未收到，所以，此种情况必须立即冲正。</li>
 * </ol>
 * </li>
 * <ol>
 * </ol>
 * <li>BEFORE_TRADE: 这个阶段，TradeResult只可能有一个结果，就是FAILURE(交易失败)
 * <ol>
 * <li>
 * FAILURE: 表示业务处理请求尚未发起就失败了，具体可能原因可能是业务处理的数据准备抛异常或业务处理的请求连接不成功</li>
 * </ol>
 * </li> <li>TRADE:
 * 这个阶段，TradeResult可能有三个结果，就是SUCCESS(交易成功),FAILURE(交易失败),TIMEOUT(交易超时)
 * <ol>
 * <li>
 * FAILURE: 表示可以业务处理是失败的，这种情况支付必定已经发起至业务接口机，只是返回业务处理失败信息，此种情况如果包含银联交易，应该立即执行银联冲正
 * </li>
 * <li>
 * TIMEOUT:
 * 表示业务处理超时（可能是发送，接收数据超时或发送接收数据出错，全部属于超时，即只要数据已经开始发出，只要未成功接收到返回结果是成功或失败，就属于超时），
 * 这种情况业务接口机可能银联收到支付请求也可能未收到，此种情况分以下情况处理：
 * <ol>
 * <li>
 * 对现金等无法冲正的支付渠道：提示用户正在办理，等待一段时间再查看处理结果。营业员应该从业务后台查看该笔交易是否已经成功</li>
 * </ol>
 * </li>
 * </ol>
 * </li> </ol>
 * 
 * @author 赵明
 */
public enum TradePhase {
	/**
	 * 支付前的准备阶段，含支付请求初始化及支付请求连接的建立等阶段
	 */
	BEFORE_PAY {

		@Override
		public String toString() {
			return "支付前准备";
		}
	},
	/**
	 * 支付阶段，当支付连接建立后，开始发送数据直至支付完成
	 */
	PAY {

		@Override
		public String toString() {
			return "支付";
		}
	},
	/**
	 * 业务处理前的准备阶段，含业务处理请求初始化及业务处理请求连接的建立等阶段
	 */
	BEFORE_TRADE {

		@Override
		public String toString() {
			return "交易前准备";
		}
	},
	/**
	 * 业务处理阶段，当业务处理连接建立后，开始发送数据直至业务处理完成
	 */
	TRADE {

		@Override
		public String toString() {
			return "交易";
		}
	},
	BEFORE_REVERSAL {
		@Override
		public String toString() {
			return "准备冲正";
		}
	},
	REVERSAL {
		@Override
		public String toString() {
			return "冲正";
		}
	};

	static public TradePhase valueOf(int v) {
		switch (v) {
		case 0:
			return BEFORE_PAY;
		case 1:
			return PAY;
		case 2:
			return BEFORE_TRADE;
		case 3:
			return TRADE;
		case 4:
			return BEFORE_REVERSAL;
		case 5:
			return REVERSAL;
		default:
			return TRADE;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public TradePhase valueOfIntString(Object str) {
		if (str == null)
			return BEFORE_TRADE;
		return valueOf(Integer.valueOf(str.toString()));
	}

}
