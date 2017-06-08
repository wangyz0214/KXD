package usaco;

public class TestMitiThread {

	/**
	 * 扩展java.lang.Thread类
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(Thread.currentThread().getName() + "线程运行开始");
		new MitiSay("A").start();
		new MitiSay("B").start();
		System.out.println(Thread.currentThread().getName() + "线程运行结束");
	}
}

class MitiSay extends Thread {
	public MitiSay (String threadName){
		super(threadName);
	}
	
	public void run() {
		System.out.println(getName() + "线程运行开始");
		for (int i = 0; i < 10; i++){
			System.out.println(i + " " + getName());
			try {
				sleep((int) Math.random() * 10);  //方法调用目的是不让当前线程独自霸占该进程所获取的CPU资源，以留出一定时间给其他线程执行的机会。
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(getName() + "线程运行结束");
	}
}