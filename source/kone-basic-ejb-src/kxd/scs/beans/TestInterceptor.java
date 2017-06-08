package kxd.scs.beans;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class TestInterceptor {
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@AroundInvoke
	public Object log(InvocationContext ctx) throws Exception {
		System.out.println("*** HelloInterceptor intercepting");
		long start = System.currentTimeMillis();
		try {
			System.out.println("*** " + ctx.getMethod().getName()
					+ " 已经被调用! *** ");
			return ctx.proceed();
		} catch (Exception e) {
			throw e;

		} finally {
			long time = System.currentTimeMillis() - start;
			System.out.println("用时:" + time + "ms");
		}
	}
}
