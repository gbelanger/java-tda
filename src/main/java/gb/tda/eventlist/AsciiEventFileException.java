package gb.tda.eventlist;

public class AsciiEventFileException extends EventFileException {

    public AsciiEventFileException() {
        super();
    }

    public AsciiEventFileException (String msg) {
        super(msg);
    }

    public AsciiEventFileException (String msg, Exception e) {
        super(msg, e);
    }

}
