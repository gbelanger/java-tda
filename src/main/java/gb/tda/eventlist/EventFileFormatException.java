package gb.tda.eventlist;

public class EventFileFormatException extends EventFileException {

    public EventFileFormatException() {
        super();
    }

    public EventFileFormatException (String msg) {
        super(msg);
    }

    public EventFileFormatException (String msg, Exception e) {
        super(msg, e);
    }

}
