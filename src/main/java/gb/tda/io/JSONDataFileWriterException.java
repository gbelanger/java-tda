package gb.tda.io;

public class JSONDataFileWriterException extends Exception {

    public JSONDataFileWriterException () {
        super();
    }
    
    public JSONDataFileWriterException (String msg) {
        super(msg);
    }
    
    public JSONDataFileWriterException (String msg, Exception e) {
        super(msg, e);
    }

}
