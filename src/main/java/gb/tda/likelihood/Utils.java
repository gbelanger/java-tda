package gb.tda.likelihood;

import org.apache.log4j.Logger;

public final class Utils {

    private static Logger logger = Logger.getLogger(Utils.class);

    public static void checkArrayLengthsAreEqual(double[] array1, double[] array2) throws IllegalArgumentException {
        if (array1.length != array2.length) {
            throw new IllegalArgumentException("Number of elements is different: array1.length ("+array1.length+") != array2.length ("+array2.length+")");
        }
    }

}