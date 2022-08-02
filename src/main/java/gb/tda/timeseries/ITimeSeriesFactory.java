package gb.tda.timeseries;

public interface ITimeSeriesFactory {

    // Create a time series from an input file
    public static ITimeSeries create(String filename) throws TimeSeriesFileException {
        return TimeSeriesFileReader.read(filename);
    }

}