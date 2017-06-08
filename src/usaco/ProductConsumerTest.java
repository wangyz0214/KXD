package usaco;

public class ProductConsumerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SynchronizedStack stack = new SynchronizedStack(5);
		Runnable source = new Producer(stack);
		Runnable sink = new Consumer(stack);
		
		Thread t1 = new Thread(source);
		Thread t2 = new Thread(sink);
		
		t1.start();
		t2.start();
	}

}
