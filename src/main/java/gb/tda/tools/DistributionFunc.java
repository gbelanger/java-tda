package gb.tda.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import hep.aida.ref.histogram.FixedAxis;
import hep.aida.ref.histogram.Histogram1D;
import cern.jet.random.Exponential;
import cern.colt.list.DoubleArrayList;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister64;
import org.apache.log4j.Logger;
import gb.tda.utils.BasicStats;
import gb.tda.utils.MinMax;
import gb.tda.utils.DataUtils;
	
/**
 *
 * @author <a href="mailto: guilaume.belanger@esa.int">Guillaume Belanger</a>
 * @version 2017 April (last modified)
 *
 **/

public final class DistributionFunc {

    static Logger logger = Logger.getLogger(DistributionFunc.class);

    public static Histogram1D getPDFHisto(final Histogram1D histo, final String dataType) {
	//   Get axis properties from original histo  
	FixedAxis axis = (FixedAxis) histo.axis();
	int nbins = axis.bins();
	double[] binWidths = new double[nbins+2];
	double[] binHeights = new double[nbins+2];
	for (int i = 0; i < nbins+1; i++) {
	    if (dataType.equals("counts"))  binWidths[i] = axis.binWidth(i);
	    else binWidths[i] = 1.0;
	    binHeights[i] = histo.binHeight(i);
	}
		
	//   Check number of "out of range" entries  
	int extraEntries = histo.extraEntries();
	if (extraEntries != 0) {
	    System.out.println("Log  : There are "+extraEntries+" extra entries");
	    System.out.println("Warn : Out of range entries will be ignored");
	}
		
	//   Calculate mean occurence frequency for each bin  
	double mean = 0;
	double maxDt = 30*BasicStats.getMean(binWidths)/MinMax.getNonZeroMin(binHeights);
	double[] means = new double[nbins+2];
	for (int i=0; i < nbins+1; i++) {
	    mean = binWidths[i]/binHeights[i];
	    if (mean > 0.0 && mean <= maxDt)
		means[i]  = mean;
	}
	double min = MinMax.getMin(means);
	double max = MinMax.getMax(means);
	double pdfBinWidth = (max - min)/nbins;
	double sum = BasicStats.getSum(means);
	System.out.println(min+"	"+max);
		
	//   Construct pdf histo
	FixedAxis pdfAxis = new FixedAxis(nbins, min, max);
	double[] pdf =  new double[nbins+2];
	double[] errors = new double[nbins+2];
	double[] binCentres = new double[nbins+2];
	for (int i=0; i < nbins+1; i++) {
	    pdf[i] = means[i]/sum;
	    errors[i] = 0;
	    binCentres[i] = pdfAxis.binCenter(i);
	}
	String title = "Probability Distribution Function";
	Histogram1D pdfHisto = new Histogram1D("PDF", title, pdfAxis);
	pdfHisto.setContents(pdf, errors, null, null, null);
	return pdfHisto;		
    }
	
    public static double[] getCDF(double[] binHeights) {
	//  Get normalising factor;
	double sumOfBinHeights = BasicStats.getSum(binHeights);
	int nbins = binHeights.length;
	double[] pdf = new double[nbins];
	double[] cdf = new double[nbins];
	double sum = 0;

	//  Compute cummulative bin heights
	for (int i=0; i < nbins; i++) {
	    pdf[i] = binHeights[i]/sumOfBinHeights;
	    sum += pdf[i];
	    cdf[i] = sum;
	}
	return cdf;
    }
	
    public static Histogram1D getCDFHisto(final Histogram1D histo) {
	//   Get axis and number of bins from original histo  
	FixedAxis axis = (FixedAxis) histo.axis();
	int nbins = axis.bins();
		
	//   Check number of "out of range" entries  
	int extraEntries = histo.extraEntries();
	if (extraEntries != 0) {
	    System.out.println("Log  : There are "+extraEntries+" extra entries");
	    System.out.println("Warn : Out of range entries will be ignored");
	}
				
	//   Construct the cumulative histo  
	double sumOfBinHeights = histo.sumBinHeights();
	double[] cumProb =  new double[nbins+2];
	double[] errors = new double[nbins+2];
	double sumOfProbs = 0;
	for (int i=0; i < nbins+1; i++) {
	    sumOfProbs += histo.binHeight(i)/sumOfBinHeights;
	    cumProb[i] = sumOfProbs;
	    errors[i] = 0;
	}
	String title = "Cumulative Distribution Function";
	Histogram1D cumProbDistHisto = new Histogram1D("CDF", title, axis);
	cumProbDistHisto.setContents(cumProb, errors, null, null, null);
	return cumProbDistHisto;
    }

    public static double[] getCumulativeDist(final double[] binHeights) {
	double[] cumProbDistFunc = new double[binHeights.length];
	double sum = 0;
	for (int i=0; i < binHeights.length; i++) {
	    sum += binHeights[i];
	    cumProbDistFunc[i] = sum;
	}
	return cumProbDistFunc;
    }
	
    public static Histogram1D getCumulativeHisto(final Histogram1D histo) {
	//   Get axis and number of bins from original histo  
	FixedAxis axis = (FixedAxis) histo.axis();
	int nbins = axis.bins();
		
	//   Check number of "out of range" entries  
	int extraEntries = histo.extraEntries();
	if (extraEntries != 0) {
	    System.out.println("Log  : There are "+extraEntries+" extra entries");
	    System.out.println("Warn : Out of range entries will be ignored");
	}
				
	//   Construct the cumulative histo  
	double[] cumProb =  new double[nbins+2];
	double[] errors = new double[nbins+2];
	double sumOfProbs = 0;
	for (int i=0; i < nbins+1; i++) {
	    sumOfProbs += histo.binHeight(i);
	    cumProb[i] = sumOfProbs;
	    errors[i] = 0;
	}
	String title = "Cumulative Distribution Function";
	Histogram1D cumProbDistHisto = new Histogram1D("CDF", title, axis);
	cumProbDistHisto.setContents(cumProb, errors, null, null, null);

	return cumProbDistHisto;
    }
    
    public static double[] getRandom(final Histogram1D cdfHisto, final int nevents) {
 	MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
	return getRandom(cdfHisto, nevents, engine);
    }

    public static double[] getRandom(final Histogram1D cdfHisto, final int nevents, final MersenneTwister64 engine) {
	//  Get axis properties and bin edges
	FixedAxis axis = (FixedAxis) cdfHisto.axis();
	int nbins = axis.bins();
	double[] binEdges = new double[nbins+1];
	for (int i=1; i <= nbins+1; i++)
	    binEdges[i-1] = axis.binLowerEdge(i);

	//  Get the bin heights
	double[] cdfBinHeights = new double[nbins];
	for (int i=0; i < nbins; i++) {
	    cdfBinHeights[i] = cdfHisto.binHeight(i);
	}

	//  Perform inversion on cdf and fill events array
  	Uniform uniform = new Uniform(0, 1, engine);
	double[] events = new double[nevents];
	int i=0;
 	while  (i < nevents) {
	    double r = uniform.nextDouble();
	    
	    //  Almost always r is not found in the array, and therefore, the method return: - (insertionPoint -1), 
	    //  where insertionPoint is the point at which the number would be inserted if it were in the array
	    int bin = -(Arrays.binarySearch(cdfBinHeights, r) +1);

	    //  In the rare case that the number is actually found, then we need to revert back to the right index
	    if (bin < 0) bin = -bin-1;

	    //  Randomize around the bin's edge
	    double x = binEdges[bin];
	    double binWidth = axis.binWidth(bin);
	    if (bin >= 0 &&  bin < (nbins-1)) {
		if (bin == 0) {
		    x -= binWidth*(r/cdfBinHeights[bin]);
		    events[i] = x;
		    //System.out.println(r+"	"+cdfBinHeights[bin]+"	"+binEdges[bin]+"	"+x);
		}
		else if (r > cdfBinHeights[bin-1]) {
		    x += binWidth*(r - cdfBinHeights[bin-1]) / (cdfBinHeights[bin] - cdfBinHeights[bin-1]);
		    events[i] = x;		    
		    //System.out.println(r+"	"+cdfBinHeights[bin]+"	"+binEdges[bin]+"	"+x);
		}
		else {
		    events[i] = x;
		}
		i++;
	    }
	}
	return events;
    }

    public static int[] getRandomBinIndexes(final Histogram1D cdfHisto, final int nevents) {
 	MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
	return getRandomBinIndexes(cdfHisto, nevents, engine);
    }
    
    public static int[] getRandomBinIndexes(final Histogram1D cdfHisto, final int nevents, final MersenneTwister64 engine) {
	//  Get axis properties and bin edges
	FixedAxis axis = (FixedAxis) cdfHisto.axis();
	int nbins = axis.bins();
	double[] binEdges = new double[nbins+1];
	for (int i=1; i <= nbins+1; i++)
	    binEdges[i-1] = axis.binLowerEdge(i);

	//  Get the bin heights
	double[] cdfBinHeights = new double[nbins];
	for (int i=0; i < nbins; i++) {
	    cdfBinHeights[i] = cdfHisto.binHeight(i);
	}

	//  Perform inversion on cdf and fill events array
  	Uniform uniform = new Uniform(0, 1, engine);
	int[] binIndexes = new int[nevents];
	int i=0;
 	while  (i < nevents) {
	    double r = uniform.nextDouble();
	    
	    //  Almost always r is not found in the array, and therefore,
	    //  the method return: -(insertionPoint-1), where
	    //  insertionPoint is the point at which the number
	    //  would be inserted if it were in the array.
	    int bin = -(Arrays.binarySearch(cdfBinHeights, r) +1);

	    //  In the rare case that the number is actually found,
	    //  we need to revert back to the right index.
	    if (bin < 0) bin = -bin-1;
	    binIndexes[i] = bin;
	    i++;
	}
	return binIndexes;
    }

    public static double[] getRandomFromRates(final Histogram1D cdfHisto, final int nevents, final double[] rates) {
 	MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
	return getRandomFromRates(cdfHisto, nevents, rates, engine);
    }

    public static double[] getRandomFromRates(final Histogram1D cdfHisto, final int nevents, final double[] rates, final MersenneTwister64 engine) {
	int[] binIndexes = getRandomBinIndexes(cdfHisto, (int)1.1*nevents, engine);
	Arrays.sort(binIndexes);
	DoubleArrayList t = new DoubleArrayList();
	int i = 0;
	double time = 0;
	FixedAxis axis = (FixedAxis) cdfHisto.axis();
	double duration = axis.binUpperEdge(axis.bins()-1);
	Exponential exponential = new Exponential(1, engine);
	while (time < duration) {
	    int bin = binIndexes[i];
	    double dt = exponential.nextDouble(rates[bin]);
	    time += dt;
	    t.add(time);
	    i++;
	}
	t.trimToSize();
	return t.elements();
    }
    
}
