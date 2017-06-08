package usaco;

import java.io.*;
import java.util.HashMap;

import jxl.*;
import jxl.read.biff.BiffException;

public class project2 {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws BiffException 
	 */
	public static void main(String[] args) throws BiffException, IOException {
		HashMap<String,String> map=new HashMap<String,String>();
		String fileNode = "E:\\test.xls"; // Excel文件所在路径
        File file = new File(fileNode); // 创建文件对象  
        Workbook wb = Workbook.getWorkbook(file);
        Sheet sheet = wb.getSheet(0); // 从工作区中取得页（Sheet）
        
        for (int i = 0; i < sheet.getRows(); i++) { // 循环打印Excel表中的内容 
    		
    		map.put("first", sheet.getCell(0, i).getContents().trim());
    		map.put("second", sheet.getCell(1, i).getContents().trim());
    		map.put("third", sheet.getCell(2, i).getContents().trim());
    		
    		String first = map.get("first");
    		String second = map.get("second");
    		String third = map.get("third");
    		
        	System.out.println("first---" + first + "---second---" + second + "---third---" + third);
        }
	}
}