package kxd.engine.scs.web.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class SilverlightTag extends BaseTag {
	private static final long serialVersionUID = 1L;

	@Override
	public void release() {
		super.release();
	}

	public int doEndTag() throws JspException {
		try {
			JspWriter writer = pageContext.getOut();
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			if (pageContext.getAttribute("CONTEXTSILVERLIGHTENDERED") == null) {
				pageContext.setAttribute("CONTEXTSILVERLIGHTENDERED", true);
				writer.write("<script type='text/javascript' src='"
						+ request.getContextPath()
						+ "/scripts/core/silverlight.js'></script>");
				writer.write("<script type='text/javascript'>");
				writer.write("function isDebug(){return false;}");
				writer.write("function showAd(url) {"
						+ "document.getElementById(\"adframe\").src = url;"
						+ "document.getElementById(\"adframe\").style.visibility = \"visible\";"
						+ "return true;}");
				writer.write("function hideAd() {"
						+ "document.getElementById(\"adframe\").src = 'about:blank';"
						+ "document.getElementById(\"adframe\").style.visibility = \"hidden\";"
						+ "return true;}");
				writer.write("function getContextPath(){return \""
						+ request.getContextPath() + "\";}");
				writer.write("function getWebRoot(){return \"\";}");
				writer.write("function getXapName(){return \""
						+ request.getContextPath()
						+ "/silverlight/Kxd.TermFace.App.xap\";}");
				writer.write("</script>");
			}
			writer.write("<object data=\"data:application/x-silverlight-2,\" "
					+ "type=\"application/x-silverlight-2\" width=\"100%\" height=\"100%\">");
			writer.write("<param name=\"source\" value=\""
					+ request.getContextPath()
					+ "/silverlight/Kxd.TermFace.App.xap\"/>");
			writer.write("<param name=\"onError\" value=\"onSilverlightError\" />");
			writer.write("<param name=\"enableGPUAcceleration\" value=\"true\"/>");
			writer.write("<param name=\"background\" value=\"black\" />");
			writer.write("<param name=\"minRuntimeVersion\" value=\"5.0.61118.0\" />");
			writer.write("</object>");
			writer.write("<iframe id=\"_sl_historyFrame\" style=\"visibility:hidden;height:0px;width:0px;border:0px\"></iframe>");
			writer.write("<iframe id=\"adframe\" style=\"visibility:hidden;height:100%;width:100%;border:0px;position:absolute;left:0px;top:0px;\"></iframe>");
			return super.doEndTag(); 
		} catch (IOException e) {
			throw new JspException(e);
		}
	}

}
