package gb.esac.test;

import gb.esac.eventlist.EventList;
import gb.esac.montecarlo.RedNoiseGenerator;
import gb.esac.periodogram.FFTPeriodogram;
import gb.esac.periodogram.PeriodogramMaker;
import gb.esac.timeseries.TimeSeries;
import gb.esac.timeseries.TimeSeriesMaker;
import gb.esac.timeseries.TimeSeriesResampler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import org.apache.log4j.Logger;


public class TestRedNoiseLeak {

    static Logger logger  = Logger.getLogger(TestRedNoiseLeak.class);

    public static void main(String[] args) throws Exception   {
	
	DecimalFormat number = new DecimalFormat("0.00");
	DecimalFormat sci = new DecimalFormat("0E0");

	//  Generate long LightCurve
	logger.info("Generating long LightCurve");
	double duration = 1e5;
	double meanRate = 5;
	double lengthOfSegment = 1e3;
	double alpha = 1;


	//  Check for arguments
	if ( args.length == 4 ) {	 
	    duration = (new Double(args[0])).doubleValue();
	    meanRate = (new Double(args[1])).doubleValue();
	    lengthOfSegment = (new Double(args[2])).doubleValue();
	    alpha = (new Double(args[3])).doubleValue();
	}

	//  Generate arrival times
  	double[] t = RedNoiseGenerator.generateArrivalTimes(meanRate, duration, alpha);
 	int nSegments = (int) Math.floor(duration/lengthOfSegment);
 	EventList evlist = new EventList(t);
 	TimeSeries lc_full = TimeSeriesMaker.makeTimeSeries(evlist);
	double binTime = lc_full.getBinWidth();


	//  Write a rebinned version of the full light curve
	logger.info("Rebin long LightCurve and write to qdp file");
	int bins = (int) Math.pow(2,19);
	TimeSeries lc_full_resampled = TimeSeriesResampler.resample(lc_full, bins);
	lc_full_resampled.writeCountsAsQDP("rnl-lc.qdp");

	//  Make PSD
	logger.info("Making PSD, rebinning and writing to qdp file");
	FFTPeriodogram psdFull = PeriodogramMaker.makeFFTPeriodogram(lc_full_resampled);
	psdFull = (FFTPeriodogram) psdFull.rebin(10, "papadakis");


	//  Loop on segments of long light curve
	int i=0;
	bins = (int) Math.pow(2,8); // for 1e3
	//bins = (int) Math.pow(2,11); // for 1e4
	double from = i*lengthOfSegment;
	double to = (i+1)*lengthOfSegment;
	double[] times = evlist.getArrivalTimesFromTo(from, to);
	FFTPeriodogram psd = PeriodogramMaker.makeFFTPeriodogram(TimeSeriesMaker.makeTimeSeries(times, bins));
	FFTPeriodogram sumPsd = PeriodogramMaker.makeFFTPeriodogram(TimeSeriesMaker.makeTimeSeries(times, bins));

	while ( i < nSegments ) {

	    from = i*lengthOfSegment;
	    to = (i+1)*lengthOfSegment;
	    times = evlist.getArrivalTimesFromTo(from, to);
	    psd = PeriodogramMaker.makeFFTPeriodogram(TimeSeriesMaker.makeTimeSeries(times, bins));
	    sumPsd = (FFTPeriodogram) sumPsd.add(psd);
	    i++;
	}
	FFTPeriodogram scaledPsd_seg = (FFTPeriodogram) sumPsd.scale(1d/nSegments);


	//  Loop on short light curves
	times = RedNoiseGenerator.generateArrivalTimes(meanRate, lengthOfSegment, alpha);
	psd = PeriodogramMaker.makeFFTPeriodogram(TimeSeriesMaker.makeTimeSeries(times, bins));
	sumPsd = PeriodogramMaker.makeFFTPeriodogram(TimeSeriesMaker.makeTimeSeries(times, bins));
	i=1;
	int nLCs = 100;
	while ( i < nLCs ) {

	    times= RedNoiseGenerator.generateArrivalTimes(meanRate, lengthOfSegment, alpha);
	    psd = PeriodogramMaker.makeFFTPeriodogram(TimeSeriesMaker.makeTimeSeries(times, bins));
	    sumPsd = (FFTPeriodogram) sumPsd.add(psd);
	    i++;
	}
	FFTPeriodogram scaledPsd_lc = (FFTPeriodogram) sumPsd.scale(1d/nLCs);


	//  Combine the results in a single plot
	PrintWriter pw = 
	    new PrintWriter
	    (new BufferedWriter
	     (new FileWriter("rnlForAlpha"+alpha+"-"+sci.format(duration)+"-"+sci.format(lengthOfSegment)+".qdp")));

	String[] header = new String[] {
	    "DEV /XS",
	    "READ 1 2",
	    "LAB T", "LAB F",
	    "TIME OFF",
	    "LINE STEP",
	    "LOG ON",
	    "LW 4", "CS 1.3",
	    "LAB X Frequency (Hz)",
	    "LAB Y Power",
	    "LAB 1 VPOS 0.83 0.73 \"\\ga = "+alpha+", T\\dseg\\u = "+sci.format(lengthOfSegment)+"\"",
	    "LAB 1 JUST RIGHT CSIZE 1.4",
	    "VIEW 0.1 0.15 0.9 0.85",
	    "SKIP SINGLE",
	    "!"
	};
	for ( i=0; i < header.length; i++ ) {
	    pw.println(header[i]);
	}

	FFTPeriodogram[] psds = new FFTPeriodogram[] {psdFull, scaledPsd_seg, scaledPsd_lc};
	for ( i=0; i < psds.length; i++ ) {

	    double[] freqs = psds[i].getFreqs();
	    double[] pow = psds[i].getPowers();
	    for ( int k=0; k < freqs.length; k++ ) {
		pw.println(freqs[k]+"\t"+pow[k]);
	    }
	    pw.println("NO NO");
	    pw.flush();
	}
	pw.close();



    }

}