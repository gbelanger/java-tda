package gb.tda.timeseries;

import java.io.IOException;

public interface ITimeSeriesFileReader {
    ITimeSeries read(String filename) throws TimeSeriesFileException, TimeSeriesException, BinningException, IOException;

}
