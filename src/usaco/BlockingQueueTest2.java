package usaco;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueTest2 {
	
	public class Basket{
		BlockingQueue<String> basket = new LinkedBlockingQueue<String>(3);
		
		public void produce() throws InterruptedException {
			basket.put("An Apple");
		}
		
		public String consume() throws InterruptedException {
			return basket.take();
		}
	}
	
	class Producer implements Runnable{
		private String instance;
		private Basket basket;
		
		public Producer(String instance, Basket basket){
			this.instance = instance;
			this.basket = basket;
		}

		@Override
		public void run() {
			try {
				while(true){
					System.out.println("生产苹果 ： " + instance);
					basket.produce();
					Thread.sleep(300);
				}
			} catch (InterruptedException e) {
				System.out.println("Producer Interrupted");
			}
		}
	}
	
	class Consumer implements Runnable{
		private String instance;
		private Basket basket;
		
		public Consumer(String instance , Basket basket){
			this.instance = instance;
			this.basket = basket;
		}
		@Override
		public void run() {
			try {
				System.out.println("消费苹果 ： " + instance);
				System.out.println(basket.consume());
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Consumer Interrupted");
			}
		}
	}

	public static void main(String[] args) {
		BlockingQueueTest2 test = new BlockingQueueTest2();
		
		Basket basket = test.new Basket();
		
		ExecutorService service = Executors.newCachedThreadPool();
		Producer producer = test.new Producer("生产者001", basket);
		Producer producer2 = test.new Producer("生产者002", basket);
	    Consumer consumer = test.new Consumer("消费者001", basket);
	    
	    service.submit(producer);
	    service.submit(producer2);
	    service.submit(consumer);

	}

}
