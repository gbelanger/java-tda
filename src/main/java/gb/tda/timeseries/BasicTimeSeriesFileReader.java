package gb.tda.timeseries;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

import gb.tda.io.AsciiDataFileFormatException;
import gb.tda.io.AsciiDataFileReader;

public class BasicTimeSeriesFileReader implements ITimeSeriesFileReader {

    private static Logger logger  = Logger.getLogger(BasicTimeSeriesFileReader.class);

    public ITimeSeries read(String filename) throws TimeSeriesFileException, TimeSeriesException, BinningException, IOException  {
        AsciiDataFileReader dataFile;
        try {
            dataFile = new AsciiDataFileReader(filename);
        }
        catch (AsciiDataFileFormatException e) {
            throw new AsciiTimeSeriesFileException("Problem reading file", e);
        }

        // Define possible data columns
        double[] times = null;
        double[] intensities = null;
        double[] uncertainties = null;

        // Read the data
        int ncols = dataFile.getNDataCols();
        if (ncols == 1) {
            throw new TimeSeriesFileFormatException("There's only 1 data column. Not a BasicTimeSeries. Use BinnedTimeSeriesFileReader.");
        }
        else if (ncols == 2) {
            times = dataFile.getDblCol(0);
            intensities = dataFile.getDblCol(1);
            return BasicTimeSeriesFactory.create(times, intensities);
        }
        else if (ncols == 3) {
            uncertainties = dataFile.getDblCol(2);
            return BasicTimeSeriesFactory.create(times, intensities, uncertainties);
        }
        else {
            throw new TimeSeriesFileFormatException(
                    "BasicTimeSeriesFileReader accepts 2 formats:\n"+
                    " - 2 cols = times, intensities\n"+
                    " - 3 cols = times, intensities, uncertainties");
        }
    }

}
