/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.test.ejb.remote;

import javax.ejb.Remote;

/**
 *
 * @author zhaom
 */
@Remote
public interface NewSessionBeanRemote {

    String toUpperCase(String str);
}
