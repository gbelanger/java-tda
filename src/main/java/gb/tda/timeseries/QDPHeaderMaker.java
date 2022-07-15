package gb.tda.timeseries;

import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;


/**
 * Class <code>QDPHeaderMaker</code> constructs headers for <code>QDPTimeSeriesFileWriter</code>
 * @author G. Belanger</a>
 */


public class QDPHeaderMaker {

    private static final Logger logger = Logger.getLogger(QDPHeaderMaker.class);
    
    private static final DecimalFormat noDigits = new DecimalFormat("0");
    private static final DecimalFormat twoDigits = new DecimalFormat("0.00");
    private static final DecimalFormat threeDigits = new DecimalFormat("0.000");
    private static final DecimalFormat fourDigits = new DecimalFormat("0.0000");

    // Column names
	//// Simple
	private static final String COL_NAMES_SIMPLE = "times, intensities";
	private static final String COL_NAMES_SIMPLE_UNCERTAINTIES = "times, intensities, uncertainties";
	//// Binned
	private static final String COL_NAMES_BINNED = "binCentres, halfBinWidths, intensities";
	private static final String COL_NAMES_BINNED_UNCERTAINTIES = "binCentres, halfBinWidths, intensities, uncertainties";
    //// CodedMaskTimeSeries
	private static final String COL_NAMES_COUNTS_CODED_MASK = "binCentres, halfBinWidths, counts, distToPointingAxis";
    private static final String COL_NAMES_RATES_CODED_MASK = "binCentres, halfBinWidths, rates, uncertainties, distToPointingAxis";
    private static final String COL_NAMES_ALLDATA_CODED_MASK = "binCentres, halfBinWidths, rates, uncertainties, distToPointingAxis, rasOfPointing, decsOfPointing, exposuresOnTarget, effectivePointingDurations";

	public static String[] getHeader(ITimeSeries ts, String filename, String producedBy) {
		logger.info("Making header (counts)");
		String serr = "1 2";
		String columnNames = COL_NAMES_SIMPLE;
		if (ts.uncertaintiesAreSet()) {
			serr = "SERR 2";
			columnNames = COL_NAMES_SIMPLE_UNCERTAINTIES;
		}
		String yLabel = "Intensity";
		String[] commentBlock = getCommentBlock(ts, producedBy, filename, columnNames);
		String[] qdpBlock = getQDPBlock(ts, serr, yLabel);
		return ArrayUtils.addAll(commentBlock, qdpBlock);
	}

	public static String[] getHeader(IBinnedTimeSeries ts, String filename, String producedBy) {
		logger.info("Making header (binned)");
		String serr = "SERR 1";
		String columnNames = COL_NAMES_BINNED;
		if (ts.uncertaintiesAreSet()) {
			serr = "SERR 1 2";
			columnNames = COL_NAMES_BINNED_UNCERTAINTIES;
		}
		String yLabel = "Intensity (per bin)";
		double binWidth = 0;
		try { binWidth = ts.binWidth(); }
		catch (TimeSeriesException ignored) {}
		if (binWidth != 0) {
			yLabel = "Intensity (per "+twoDigits.format(binWidth)+" "+ts.timeUnit()+")";
		}
		String[] commentBlock;
		String[] qdpBlock = getQDPBlock(ts, serr, yLabel);
		try {
			// CodedMaskTimeSeries
			commentBlock = getCommentBlock((CodedMaskTimeSeries) ts, producedBy, filename, columnNames);
			qdpBlock = getQDPBlock((CodedMaskTimeSeries) ts, serr, yLabel);
		}
		catch (ClassCastException classCastException) {
			try {
				// IAstroTimeSeries
				commentBlock = getCommentBlock((IAstroTimeSeries) ts, producedBy, filename, columnNames);
				qdpBlock = getQDPBlock((IAstroTimeSeries) ts, serr, yLabel);
			} catch (ClassCastException classCastException2) {
				// IBinnedTimeSeries
				commentBlock = getCommentBlock(ts, producedBy, filename, columnNames);
			}
		}
		String[] header = ArrayUtils.addAll(commentBlock, qdpBlock);
		return header;
	}

    public static String[] getAllDataHeader(CodedMaskTimeSeries ts, String filename, String producedBy) {
		logger.info("Making header (all data)");
        String serr = "SERR 1 2";
		String yLabel="Intensity (s\\u-1\\d)";
		String columnNames = COL_NAMES_ALLDATA_CODED_MASK;
		String[] commentBlock = getCommentBlock(ts, producedBy, filename, columnNames);
		String[] qdpBlock = getQDPBlock(ts, serr, yLabel);
		String[] header = ArrayUtils.addAll(commentBlock, qdpBlock);
		return header;
    }


    ////
    //   Private methods
    ////
    
    private static String[] getCommentBlock(ITimeSeries ts, String producedBy, String filename, String columnNames) {
		double mean = ts.meanIntensity();
		double errorOnMean = ts.errorOnMeanIntensity();
		double signif = mean/errorOnMean;
		String[] commentBlock = new String[] {
			"! Filename: " + filename,
			"! Time series class: "+ts.getClass().getCanonicalName(),
			"! Produced by: "+ producedBy,
			"! Date: "+new Date(),
			"! Author: G. Belanger",
			"! TStart: "+ts.tStart(),
			"! Duration: "+ts.duration()+" "+ts.timeUnit(),
			"! Mean Intensity: "+threeDigits.format(mean)+" +/- "+threeDigits.format(errorOnMean),
			"! Detection Significance: "+threeDigits.format(signif),
			"! Columns: "+columnNames,
			"!"
		};
		return commentBlock;
    }

	private static String[] getCommentBlock(IAstroTimeSeries ts, String producedBy, String filename, String columnNames) {
		double mean = ts.meanIntensity();
		double errorOnMean = ts.errorOnMeanIntensity();
		double signif = mean/errorOnMean;
		String[] commentBlock = new String[] {
				"! Filename: " + filename,
				"! Time series class: "+ts.getClass().getCanonicalName(),
				"! Produced by: "+ producedBy,
				"! Date: "+new Date(),
				"! Author: G. Belanger",
				"! Target Name: "+ts.targetName(),
				"! Target RA: "+threeDigits.format(ts.targetRA()),
				"! Target Dec: "+threeDigits.format(ts.targetDec()),
				"! Telescope: "+ts.telescope(),
				"! Instrument: "+ts.instrument(),
				"! Energy range min: "+ts.energyRangeMin(),
				"! Energy range max: "+ts.energyRangeMax(),
				"! Relative time error: "+ts.relTimeError(),
				"! Absolute time error: "+ts.absTimeError(),
				"! TStart: "+ts.tStart(),
				"! MJD Ref: "+ts.mjdref(),
				"! Duration: "+(int) ts.duration()+" "+ts.timeUnit(),
				"! Number of observations = "+ts.nBins(),
				"! Sum of pointings: "+(int) ts.sumOfBinWidths()+" "+ts.timeUnit(),
				"! Ontime: "+(int) ts.exposureOnTarget()+" "+ts.timeUnit(),
				"! Livetime: "+(int) ts.exposureOnTarget()+" "+ts.timeUnit(),
				"! Exposure on target: "+(int) ts.exposureOnTarget()+" "+ts.timeUnit(),
				"! Mean Intensity: "+threeDigits.format(mean)+" +/- "+threeDigits.format(errorOnMean),
				"! Detection Significance: "+threeDigits.format(signif),
				"! Columns: "+columnNames,
				"!"
		};
		return commentBlock;
	}

	private static String[] getCommentBlock(CodedMaskTimeSeries ts, String producedBy, String filename, String columnNames) {
		double mean = ts.meanIntensity();
		double errorOnMean = ts.errorOnMeanIntensity();
		double signif = mean/errorOnMean;
		String[] commentBlock = new String[] {
				"! Filename: " + filename,
				"! Time series class: "+ts.getClass().getCanonicalName(),
				"! Produced by: "+ producedBy,
				"! Date: "+new Date(),
				"! Author: G. Belanger",
				"! Target Name: "+ts.targetName(),
				"! Target RA: "+threeDigits.format(ts.targetRA()),
				"! Target Dec: "+threeDigits.format(ts.targetDec()),
				"! Telescope: "+ts.telescope(),
				"! Instrument: "+ts.instrument(),
				"! Max distance for full coding: "+((CodedMaskTimeSeries)ts).maxDistForFullCoding(),
				"! Energy range min: "+ts.energyRangeMin(),
				"! Energy range max: "+ts.energyRangeMax(),
				"! TStart: "+ts.tStart(),
				"! Duration: "+(int) ts.duration()+" "+ts.timeUnit(),
				"! Number of observations = "+ts.nBins(),
				"! Number fully coded = "+((CodedMaskTimeSeries)ts).nFullyCoded(),
				"! Sum of pointings: "+(int) ((CodedMaskTimeSeries)ts).sumOfEffectivePointingDurations()+" s",
				"! Exposure on target: "+(int) ts.exposureOnTarget()+" s",
				"! Mean Intensity: "+threeDigits.format(mean)+" +/- "+threeDigits.format(errorOnMean),
				"! Detection Significance: "+threeDigits.format(signif),
				"! Columns: "+columnNames,
				"!"
		};
		return commentBlock;
	}


	private static String[] getQDPBlock(ITimeSeries ts, String serr, String yLabel) {
		// Line and marker
		String marker = "MA 17 on 2";
		// X-axis
		double[] times = ts.getTimes();
        double xmin =  times[0] - 0.05*ts.duration();
        double xmax =  times[times.length-1] + 0.05*ts.duration();
		double xRange = xmax - xmin;
		// Y-axis
		double min = ts.minIntensity() - ts.meanDeviationInIntensities();
		double max = ts.maxIntensity() + ts.meanDeviationInIntensities();
		if (yLabel.contains("cts per")) { // Working with Counts
			marker = "LINE STEP ON 2";
			min = ts.minIntensity();
			max = ts.maxIntensity();
		}
		String timeRef = ")";
		if (ts.tStart() > 1) {
			timeRef = " since "+ts.tStart()+")";
		}
        double yRange = max - min;	
        double ymin = min; // -0.1*yRange;
        double ymax = max; // +0.1*yRange;	
        String[] qdpBlock = new String[] {
            "DEV /XS",
            "READ "+serr,
            "LAB T", "LAB F",
            "TIME OFF",
	    	marker,
            "LINE STEP ON 3",
            "LW 4", "CS 1.1",
            "LAB X Time (s"+timeRef,
            "LAB Y "+yLabel,
            "VIEW 0.1 0.2 0.9 0.8",
            "R X "+twoDigits.format(xmin)+" "+twoDigits.format(xmax),
            "R Y "+twoDigits.format(ymin)+" "+twoDigits.format(ymax),
            "!"
        };
		return qdpBlock;
    }

	private static String[] getQDPBlock(IBinnedTimeSeries ts, String serr, String yLabel) {
		// Line and marker
		String marker = "MA 17 on 2";
		// X-axis
		double[] binEdges = ts.getBinEdges();
		double xmin = binEdges[0] - 0.05 * ts.duration();
		double xmax = binEdges[binEdges.length - 1] + 0.05 * ts.duration();
		double xRange = xmax - xmin;
		// Y-axis
		double min = ts.minIntensity() - ts.meanDeviationInIntensities();
		double max = ts.maxIntensity() + ts.meanDeviationInIntensities();
		if (yLabel.contains("cts per")) { // Working with Counts
			marker = "LINE STEP ON 2";
			min = ts.minIntensity();
			max = ts.maxIntensity();
		}
		String timeRef = ")";
		if (ts.tStart() > 1) {
			timeRef = " since " + ts.tStart() + ")";
		}
		double yRange = max - min;
		double ymin = min; // -0.1*yRange;
		double ymax = max; // +0.1*yRange;
		String[] qdpBlock = new String[]{
				"DEV /XS",
				"READ " + serr,
				"LAB T", "LAB F",
				"TIME OFF",
				marker,
				"LINE STEP ON 3",
				"LW 4", "CS 1.1",
				"LAB X Time (s" + timeRef,
				"LAB Y " + yLabel,
				"VIEW 0.1 0.2 0.9 0.8",
				"R X " + twoDigits.format(xmin) + " " + twoDigits.format(xmax),
				"R Y "+twoDigits.format(ymin)+" "+twoDigits.format(ymax),
				"!"
		};
		return qdpBlock;
	}

	private static String[] getQDPBlock(IAstroTimeSeries ts, String serr, String yLabel) {
		// Marker
		String marker = "MA 17 on 2";
		// X-axis
		double[] binEdges = ts.getBinEdges();
		double xmin = binEdges[0] - 0.05 * ts.duration();
		double xmax = binEdges[binEdges.length - 1] + 0.05 * ts.duration();
		double xRange = xmax - xmin;
		// Y-axis
		double min = ts.minIntensity() - ts.meanDeviationInIntensities();
		double max = ts.maxIntensity() + ts.meanDeviationInIntensities();
		if (yLabel.contains("cts per")) { // Working with Counts
			marker = "LINE STEP ON 2";
			min = ts.minIntensity();
			max = ts.maxIntensity();
		}
		String timeRef = ")";
		if (ts.tStart() > 1) {
			timeRef = " since " + ts.tStart();
		}
		double yRange = max - min;
		double ymin = min; // -0.1*yRange;
		double ymax = max; // +0.1*yRange;

		String telescope = ts.telescope();
		String instrument = ts.instrument();
		String oTitle = telescope+"/"+instrument+" Time Series";
		double emin = ts.energyRangeMin();
		double emax = ts.energyRangeMax();
		if (!Double.isNaN(emin) && !Double.isNaN(emax)) {
			String eminStr = eminStr = String.valueOf(emin);
			String emaxStr = emaxStr = String.valueOf(emax);
			if (Math.round(emin) == emin && Math.round(emax) == emax) {
				eminStr = noDigits.format(emin);
				emaxStr = noDigits.format(emax);
			}
			oTitle = telescope + "/" + instrument + " Time Series (" + eminStr + "-" + emaxStr + " keV)";
		}
		double ra = ts.targetRA();
		double dec = ts.targetDec();
		String title = ts.targetName()+" (RA="+twoDigits.format(ra)+", Dec="+twoDigits.format(dec)+")";
		String y2Label = yLabel;
		if (ts.tStart() != 0.0) {
			timeRef = " since " + ts.tStart();
		}
		int nGood =  ts.nBins();
		int duration = (int) ts.duration();
		int ontime = (int)  ts.livetime();
		int effectiveExposure = (int)  ts.exposureOnTarget();
		double meanRate = ts.meanRate();
		double errorOnMean = ts.errorOnMeanRate();
		double signif = meanRate/errorOnMean;
		// Assemble
		String[] qdpBlock = new String[] {
				"DEV /XS",
				"READ "+serr,
				"LAB F",
				"TIME OFF",
				"LW 3",
				"CS 1.1",
				"LAB OT "+oTitle,
				"LAB T "+title,
				marker,
				"CO OFF 1 4 5 6 7",
				"LAB X Time ("+ts.timeUnit()+" "+timeRef+")",
				"LAB Y2 "+yLabel,
//				"LAB Y3 "+y3Label,
				"R Y2 "+threeDigits.format(ymin)+" "+threeDigits.format(ymax),
//				"R Y3 -2 17",
				"R X "+threeDigits.format(xmin)+" "+threeDigits.format(xmax),
				"VIEW 0.1 0.2 0.9 0.8",
//				"WIN 3",
//				"LOC 0 0.1 1 0.4",
//				"LAB 100 POS "+twoDigits.format(xmin + 0.01*xRange)+" 4.15 LINE 0 0.98 \"",
//				"LAB 100 LS 4 JUST CEN",
//				"LAB 101 VPOS 0.12 0.23 \""+(int)Math.round(fracPC*100)+"%\" CS 0.55 JUST CEN",
//				"LAB 102 VPOS 0.12 0.207 \""+(int)Math.round(fracFC*100)+"%\" CS 0.55 JUST CEN",
				//"LAB 101 POS "+twoDigits.format(xmin + 0.025*xRange)+" 5.35 \""+(int)Math.round(fracPC*100)+"%\" CS 0.55 JUST CEN",
				//"LAB 102 POS "+twoDigits.format(xmin + 0.025*xRange)+" 2.95 \""+(int)Math.round(fracFC*100)+"%\" CS 0.55 JUST CEN",
				"WIN 2",
				"LOC 0 0.22 1 0.92",
				"LAB 200 VPOS 0.12 0.75 \"Target Name: "+ts.targetName()+"\" CS 0.55 JUST LEFT",
				"LAB 201 VPOS 0.12 0.73 \"Mean Rate = "+threeDigits.format(meanRate)+" +/- "+threeDigits.format(errorOnMean)+" s\\u-1\\d\" CS 0.55 JUST LEFT",
				"LAB 202 VPOS 0.12 0.71 \"Detection Significance = "+threeDigits.format(signif)+"\" CS 0.55 JUST LEFT",
				"LAB 203 VPOS 0.88 0.75 \"Number of observations = "+nGood+"\" CS 0.55 JUST RIGHT",
				"LAB 204 VPOS 0.88 0.73 \"Time series duration = "+duration+" s\" CS 0.55 JUST RIGHT",
				"LAB 205 VPOS 0.88 0.71 \"Sum of pointings = "+ontime+" s\" CS 0.55 JUST RIGHT",
				"LAB 206 VPOS 0.88 0.69 \"Exposure on target = "+effectiveExposure+" s\" CS 0.55 JUST RIGHT",
				"!"
		};
		return qdpBlock;
	}

	private static String[] getQDPBlock(CodedMaskTimeSeries ts, String serr, String yLabel) {
		// Marker
		String marker = "MA 17 on 2";
		// X-axis
		double[] binEdges = ts.getBinEdges();
		double xmin = binEdges[0] - 0.05 * ts.duration();
		double xmax = binEdges[binEdges.length - 1] + 0.05 * ts.duration();
		double xRange = xmax - xmin;
		// Y-axis
		double min = ts.minIntensity() - ts.meanDeviationInIntensities();
		double max = ts.maxIntensity() + ts.meanDeviationInIntensities();
		if (yLabel.contains("cts per")) { // Working with Counts
			marker = "LINE STEP ON 2";
			min = ts.minIntensity();
			max = ts.maxIntensity();
		}
		String timeRef = ")";
		if (ts.tStart() > 1) {
			timeRef = " since " + ts.tStart() + ")";
		}
		double yRange = max - min;
		double ymin = min; // -0.1*yRange;
		double ymax = max; // +0.1*yRange;

		String telescope = ts.telescope();
		String instrument = ts.instrument();
		double emin = ts.energyRangeMin();
		double emax = ts.energyRangeMax();
		String eminStr = eminStr = String.valueOf(emin);
		String emaxStr = emaxStr = String.valueOf(emax);
		if (Math.round(emin) == emin && Math.round(emax) == emax) {
			eminStr = noDigits.format(emin);
			emaxStr = noDigits.format(emax);
		}
		String oTitle = telescope+"/"+instrument+" Time Series ("+eminStr+"-"+emaxStr+" keV)";
		double ra = ts.targetRA();
		double dec = ts.targetDec();
		String title = ts.targetName()+" (RA="+twoDigits.format(ra)+", Dec="+twoDigits.format(dec)+")";
		String y2Label = yLabel;
		if (ts.tStart() != 0.0) {
			timeRef = " since MJD "+(ts.tStart()/86400)+")";
		}
		String y3Label="Angle (deg)";
		double fracFC = ((CodedMaskTimeSeries)ts).fullyCodedFraction();
		double fracPC = 1. - fracFC;
		int nGood =  ((CodedMaskTimeSeries)ts).nNonNaN_distToPointingAxis();
		int duration = (int) ts.duration();
		int ontime = (int)  ((CodedMaskTimeSeries)ts).sumOfEffectivePointingDurations();
		int effectiveExposure = (int)  ((CodedMaskTimeSeries)ts).exposureOnTarget();
		double meanRate = ts.meanRate();
		double errorOnMean = ts.errorOnMeanRate();
		double signif = meanRate/errorOnMean;
		// Assemble
		String[] qdpBlock = new String[] {
				"DEV /XS",
				"READ "+serr,
				"PLOT VERT",
				"LAB F",
				"TIME OFF",
				"LW 3",
				"CS 1.1",
				"LAB OT "+oTitle,
				"LAB T "+title,
				marker,
				"MA 2 ON 3",
				"CO 2 ON 3",
				"CO OFF 1 4 5 6 7",
				"LAB X Time (s" + timeRef,
				"LAB Y2 "+yLabel,
				"LAB Y3 "+y3Label,
				"R Y2 "+threeDigits.format(ymin)+" "+threeDigits.format(ymax),
				"R Y3 -2 17",
				"R X "+threeDigits.format(xmin)+" "+threeDigits.format(xmax),
				"VIEW 0.1 0.2 0.9 0.8",
				"WIN 3",
				"LOC 0 0.1 1 0.4",
				"LAB 100 POS "+twoDigits.format(xmin + 0.01*xRange)+" 4.15 LINE 0 0.98 \"",
				"LAB 100 LS 4 JUST CEN",
				"LAB 101 VPOS 0.12 0.23 \""+(int)Math.round(fracPC*100)+"%\" CS 0.55 JUST CEN",
				"LAB 102 VPOS 0.12 0.207 \""+(int)Math.round(fracFC*100)+"%\" CS 0.55 JUST CEN",
				//"LAB 101 POS "+twoDigits.format(xmin + 0.025*xRange)+" 5.35 \""+(int)Math.round(fracPC*100)+"%\" CS 0.55 JUST CEN",
				//"LAB 102 POS "+twoDigits.format(xmin + 0.025*xRange)+" 2.95 \""+(int)Math.round(fracFC*100)+"%\" CS 0.55 JUST CEN",
				"WIN 2",
				"LOC 0 0.22 1 0.92",
				"LAB 200 VPOS 0.12 0.75 \"Target Name: "+ts.targetName()+"\" CS 0.55 JUST LEFT",
				"LAB 201 VPOS 0.12 0.73 \"Mean Rate = "+threeDigits.format(meanRate)+" +/- "+threeDigits.format(errorOnMean)+" s\\u-1\\d\" CS 0.55 JUST LEFT",
				"LAB 202 VPOS 0.12 0.71 \"Detection Significance = "+threeDigits.format(signif)+"\" CS 0.55 JUST LEFT",
				"LAB 203 VPOS 0.88 0.75 \"Number of observations = "+nGood+"\" CS 0.55 JUST RIGHT",
				"LAB 204 VPOS 0.88 0.73 \"Time series duration = "+duration+" s\" CS 0.55 JUST RIGHT",
				"LAB 205 VPOS 0.88 0.71 \"Sum of pointings = "+ontime+" s\" CS 0.55 JUST RIGHT",
				"LAB 206 VPOS 0.88 0.69 \"Exposure on target = "+effectiveExposure+" s\" CS 0.55 JUST RIGHT",
				"!"
		};
		return qdpBlock;
	}

}
