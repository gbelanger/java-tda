//package gb.codetda.transients;

import java.util.Date;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister64;

//import gb.codetda.eventlist.EventList;
//import gb.codetda.montecarlo.WhiteNoiseGenerator;
//import gb.codetda.montecarlo.RedNoiseGenerator;
//import gb.codetda.timeseries.TimeSeries;
//import gb.codetda.timeseries.TimeSeriesMaker;

public final class EventListTransientDetectorWithGroupingTester {

	private static MersenneTwister64 engine = new MersenneTwister64(new Date());

    public static void main(String[] args) throws Exception {
	
		double mean = 1;
		double duration = 1000;
		//double[] white = WhiteNoiseGenerator.generateArrivalTimes(mean, duration);
		//EventList evlist = new EventList(white);
		//EventListTransientDetector.detectTransient(evlist);

		// double alpha = 2;
		// double[] red = RedNoiseGenerator.generateArrivalTimes(mean, duration, alpha);
		// EventList evlist = new EventList(red);
		// EventListTransientDetectorWithGrouping.detectTransient(evlist);	

		int n = 30;
		double sigma = 3;
		double rmean = 10;
		Normal normal = new Normal(rmean,sigma,engine);
		for (int i = 0; i < n; i++) {
			double alpha = 0.5;
			rmean = normal.nextDouble();
			double[] red = RedNoiseGenerator.generateArrivalTimes(rmean, duration, alpha);
			EventList evlist = new EventList(red);
			TimeSeries ts = TimeSeriesMaker.makeTimeSeries(evlist,1d);
			ts.writeCountsAsQDP("results/ts/ts_"+i+".qdp");
		}

    }

}
