package gb.tda.timeseries;

import java.text.DecimalFormat;

public interface ITimeSeriesFileWriter {

    int bufferSize = 256000;
    DecimalFormat noDigits = new DecimalFormat("0");
    DecimalFormat oneDigit = new DecimalFormat("0.0");
    DecimalFormat twoDigits = new DecimalFormat("0.00");
    DecimalFormat threeDigits = new DecimalFormat("0.000");
    DecimalFormat stats = new DecimalFormat("0.00E00");
    DecimalFormat number = new DecimalFormat("0.000");
    DecimalFormat timeFormat = new DecimalFormat("0.000E0");

    static void writeToFile(ITimeSeries ts, String filename) throws Exception {}
    static void writeToFile(ITimeSeries ts, double[] function, String filename) throws Exception {}
    static void writeToFileWithSampling(ITimeSeries ts, String filename) throws Exception {}

    static void writeToFile(IBinnedTimeSeries ts, String filename) throws Exception {}
    static void writeToFile(IBinnedTimeSeries ts, double[] function, String filename) throws Exception {}
    static void writeToFileWithSampling(IBinnedTimeSeries ts, String filename) throws Exception {}

}
