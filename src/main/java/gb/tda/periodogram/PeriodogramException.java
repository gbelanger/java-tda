package gb.tda.periodogram;

public class PeriodogramException extends Exception {

    public PeriodogramException () {
        super();
    }

    public PeriodogramException (String msg) {
        super(msg);
    }

    public PeriodogramException (String msg, Exception e) {
        super(msg+" ", e);
    }

}
