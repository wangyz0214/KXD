package kxd.util;

public class SimpleTreeNode<E, V> extends TreeNode<E> {
	private static final long serialVersionUID = -7152134155599137221L;
	V data;

	public SimpleTreeNode() {
		super();
	}

	public SimpleTreeNode(E id) {
		super(id);
	}

	public SimpleTreeNode(E id, V data) {
		super(id);
		this.data = data;
	}

	@Override
	public String getText() {
		return data == null ? "" : data.toString();
	}

	@Override
	public void setText(String text) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setIdString(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getIdString() {
		return getId() == null ? "" : getId().toString();
	}

	@Override
	public IdableObject<E> createObject() {
		return new SimpleTreeNode<E, V>();
	}

	@Override
	protected String toDisplayLabel() {
		return "";
	}

	public V getData() {
		return data;
	}

	public void setData(V data) {
		this.data = data;
	}

}
