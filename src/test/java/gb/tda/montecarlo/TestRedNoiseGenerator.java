package gb.tda.montecarlo;

import gb.tda.periodogram.FFTPeriodogram;
import gb.tda.timeseries.TimeSeriesMaker;
import gb.tda.timeseries.TimeSeries;
import gb.tda.eventlist.AstroEventList;
import gb.tda.periodogram.PeriodogramMaker;
import gb.tda.io.AsciiDataFileWriter;
import gb.tda.binner.Binner;
import gb.tda.binner.BinningUtils;

public class TestRedNoiseGenerator {

    public static void main(String[] args) throws Exception {

	double mean = 10;
	double duration = Double.valueOf(args[0]);
	double alpha = 2;
	double bintime = 1d;
	
	
	boolean generate = true;
	if (generate) {
	    double[] times = RedNoiseGenerator.generateArrivalTimes(mean, duration, alpha);
	    AstroEventList evlist = new AstroEventList(times);
	    TimeSeries ts = TimeSeriesMaker.makeTimeSeries(evlist, bintime);
	    FFTPeriodogram fft = PeriodogramMaker.makePlainFFTPeriodogram(ts);
	    evlist.writeTimesAsQDP("evlist.qdp");
	    ts.writeRatesAsQDP("ts.qdp");
	    fft.writeAsQDP("fft.qdp");
	}
	
	boolean compare = true;
	if (compare) {
	    AstroEventList evlist_cdf = new AstroEventList("tk_times_cdf.qdp");
	    AstroEventList evlist_rates = new AstroEventList("tk_times_rates.qdp");
	    TimeSeries ts_cdf = TimeSeriesMaker.makeTimeSeries(evlist_cdf, bintime);
	    TimeSeries ts_rates = TimeSeriesMaker.makeTimeSeries(evlist_rates, bintime);
	    ts_cdf.writeCountsAsQDP("ts_cdf.qdp");
	    ts_rates.writeCountsAsQDP("ts_rates.qdp");
	}
    }
}
