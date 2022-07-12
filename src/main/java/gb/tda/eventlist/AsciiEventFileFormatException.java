package gb.tda.eventlist;

public class AsciiEventFileFormatException extends EventFileFormatException {

    public AsciiEventFileFormatException() {
        super();
    }

    public AsciiEventFileFormatException (String msg) {
        super(msg);
    }

    public AsciiEventFileFormatException (String msg, Exception e) {
        super(msg, e);
    }

}
