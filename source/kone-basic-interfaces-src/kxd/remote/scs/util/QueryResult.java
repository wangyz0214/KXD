package kxd.remote.scs.util;

import java.io.Serializable;
import java.util.List;

public class QueryResult<E> implements Serializable {
	private static final long serialVersionUID = 1L;
	private int count;
	private List<E> resultList;

	public QueryResult(int count, List<E> resultList) {
		super();
		this.count = count;
		this.resultList = resultList;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<E> getResultList() {
		return resultList;
	}

	public void setResultList(List<E> resultList) {
		this.resultList = resultList;
	}
}
