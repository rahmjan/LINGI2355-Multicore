public class Volatile {
	static volatile boolean stop = false;
	
	public static void main(String[] args) {
		Thread tA, tB;
		
		tA = new Thread(new Runnable() {
			public void run() {
				while (!stop) { }
				System.out.println("Reader stops ");
			}
		});
		
		tB = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
				stop=true;
			}
		});
		
		long start = System.nanoTime();
		tA.start(); tB.start();
		try {
			tA.join(); tB.join();
		} catch (InterruptedException e) {}
		
		System.out.println("Total running time: "+((System.nanoTime()-start)/1000000)+" ms.");
	}
}
