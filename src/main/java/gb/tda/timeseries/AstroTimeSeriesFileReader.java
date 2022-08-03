package gb.tda.timeseries;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

public class AstroTimeSeriesFileReader {
    private static Logger logger  = Logger.getLogger(AstroTimeSeriesFileReader.class);

	// Define the specific format readers
	// IMP: Fits reader must come first because FITS is actually an ascii file with a special header
    private static ITimeSeriesFileReader[] formats = {
			new FitsAstroTimeSeriesFileReader(),
			new AsciiAstroTimeSeriesFileReader(),
			new QDPAstroTimeSeriesFileReader()
	};

	// Loop through the formats
	static ITimeSeries read(String filename) throws TimeSeriesFileException {
		logger.info("Reading file "+(new File(filename)).getPath());
		Exception e = new Exception();
		for (ITimeSeriesFileReader reader : formats) {
			try {
				return reader.read(filename);
			}
			catch (TimeSeriesFileException e1) { e = e1; }
			catch (TimeSeriesException e2) { e = e2; }
			catch (BinningException e3) { e = e3; }
			catch (IOException e4) { e = e4;}
		}
		throw new TimeSeriesFileException("Unknown format: not FITS, ASCII, or QDP.", e);
    }
}
