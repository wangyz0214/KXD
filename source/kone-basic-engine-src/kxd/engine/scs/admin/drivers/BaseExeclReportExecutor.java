package kxd.engine.scs.admin.drivers;

import java.util.ArrayList;
import java.util.Collection;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import kxd.net.ExcelExecutor;
import kxd.net.HttpRequest;
import kxd.util.DateTime;
import kxd.util.KeyValue;
import kxd.util.excel.ExcelCell;
import kxd.util.excel.ExcelSheet;

abstract public class BaseExeclReportExecutor implements ExcelExecutor {
	protected int createSheetHead(HttpRequest request, ExcelSheet sheet,
			int cols, String reportName, Object... params) {
		ArrayList<ExcelCell> ls = sheet.add();
		cols--;
		int row = 0;
		ls.add(new ExcelCell(row++, 0, reportName, "黑体", 24, 0,
				ExcelCell.CENTRE, 0, cols));
		String p = request.getParameterDef("hall", null);
		if (p != null) {
			ls.add(new ExcelCell(row++, 0, p, "Arial", 10, 0, ExcelCell.LEFT,
					0, cols));
		}
		for (Object o : params) {
			ls.add(new ExcelCell(row++, 0, o.toString(), "Arial", 10, 0,
					ExcelCell.LEFT, 0, cols));
		}
		ls.add(new ExcelCell(row++, 0, "报表打印时间："
				+ new DateTime().format("yyyy-MM-dd HH:mm:ss"), "Arial", 10, 0,
				ExcelCell.LEFT, 0, cols));
		return row;
	}

	protected int createDetailHead1(HttpRequest request, ExcelSheet sheet,
			int row, Object... params) throws Throwable {
		ArrayList<ExcelCell> ls = sheet.add();
		WritableCellFormat hf = new WritableCellFormat();
		hf.setAlignment(Alignment.CENTRE);
		hf.setVerticalAlignment(VerticalAlignment.CENTRE);
		hf.setFont(new WritableFont(WritableFont.createFont("Arial"), 10,
				WritableFont.BOLD));
		hf.setBackground(Colour.GRAY_25);
		hf.setBorder(Border.ALL, BorderLineStyle.THIN);
		int i = 0;
		ls.add(new ExcelCell(row, i++, "省分", hf));
		ls.add(new ExcelCell(row, i++, "地市", hf));
		ls.add(new ExcelCell(row, i++, "营业厅", hf));
		ls.add(new ExcelCell(row, i++, "终端编码", hf));
		for (Object o : params) {
			ls.add(new ExcelCell(row, i++, o.toString(), hf));
		}
		return ++row;
	}

	protected int createHead1(HttpRequest request, ExcelSheet sheet, int row,
			Object... params) throws Throwable {
		ArrayList<ExcelCell> ls = sheet.add();
		WritableCellFormat hf = new WritableCellFormat();
		hf.setVerticalAlignment(VerticalAlignment.CENTRE);
		hf.setAlignment(Alignment.CENTRE);
		hf.setFont(new WritableFont(WritableFont.createFont("Arial"), 10,
				WritableFont.BOLD));
		hf.setBackground(Colour.GRAY_25);
		hf.setBorder(Border.ALL, BorderLineStyle.THIN);
		int i = 0;
		ls.add(new ExcelCell(row, i++, "地区", hf));
		for (Object o : params) {
			ls.add(new ExcelCell(row, i++, o.toString(), hf));
		}
		return ++row;
	}

	@SuppressWarnings("unchecked")
	protected int createHead3(HttpRequest request, ExcelSheet sheet, int row,
			String category, Collection<KeyValue<String, Object[]>> fields,
			Object... params) throws Throwable {
		ArrayList<ExcelCell> ls = sheet.add();
		WritableCellFormat hf = new WritableCellFormat();
		hf.setVerticalAlignment(VerticalAlignment.CENTRE);
		hf.setAlignment(Alignment.CENTRE);
		hf.setFont(new WritableFont(WritableFont.createFont("Arial"), 10,
				WritableFont.BOLD));
		hf.setBackground(Colour.GRAY_25);
		hf.setBorder(Border.ALL, BorderLineStyle.THIN);
		int i = 0;
		ls.add(new ExcelCell(row, i++, "地区", hf, 2, 0));
		if (fields != null) {
			int start = i;
			for (KeyValue<String, Object[]> f : fields) {
				ls.add(new ExcelCell(row + 1, i, f.getKey(), hf, 0, f
						.getValue().length - 1));
				for (Object v : f.getValue()) {
					ls.add(new ExcelCell(row + 2, i++, v.toString(), hf, 0, 0));
				}
			}
			ls.add(new ExcelCell(row, start, category, hf, 0, i - start - 1));
		}
		for (Object o : params) {
			KeyValue<String, String[]> f = (KeyValue<String, String[]>) o;
			ls.add(new ExcelCell(row, i, f.getKey(), hf, 1,
					f.getValue().length - 1));
			for (String v : f.getValue()) {
				ls.add(new ExcelCell(row + 2, i++, v, hf, 0, 0));
			}
		}
		return row + 3;
	}

	@SuppressWarnings("unchecked")
	protected int createDetailHead3(HttpRequest request, ExcelSheet sheet,
			int row, String category,
			Collection<KeyValue<String, Object[]>> fields, Object... params)
			throws Throwable {
		ArrayList<ExcelCell> ls = sheet.add();
		WritableCellFormat hf = new WritableCellFormat();
		hf.setVerticalAlignment(VerticalAlignment.CENTRE);
		hf.setAlignment(Alignment.CENTRE);
		hf.setFont(new WritableFont(WritableFont.createFont("Arial"), 10,
				WritableFont.BOLD));
		hf.setBackground(Colour.GRAY_25);
		hf.setBorder(Border.ALL, BorderLineStyle.THIN);
		int i = 0;
		ls.add(new ExcelCell(row, i, "归属", hf, 1, 2));
		ls.add(new ExcelCell(row + 2, i++, "省分", hf, 0, 0));
		ls.add(new ExcelCell(row + 2, i++, "地市", hf, 0, 0));
		ls.add(new ExcelCell(row + 2, i++, "营业厅", hf, 0, 0));
		ls.add(new ExcelCell(row, i++, "终端编码", hf, 2, 0));
		if (fields != null) {
			int start = i;
			for (KeyValue<String, Object[]> f : fields) {
				ls.add(new ExcelCell(row + 1, i, f.getKey(), hf, 0, f
						.getValue().length - 1));
				for (Object v : f.getValue()) {
					ls.add(new ExcelCell(row + 2, i++, v.toString(), hf, 0, 0));
				}
			}
			ls.add(new ExcelCell(row, start, category, hf, 0, i - start - 1));
		}
		for (Object o : params) {
			KeyValue<String, String[]> f = (KeyValue<String, String[]>) o;
			ls.add(new ExcelCell(row, i, f.getKey(), hf, 1,
					f.getValue().length - 1));
			for (String v : f.getValue()) {
				ls.add(new ExcelCell(row + 2, i++, v, hf, 0, 0));
			}
		}
		return row + 3;
	}

	@SuppressWarnings("unchecked")
	protected int createDetailHead2(HttpRequest request, ExcelSheet sheet,
			int row, String category, Collection<String> fields,
			Object... params) throws Throwable {
		ArrayList<ExcelCell> ls = sheet.add();
		WritableCellFormat hf = new WritableCellFormat();
		hf.setVerticalAlignment(VerticalAlignment.CENTRE);
		hf.setAlignment(Alignment.CENTRE);
		hf.setFont(new WritableFont(WritableFont.createFont("Arial"), 10,
				WritableFont.BOLD));
		hf.setBackground(Colour.GRAY_25);
		hf.setBorder(Border.ALL, BorderLineStyle.THIN);
		int i = 0;
		ls.add(new ExcelCell(row, i, "归属", hf, 0, 2));
		ls.add(new ExcelCell(row + 1, i++, "省分", hf, 0, 0));
		ls.add(new ExcelCell(row + 1, i++, "地市", hf, 0, 0));
		ls.add(new ExcelCell(row + 1, i++, "营业厅", hf, 0, 0));
		ls.add(new ExcelCell(row, i++, "终端编码", hf, 1, 0));
		if (fields != null) {
			int start = i;
			for (String f : fields) {
				ls.add(new ExcelCell(row + 1, i++, f, hf, 0, 0));
			}
			ls.add(new ExcelCell(row, start, category, hf, 0, i - start - 1));
		}
		for (Object o : params) {
			KeyValue<String, String[]> f = (KeyValue<String, String[]>) o;
			ls.add(new ExcelCell(row, i, f.getKey(), hf, 0,
					f.getValue().length - 1));
			for (String v : f.getValue()) {
				ls.add(new ExcelCell(row + 1, i++, v, hf, 0, 0));
			}
		}
		return row + 2;
	}

	@SuppressWarnings("unchecked")
	protected int createHead2(HttpRequest request, ExcelSheet sheet, int row,
			String category, Collection<String> fields, Object... params)
			throws Throwable {
		ArrayList<ExcelCell> ls = sheet.add();
		WritableCellFormat hf = new WritableCellFormat();
		hf.setVerticalAlignment(VerticalAlignment.CENTRE);
		hf.setAlignment(Alignment.CENTRE);
		hf.setFont(new WritableFont(WritableFont.createFont("Arial"), 10,
				WritableFont.BOLD));
		hf.setBackground(Colour.GRAY_25);
		hf.setBorder(Border.ALL, BorderLineStyle.THIN);
		int i = 0;
		ls.add(new ExcelCell(row, i++, "地区", hf, 1, 0));
		if (fields != null) {
			int start = i;
			for (String f : fields) {
				ls.add(new ExcelCell(row + 1, i++, f, hf, 0, 0));
			}
			ls.add(new ExcelCell(row, start, category, hf, 0, i - start - 1));
		}
		for (Object o : params) {
			KeyValue<String, String[]> f = (KeyValue<String, String[]>) o;
			ls.add(new ExcelCell(row, i, f.getKey(), hf, 0,
					f.getValue().length - 1));
			for (String v : f.getValue()) {
				ls.add(new ExcelCell(row + 1, i++, v, hf, 0, 0));
			}
		}
		return row + 2;
	}
}
