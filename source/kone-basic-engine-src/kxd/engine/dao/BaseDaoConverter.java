package kxd.engine.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract public class BaseDaoConverter<E> implements DaoConverter<E> {
	abstract protected E doConvert(Object result);

	private Dao dao;

	@Override
	public E convert(Object result) {
		return doConvert(result);
	}

	@Override
	public List<E> convert(List<?> results) {
		List<E> ls = new ArrayList<E>();
		Iterator<?> it = results.iterator();
		while (it.hasNext()) {
			ls.add(doConvert(it.next()));
		}
		return ls;
	}

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

}
