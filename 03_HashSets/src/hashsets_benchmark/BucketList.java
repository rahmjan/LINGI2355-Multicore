package hashsets_benchmark;

public class BucketList /* implements Set */ {

	// Beginning provided code
	static final int WORD_SIZE = 24;
	static final int LO_MASK = 0x00000001;
	static final int HI_MASK = 0x00800000;
	static final int MASK = 0x00FFFFFF;
	public static int reverse(int key) {
		int loMask = LO_MASK;
		int hiMask = HI_MASK;
		int result = 0;
		for (int i = 0; i < WORD_SIZE; i++) {
			if ((key & loMask) != 0) {  // bit set
				result |= hiMask;
			}
			loMask <<= 1;
			hiMask >>>= 1;  // fill with 0 from left
		}
		return result;
	}
	public int makeOrdinaryKey(int value) {
		int code = value & MASK; // take 3 lowest bytes
		return reverse(code | HI_MASK);
	}
	private static int makeSentinelKey(int value) {
		return reverse(value & MASK);
	}
	public static int hashCode(int value) {
		return value & MASK;
	}
	// End provided code

}
