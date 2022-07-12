package gb.tda.eventlist;

public class EventFileException extends EventListException {

    public EventFileException() {
        super();
    }

    public EventFileException (String msg) {
        super(msg);
    }

    public EventFileException (String msg, Exception e) {
        super(msg, e);
    }

}
