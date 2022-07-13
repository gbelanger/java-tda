package gb.tda.timeseries;

import java.io.IOException;
import org.apache.log4j.Logger;

import gb.tda.eventlist.AsciiEventFileException;
import gb.tda.eventlist.AsciiEventFileReader;
import gb.tda.eventlist.EventList;
import gb.tda.eventlist.EventListException;
import gb.tda.io.AsciiDataFileFormatException;
import gb.tda.io.AsciiDataFileReader;

/**
 * Class <code>AsciiTimeSeriesFileReader</code> reads a times series file in ASCII format.
 *
 * If it contains only 1 column, then it is treated as an event file
 . * The TimeSeries will have adjacent bins of equal widths.
 *
 * If it contains 2 columns, then 
 * the first column are the binCentres and the second are the counts in each bin.
 * The TimeSeries will have adjacent bins with widths determined from the binCentres.
 *
 * If it contains 3 columns, then 
 * the first column are the binCentres, the second are the rates, and the third are the errors.
 * The TimeSeries will have adjacent bins with widths determined from the binCentres.
 *
 * If it contains 4 columns (or more), then 
 * the first column are the binCentres, the second are the halfBinWidths, the third are the rates, the third are the errors.
 * The TimeSeries will have bins defined by the binCentres and corresponding widths.
 *
 * @author G. Belanger
 *
 */
class AsciiTimeSeriesFileReader implements ITimeSeriesFileReader {

    private static final Logger logger  = Logger.getLogger(AsciiTimeSeriesFileReader.class);
    
    public ITimeSeries readTimeSeriesFile(String filename) throws TimeSeriesFileException, TimeSeriesException, BinningException, IOException  {
		AsciiDataFileReader dataFile;
		try {
			dataFile = new AsciiDataFileReader(filename);
		}
		catch ( AsciiDataFileFormatException e ) {
			throw new AsciiTimeSeriesFileException("Problem reading ASCII data file", e);
		}
        // Read the tStart time from the label so the information is't lost when we read and re-read the files
		String[] header = dataFile.getHeader();
		String stringsToFind = "LAB X Time (s) since MJD ";
		int j=0;
		boolean found = header[j].contains(stringsToFind);
		while ( ! found ) {
			j++;
			found = header[j].contains(stringsToFind);
		}
		int tStartRow = j;
		String tStartStr = header[tStartRow].substring((header[tStartRow].indexOf(stringsToFind))+stringsToFind.length());;
		double tStart = Double.parseDouble(tStartStr);

		// NOT USING TSTART YET

		// Back to reading the file
		int ncols = dataFile.getNDataCols();
		if ( ncols == 1 ) {
			//  If there is only 1 column, assume event list
			try {
				return TimeSeriesFactory.makeTimeSeries(new AsciiEventFileReader().readEventFile(filename));
			}
			catch ( AsciiEventFileException e ) {
				throw new AsciiTimeSeriesFileException("Problem reading ASCII data file", e);
			}
			catch ( EventListException e ) {
				throw new AsciiTimeSeriesFileException("Problem reading ASCII event file", e);
			}
		}
		else if ( ncols == 2 ) {
			double[] binCentres = dataFile.getDblCol(0);
			double[] counts = dataFile.getDblCol(1);
			double[] binEdges;
			try {
				binEdges = BinningUtils.getBinEdgesFromBinCentres(binCentres);
			}
			catch ( BinningException e ) {
				throw new TimeSeriesFileException("Cannot construct bin edges", e);
			}
			return TimeSeriesFactory.makeTimeSeries(binEdges, counts);
		}
		else if ( ncols == 3 ) {
			double[] binCentres = dataFile.getDblCol(0);
			double[] rates = dataFile.getDblCol(1);
			double[] errorsOnRates = dataFile.getDblCol(2);
			return TimeSeriesFactory.makeTimeSeries(binCentres, rates, errorsOnRates);
		}
		else if ( ncols == 4 ) {
			double[] binCentres = dataFile.getDblCol(0);
			double[] dtOver2 = dataFile.getDblCol(1);
			double[] rates = dataFile.getDblCol(2);
			double[] errorsOnRates = dataFile.getDblCol(3);
			return TimeSeriesFactory.makeTimeSeries(binCentres, dtOver2, rates, errorsOnRates);
		}
		else {
			throw new AsciiTimeSeriesFileException("Not an ASCII time series file. "+
			"Format can be:  1 col = arrival times;  "+ 
			"2 cols = binCentres and counts;   3 cols = binCentres, rates and errors; "+
			"4 cols (or more) = binCentres, halfBinWidths, rates, errors");
		}
    }

}
