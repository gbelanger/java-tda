package gb.tda.timeseries;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

import gb.tda.io.AsciiDataFileReader;
import gb.tda.io.AsciiDataFileFormatException;


public class RatesTimeSeriesFileReader implements ITimeSeriesFileReader {

    private static Logger logger  = Logger.getLogger(RatesTimeSeriesFileReader.class);

    public ITimeSeries read(String filename) throws TimeSeriesFileException, TimeSeriesException, BinningException, IOException  {

        AsciiDataFileReader dataFile;
        try {
            dataFile = new AsciiDataFileReader(filename);
        }
        catch (AsciiDataFileFormatException e) {
            throw new AsciiTimeSeriesFileException("Problem reading file", e);
        }

        // Read the data
        int ncols = dataFile.getNDataCols();
        if (ncols == 1) {
            throw new TimeSeriesFileFormatException(
                    "\n\tCannot create RatesTimeSeries: There is only 1 column (probably an event list). Use CountsTimeSeriesFileReader.");
        }
        else if (ncols == 2) {
            throw new TimeSeriesFileFormatException(
                    "\n\tCannot create RatesTimeSeries: There are only 2 columns. Use CountsTimeSeriesFileReader or BasicTimeSeriesFileReader.");
        }
        else if (ncols == 3) {
            double[] times = dataFile.getDblCol(0);
            double[] intensities = dataFile.getDblCol(1);
            double[] uncertainties = dataFile.getDblCol(2);
            return RatesTimeSeriesFactory.create(times, intensities, uncertainties);
        }
        else if (ncols == 4) {
            double[] times = dataFile.getDblCol(0);
            double[] halfBinWidths = dataFile.getDblCol(1);
            double[] intensities = dataFile.getDblCol(2);
            double[] uncertainties = dataFile.getDblCol(3);
            return RatesTimeSeriesFactory.create(times, halfBinWidths, intensities, uncertainties);
        }
        else {
            throw new TimeSeriesFileFormatException(
                    "\n\tNot a BasicTimeSeries file. BasicTimeSeriesFileReader accepts 2 formats:"+
                            "\n\t - 2 cols = times, intensities"+
                            "\n\t - 3 cols = times, intensities, uncertainties");
        }
    }

}
