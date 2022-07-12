package gb.tda.transients;

import cern.colt.list.DoubleArrayList;
import gb.tda.binner.Binner;
import gb.tda.eventlist.EventList;
import gb.tda.io.AsciiDataFileReader;
import gb.tda.io.AsciiDataFileWriter;
import gb.tda.likelihood.InverseExponentialLikelihood;
import gb.tda.timeseries.TimeSeries;
import gb.tda.timeseries.TimeSeriesMaker;
import gb.tda.tools.BasicStats;
import gb.tda.tools.DataUtils;
import gb.tda.tools.MinMax;
import org.apache.log4j.Logger;

public final class TimeSeriesTransientDetector {

    private static Logger logger  = Logger.getLogger(TimeSeriesTransientDetector.class);    

    public static void detectTransient(TimeSeries[] timeseriesArray) throws Exception {

		// Define variables
		DoubleArrayList ratesList = new DoubleArrayList();
		DoubleArrayList likelihoodList = new DoubleArrayList();
		DoubleArrayList avgList = new DoubleArrayList();
		DoubleArrayList varList = new DoubleArrayList();
		DoubleArrayList timesList = new DoubleArrayList();
		DoubleArrayList thresholdList = new DoubleArrayList();
		InverseExponentialLikelihood likelihood = new InverseExponentialLikelihood();
		double time = 0;
		double rate = 0;
		double sum = 0;
		double avg = 0;
		int i = 0;
		int k = 0;
		int n = 0;
		int count = 0;
		int timeseriesCounter = 0;
		double detectionLikelihood = 0;
		
		for (TimeSeries timeseries : timeseriesArray) {

		    timeseriesCounter++;
		    double[] rates = timeseries.getRates();
		    double[] errors = timeseries.getErrorsOnRates();
		    double mean = timeseries.meanRate();
		    double[] binCentres = timeseries.getBinCentres();
		    
		    // // Make histogram of interarrival times
		    // AsciiDataFileWriter out1 = new AsciiDataFileWriter("histoDeltaTs_evlist"+evlistCounter+".qdp");
		    // out1.writeHisto(Binner.makePDF(interArrivalTimes, 0, 5*mean, 30), "Delta T (s)");

		    // Define the thresholdTimescale
		    // make periodogram
		    // fit periodogram
		    
		    // compute grouping timescale
		    double groupingTimescale = 100;
		    int nEventsPerThresholdGroup = Math.round(mean*groupingTimescale);
		    
		    //  Sum the first 5 events to estimate the avg
		    while (i < 5) {
				timesList.add(binCentres[i]);
				rate = rates[i];
				n++;
				sum += rate;
				i++;
		    }
		    avg = sum/n;
		    avgList.add(avg);
		    ratesList.add(avg);

		    while (i < rates.length) {

				timesList.add(binCentres[i]);
				rate = rates[i];
				ratesList.add(rate);
				if (i == 0) avg = rate;

				// Define threshold from avg, and offset by 3 sigma (0.5 per sigma in Normal log-likelihood)
				double threshold = likelihood.getLogLikelihood(avg, avg) - 1.5;
				thresholdList.add(threshold);
				
				double l = likelihood.getLogLikelihood(mean, rate);
				if (l >= threshold) {
				    likelihoodList.add(l);
				    k=0;
				    n++;
				    sum += rate;
				    avg = sum/n;
				    avgList.add(avg);
				    detectionLikelihood = 0;
				    i++;
				}
				else {
				    detectionLikelihood += l;
				    avgList.add(avg);
				    k++;
				    if (k >= 8) {
					count++;
					likelihoodList.add(detectionLikelihood);
				    }
				    i++;
				}

		    }
		    timesList.trimToSize();
		    ratesList.trimToSize();
		    avgList.trimToSize();
		    likelihoodList.trimToSize();
		    thresholdList.trimToSize();

		    double[] times = timesList.elements();
		    double[] rates = ratesList.elements();
		    double[] avgs = avgList.elements();
		    double[] likelihoods = likelihoodList.elements();
		    //double minLogL = MinMax.getNonZeroMin(likelihoods);
		    double[] thresholds = thresholdList.elements();
		    
		    AsciiDataFileWriter out2 = new AsciiDataFileWriter("histoInstantRates.qdp");
		    out2.writeHisto(Binner.makePDF(rates, 0, mean*10, 30), "Rates (cps)");

		    AsciiDataFileWriter out3 = new AsciiDataFileWriter("thresholdsAndLikelihoods.qdp");
		    String xLabel = "Time (s)";
		    String[] header = new String[] {
				"DEV /XS",
				"READ 1",
				"LAB T", "LAB F",
				"TIME OFF",
				"LINE OFF",
				"MA 1 ON",
				"MA SIZE 2",
				//"GRID Y 5",
				//"LINE ON 3",
				"LW 4",
				"CS 1.4",
				"LAB X "+xLabel,
				"LAB Y3 Thresholds",
				"LAB Y2 Log-Likelihood",
				"VIEW 0.1 0.1 0.9 0.9",
				//"R Y2 0 86",
				//"R Y3 -55 -1",
				"PLOT VERT",
				    "!"
		    };
		    out3.writeData(header, times, likelihoods, thresholds);

		}
    }

    public static void detectTransient(String filename) throws Exception {
	detectTransient(new TimeSeries(filename));
    }        

    public static void detectTransient(String[] filenames) throws Exception {
	TimeSeries[] timeseries = new TimeSeries[filenames.length];
	int i = 0;
	for (String filename : filenames) {
	    timeseries[i] = new TimeSeries(filename);
	    i++;
	}
	detectTransient(timeseries);
    }

    public static void detectTransient(TimeSeries timeseries) throws Exception {
	detectTransient(new TimeSeries[] {timeseries});
    }

    
}
