public class FinalCountdowns {

	public static void main(String[] args) {
		Thread[] t = new Thread[8];

		for (int i=0; i<8; i++) {
			t[i] = new Thread(new Runnable() {
				public void run() {

					long thID = Thread.currentThread().getId();

					for (int c=5; c>0; c--) {
						System.out.print("Thread_ID: " + thID + ": " + c + "... \n");
						try{
							Thread.sleep(1000);
						} catch (InterruptedException e) {}
					}
					System.out.println("ready!");
				}
			});
		}

		for (int i=0; i<8; i++) {
			t[i].start();
		}

		for (int i=0; i<8; i++) {
			try{
				t[i].join();
			} catch (InterruptedException e) {}
		}

		System.out.println("IGNITION!!!");
	}
}
