package gb.tda.timeseries;

import java.io.IOException;
import org.apache.log4j.Logger;
import gb.tda.eventlist.AsciiEventFileReader;
import gb.tda.eventlist.AsciiEventFileException;
import gb.tda.eventlist.EventListException;
import gb.tda.io.AsciiDataFileReader;
import gb.tda.io.AsciiDataFileFormatException;

/**
 * Class <code>AsciiTimeSeriesFileReader</code> reads a times series file in ASCII format.
 *
 * If it contains only 1 column, then it is treated as an event file
 . * The TimeSeries will have adjacent bins of equal widths.
 *
 * If it contains 2 columns, then 
 * the first column are the binCentres and the second are the intensities in each bin.
 * The TimeSeries will have adjacent bins with widths determined from the binCentres.
 *
 * If it contains 3 columns, then 
 * the first column are the binCentres, the second are the intensities, and the third are the errors.
 * The TimeSeries will have adjacent bins with widths determined from the binCentres.
 *
 * If it contains 4 columns (or more), then 
 * the first column are the binCentres, the second are the halfBinWidths, the third are the intensities, the third are the errors.
 * The TimeSeries will have bins defined by the binCentres and corresponding widths.
 *
 * @author G. Belanger
 *
 */
public class AsciiAstroTimeSeriesFileReader implements ITimeSeriesFileReader {

    private static final Logger logger  = Logger.getLogger(AsciiAstroTimeSeriesFileReader.class);

	public ITimeSeries read(String filename) throws TimeSeriesFileException, TimeSeriesException, BinningException, IOException  {
		AsciiDataFileReader dataFile;
		try {
			dataFile = new AsciiDataFileReader(filename);
		}
		catch (AsciiDataFileFormatException e) {
			throw new AsciiTimeSeriesFileException("Problem reading ASCII data file", e);
		}

		// Read the data
		int ncols = dataFile.getNDataCols();
		if (ncols == 1) {
			//  If there is only 1 column, assume event list
			try {
				return BinnedTimeSeriesFactory.create(new AsciiEventFileReader().read(filename));
			}
			catch (AsciiEventFileException e) {
				throw new AsciiTimeSeriesFileException("Problem reading ASCII data file", e);
			}
			catch (EventListException e) {
				throw new AsciiTimeSeriesFileException("Problem reading ASCII event file", e);
			}
		}
		else {
			// Two or more columns
			double[] times = dataFile.getDblCol(0);
			double[] intensities = dataFile.getDblCol(1);
			double[] binEdges;
			if (ncols == 2) {
				return BasicTimeSeriesFactory.create(times, intensities);
			}
			else if (ncols == 3) {
				double[] uncertainties = dataFile.getDblCol(2);
				return BasicTimeSeriesFactory.create(times, intensities, uncertainties);
			}
			else if (ncols == 4) {
				double[] binCentres = dataFile.getDblCol(0);
				double[] dtOver2 = dataFile.getDblCol(1);
				try {
					binEdges = BinningUtils.getBinEdgesFromBinCentresAndHalfWidths(binCentres, dtOver2);
				} catch (BinningException e) {
					throw new TimeSeriesFileException("Cannot construct bin edges", e);
				}
				intensities = dataFile.getDblCol(2);
				double[] uncertainties = dataFile.getDblCol(3);
				return BinnedTimeSeriesFactory.create(binEdges, intensities, uncertainties);
			}
			else {
				throw new AsciiTimeSeriesFileException("Not an ASCII time series file. " +
						"Format can be:  1 col = arrival times;  " +
						"2 cols = binCentres and intensities;   3 cols = binCentres, intensities and errors; " +
						"4 cols (or more) = binCentres, halfBinWidths, intensities, errors");
			}
		}
    }

}
