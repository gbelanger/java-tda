package gb.tda.eventlist;

import java.io.IOException;


public interface IEventFileReader {

    EventList readEventFile(String filename) throws EventFileException, EventListException, IOException ;

}
