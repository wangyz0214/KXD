package usaco;

public class Producer implements Runnable {
	private SynchronizedStack stack;
	
	public Producer(SynchronizedStack s){
		stack = s;
	}
	@Override
	public void run() {
		char ch;
		for(int i = 0; i < 100; i++){
			ch = (char) (Math.random() * 26 + 'A');
			stack.push(ch);
			System.out.println("Produced : " + ch);
			try {
				Thread.sleep((int) (Math.random() * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
