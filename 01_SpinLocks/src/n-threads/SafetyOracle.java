public class SafetyOracle {
	private static volatile int threadInCS = -1 ;
	private static volatile boolean fatal = false;
	
	public static void setFatal (boolean fatal) {
		SafetyOracle.fatal = fatal;
	}
	
	private static void failWith(String s) { 
		System.err.println(s);
		if (fatal) System.exit(-1);
	}
	
	public static void enterCS (int id) {
		if (threadInCS != -1) {
			failWith("Thread "+id+" entering its critical section while thread "+threadInCS+" is still in its CS");
		}
		threadInCS = id;
	}
	
	public static void leaveCS (int id) {
		if (threadInCS != id) {
			failWith("Thread "+id+" is leaving its critical section, but oracle thinks Thread "+threadInCS+" is currently in!");
		} else {
			threadInCS = -1;
		}		
	}
}
