package kxd.engine.ui.tags.website;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import kxd.json.JSONException;
import kxd.json.JSONObject;
import kxd.util.TreeNode;

public class TreeTag extends BaseTagSupport {
	private static final long serialVersionUID = 1L;
	private Object treeData;
	private String styleClass;
	private String style;
	private String treeImageRoot;
	private String iconRoot;
	private boolean displayLink = false, checkBoxes;
	private Object value;
	private String baseLoadUrl;
	private int mode, checkOption;
	private String onNodeCreate;
	private String onNodeDestroy, onNodeChange;
	private String buttonClass;
	private String popupClass;
	private String buttonText;
	private String buttonStyle;
	private String buttonWidth;

	@Override
	public void release() {
		super.release();
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void uninit() {
		buttonClass = null;
		popupClass = null;
		buttonText = null;
		buttonWidth = null;
		buttonStyle = null;
		onNodeDestroy = null;
		onNodeChange = null;
		displayLink = false;
		onNodeCreate = null;
		value = null;
		treeData = null;
		styleClass = null;
		treeImageRoot = null;
		iconRoot = null;
		style = null;
		super.uninit();
	}

	public String getPopupClass() {
		return popupClass;
	}

	public void setPopupClass(String popupClass) {
		this.popupClass = popupClass;
	}

	@Override
	public String getId() {
		String id = super.getId();
		if (id == null) {
			id = "tree" + new Random(1000000).nextLong();
			setId(id);
		}
		return id;
	}

	@Override
	protected int startTag(JspWriter writer) throws JspException, IOException {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		String id = getId();
		writer.write("<input type='hidden' id='" + id + "_oldvalue' name='"
				+ id + "_oldvalue'/>");
		writer.write("<input type='hidden' id='" + id + "_value' name='" + id
				+ "_value'/>");
		writer.write("<input type='hidden' id='" + id + "_desp' name='" + id
				+ "_desp'/>");
		if (getButtonText() != null) {
			writer.write("<div id='" + id + "_t' value='" + getButtonText()
					+ "'");
			writeAttribute(writer, "style", getButtonStyle());
			writeAttribute(writer, "class", getButtonClass());
			writer.write("></div>");
		}
		writer.write("<div");
		writeAttribute(writer, "id", id);
		if (getButtonText() != null) {
			String style = getStyle();
			if (style == null)
				style = "display:none";
			else
				style += ";display:none";
			writeAttribute(writer, "style", style);
		} else {
			writeAttribute(writer, "style", getStyle());
		}
		writeAttribute(writer, "class", getStyleClass());
		writer.write("></div><script>");
		if (getButtonText() != null) {
			writer.write("var "
					+ id
					+ "_button=new DropdownButton(null,{'id':'"
					+ id
					+ "_t','dropdown':{'id':'"
					+ id
					+ "'"
					+ (getPopupClass() == null ? "" : ",'class':'"
							+ getPopupClass() + "'")
					+ ",'title':'"
					+ getButtonText()
					+ "'},'inputid':'"
					+ id
					+ "_value'"
					+ (getButtonWidth() == null ? "" : ",'width':"
							+ getButtonWidth()) + "});");
		}
		writer.write("var " + id + "= new TreeNode({expanded:true});");
		if (getButtonText() != null) {
			writer.write(id + ".buttontext='" + getButtonText() + "';");
			writer.write(id + ".button=" + id + "_button;");
		}
		Object d = getTreeData();
		if (d instanceof TreeNode<?>) {
			TreeNode<?> node = (TreeNode<?>) d;
			node.setExpanded(true);
			JSONObject o = new JSONObject();
			try {
				node.toJson(o, mode, 10000);
				if (o.has("children"))
					d = o.get("children").toString();
			} catch (JSONException e) {
				throw new JspException(e);
			}
		}
		if (getOnNodeCreate() != null) {
			writer.write(id + ".onNodeCreate=function(node){"
					+ getOnNodeCreate() + "};");
		}
		if (getOnNodeDestroy() != null) {
			writer.write(id + ".onNodeDestroy=function(node){"
					+ getOnNodeDestroy() + "};");
		}
		if (getOnNodeChange() != null) {
			writer.write(id + ".onNodeChange=function(node){"
					+ getOnNodeChange() + "};");
		}
		writer.write(id + ".addAll(" + d + ");");
		writer.write(id + ".create({'treeid':'" + id + "',imgroot:'"
				+ request.getContextPath() + getTreeImageRoot()
				+ "',iconroot:'" + request.getContextPath() + getIconRoot()
				+ "',displayLink:" + isDisplayLink() + ",baseLoadUrl:'"
				+ getBaseLoadUrl() + "',checkboxes:" + isCheckBoxes()
				+ ",checkoption:" + getCheckOption() + "});");
		if (getValue() != null) {
			writer.write(id + ".selectById(" + getValue() + ");$('" + id
					+ "_oldvalue').value=$('" + id + "_value').value;");
		} else if (isCheckBoxes()) {
			writer.write("$('" + id + "_value').value=$('" + id
					+ "_oldvalue').value=" + id + ".getCheckBoxesValue();");
		} else if (!isDisplayLink()) {
			writer.write("if(" + id + ".children.length>0)" + id
					+ ".children[0].select();$('" + id
					+ "_oldvalue').value=$('" + id + "_value').value;");
		}
		writer.write("</script>");
		return SKIP_BODY;
	}

	@Override
	protected int endTag(JspWriter writer) throws JspException, IOException {
		return EVAL_PAGE;
	}

	public Object getTreeData() {
		return treeData;
	}

	public void setTreeData(Object treeData) {
		this.treeData = treeData;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getTreeImageRoot() {
		if (treeImageRoot != null)
			return treeImageRoot;
		else
			return "/images/treeimages";
	}

	public void setTreeImageRoot(String treeImageRoot) {
		this.treeImageRoot = treeImageRoot;
	}

	public String getIconRoot() {
		if (iconRoot != null)
			return iconRoot;
		else
			return "/images/funcicons";
	}

	public void setIconRoot(String iconRoot) {
		this.iconRoot = iconRoot;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public boolean isDisplayLink() {
		return displayLink;
	}

	public void setDisplayLink(boolean displayLink) {
		this.displayLink = displayLink;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getBaseLoadUrl() {
		return baseLoadUrl;
	}

	public void setBaseLoadUrl(String baseLoadUrl) {
		this.baseLoadUrl = baseLoadUrl;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getOnNodeCreate() {
		return onNodeCreate;
	}

	public void setOnNodeCreate(String onNodeCreate) {
		this.onNodeCreate = onNodeCreate;
	}

	public String getOnNodeDestroy() {
		return onNodeDestroy;
	}

	public void setOnNodeDestroy(String onNodeDestroy) {
		this.onNodeDestroy = onNodeDestroy;
	}

	public String getOnNodeChange() {
		return onNodeChange;
	}

	public void setOnNodeChange(String onNodeChange) {
		this.onNodeChange = onNodeChange;
	}

	public String getButtonClass() {
		return buttonClass;
	}

	public void setButtonClass(String buttonClass) {
		this.buttonClass = buttonClass;
	}

	public String getButtonText() {
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public String getButtonStyle() {
		return buttonStyle;
	}

	public void setButtonStyle(String buttonStyle) {
		this.buttonStyle = buttonStyle;
	}

	public boolean isCheckBoxes() {
		return checkBoxes;
	}

	public void setCheckBoxes(boolean checkBoxes) {
		this.checkBoxes = checkBoxes;
	}

	public int getCheckOption() {
		return checkOption;
	}

	public void setCheckOption(int checkOption) {
		this.checkOption = checkOption;
	}

	public String getButtonWidth() {
		return buttonWidth;
	}

	public void setButtonWidth(String buttonWidth) {
		this.buttonWidth = buttonWidth;
	}
}
