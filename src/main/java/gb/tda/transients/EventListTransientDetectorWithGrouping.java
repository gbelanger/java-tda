package gb.tda.transients;

import java.util.concurrent.TimeUnit;    
import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.apache.log4j.Logger;
import gb.tda.binner.Binner;
import gb.tda.eventlist.EventList;
import gb.tda.io.AsciiDataFileReader;
import gb.tda.io.AsciiDataFileWriter;
import gb.tda.likelihood.InverseExponentialLikelihood;
import gb.tda.timeseries.TimeSeries;
import gb.tda.timeseries.TimeSeriesMaker;
import gb.tda.periodogram.FFTPeriodogram;
import gb.tda.periodogram.PeriodogramMaker;
import gb.tda.tools.BasicStats;
import gb.tda.tools.DataUtils;
import gb.tda.tools.MinMax;
import gb.tda.tools.LikelihoodFitter;

public final class EventListTransientDetectorWithGrouping {

    private static Logger logger  = Logger.getLogger(EventListTransientDetector.class);    
    private static double startTime = System.currentTimeMillis();
    
    public static void detectTransient(EventList[] evlist_array) throws Exception {
	
		// Define variables
		DoubleArrayList ratesList = new DoubleArrayList();
		DoubleArrayList backgroundRatesList = new DoubleArrayList();
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
		int plotIdx = 0;
		boolean makeHistos = true;
		boolean makePlots = false;
		boolean makePlotSequence = false;
		int nDeep = 0;
		int nDetections = 0;
		int evlistCounter = 0;
		double detectionLikelihood = 0;
		
		for (EventList evlist : evlist_array) {

		    evlistCounter++;
		    double[] arrivalTimes = evlist.getArrivalTimes();
		    double[] interArrivalTimes = evlist.getInterArrivalTimes();
		    double mean = evlist.meanRate();
		    
		    // Define the groupging timescale
		    double groupTimescale = computeGroupingTimescale(evlist);
		    int groupSize = (int) Math.round(mean*groupTimescale);
		    TimeSeries ts = TimeSeriesMaker.makeTimeSeries(evlist,groupTimescale/2);

		    if (makePlots) {
		    	// Time series binned at grouping time scale
		    	ts.writeCountsAsQDP("results/ts.qdp");
		    	// Histogram of interarrival times
			    AsciiDataFileWriter out1 = new AsciiDataFileWriter("results/histoDeltaTs_evlist"+evlistCounter+".qdp");
			    out1.writeHisto(Binner.makePDF(interArrivalTimes, 0, 6*mean, 30), "Delta T (s)");
		    }

		    while (i < interArrivalTimes.length) {

				time += interArrivalTimes[i];
				timesList.add(time);
				rate = 1d/interArrivalTimes[i];
				ratesList.add(rate);
				if (i == 0) avg=rate;

				// Define threshold from avg, and offset by 3 to 5 sigma (0.5 per sigma in Normal log-likelihood)
				double threshold = likelihood.getLogLikelihood(avg,avg) - 1.5;
				thresholdList.add(threshold);
				
				double l = likelihood.getLogLikelihood(avg, rate);
				if (l >= threshold) {
				    backgroundRatesList.add(rate);
				    likelihoodList.add(l);
				    k=0;
				    // Compute average rate
				    plotIdx++;
				    avg = computeGroupAverage(backgroundRatesList,groupSize,plotIdx,makeHistos);
				    // Reset detectionLikelihood
				    detectionLikelihood = 0;
				}
				else {
				    plotIdx++;
				    double aaaa = computeGroupAverage(backgroundRatesList,groupSize,plotIdx,makeHistos);
				    detectionLikelihood += l;
				    k++;
				    //  Require 8 subsequent sub-threshold likelihoods to claim detection
				    if (k >= 8) {
						nDetections++;
						likelihoodList.add(l);
						logger.info("DETECTION!!!");
				    }
				    else {
				    	likelihoodList.add(-5);
				    }
				}
				i++;
				avgList.add(avg);
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
		    
		    if (makePlots) {
		    	// Histogram of instant rates
			    AsciiDataFileWriter out2 = new AsciiDataFileWriter("results/histoInstantRates.qdp");
			    out2.writeHisto(Binner.makePDF(rates, 0, mean*10, 30), "Rates (cps)");
			    // Time series of likelihoods
			    AsciiDataFileWriter out3 = new AsciiDataFileWriter("results/thresholdsAndLikelihoods.qdp");
			    String xLabel = "Time (s)";
			    String plotname = "thresholdsAndLikelihoods.ps";
			    String[] header = new String[] {
					"DEV /XS",
					"READ 1",
					"LAB T", "LAB F",
					"TIME OFF",
					"LINE OFF",
					"MA 1 ON",
					"MA SIZE 2",
					"GRID Y 5",
					"LW 4",
					"CS 1.3",
					"LAB X "+xLabel,
					"LAB Y3 Thresholds",
					"LAB Y2 Log-Likelihood",
					"VIEW 0.1 0.2 0.9 0.8",
					"PLOT OVERLAY",
					"R Y -3.9 0.1",
					"R X -25 1025",
					"HARD "+plotname+"/cps",
					"!"
			    };
			    out3.writeData(header, times, likelihoods, thresholds);
			}

		    //  This is to make a sequence of plots to make a video
		    if (makePlotSequence) {
			    DoubleArrayList likeList = new DoubleArrayList();
			    DoubleArrayList tList = new DoubleArrayList();	
			    DoubleArrayList threshList = new DoubleArrayList();	    
			    for (int m = 0; m < likelihoods.length; m++) {
			    	likeList.add(likelihoods[m]);
			    	threshList.add(thresholds[m]);
			    	tList.add(times[m]);
			    	likeList.trimToSize();
			    	threshList.trimToSize();
			    	tList.trimToSize();
			    	AsciiDataFileWriter out = new AsciiDataFileWriter("results/likelihoods/likelihoods_"+(m+1)+".qdp");
			    	String plotname = "likelihoods_"+(m+1)+".gif";
			    	String xLabel = "Time (s)";
				    String[] header = new String[] {
						"DEV /XS","READ 1","LAB T", "LAB F","TIME OFF","LINE OFF","MA 1 ON","MA SIZE 2",
						"GRID Y 5","LW 4","CS 1.3","LAB X "+xLabel,"LAB Y3 Thresholds","LAB Y2 Log-Likelihood",
						"VIEW 0.1 0.2 0.9 0.8","PLOT OVERLAY","R Y -3.9 0.1","R X -25 1025",
						"HARD "+plotname+"/gif","!"
				    };
				    out.writeData(header, tList.elements(), likeList.elements(), threshList.elements());
				    TimeUnit.MILLISECONDS.sleep(1);
			    }
			}

		}
    }

    private static double computeGroupingTimescale(EventList evlist) throws Exception {
		double mean = evlist.meanRate();
		double bintime = 5/mean;
		TimeSeries ts = TimeSeriesMaker.makeTimeSeries(evlist, bintime);
		FFTPeriodogram psd = PeriodogramMaker.makePlainFFTPeriodogram(ts);
		psd.writeAsQDP("results/psd.qdp");
		double[] fitResults = LikelihoodFitter.fitPowerLawWithFloor(psd.getFreqs(),psd.getPowers());
		double norm = fitResults[0];
		double alpha = fitResults[1];
		double floor = fitResults[2];
		return 0.5*Math.pow(floor/norm, 1./alpha);
    }
    
    private static double computeGroupAverage(DoubleArrayList list, int groupSize, int plotIdx, boolean makeHisto) throws Exception {
		list.trimToSize();
		double average = 0;
		if (list.size() >= groupSize) {
		    int from = list.size() - groupSize;
		    int to = list.size() - 1;
		    DoubleArrayList groupList = (DoubleArrayList) list.partFromTo(from, to);
		    average = Descriptive.mean(groupList);
		}
		else {
		    average = Descriptive.mean(list);
		}
		if (makeHisto) {
			//double timeTag = System.currentTimeMillis() - startTime;
		    //AsciiDataFileWriter out = new AsciiDataFileWriter("histos/histo_"+timeTag+".qdp");
		    AsciiDataFileWriter out = new AsciiDataFileWriter("results/histos/histo_"+plotIdx+".qdp");
		    double ymin = 0;
		    double ymax = 170;
		    out.writeHisto(Binner.makeHisto(list.elements(),0,10,30),ymin,ymax,"Instant Rate");
		    TimeUnit.MILLISECONDS.sleep(5);
		}
		return average;
    }

    
    public static void detectTransient(String filename) throws Exception {
		detectTransient(new EventList(filename));
    }        

    public static void detectTransient(String[] filenames) throws Exception {
		EventList[] evlist_array = new EventList[filenames.length];
		int i = 0;
		for (String filename : filenames) {
		    evlist_array[i] = new EventList(filename);
		    i++;
		}
		detectTransient(evlist_array);
    }

    public static void detectTransient(EventList evlist) throws Exception {
		detectTransient(new EventList[] {evlist});
    }

    
}
