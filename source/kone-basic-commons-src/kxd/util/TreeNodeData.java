package kxd.util;

public interface TreeNodeData<E> extends Idable<E> {
	public E getId();

	public int getDepth();

	public void copyDataToTreeNode(TreeNode<?> node);

	public void copyDataFromTreeNode(TreeNode<?> node);
}
