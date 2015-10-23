package lilylicious.staticchunkmanager.util;

public class MathUtils {

    //floorDiv is a Java 8 specific method so we backport it here
    public static int floorDiv(int x, int y) {
        return (x >= 0 ? x / y : ((x + 1) / y) - 1);
    }
    
}
