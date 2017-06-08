import java.text.SimpleDateFormat;
import java.util.Date;


public class Test1 {
	 public static void main(String[] args) {
		 
//		 SimpleDateFormat formatter; 
		 SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss"); 
		 String ctime = formatter.format(new Date()).replaceAll("-", "/"); 
		 System.out.println(ctime);
	}
}
