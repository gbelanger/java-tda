package gb.tda.timeseries;

import java.io.IOException;

public interface ITimeSeriesFileReader {

    ITimeSeries readTimeSeriesFile(String filename) throws TimeSeriesFileException, TimeSeriesException, BinningException, IOException ;

}
