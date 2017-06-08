package kxd.engine.scs.web.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

class FlashCommand {
	String command, params;
	String link, action;
	String sendKey, handleKey;
}

public class FlashTag extends BaseTag {
	private static final long serialVersionUID = 1L;
	/**
	 * FSCommand事件
	 */
	private String onFSCommand;
	/**
	 * obj的name
	 */
	private String name;

	/**
	 * obj css样式
	 */
	private String styleClass;

	/**
	 * obj css样式
	 */
	private String style;

	/**
	 * 以像素值或浏览器窗口的百分比值来指定应用程序的高度。 值: n 或 n%
	 */
	private String height;

	/**
	 * 以像素值或浏览器窗口的百分比值来指定应用程序的宽度。 值: n 或 n%
	 */
	private String width;

	/**
	 * 指定要加载的 SWF 文件的名称。仅适用于 object。 值: movieName.swf
	 */
	private String movie;

	/**
	 * （可选）指定在应用程序回放期间使用的消除锯齿级别。因为消除锯齿需要更快的处理器先对 SWF 文件的每一帧进行平滑处理，<br>
	 * 然后再将它们呈现到观众屏幕上，所以需要根据要优化速度还是优化外观来选择一个值：
	 * Low,Autolow,Autohigh,Medium,High,Best
	 */
	private String quality;

	/**
	 * （可选）指定应用程序的背景色。使用此属性来覆盖在 Flash SWF 文件中指定的背景色设置。<br>
	 * 此属性不影响 HTML 页面的背景色。
	 */
	private String bgcolor;

	/**
	 * （可选）使您可以使用 Internet Explorer 4.0 中的透明 Flash 内容、绝对定位和分层显示的功能。<br>
	 * 此标记/属性仅在带有 Flash Player ActiveX 控件的 Windows 中有效。<br>
	 */
	private String wmode = "opaque";

	/**
	 * 是否允许全屏，如果不写这一段，默认是不能全屏的;
	 */
	private String allowFullScreen;

	/**
	 * 使用 allowscriptaccess 使 Flash 应用程序可与承载它的 HTML 页通信。此参数是必需的，<br>
	 * 因为 fscommand() 和 getURL() 操作可能导致 JavaScript 使用 HTML 页的权限，<br>
	 * 而该权限可能与 Flash 应用程序的权限不同。这与跨域安全性有着重要关系。 <br>
	 * always | never | samedomain<br>
	 * 所有 HTML 发布模板使用的默认值均为 samedomain。
	 */
	private String allowscriptaccess;

	/**
	 * （可选）指定当观众在浏览器中右击 (Windows) 或按住 Command 键单击 (Macintosh) 应用程序区域时将显示的菜单类型。<br>
	 * true | false
	 */
	private String menu;
	/**
	 * （可选）指定应用程序是否在浏览器中加载时就开始播放。如果您的 Flash 应用程序是交互式的，<br>
	 * 则可以让用户通过单击按钮或执行某些其他任务来开始播放。在这种情况下，将 play 属性设置为 false 可禁止应用程序自动开始播放。<br>
	 * 如果忽略此属性，默认值为 true。
	 */
	private String play;

	/**
	 * （可选）指定 Flash 内容在它到达最后一帧后是无限制重复播放还是停止。如果忽略此属性，默认值为 true。 值: true | false
	 */
	private String loop;

	/**
	 * （可选）当 width 和 height 值是百分比时，定义应用程序如何放置在浏览器窗口中。 showall | noborder |
	 * exactfit
	 */
	private String scale;

	ArrayList<FlashCommand> commandList = new ArrayList<FlashCommand>();

	@Override
	public void release() {
		super.release();
	}

	public int doStartTag() throws JspTagException {
		commandList.clear();
		return EVAL_BODY_INCLUDE;
	}

	private void writeFlashParam(JspWriter writer, String name, String value)
			throws IOException {
		if (value != null)
			writer.write("<param name='" + name + "' value='" + value + "'/>");
	}

	public int doEndTag() throws JspTagException {
		JspWriter writer = pageContext.getOut();
		try {
			Iterator<FlashCommand> it = commandList.iterator();
			if (it.hasNext()) {
				String func = "fscommand_" + getId();
				writer.write("<script type='text/javascript'>function " + func
						+ "(command,args){");
				boolean start = true;
				while (it.hasNext()) {
					FlashCommand cmd = it.next();
					if (!start)
						writer.write("else ");
					else
						start = false;
					writer.write("if(command=='" + cmd.command + "' && args=='"
							+ cmd.params + "'){");
					if (cmd.link != null) {
						writer.write("document.location='");
						writeUrl(writer, cmd.link);
						writer.write("';");
					}
					if (cmd.action != null) {
						writer.write(cmd.action);
					}
					if (cmd.sendKey != null) {
						writer.write("$BR.sendKeys(" + cmd.sendKey
								+ ",true);$BR.sendKeys(" + cmd.sendKey
								+ ",false);");
					}
					writer.write("}");
				}
				it = commandList.iterator();
				writer.write("};$BR.keyReceiveFuncList[$BR.keyReceiveFuncList.length]=function(k){");
				start = true;
				while (it.hasNext()) {
					FlashCommand cmd = it.next();
					if (cmd.handleKey != null) {
						int k = Integer.valueOf(cmd.handleKey);
						if (!start)
							writer.write("else ");
						else
							start = false;
						writer.write("if(k==" + k + ") " + func + "('"
								+ cmd.command + "','" + cmd.params + "');");
					}
				}
				writer.write("};");
				if (getOnFSCommand() != null) {
					writer.write("var " + getId()
							+ "_func=function(command,params){"
							+ getOnFSCommand() + "}");
				}
				writer.write("</script>");
				writer.write("<script event='FSCommand(command,args)' for='"
						+ getId() + "'>");
				writer.write(func + "(command,args);");
				if (getOnFSCommand() != null) {
					writer.write(getId() + "_func(command,args);");
				}
				writer.write("</script>");
			} else if (getOnFSCommand() != null) {
				writer.write("<script type='text/javascript'> ");
				writer.write("var " + getId()
						+ "_func=function(command,params){" + getOnFSCommand()
						+ "}");
				writer.write("</script>");
				writer.write("<script event='FSCommand(command,args)' for='"
						+ getId() + "'>");
				writer.write(getId() + "_func(command,args);");
				writer.write("</script>");
			}
			writer.write("<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase=''");
			writeAttribute(writer, "width", this.getWidth());
			writeAttribute(writer, "id", this.getId());
			writeAttribute(writer, "name", this.getName());
			writeAttribute(writer, "class", this.getStyleClass());
			writeAttribute(writer, "style", this.getStyle());
			writeAttribute(writer, "height", this.getHeight());
			writer.write("><param name='movie' value=\"");
			writeUrl(writer, this.getMovie());
			writer.write("\">");
			writeFlashParam(writer, "quality", this.getQuality());
			writeFlashParam(writer, "bgcolor", this.getBgcolor());
			writeFlashParam(writer, "wmode", this.getWmode());
			writeFlashParam(writer, "allowFullScreen",
					this.getAllowFullScreen());
			writeFlashParam(writer, "allowscriptaccess",
					this.getAllowscriptaccess());
			writeFlashParam(writer, "menu", "false");
			writeFlashParam(writer, "play", this.getPlay());
			writeFlashParam(writer, "loop", this.getLoop());
			writeFlashParam(writer, "scale", this.getScale());
			writer.write("</object>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getMovie() {
		return movie;
	}

	public void setMovie(String movie) {
		this.movie = movie;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public String getWmode() {
		return wmode;
	}

	public void setWmode(String wmode) {
		this.wmode = wmode;
	}

	public String getAllowFullScreen() {
		return allowFullScreen;
	}

	public void setAllowFullScreen(String allowFullScreen) {
		this.allowFullScreen = allowFullScreen;
	}

	public String getAllowscriptaccess() {
		return allowscriptaccess;
	}

	public void setAllowscriptaccess(String allowscriptaccess) {
		this.allowscriptaccess = allowscriptaccess;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getPlay() {
		return play;
	}

	public void setPlay(String play) {
		this.play = play;
	}

	public String getLoop() {
		return loop;
	}

	public void setLoop(String loop) {
		this.loop = loop;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getOnFSCommand() {
		return onFSCommand;
	}

	public void setOnFSCommand(String onFSCommand) {
		this.onFSCommand = onFSCommand;
	}

}
