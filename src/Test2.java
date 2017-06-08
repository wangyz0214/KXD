import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;


public abstract class Test2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//创建一个集合
		  Collection<String> books = new HashSet<String>();
		  books.add("aaaa");
		  books.add("bbbbb");
		  books.add("ccccccc");
		  //获取books集合对应的迭代器
		  java.util.Iterator<String> it = books.iterator();
		  while(it.hasNext())
		  {
		   String book = (String)it.next();
		   System.out.println(book);
		   if (book.equals("bbbbbb"))
		   {
		    it.remove();
		   }
		   //对book变量赋值，不会改变集合元素本身
		   book = "测试字符串";
		  }
		  System.out.println("---------------------------");
		  System.out.println(books);
		  
		  testArray();

	}

	private static void testArray() {
		// TODO Auto-generated method stub
		System.out.println("11111");
	}
	
	

}
