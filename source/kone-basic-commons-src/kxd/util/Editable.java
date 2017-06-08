package kxd.util;

import java.io.Serializable;


public interface Editable extends Serializable {
	public boolean isModified();
	public void setModified(boolean value);
}
