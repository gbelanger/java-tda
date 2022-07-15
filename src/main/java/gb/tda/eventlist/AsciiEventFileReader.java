package gb.tda.eventlist;

import java.io.IOException;

import gb.tda.io.AsciiDataFileReader;
import gb.tda.io.AsciiDataFileFormatException;

import org.apache.log4j.Logger;


public class AsciiEventFileReader implements IEventFileReader{

    private static Logger logger  = Logger.getLogger(AsciiEventFileReader.class.getName());

    public AstroEventList read(String evlistFilename) throws AsciiEventFileException, EventListException, IOException {

		AsciiDataFileReader dataFile = null;
		try {
			logger.info("Attempting to read ASCII event file.");
		    dataFile = new AsciiDataFileReader(evlistFilename);
		}
		catch (AsciiDataFileFormatException e) {
		    throw new AsciiEventFileException("Problem reading ASCII data file", e);
		}

		int ncols = dataFile.getNDataCols();
		if (ncols == 1) {

		    double[] times = dataFile.getDblCol(0);
		    return new AstroEventList(times); 
		}
		else if (ncols == 2) {

		    double[] times = dataFile.getDblCol(0);
		    double[] energies = dataFile.getDblCol(1);
		    return new AstroEventList(times, energies);
		}
		else if (ncols == 4) {
		    
		    double[] times = dataFile.getDblCol(0);
		    double[] energies = dataFile.getDblCol(1);
		    int[] xCoords = dataFile.getIntCol(2);
		    int[] yCoords = dataFile.getIntCol(3);
		    return new AstroEventList(times, energies, xCoords, yCoords);
		}
		else {
		    throw new AsciiEventFileException("Not a known ASCII event file."
			   +"Format can be:  1 col = times,  2 cols = times and energy,  4 cols = times, energy, xCoords and yCoords.");
		}

    }
    
}
