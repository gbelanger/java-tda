package gb.tda.timeseries;

import gb.tda.binner.BinningException;
import gb.tda.binner.BinningUtils;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 * Class <code>AresTimeSeriesFileReader</code> reads a times series file in ASCII format exported from the ARES system.
 *
 * @author <a href="mailto: guilaume.belanger@esa.int">Guillaume Belanger</a>
 *
 */
class AresTimeSeriesFileReader implements ITimeSeriesFileReader {

    private static Logger logger  = Logger.getLogger(AresTimeSeriesFileReader.class);
    
    public ITimeSeries readTimeSeriesFile(String filename) throws TimeSeriesFileException, TimeSeriesException, BinningException, IOException {

        // Read input data
        Scanner scanner = new Scanner(filename);
        String line = scanner.nextLine();
        StringTokenizer tokenizer = new StringTokenizer(line, "	");
        if (tokenizer.countTokens() != 2) {
            throw new TimeSeriesFileFormatException("ARES time series input file should contain two columns: Date and Measurement.");
        }
        tokenizer.nextToken(); // skip the word "Date"
        String parameter = (new StringTokenizer(tokenizer.nextToken())).nextToken();
        logger.info("Reading time series data for parameter "+parameter);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        List<LocalDateTime> dateTimeList = new ArrayList<LocalDateTime>();
        List<Double> measurementsList = new ArrayList<Double>();
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            tokenizer = new StringTokenizer(line, "	");
            dateTimeList.add(LocalDateTime.parse(tokenizer.nextToken(),formatter));
            measurementsList.add(Double.valueOf(tokenizer.nextToken()));
        }
        // Redefine time as seconds elapsed from the first measurement
        double[] timeInSeconds = new double[dateTimeList.size()];
        double[] measurements = new double[dateTimeList.size()];
        int i = 0;
        LocalDateTime start = dateTimeList.get(0);
        for (LocalDateTime dateTime : dateTimeList) {
            Duration diff = Duration.between(dateTimeList.get(i), start);
            timeInSeconds[i] = diff.getSeconds();
            measurements[i] = measurementsList.get(i).doubleValue();
        }
        // Make time series
        return TimeSeriesFactory.makeTimeSeries(timeInSeconds,measurements);
    }
    
}
