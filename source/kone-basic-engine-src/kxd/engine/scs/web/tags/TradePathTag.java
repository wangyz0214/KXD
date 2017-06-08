package kxd.engine.scs.web.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class TradePathTag extends BaseTag {
	private static final long serialVersionUID = 1L;

	@Override
	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().write(
					((HttpServletRequest) pageContext.getRequest())
							.getContextPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}
}
