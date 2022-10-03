package gb.tda.transients;

import cern.colt.list.DoubleArrayList;
import gb.tda.binner.Binner;
import gb.tda.io.AsciiDataFileWriter;
import gb.tda.likelihood.InverseExponentialLikelihood;
import gb.tda.timeseries.ITimeSeries;
import gb.tda.timeseries.BasicTimeSeries;
import gb.tda.timeseries.BasicTimeSeriesFactory;
//import gb.tda.tools.MinMax;
import org.apache.log4j.Logger;

public final class TimeSeriesTransientDetector {

	private static Logger logger  = Logger.getLogger(TimeSeriesTransientDetector.class);

	public static void detectTransient(ITimeSeries[] timeseriesArray) throws Exception {
		// Define variables
		DoubleArrayList intensitiesList = new DoubleArrayList();
		DoubleArrayList likelihoodList = new DoubleArrayList();
		DoubleArrayList avgList = new DoubleArrayList();
		DoubleArrayList varList = new DoubleArrayList();
		DoubleArrayList timesList = new DoubleArrayList();
		DoubleArrayList thresholdList = new DoubleArrayList();
		InverseExponentialLikelihood likelihood = new InverseExponentialLikelihood();
		double time = 0;
		double intensity = 0;
		double sum = 0;
		double avg = 0;
		int i = 0;
		int k = 0;
		int n = 0;
		int count = 0;
		int timeseriesCounter = 0;
		double detectionLikelihood = 0;

		for (ITimeSeries timeseries : timeseriesArray) {

			timeseriesCounter++;
			double[] intensities = timeseries.getIntensities();
			// Could eventually use the uncertainties for importance weighting
			//double[] uncertainties = timeseries.getUncertainties();
			double[] times = timeseries.getTimes();
			double mean = timeseries.meanIntensity();

			// // Make histogram of interarrival times
			// AsciiDataFileWriter out1 = new AsciiDataFileWriter("histoDeltaTs_evlist"+evlistCounter+".qdp");
			// out1.writeHisto(Binner.makePDF(interArrivalTimes, 0, 5*mean, 30), "Delta T (s)");

			// Define the thresholdTimescale
			// make periodogram
			// fit periodogram

			// compute grouping timescale
			double groupingTimescale = 100;
			int nEventsPerThresholdGroup = (int) Math.round(mean * groupingTimescale);

			//  Sum the first 5 events to estimate the avg
			while (i < 5) {
				timesList.add(times[i]);
				intensity = intensities[i];
				n++;
				sum += intensity;
				i++;
			}
			avg = sum/n;
			avgList.add(avg);
			intensitiesList.add(avg);

			while (i < intensities.length) {

				timesList.add(times[i]);
				intensity = intensities[i];
				intensitiesList.add(intensity);
				if (i == 0) avg = intensity;

				// Define threshold from avg, and offset by 3 sigma (0.5 per sigma in Normal log-likelihood)
				double threshold = likelihood.getLogLikelihood(avg, avg) - 1.5;
				thresholdList.add(threshold);

				double l = likelihood.getLogLikelihood(mean, intensity);
				if (l >= threshold) {
					likelihoodList.add(l);
					k=0;
					n++;
					sum += intensity;
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
			intensitiesList.trimToSize();
			avgList.trimToSize();
			likelihoodList.trimToSize();
			thresholdList.trimToSize();

			times = timesList.elements();
			intensities = intensitiesList.elements();
			double[] avgs = avgList.elements();
			double[] likelihoods = likelihoodList.elements();
			//double minLogL = MinMax.getNonZeroMin(likelihoods);
			double[] thresholds = thresholdList.elements();

			AsciiDataFileWriter out2 = new AsciiDataFileWriter("histoInstantIntensities.qdp");
			out2.writeHisto(Binner.makePDF(intensities, 0, mean*10, 30), "Intensities (cps)");

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
		detectTransient(BasicTimeSeriesFactory.create(filename));
	}

	public static void detectTransient(String[] filenames) throws Exception {
		ITimeSeries[] timeseries = new ITimeSeries[filenames.length];
		int i = 0;
		for (String filename : filenames) {
			timeseries[i] = BasicTimeSeriesFactory.create(filename);
			i++;
		}
		detectTransient(timeseries);
	}

	public static void detectTransient(ITimeSeries timeseries) throws Exception {
		detectTransient(new ITimeSeries[] {timeseries});
	}

}
