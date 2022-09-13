package gb.tda.timeseries;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

import gb.tda.eventlist.AsciiEventFileException;
import gb.tda.eventlist.AsciiEventFileReader;
//import gb.tda.eventlist.AstroEventList;
import gb.tda.eventlist.EventListException;
import gb.tda.io.AsciiDataFileFormatException;
import gb.tda.io.AsciiDataFileReader;

public class BinnedTimeSeriesFileReader implements ITimeSeriesFileReader {

    private static Logger logger  = Logger.getLogger(BinnedTimeSeriesFileReader.class);

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
            //  If there is only 1 column, assume event list
            try {
                return BinnedTimeSeriesFactory.create(new AsciiEventFileReader().read(filename));
            }
            catch (AsciiEventFileException e) {
                throw new AsciiTimeSeriesFileException("Problem reading data file", e);
            }
            catch (EventListException e) {
                throw new AsciiTimeSeriesFileException("Problem reading event file", e);
            }
        }
        else {
            // Two or more columns
            double[] binCentres = dataFile.getDblCol(0);
            double[] binEdges = BinningUtils.getBinEdgesFromBinCentres(binCentres);
            if (ncols == 2) {
                double[] intensities = dataFile.getDblCol(1);
                return BinnedTimeSeriesFactory.create(binEdges, intensities);
            }
            else if (ncols == 3) {
                double[] intensities = dataFile.getDblCol(1);
                double[] uncertainties = dataFile.getDblCol(2);
                return BinnedTimeSeriesFactory.create(binEdges, intensities, uncertainties);
            }
            else if (ncols == 4) {
                double[] halfBinWidths = dataFile.getDblCol(1);
                double[] intensities = dataFile.getDblCol(2);
                double[] uncertainties = dataFile.getDblCol(3);
                try {
                    binEdges = BinningUtils.getBinEdgesFromBinCentresAndHalfWidths(binCentres, halfBinWidths);
                } catch (BinningException e) {
                    throw new TimeSeriesFileException("Cannot construct bin edges", e);
                }
                return BinnedTimeSeriesFactory.create(binEdges, intensities, uncertainties);
            }
            else {
                throw new AsciiTimeSeriesFileException(
                        "\n\tNot a BinnedTimeSeries file. BinnedTimeSeriesFileReader accepts 4 formats: " +
                                "\n\t - 1 col = event times;" +
                                "\n\t - 2 cols = binCentres, intensities;" +
                                "\n\t - 3 cols = binCentres, intensities, errors;" +
                                "\n\t - 4 cols = binCentres, halfBinWidths, intensities, errors.");
            }
        }
    }

}
