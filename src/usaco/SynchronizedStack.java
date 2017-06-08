package usaco;

/**
 * 同步堆栈类
 * @param args
 */

public class SynchronizedStack {
	private int index = 0;
	private int size = 100;
	
	private char[] data;
	
	public SynchronizedStack(int size){
		System.out.println("堆栈被创建");
		this.size = size;
		data = new char[size];
	}
	
	//创建数据
	public synchronized void push(char c){
		while (index == size){
			try {
				System.out.println("堆栈满了");
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
		data[index] = c;
		index++;
		this.notify();
	}
	
	//消费数据
	public synchronized char pop(){
		while(index == 0){
			try {
				System.out.println("堆栈空了");
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
		index--;
		char ch = data[index];
		this.notify();
		return ch;
	}
	
	//显示堆栈内容
	public synchronized void print(){
		for(int i = 0; i < data.length; i++){
			System.out.println(data[i]);
		}
		this.notify();
	}
}
