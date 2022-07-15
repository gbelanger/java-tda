package gb.tda.eventlist;

import java.io.IOException;


public interface IEventFileReader {

    IEventList read(String filename) throws EventFileException, EventListException, IOException ;

}
