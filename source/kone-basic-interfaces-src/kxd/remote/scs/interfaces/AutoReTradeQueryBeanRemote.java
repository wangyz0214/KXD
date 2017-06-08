package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.transaction.AutoReTradeData;

/**
 * 
 * @author 赵明
 */
@Remote
public interface AutoReTradeQueryBeanRemote {

    /**
     * 查询需要重做的交易列表
     * 
     * @return
     */
    public List<AutoReTradeData> getNeedAutoReTradeList();
}
