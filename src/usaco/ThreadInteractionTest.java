package usaco;

public class ThreadInteractionTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadSum sum = new ThreadSum();
		sum.start();
		
		synchronized (sum) {
			try {
				System.out.println("等待计算");
				sum.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("zonghe = " + sum.total);
		}
	}

}


