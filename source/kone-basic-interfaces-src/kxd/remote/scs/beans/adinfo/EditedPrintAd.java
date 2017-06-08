package kxd.remote.scs.beans.adinfo;

import kxd.remote.scs.beans.BasePrintAd;
import kxd.util.IdableObject;

import org.apache.log4j.Logger;

public class EditedPrintAd extends BasePrintAd {
	private static final long serialVersionUID = 1L;
	private String content;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
	}

	public EditedPrintAd() {
		super();
	}

	public EditedPrintAd(Integer id) {
		super(id);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedPrintAd))
			return;
		EditedPrintAd d = (EditedPrintAd) src;
		content = d.content;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedPrintAd();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
