package simpleorm;

public class Utils {
	private static final boolean isDebug = false;
	
	public static void debugPrint(Object msg) {
		if(isDebug) {
			System.out.println(msg);
		}
	}
}
