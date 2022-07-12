package gb.tda.periodogram;

public class WindowFunctionException extends Exception {

    public WindowFunctionException () {
        super();
    }

    public WindowFunctionException (String msg) {
        super(msg);
    }

    public WindowFunctionException (String msg, Exception e) {
        super(msg+" ", e);
    }
}
