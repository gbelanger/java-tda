package gb.esac.test;


import cern.colt.list.DoubleArrayList;
import cern.jet.random.Poisson;
import cern.jet.random.engine.MersenneTwister64;
import gb.esac.aida.functions.LogNormalFunction;
import gb.esac.binner.Binner;
import gb.esac.binner.BinningUtils;
import gb.esac.eventlist.AstroEventList;
import gb.esac.io.AsciiDataFileWriter;
import gb.esac.montecarlo.RedNoiseGenerator;
import gb.esac.periodogram.FFTPeriodogram;
import gb.esac.periodogram.PeriodogramMaker;
import gb.esac.timeseries.TimeSeries;
import gb.esac.timeseries.TimeSeriesMaker;
import gb.esac.timeseries.TimeSeriesOperations;
import gb.esac.timeseries.TimeSeriesResampler;
import gb.esac.tools.BasicStats;
import gb.esac.tools.PrimitivesConverter;
import gb.esac.tools.DataUtils;
import gb.esac.tools.MinMax;
import hep.aida.IAnalysisFactory;
import hep.aida.IAxis;
import hep.aida.IFitFactory;
import hep.aida.IFitResult;
import hep.aida.IFitter;
import hep.aida.IFunction;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.ITree;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;


public class TestRmsFluxRelation {

    static Logger logger = Logger.getLogger(TestRmsFluxRelation.class);

    public static void main(String[] args) throws Exception  {

	double meanRate = 3377; // for Cyg X-1
	meanRate = 409; // for Sax J1808
	double duration = 23000;  // 23 ks for SAX J1808.4-3658
	double alpha = 1;

	//  Simulated event list
	String filename;
	//filename = "/Users/gbelanger/javaProgs/simEvlist-SaxJ1808_simu1.fits";

	//AstroEventList evlist = new AstroEventList(filename);
   // 	AstroEventList evlist = new AstroEventList(RedNoiseGenerator.generateArrivalTimes(meanRate, duration, alpha));
//    	double dt = 0.016;
//    	TimeSeries ts = TimeSeriesMaker.makeTimeSeries(evlist, dt);

	//  With an existing event list
   	//filename = "/Users/gbelanger/Documents/astroData/rxte/saxj1808_lightcurve_16msec.lc";
 	//filename = "/Users/gbelanger/Documents/astroData/rxte/cygx1_lightcurve_16msec.lc";
	filename = "all-times.qdp";
	AstroEventList evlist = new AstroEventList(filename);
	duration = evlist.duration();
	meanRate = evlist.meanRate();
	double[] times_all = evlist.getArrivalTimes();
	double binTime = 0.1;
	TimeSeries ts = TimeSeriesMaker.makeTimeSeries(times_all, binTime);

// 	double[] rates = ts.getRates();
// 	AsciiDataFileWriter ratesOut = new AsciiDataFileWriter("ratesHisto-dt_"+binTime+"s.qdp");
// 	ratesOut.writeHisto(Binner.makeHisto(rates, 50), "Count Rate");


 	//  Make PDF of the bin heights
 	double[] binHeights = ts.getIntensities();
	double meanBinHeight = ts.meanIntensity();
       	double[] normBinHeights = DataUtils.normalise(binHeights, 1/meanBinHeight);
	int min = (int) ts.minBinHeight();
	int max = (int) ts.maxBinHeight();
	int nHistoBins = max-min;
	AsciiDataFileWriter adfw = new AsciiDataFileWriter("histoOfBinHeights.qdp");
	//IHistogram1D pdf = Binner.makePDF(normBinHeights, min/meanBinHeight, max/meanBinHeight, nHistoBins);
	IHistogram1D pdf = Binner.makePDF(binHeights, min, max, nHistoBins);

	//  Fit the histo with the LogNormal distribution
// 	IAnalysisFactory af = IAnalysisFactory.create();
// 	ITree tree = af.createTreeFactory().create();
// 	IHistogramFactory hf = af.createHistogramFactory(tree);
//  	IFitFactory fitF   = af.createFitFactory();
// 	IFitter fitter = fitF.createFitter("Chi2", "jminuit");

// 	IFunction logN = new LogNormalFunction("Log-Normal Function");
// 	double[] logBinHeights = PrimitivesConverter.lin2logSpace(binHeights);
// 	double mu = BasicStats.getMean(logBinHeights);
// 	double sig = Math.sqrt(BasicStats.getVariance(logBinHeights));
// 	double norm = 1e3;
// 	logN.setParameter("mean", mu);
// 	logN.setParameter("sigma", sig);
// 	logN.setParameter("norm", norm);
// 	IFitResult fitRes = fitter.fit(pdf, logN);
// 	IAxis axis = pdf.axis();
// 	double[] function = new double[nHistoBins];
// 	for (int i=0; i < nHistoBins; i++) {
// 	    double x = axis.binCenter(i);
// 	    function[i] = fitRes.fittedFunction().value(new double[] {x});
// 	}


// 	Poisson poisson = new Poisson(meanBinHeight, new MersenneTwister64(new java.util.Date()));
// 	double[] function = new double[nHistoBins];
// 	for (int i=0; i < nHistoBins; i++) {
// 	    function[i] = poisson.pdf(min + i);
// 	}
//  	adfw.writeHisto(pdf, function, "Bin Heights");
	adfw.writeHisto(pdf, "PDF of Bin Heights");


	//  Construct RMS vs Flux using segments of the event list
	double lengthOfSegment = 20;
	int nSegments = (int) Math.floor(duration/lengthOfSegment);
	DoubleArrayList fluxList = new DoubleArrayList();
	DoubleArrayList rmsList = new DoubleArrayList();
	DoubleArrayList rmsFromPsdList = new DoubleArrayList();
	DoubleArrayList timeAxisList = new DoubleArrayList();
	for (int i=0; i < nSegments; i++) {

	    double from = i*lengthOfSegment;
	    double to = (i+1)*lengthOfSegment;

	    //  With event list
	    //double[] times = evlist.getArrivalTimesFromTo(from, to);
	    //double flux = times.length/lengthOfSegment + Math.random()/1e8;
	    //TimeSeries lc = TimeSeriesMaker.makeTimeSeries(new AstroEventList(times), dt);
	    
	    //  With light curve
	    TimeSeries seg = null;
	    try { 
		seg = TimeSeriesOperations.getSegment(ts, from, to);
		double flux = seg.meanRate();
		double counts = seg.sumOfBinHeights();
		if (counts > 30) {
		    fluxList.add(flux);
		    
		    double t = (from+to)/2d;
		    timeAxisList.add(t);
		    
		    //  RMS from lightcurve
 		    double rms = Math.sqrt(seg.varianceInRates());
 		    rmsList.add(rms);
		    
		    //  RMS from periodogram
		    FFTPeriodogram psd = PeriodogramMaker.makePlainFFTPeriodogram(seg,"variance");
		    rmsFromPsdList.add(Math.sqrt(psd.getIntegratedPower()));
		}
	    }
	    catch (NullPointerException e) {}
	}
	fluxList.trimToSize();
	timeAxisList.trimToSize();
	rmsList.trimToSize();
	rmsFromPsdList.trimToSize();

	AsciiDataFileWriter out = new AsciiDataFileWriter("rmsVsFlux.qdp");
	String[] headerRmsVsFlux = new String[] {
		"DEV /XS",
		"READ 1 2",
		"LAB T", "LAB F",
		"TIME OFF",
		"LINE OFF",
		"MA 16 ON",
		"MA SIZE 1.0",
		"LW 3", "CS 1.3",
		"VIEW 0.25 0.1 0.75 0.9",
		"LAB X Flux (cps)",
		"LAB Y \\gs (cps)",
		"!"
	};
	out.writeData(headerRmsVsFlux, fluxList.elements(), rmsFromPsdList.elements());

	out = new AsciiDataFileWriter("rmsAndFluxVsTime.qdp");
	String[] header2 = new String[] {
		"DEV /XS",
		"READ 1 2 3",
		"LAB T", "LAB F",
		"TIME OFF",
		"LINE OFF",
		"MA 16 ON",
		"MA SIZE 1.0",
		"LW 3", "CS 1.3",
		"VIEW 0.1 0.1 0.9 0.9",
		"LAB X Time (s)",
		"LAB Y3 \\gs (cps)",
		"LAB Y2 Flux (cps)",
		"PLOT VERT",
		"!"
	};
	out.writeData(header2, timeAxisList.elements(), fluxList.elements(), rmsList.elements(), rmsFromPsdList.elements());


 	//  Sort fluxes
	double[] fluxes = fluxList.elements();
	double[] fluxSorted = Arrays.copyOf(fluxes, fluxes.length);
	Arrays.sort(fluxSorted);

	//  Match the rms values to the corresponding flux
 	double[] rmsSorted = new double[fluxSorted.length];
 	for (int i=0; i < fluxSorted.length; i++) {
 	    int index = fluxList.indexOf(fluxSorted[i]);
	    rmsSorted[i] = rmsList.get(index);
 	    //rmsSorted[i] = rmsFromPsdList.get(index);
 	}
 	out = new AsciiDataFileWriter("rmsVsFlux-sorted.qdp");
  	out.writeData(headerRmsVsFlux, fluxSorted, rmsSorted);


	//  Group data in flux bins
	double minFlux = MinMax.getMin(fluxSorted);
	double maxFlux = MinMax.getMax(fluxSorted);
	int nBins = (int) Math.floor(nSegments/30);
	nBins = 5;
	double binWidth = (maxFlux-minFlux)/nBins;
	double[] binEdges = BinningUtils.getBinEdges(minFlux, maxFlux, nBins);
 	DoubleArrayList fluxesInBin = new DoubleArrayList();
 	DoubleArrayList rmsesInBin = new DoubleArrayList();
	double[] avgFluxes = new double[nBins];
	double[] avgRmses = new double[nBins];
	double[] errOnAvgFluxes = new double[nBins];
	double[] errOnAvgRmses = new double[nBins];
	int[] nElementsInBin = new int[nBins];
	int i=0;
	int k=0;
	while (i < nSegments && (2*k+1) < binEdges.length) {
	    double binEdge = binEdges[2*k+1];
	    double flux = fluxSorted[i];
	    double rms = rmsSorted[i];
	    int n = 0;
	    while (i < nSegments && flux < binEdge) {
		fluxesInBin.add(flux);
		rmsesInBin.add(rms);
		i++;
		n++;
		try {
		    flux = fluxSorted[i];
		    rms = rmsSorted[i];
		}
		catch (ArrayIndexOutOfBoundsException e) {i++;}
	    }
	    fluxesInBin.trimToSize();
	    rmsesInBin.trimToSize();
	    
	    nElementsInBin[k] = fluxesInBin.size();
	    avgFluxes[k] = BasicStats.getMean(fluxesInBin.elements());
	    errOnAvgFluxes[k] = BasicStats.getErrOnMean(fluxesInBin.elements());
	    avgRmses[k] = BasicStats.getMean(rmsesInBin.elements());
	    errOnAvgRmses[k] = BasicStats.getErrOnMean(rmsesInBin.elements());

	    k++;
	    try { binEdge = binEdges[2*k+1]; }
	    catch (ArrayIndexOutOfBoundsException e) { k++; }

	    fluxesInBin.clear();
	    rmsesInBin.clear();
	}

// 	//  Throw out all the bins that have < 30 flux values
// 	int nGoodBins = 0;
// 	for (int j=0; j < nBins; j++) {
// 	    if (nElementsInBin[j] >= 30) nGoodBins++;
// 	}
// 	double[] finalAvgFluxes = new double[nGoodBins];
// 	double[] finalErrOnAvgFluxes = new double[nGoodBins];
// 	double[] finalAvgRmses = new double[nGoodBins];
// 	double[] finalErrOnAvgRmses = new double[nGoodBins];
// 	k=0;
// 	for (int j=0; j < nBins; j++) {
// 	    if (nElementsInBin[j] >= 30) {
// 		finalAvgFluxes[k] = avgFluxes[j];
// 		finalErrOnAvgFluxes[k] = errOnAvgFluxes[j];
// 		finalAvgRmses[k] = avgRmses[j];
// 		finalErrOnAvgRmses[k] = errOnAvgRmses[j];
// 		k++;
// 	    }
// 	}

	out = new AsciiDataFileWriter("rmsVsFlux-binned.qdp");
	String[] header = new String[] {
		"DEV /XS",
		"READ SERR 1 2",
		"LAB T", "LAB F",
		"TIME OFF",
		"LINE OFF",
		"MA 16 ON",
		"MA SIZE 1.0",
		"LW 3", "CS 1.3",
		"VIEW 0.25 0.1 0.75 0.9",
		"LAB X Flux (cps)",
		"LAB Y \\gs (cps)",
		"!"
	};
 	//out.writeData(header, finalAvgFluxes, finalErrOnAvgFluxes, finalAvgRmses, finalErrOnAvgRmses); 
	out.writeData(header, avgFluxes, errOnAvgFluxes, avgRmses, errOnAvgRmses); 


    }

}