//package gb.codetda.transients;

//import gb.codetda.eventlist.AstroEventList;
//import gb.codetda.montecarlo.WhiteNoiseGenerator;
//import gb.codetda.montecarlo.RedNoiseGenerator;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister64;

public final class TimeSeriesTransientDetectorTester {

    public static void main(String[] args) throws Exception {

	// Construct the bin edges according lsst cadence
	double dwell = 14;
	double deadtime = 1;
	double meanSlew = 5;
	double stddevSlew = 1;
	MersenneTwister64 engine = new MersenneTwister64();
	Normal normal = new Normal(meanSlew, stddevSlew, engine);
	int nVisits = 1000;
	double minDuration = 20000;
	double[] binEdges = new double[nVisits*3];
	int i = 0;
	while (binEdges[3*i+2] < minDuration) {
	    double slew = normal.nextDoule();
	    binEdges[3*i] = i*(dwell+deadtime+slew);
	    binEdges[3*i+1] = binEdges[3*i] + dwell;
	    binEdges[3*i+2] = binEdges[3*i+1] + deadtime + slew;
	}
	double duration = binEdges[binEdges.length - 1];

	// Define the mean and spectral index
	double meanRate = 2;
	double alpha = 2;
	
	// Generate the red noise rates
	double[] rates = TimmerKonig.getRates(meanRates, duration, alpha);
	
    }

}
