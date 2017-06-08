/*
众所周知，在每一个彗星后都有一只UFO。这些UFO时常来收集地球上的忠诚支持者。不幸的是，他们的飞碟每次出行都只能带上一组支持者。因此，他们要用一种聪明的方案让这些小组提前知道谁会被彗星带走。
他们为每个彗星起了一个名字，通过这些名字来决定这个小组是不是被带走的那个特定的小组（你认为是谁给这些彗星取的名字呢？）。关于如何搭配的细节会在下面告诉你；
你的任务是写一个程序，通过小组名和彗星名来决定这个小组是否能被那颗彗星后面的UFO带走。
小组名和彗星名都以下列方式转换成一个数字：最终的数字就是名字中所有字母的积，其中“A”是1，“Z”是26。例如，“USACO”小组就是21*19*1*3*15=17955。如果小组的数字 mod 47等于彗星的数字mod 47,你就得告诉这个小组需要准备好被带走！
（记住“a mod b”是a除以b的余数；34 mod 10等于4，一般而言，当其中的a是负数时，不论b的符号如何结果都是负数，但是在数学计算中却不是这样，需要谨记！）
写出一个程序，读入彗星名和小组名并算出用上面的方案能否将两个名字搭配起来，如果能搭配，就输出“GO”，否则输出“STAY”。小组名和彗星名均是没有空格或标点的一串大写字母（不超过6个字母）。 
*/



package usaco;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class project1 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader f = new BufferedReader(new FileReader("D:\\ride.in"));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("D:\\ride.out")));
		
		String s1 = f.readLine();
		String s2 = f.readLine();
		
		int num1 = 1;
		int num2 = 1;
		
		for(int i = 0; i < s1.length(); i++){
			num1 *= s1.charAt(i) - 'A' + 1;
		}
		for(int i = 0; i < s1.length(); i++){
			num2 *= s2.charAt(i) - 'A' + 1;
		}
		
		if (num1 % 47 == num2 % 47){
			System.out.println("GO" + "\n");
			out.write("GO" + "\n");
		}else {
			System.out.println("STAY" + "\n");
			out.write("STAY" + "\n");
		}
		
		out.close();
		System.exit(0);
	}

}
