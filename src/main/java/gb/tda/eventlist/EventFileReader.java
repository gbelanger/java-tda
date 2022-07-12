package gb.tda.eventlist;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;


public class EventFileReader {

    private static Logger logger  = Logger.getLogger(EventFileReader.class.getName());
    private static IEventFileReader[] formats = {new FitsEventFileReader(), new AsciiEventFileReader()};

    static EventList readEventFile(String filename) throws EventFileException, EventListException, IOException {
		logger.info("Reading file "+(new File(filename)).getPath());		
		for ( IEventFileReader reader : formats ) {
		    try {
				return reader.readEventFile(filename);
			    }
		    catch ( EventFileFormatException e ) {}
		}
		throw new EventFileException("File is not a FITS or ASCII event file.");
    }
    
}
