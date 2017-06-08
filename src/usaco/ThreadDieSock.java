package usaco;

public class ThreadDieSock implements Runnable{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ThreadDieSock run01 = new ThreadDieSock();
		ThreadDieSock run02 = new ThreadDieSock();
		run01.flag = 1;
		run02.flag = 0;
		Thread thread01 = new Thread(run01);
		Thread thread02 = new Thread(run02);
		thread01.run();
		thread02.run();
	}
	
	private int flag = 1;
	private Object obj1 = new Object(), obj2 = new Object();

	@Override
	public void run() {
		System.out.println("flag = " + flag);
		if(flag == 1){
			synchronized (obj1){
				System.out.println("锁定1 ");
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (obj2){
				System.out.println("1");
			}
		}
		if(flag == 0){
			synchronized (obj2){
				System.out.println("锁定2 ");
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (obj1){
				System.out.println("2");
			}
		}
	}

}
