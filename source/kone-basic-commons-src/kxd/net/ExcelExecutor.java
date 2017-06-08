package kxd.net;

import java.io.IOException;

import jxl.write.WritableWorkbook;

/**
 * Excel生成器接口。
 * 
 * @author 赵明
 * @see kxd.web.servlets#XMLTradeManager
 * @version 4.1
 * 
 */
public interface ExcelExecutor {
	public void execute(HttpRequest request, WritableWorkbook book)
			throws IOException, NoSuchFieldException;
}
