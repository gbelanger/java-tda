package gb.tda.eventlist;

public class FitsEventFileFormatException extends EventFileFormatException {

    public FitsEventFileFormatException() {
        super();
    }

    public FitsEventFileFormatException (String msg) {
        super(msg);
    }

    public FitsEventFileFormatException (String msg, Exception e) {
        super(msg, e);
    }

}
