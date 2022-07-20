package gb.tda.eventlist;

import java.io.IOException;
import org.apache.log4j.Logger;

import gb.tda.io.AsciiDataFileWriter;

final class EventListWriter {

	// Improvements:
	// - write in FITS format

    private static Logger logger  = Logger.getLogger(EventListWriter.class.getName());

	static void writeAsQDP(IEventList evlist, String filename) throws IOException {
		double[] y = new double[evlist.nEvents()];
		for (int i = 0; i < evlist.nEvents(); i++) {
			y[i] = 0.15;
		}
		AsciiDataFileWriter out = new AsciiDataFileWriter(filename);
		String[] header = new String[] {
				"DEV /XS",
				"LAB T", "LAB F",
				"TIME OFF",
				"LINE OFF",
				"MA 39 ON 1", "MA SIZE 5",
				"LW 4", "CS 1.3",
				"LAB X Time (s)",
				"VIEW 0.1 0.2 0.9 0.8",
				"SKIP SINGLE",
				"!"
		};
		out.writeData(header, evlist.getArrivalTimes(), y);
		logger.info("Event list arrival times written to "+filename);
	}

    static void writeTimesAsQDP(IEventList evlist, String filename) throws IOException {
		writeAsQDP(evlist, filename);
    }

    static void writeEnergiesVsTimeAsQDP(AstroEventList evlist, String filename) throws IOException, EventListException {
		AsciiDataFileWriter out = new AsciiDataFileWriter(filename);
		String[] header = new String[] {
		    "DEV /XS",
		    "LAB T", "LAB F",
		    "TIME OFF",
		    "LINE OFF",
		    "MA 2 ON", "MA SIZE 3",
		    "LW 4", "CS 1.3",
		    "LAB X Time (s)",
		    "LAB Y Energy",
		    "VIEW 0.1 0.2 0.9 0.8",
		    "SKIP SINGLE",
		    "!"
		};
		out.writeData(header, evlist.getArrivalTimes(), evlist.getEnergies());
		logger.info("Event list energies vs time written to "+filename);
    }

    static void writeXYCoordsAsQDP(AstroEventList evlist, String filename) throws IOException, EventListException {
		AsciiDataFileWriter out = new AsciiDataFileWriter(filename);
		String[] header = new String[] {
		    "DEV /XS",
		    "LAB T", "LAB F",
		    "TIME OFF",
		    "LINE OFF",
		    "MA 2 ON", "MA SIZE 0.5",
		    "LW 3", "CS 1.3",
		    "LAB X Time (s)",
		    "LAB Y Energy",
		    "VIEW 0.2 0.1 0.8 0.9",
		    "SKIP SINGLE",
		    "!"
		};
		out.writeData(header, evlist.getXCoords(), evlist.getYCoords());
		logger.info("Event detector coordinates written as Y vs X to "+filename);
    }

    

}
