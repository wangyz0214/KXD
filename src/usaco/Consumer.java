package usaco;

public class Consumer implements Runnable{
	private SynchronizedStack stack;
	
	public Consumer(SynchronizedStack s){
		stack = s;
	}

	@Override
	public void run() {
		char ch;
		for(int i = 0; i < 100; i++){
			ch = stack.pop();
			System.out.println("Consumed : " + ch);
			try {
				Thread.sleep((int) (Math.random() * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
