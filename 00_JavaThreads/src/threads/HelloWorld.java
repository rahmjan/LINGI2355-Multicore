public class HelloWorld implements Runnable {
	private final String s ;
	
	public HelloWorld(String s) {
		this.s = s ;
	}

	public void run() {
		System.out.println(s);
	}

	public static void main(String[] args) {
		Thread t = new Thread(new HelloWorld("Hello world!"));
		
		t.start();
	}
}
