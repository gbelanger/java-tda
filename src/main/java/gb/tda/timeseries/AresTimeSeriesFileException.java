package gb.tda.timeseries;

public class AresTimeSeriesFileException extends TimeSeriesFileException {

    public AresTimeSeriesFileException() {
        super();
    }

    public AresTimeSeriesFileException (String msg) {
        super(msg);
    }

    public AresTimeSeriesFileException (String msg, Exception e) {
        super(msg, e);
    }

}
