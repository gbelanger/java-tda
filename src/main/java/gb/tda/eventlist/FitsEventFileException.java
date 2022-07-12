package gb.tda.eventlist;

public class FitsEventFileException extends EventFileException {

    public FitsEventFileException() {
        super();
    }

    public FitsEventFileException (String msg) {
        super(msg);
    }

    public FitsEventFileException (String msg, Exception e) {
        super(msg, e);
    }

}
