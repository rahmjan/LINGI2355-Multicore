public class FinalCountdowns {
	public static void main(String[] args) {
		Thread[] t = new Thread[8];
		for (int i=0; i<8; i++) {
			t[i] = new Thread(new Runnable() {
				public void run() {
					for (int c=5; c>0; c--) {
						System.out.print(c+"... ");
					}
					System.out.println("ready!");
				}
			});
		}
		for (int i=0; i<8; i++) {
			t[i].start();
		}
		System.out.println("IGNITION!!!");
	}
}
