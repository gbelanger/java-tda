package gb.tda.timeseries;

public class BasicTimeSeriesFileReaderTest {

    public static void main(String[] args) throws Exception {
        // There is only one method: read(filename)
        // There are only two accepted formats: 2 or 3 columns

        BasicTimeSeriesFileReader reader = new BasicTimeSeriesFileReader();

        // Test 1 col : throws TimeSeriesFileFormatException
        String filename = "times.dat";
//        BasicTimeSeries ts1 = (BasicTimeSeries) reader.read(filename);

        // Test 2 cols
        filename = "basic-time-series1.qdp";
        BasicTimeSeries ts2 = (BasicTimeSeries) reader.read(filename);

        // test 3 cols
        filename = "basic-time-series3.qdp";
        BasicTimeSeries ts3 = (BasicTimeSeries) reader.read(filename);

        // test 4 cols : throws TimeSeriesFileFormatException
//        filename = "binned-time-series3.qdp";
//        BasicTimeSeries ts4 = (BasicTimeSeries) reader.read(filename);

    }

}