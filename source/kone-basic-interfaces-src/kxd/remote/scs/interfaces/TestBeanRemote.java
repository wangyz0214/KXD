package kxd.remote.scs.interfaces;

import javax.ejb.Remote;

/**
 * 
 * @author 赵明
 */
@Remote
public interface TestBeanRemote {
	public String toUpperCase(String a);
}
