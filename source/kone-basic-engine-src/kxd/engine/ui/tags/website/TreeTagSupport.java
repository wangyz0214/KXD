package kxd.engine.ui.tags.website;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import kxd.util.TreeNode;

abstract public class TreeTagSupport extends BaseTagSupport {
	private static final long serialVersionUID = 1L;
	private String imagePath;
	private boolean rootRendered;
	private boolean checkboxes, radioboxes;
	private int checkOption;
	Object treelist;
	private Object value;
	protected String contextPath;
	private String loadFunction;
	String style;
	String styleClass;
	private String onValueChanged;
	private String dropdownButtonText;
	private int dropdownHeight;
	private boolean dropdown = false;
	private String checkedvalues;

	@Override
	public void release() {
		onValueChanged = null;
		dropdownButtonText = null;
		imagePath = null;
		treelist = null;
		contextPath = null;
		style = null;
		styleClass = null;
		super.release();
	}

	@Override
	public void init() {
		selectid = null;
		checkedvalues = "";
		buttonText = this.dropdownButtonText;
		super.init();
	}

	@Override
	public void uninit() {
		checkedvalues = null;
		value = null;
		super.uninit();
	}

	public String getOnValueChanged() {
		return onValueChanged;
	}

	public void setOnValueChanged(String onValueChanged) {
		this.onValueChanged = onValueChanged;
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

	public String getLoadFunction() {
		return loadFunction;
	}

	public void setLoadFunction(String loadFunction) {
		this.loadFunction = loadFunction;
	}

	public String getImagePath() {
		return contextPath + imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean isRootRendered() {
		return rootRendered;
	}

	public void setRootRendered(boolean rootRendered) {
		this.rootRendered = rootRendered;
	}

	public boolean isCheckboxes() {
		return checkboxes;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public void setCheckboxes(boolean checkboxes) {
		this.checkboxes = checkboxes;
	}

	public boolean isRadioboxes() {
		return radioboxes;
	}

	public void setRadioboxes(boolean radioboxes) {
		this.radioboxes = radioboxes;
	}

	public int getCheckOption() {
		return checkOption;
	}

	public void setCheckOption(int checkOption) {
		this.checkOption = checkOption;
	}

	public Object getTreelist() {
		return treelist;
	}

	public void setTreelist(Object treelist) {
		this.treelist = treelist;
	}

	public String getChildDivId(TreeNode<?> node) {
		return getId() + "_child" + node.getId();
	}

	private String getNodeId(TreeNode<?> node) {
		return getId() + "_node" + node.getId();
	}

	private String getNodeImgId(TreeNode<?> node) {
		return getId() + "_ni" + node.getId();
	}

	private String imageI;
	private String imageWhite;
	private String imageLoading;
	private NodeImageData imageT;
	private NodeImageData imageR;
	private NodeImageData imageL;
	private NodeImageData imageF;

	class NodeImageData {
		String single, minus, plus;
		String p;

		NodeImageData(String p, String single, String minus, String plus) {
			this.single = single;
			this.minus = minus;
			this.plus = plus;
			this.p = p;
		}
	}

	private void prepareImages() {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		contextPath = request.getContextPath();
		String rootPath = getImagePath();
		if (!rootPath.endsWith("/"))
			rootPath += "/";
		imageI = rootPath + "I.gif";
		imageWhite = rootPath + "white.gif";
		imageLoading = rootPath + "loading.gif";
		imageT = new NodeImageData("T", rootPath + "T.gif", rootPath
				+ "Tminus.gif", rootPath + "Tplus.gif");
		imageR = new NodeImageData("R", rootPath + "R.gif", rootPath
				+ "Rminus.gif", rootPath + "Rplus.gif");
		imageF = new NodeImageData("F", rootPath + "F.gif", rootPath
				+ "Fminus.gif", rootPath + "Fplus.gif");
		imageL = new NodeImageData("L", rootPath + "L.gif", rootPath
				+ "Lminus.gif", rootPath + "Lplus.gif");
	}

	abstract protected TreeNode<?> getTree(Object treelist);

	abstract protected String getLoadUrl(TreeNode<?> node);

	abstract protected String getNodeIcon(TreeNode<?> node);

	protected String getNodeUrl(TreeNode<?> node) {
		return null;
	}

	protected void outputTreeNodeIcon(JspWriter writer, TreeNode<?> node)
			throws JspException, IOException {
		String url = getNodeIcon(node);
		if (url != null)
			writeText(writer, "<img src='" + contextPath + url
					+ "' align='absmiddle'/>");
	}

	private String selectid = null;

	protected void outputTreeNodeTextAttributes(JspWriter writer,
			TreeNode<?> node) throws JspException, IOException {
	}

	protected void outputTreeNodeText(JspWriter writer, TreeNode<?> node)
			throws JspException, IOException {
		String selected = "";
		if (node.getId().equals(getValue())
				|| (getValue() == null && node.isSelected())) {
			selected = " class='selected' selected='true'";
			selectid = node.getIdString();
			buttonText = node.getText();
		}
		if (node.isChecked()) {
			checkedvalues += node.getIdString() + ",";
		}
		String nid = getId() + "_" + node.getIdString();
		if (node.isDisabled()) {
			writeText(writer, "&nbsp;<span itemid='" + node.getId() + "' id='"
					+ nid + "' class='disabled' title='" + node.getText()
					+ "' href=\"#\"");
			outputTreeNodeTextAttributes(writer, node);
			writeText(writer, ">" + node.getText() + "</span>");
		} else {
			writeText(writer, "&nbsp;<a itemid='" + node.getId() + "'"
					+ selected + " id='" + getId() + "_" + node.getIdString()
					+ "' title='" + node.getText() + "'");
			String url = getNodeUrl(node);
			if (url == null)
				writeText(writer,
						" href='#' onclick=\"javascript:treenodeclick('"
								+ getId() + "','" + node.getId()
								+ "');return false;\"");
			else
				writeText(writer, " href=\"" + url + "\"");
			outputTreeNodeTextAttributes(writer, node);
			writeText(writer, ">" + node.getText() + "</a>");
		}
	}

	abstract protected boolean shouldOutputChildren(JspWriter writer,
			TreeNode<?> node) throws JspException, IOException;

	private int rootDepth;

	private NodeImageData getNodeImage(TreeNode<?> node) {
		if (node.getDepth() == rootDepth) {
			if (node.getParent() == null) {
				return imageR;
			} else if (node.getParent().getFirstChild() == node) {
				if (node.getParent().getLastChild() == node) {
					return imageR;
				} else {
					return imageF;
				}
			}
		}
		if (node.isLastChild()) {
			return imageL;
		} else {
			return imageT;
		}
	}

	protected void outputNodeImage(JspWriter writer, TreeNode<?> node)
			throws IOException {
		TreeNode<?> pnode = node.getParent();
		ArrayList<String> strings = new ArrayList<String>();
		String strTmp;
		while (pnode != null) {
			if (pnode.getDepth() < rootDepth) {
				break;
			}
			strTmp = "<img src='";
			if (pnode.isLastChild())
				strTmp += imageWhite;
			else
				strTmp += imageI;
			strTmp += "' align='absmiddle' depth='"
					+ (pnode.getDepth() - rootDepth) + "'/>";
			strings.add(0, strTmp);
			pnode = pnode.getParent();
		}
		for (int i = 0; i < strings.size(); i++)
			writer.write(strings.get(i));
		writeText(writer, "<img ");
		writeAttribute(writer, "align", "absmiddle");
		writeAttribute(writer, "id", getNodeImgId(node));
		writeAttribute(writer, "depth", (node.getDepth() - rootDepth));
		NodeImageData d = getNodeImage(node);
		writeAttribute(writer, "prefix", d.p);
		if (!node.isEmpty()) {
			writeAttribute(writer, "src", node.isExpanded() ? d.minus : d.plus);
			writeAttribute(writer, "expanded", node.isExpanded() ? "true"
					: "false");
			writeAttribute(writer, "onclick", "treeec('" + getId() + "','"
					+ getId() + "_ni" + node.getId() + "','"
					+ getChildDivId(node) + "');");
		} else {
			String loadurl = getLoadUrl(node);
			if (loadurl != null) {
				writeAttribute(writer, "src", d.plus);
				writeAttribute(writer, "expanded", "false");
				writeAttribute(writer, "loadurl", loadurl);
				writeAttribute(writer, "loadfunc", getLoadFunction());
				writeAttribute(writer, "onclick", "treeec('" + getId() + "','"
						+ getNodeImgId(node) + "','" + getChildDivId(node)
						+ "'," + getLoadFunction() + ");");
			} else {
				writeAttribute(writer, "onclick", "treeec('" + getId() + "','"
						+ getId() + "_ni" + node.getId() + "','"
						+ getChildDivId(node) + "');");
				writeAttribute(writer, "src", d.single);
			}
		}
		writeText(writer, "/>");
	}

	protected void outputCheckBox(JspWriter writer, TreeNode<?> item)
			throws IOException {
		writeText(writer, "<input");
		writeAttribute(writer, "id", item.getId());
		writeAttribute(writer, "type", "checkbox");
		writeAttribute(writer, "style", "margin:0px 2px");
		if (item.isChecked())
			writeAttribute(writer, "checked", "checked");
		if (item.isDisabled())
			writeAttribute(writer, "disabled", "disabled");
		writeAttribute(writer, "onclick", "treecheckbox('" + getId() + "','"
				+ getChildDivId(item) + "',this," + getCheckOption() + ");");
		writeText(writer, "/>");
	}

	protected void outputRadioBox(JspWriter writer, TreeNode<?> item)
			throws IOException {
		writeText(writer, "<input");
		writeAttribute(writer, "id", item.getId());
		writeAttribute(writer, "type", "radio");
		writeAttribute(writer, "style", "margin:0px 2px");
		if (item.isChecked())
			writeAttribute(writer, "checked", "checked");
		writeAttribute(writer, "onclick", "treeradiobox('" + getId()
				+ "',this);");
		writeText(writer, "/>");
	}

	protected void outputNode(JspWriter writer, TreeNode<?> node)
			throws IOException, JspException {
		if (node.getDepth() >= rootDepth) {
			writeText(writer, "<div id='" + getNodeId(node) + "' class='node'>");
			outputNodeImage(writer, node);
			if (isCheckboxes()) {
				outputCheckBox(writer, node);
			} else if (isRadioboxes()) {
				outputRadioBox(writer, node);
			}
			outputTreeNodeIcon(writer, node);
			outputTreeNodeText(writer, node);
			writeText(writer, "</div>");
		}
		outputNodeChild(writer, node);
	}

	void outputNodeChild(JspWriter writer, TreeNode<?> node)
			throws IOException, JspException {
		if (shouldOutputChildren(writer, node)) {
			String url = getLoadUrl(node);
			String childDivId = getChildDivId(node);
			if (!node.isEmpty() || url != null) {
				writeText(writer, "<div");
				writeAttribute(writer, "id", childDivId);
				String style = "";
				if ((!node.isExpanded() || url != null)) {
					style += "display:none";
				}
				writeAttribute(writer, "style", style);
				writeText(writer, ">");
			}
			if (!node.isEmpty()) {
				Iterator<?> it = node.getChildren().iterator();
				while (it.hasNext()) {
					TreeNode<?> n = (TreeNode<?>) it.next();
					writeText(writer, "<div");
					writeAttribute(writer, "checkId", n.getId());
					writeText(writer, ">");
					outputNode(writer, n);
					writeText(writer, "</div>");
				}
				writeText(writer, "</div>");
			} else if (url != null) {
				writeText(writer, "<div>");
				TreeNode<?> pnode = node;
				ArrayList<String> strings = new ArrayList<String>();
				String strTmp;
				while (pnode != null) {
					if (pnode.getDepth() < rootDepth) {
						break;
					}
					strTmp = "<img depth='" + (pnode.getDepth() - rootDepth)
							+ "' src='";
					if (pnode.isLastChild())
						strTmp += imageWhite;
					else
						strTmp += imageI;
					strTmp += "' align='absmiddle'/>";
					strings.add(0, strTmp);
					pnode = pnode.getParent();
				}
				for (int i = 0; i < strings.size(); i++)
					writeText(writer, strings.get(i));
				writeText(writer, "<img depth='"
						+ (node.getDepth() - rootDepth) + "' src='"
						+ imageL.single + "' align='absmiddle'/>");
				writeText(writer, "<span");
				writeAttribute(writer, "style", "color:gray;padding-left:2px");
				writeText(writer, "> <img");
				writeAttribute(writer, "align", "absmiddle");
				writeAttribute(writer, "src", imageLoading);
				writeText(writer, ">&nbsp;loading...");
				writeText(writer, "</span></div>");
				writeText(writer, "</div>");
			}
		}
	}

	private String buttonText;
	private boolean editEnabled;

	public boolean isEditEnabled() {
		return editEnabled;
	}

	public void setEditEnabled(boolean editEnabled) {
		this.editEnabled = editEnabled;
	}

	private void encodeTree(JspWriter writer) throws IOException, JspException {
		TreeNode<?> root = getTree(getTreelist());
		if (dropdown) {
			writeText(
					writer,
					"<div id='"
							+ getId()
							+ "_panel' class='menupanel' style='position:absolute;display:none'>");
			writeText(writer,
					"<div class='buttonshadow'></div><div class='menucontainer'><div id='"
							+ getId() + "_menu' class='menu'>");
		}
		if (isRootRendered()) {
			rootDepth = 0;
			outputNode(writer, root);
		} else {
			rootDepth = 1;
			outputNode(writer, root);
		}

		if (dropdown) {
			writeText(writer, "</div></div><div class='maskline'></div></div>");
		}
	}

	@Override
	protected int startTag(JspWriter writer) throws JspException, IOException {
		prepareImages();
		if (dropdown) {
			writeText(writer, "<div id='" + getId() + "' dropdown='" + dropdown
					+ "' dropdownheight='" + getDropdownHeight() + "'>");
			writeText(writer, "<div ");
		} else
			writeText(writer, "<div id='" + getId() + "'");
		writeAttribute(writer, "editenabled", editEnabled ? "true" : "false");
		writeAttribute(writer, "style", getStyle());
		writeAttribute(writer, "class", getStyleClass());
		writeText(writer, ">");
		if (dropdown) {
			writeText(writer,
					"<div class='buttonnormal'><div class='topline'></div>");
			writeText(writer,
					"<div class='content' style='-moz-user-select: none;' unselectable='on'>");
			writeText(writer, "<div class='buttontext' id='" + getId()
					+ "_text'>" + buttonText + "</div></div>");
			writeText(writer, "<div class='bottomline'></div></div>");
			writeText(writer, "</div>");
			encodeTree(writer);
		} else
			encodeTree(writer);
		writeText(writer, "</div>");
		String pid = getId();
		String id = pid + "_value";
		if (checkboxes || radioboxes) {
			writeText(writer, "<input type='hidden' id='" + pid
					+ "_oldvalue' name='" + pid + "_oldvalue' value='"
					+ checkedvalues + "'/>");
		}
		writeText(writer, "<input");
		writeAttribute(writer, "type", "hidden");
		setValue(selectid);
		if (checkboxes || radioboxes)
			writeAttribute(writer, "value", checkedvalues);
		else
			writeAttribute(writer, "value", selectid);
		writeAttribute(writer, "id", id);
		writeAttribute(writer, "name", id);
		if (getOnValueChanged() != null) {
			writeAttribute(writer, "onchange", getOnValueChanged());
		}
		writeText(writer, "/>");
		writeText(writer, "<input");
		writeAttribute(writer, "type", "hidden");
		writeAttribute(writer, "value", buttonText);
		writeAttribute(writer, "id", pid + "_desp");
		writeAttribute(writer, "name", pid + "_desp");
		writeText(writer, "/>");
		FormTag form = getForm();
		if (form != null) {
			String formId = form.getId();
			writeText(writer,
					"<script type='text/javascript'>input=document.getElementById('"
							+ formId + "')['" + id + "'];");
			if (selectid != null) {
				writeText(writer, "document.getElementById('" + getId()
						+ "').selectednode=document.getElementById('" + getId()
						+ "_" + selectid + "');");
			}
			writeText(writer, "</script>");
		} else if (selectid != null) {
			writeText(writer,
					"<script type='text/javascript'>document.getElementById('"
							+ getId()
							+ "').selectednode=document.getElementById('"
							+ getId() + "_" + selectid + "');</script>");
		}
		if (dropdown)
			writeText(writer,
					"<script type='text/javascript'>new DropdownButton().initialize('"
							+ getId() + "');</script>");
		return SKIP_BODY;
	}

	@Override
	protected int endTag(JspWriter writer) throws JspException, IOException {
		return EVAL_PAGE;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getDropdownButtonText() {
		return dropdownButtonText;
	}

	public void setDropdownButtonText(String dropdownButtonText) {
		this.dropdownButtonText = dropdownButtonText;
	}

	public int getDropdownHeight() {
		return dropdownHeight;
	}

	public void setDropdownHeight(int dropdownHeight) {
		this.dropdownHeight = dropdownHeight;
	}

	public boolean isDropdown() {
		return dropdown;
	}

	public void setDropdown(boolean dropdown) {
		this.dropdown = dropdown;
	}

}
