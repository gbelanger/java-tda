package gb.tda.montecarlo;

import gb.tda.eventlist.AstroEventList;
import gb.tda.eventlist.EventListSelector;
import gb.tda.periodogram.AggregatePeriodogram;
import gb.tda.periodogram.AveragePeriodogram;
import gb.tda.periodogram.FFTPeriodogram;
import gb.tda.periodogram.PeriodogramMaker;
import org.apache.log4j.Logger;


public class TestComponentDensity {

    static Logger logger = Logger.getLogger(TestComponentDensity.class);

    private static double mean = 2;
    private static double duration = 1e3;
    private static double alpha = 2.5;
    private static int nSpecs = 100;
    private static int sampling = 1;
    private static int[] factors = new int[] {1, 8, 32, 64};

    private static void readArgs(String[] args) {

	if ( args.length == 5 ) {
	    mean = (Double.valueOf(args[0])).doubleValue();
	    duration = (Double.valueOf(args[1])).doubleValue();
	    alpha = (Double.valueOf(args[2])).doubleValue();
	    nSpecs = (Integer.valueOf(args[3])).intValue();
	    sampling = (Integer.valueOf(args[4])).intValue();
	}
	else {
	    logger.info("Using default parameter values");
	    logger.info("Usage: java gb.codetda.montecarlo.TestComponentDensity mean duration alpha nSpecs sampling");
	}
    }

    private static AveragePeriodogram buildAvgPsd(double tObs, double tTot, int nFreqsPerIFS) throws Exception {

	String windowName = "Bartlett-Hann";
	String normName = "leahy";
	AggregatePeriodogram avgPsd = new AggregatePeriodogram();
	for ( int i=0; i < nSpecs; i++ ) {
	    double[] arrivalTimes = RedNoiseGenerator.generateArrivalTimes(mean, tTot, alpha, nFreqsPerIFS);
	    AstroEventList evlist = new AstroEventList(arrivalTimes);
	    evlist = new AstroEventList(EventListSelector.getArrivalTimesRandomSegment(evlist, tObs));
	    FFTPeriodogram psd = PeriodogramMaker.makeOversampledWindowedFFTPeriodogram(evlist, windowName, normName, sampling);
	    avgPsd.add(psd);
	    logger.info("Added periodogram "+(i+1)+" (of "+nSpecs+") to AggregatePeriodogram");
	}
	return avgPsd.getPeriodogram();
    }

    public static void main(String[] args) throws Exception {

	readArgs(args);
	double tObs = duration;
	for ( int i=0; i < factors.length; i++ ) {
	    double tTot = factors[i]*tObs;
	    for ( int j=0; j < factors.length; j++ ) {
		int nFreqsPerIFS = factors[j];
		AveragePeriodogram avgPsd = buildAvgPsd(tObs, tTot, nFreqsPerIFS);
		avgPsd.writeAsQDP("avgPsd-tObs_"+tObs+"-tTot_"+tTot+"-nPerIFS_"+nFreqsPerIFS+".qdp");
	    }
	}	
    }	
}
