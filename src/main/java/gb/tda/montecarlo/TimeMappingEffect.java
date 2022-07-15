package gb.tda.montecarlo;

import cern.jet.random.Poisson;
import cern.jet.random.engine.MersenneTwister64;
import gb.tda.binner.BinningUtils;
import gb.tda.binner.Resampler;;
import gb.tda.eventlist.AstroEventList;
import gb.tda.periodogram.AggregatePeriodogram;
import gb.tda.periodogram.AveragePeriodogram;
import gb.tda.periodogram.FFTPeriodogram;
import gb.tda.periodogram.PeriodogramMaker;
import gb.tda.timeseries.TimeSeries;
import gb.tda.timeseries.TimeSeriesResampler;
import gb.tda.tools.Converter;
import gb.tda.tools.DistributionFunc;
import hep.aida.ref.histogram.Histogram1D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;


public class TimeMappingEffect {

    static DecimalFormat sci = new DecimalFormat("0.0#E0");

    public static void main(String[] args) throws Exception {

	//  Arguments
	double mean = 5;
	double duration = 1e3;
	double alpha = 1;
	int n = 100;
	if ( args.length == 4 ) {
	    mean = (Double.valueOf(args[0])).doubleValue();
	    duration = (Double.valueOf(args[1])).doubleValue();
	    alpha = (Double.valueOf(args[2])).doubleValue();
	    n = (Integer.valueOf(args[3])).intValue();
	}

	//  Define number of Timmer time bins
	double effectiveNyquistBinTime = 1/(2*mean);
	effectiveNyquistBinTime += Math.ulp(effectiveNyquistBinTime);
	double nbins = Math.ceil(duration/effectiveNyquistBinTime);
 	double exponent = Math.floor(Math.log10(nbins)/Math.log10(2));
	int nTimeBins = (int) Math.pow(2, exponent);
	double tkBinTime = duration/nTimeBins;

	//  Define the number of events to draw
	MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
	Poisson poisson = new Poisson(1, engine);
	int nevents = (int) Math.rint(mean*duration);
	nevents = poisson.nextInt(nevents);
	
	//  Define resampling factors
	int [] resamplingFactors = new int[] {16};
	
	//  Define periodogram aggregates
	AggregatePeriodogram[] avgPsd = new AggregatePeriodogram[resamplingFactors.length];
	for ( int j=0; j < resamplingFactors.length; j++ ) {
	    avgPsd[j] = new AggregatePeriodogram();
	}

	//  Loop on n
	for ( int i=0; i < n; i++ ) {

// 	    double[] times = RedNoiseGenerator.generateArrivalTimes(mean, duration, alpha);
// 	    AstroEventList evlist = new AstroEventList(times);
// 	    FFTPeriodogram psd = PeriodogramMaker.makePlainFFTPeriodogram(evlist);
// 	    avgShort.add(psd);

// 	    times = RedNoiseGenerator.generateArrivalTimes(mean, 10*duration, alpha);
// 	    evlist = new AstroEventList(times);
// 	    psd = PeriodogramMaker.makePlainFFTPeriodogram(evlist);
// 	    avgMed.add(psd);

// 	    times = RedNoiseGenerator.generateArrivalTimes(mean, 100*duration, alpha);
// 	    evlist = new AstroEventList(times);
// 	    psd = PeriodogramMaker.makePlainFFTPeriodogram(evlist);
// 	    avgLong.add(psd);

	    for ( int j=0; j < resamplingFactors.length; j++ ) {

		//  Generate rates from freqs based on T=longDuration with matching time resolution
		int factor = resamplingFactors[j];
		double longDuration = factor*duration;
		double[] rates = TimmerKonig.getRates(alpha, longDuration, nTimeBins*factor);

		//  Resample the rates so that there are the same number of bins as if T=duration
 		double[] oldBinEdges = BinningUtils.getBinEdges(0, longDuration, factor*nTimeBins);
 		double[] newBinEdges = BinningUtils.getBinEdges(0, longDuration, nTimeBins);
 		double[] timmerRates = Resampler.resample(rates, oldBinEdges, newBinEdges);
// 		double[] timmerRates = rates;

		//  Map these rates onto a time line of T=duration
		double tzero = 0;
		tkBinTime = duration/timmerRates.length;
		Histogram1D lcHisto = Converter.array2histo("light curve", tzero, tkBinTime, timmerRates);
		Histogram1D cdfHisto = DistributionFunc.getCDFHisto(lcHisto);
		double[] times = DistributionFunc.getRandom(cdfHisto, nevents);
		Arrays.sort(times);
		times[nevents-1] = times[0] + duration;  //  Adjust actual duration to specified duration

		//  Make FFTPeriodogram and add to AggregatePeriodogram 
		AstroEventList evlist = new AstroEventList(times);
		FFTPeriodogram psd = PeriodogramMaker.makePlainFFTPeriodogram(evlist);
		avgPsd[j].add(psd);
	    }
	}

	PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("psd-mappingEffect-factor-"+resamplingFactors[0]+"-mu-"+mean+"-T-"+sci.format(duration)+"-alpha-"+alpha+".qdp")));
	String[] header2 = new String[] {
		"DEV /XS",
		"READ SERR 2",
		"LAB T", "LAB F",
		"TIME OFF",
		"LINE STEP",
		"LOG ON",
		"LW 4", "CS 1.5",
		"LAB X Frequency (Hz)",
		"LAB Y Power",
		"VIEW 0.2 0.1 0.8 0.9",
		"SKIP SINGLE",
		"ERR OFF",
		"!"
	};
	for ( int i=0; i < header2.length; i++ ) 
	    pw.println(header2[i]);

	for ( int j=0; j < resamplingFactors.length; j++ ) {
	    AveragePeriodogram finalPsd = avgPsd[j].getPeriodogram();
	    double[] freqs = finalPsd.getFreqs();
	    double[] powers = finalPsd.getPowers();
	    double[] errors = finalPsd.getErrors();
	    int i=0;
	    while ( i < freqs.length && freqs[i] <= 0.25 ) {
		pw.println(freqs[i]+"	"+powers[i]+"	"+errors[i]);
		i++;
	    }
	    pw.println("NO NO NO");
	}
	pw.close();

    }

}
