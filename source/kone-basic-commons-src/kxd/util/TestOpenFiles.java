package kxd.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestOpenFiles {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.print(StringUnit.arrayToString(new Integer[] { 0, 1, 2 }));
		if (args.length == 0)
			System.out.println("Usage: TestOpenFiles files");
		else {
			int files = Integer.valueOf(args[0]);
			new File("files").mkdirs();
			for (int i = 0; i < files; i++) {
				File file = new File("files/" + i + ".txt");
				try {
					new FileOutputStream(file).write("a".getBytes());
				} catch (FileNotFoundException e) {
					System.out.println("error files=" + i);
					e.printStackTrace();
					break;
				} catch (IOException e) {
					System.out.println("error files=" + i);
					e.printStackTrace();
				}
			}
		}
	}

}
