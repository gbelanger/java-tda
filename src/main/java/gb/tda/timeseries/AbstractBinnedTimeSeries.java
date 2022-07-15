package gb.tda.timeseries;

import java.util.Arrays;
import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.apache.log4j.Logger;

/**
 
 The abstract class <code>AbstractBinnedTimeSeries</code> is an object used to represent binned data 
 ordered in time. It should be viewed as a histogram whose x-axis is in units of time (seconds).
 
 The time axis is discretized into bins defined by two edges per bin; we consider that the bin width
 is constant if the variance of the bin widths is smaller than 1e-6. Gaps between bins are also defined
 by two gap edges; we consider that there is a gap in the time series if there is at least one gap that
 is longer than 1e-6 s.
 
 The Y-axis or the height of each bin shows the total number of counts per bin, and there are therefore
 no errors on the bin heights. The rates are defined as the bin height divided by the bin width.
 Upon constructing the time series, if the errors on the rates are set, then the time series is
 considered as non-Poissonian, and the errors are used to derive weights in all statistical operations.
 
 All TimeSeries instances are immutable. The constructors are package-private and used by the public classes
 TimeSeriesFactory and TimeSeriesFileReader. All the setters are private and are therefore used internally to
 define all the fields and properties of a TimeSeries instance. The getters are public and return copies of the
 internal objects like binCentres, binWidths, and intensities for example. There are no static class variables
 other than the logger, and so all are instance variables.
 
 @author G.Belanger, ESA/ESAC

**/

public abstract class AbstractBinnedTimeSeries extends AbstractTimeSeries implements ITimeSeries, IBinnedTimeSeries {

    private static Logger logger = Logger.getLogger(AbstractBinnedTimeSeries.class);
    
    // bins
    int nBins;
    private double[] binEdges;
    private double[] leftBinEdges;
    private double[] rightBinEdges;
    private double sumOfBinWidths = Double.NaN;
    private double ontime = Double.NaN;
    private double sumOfSquaredBinWidths = Double.NaN;
    private double[] binCentres;
    private double[] binWidths;
    private double[] halfBinWidths;
    private boolean binWidthIsConstant = false;
    private double minBinWidth = Double.NaN;
    private double maxBinWidth = Double.NaN;
    private double avgBinWidth = Double.NaN;
    private double varianceInBinWidths = Double.NaN;
    private double meanDeviationInBinWidths = Double.NaN;

    // gaps
    int nGaps;
    private double[] gapEdges;
    private double[] gapLengths;
    private double minGap;
    private double maxGap;
    private double meanGap;
    private double sumOfGaps;
    private boolean thereAreGaps = false;
    private int nSamplingFunctionBins;
    private double[] samplingFunctionValues;
    private double[] samplingFunctionBinEdges;
    
    //  Constructors (Package-private)
    AbstractBinnedTimeSeries() {}

    AbstractBinnedTimeSeries(IBinnedTimeSeries ts) {
        setBinEdges(ts.tStart(), ts.getBinEdges());
        setIntensities(ts.getIntensities());
        if (ts.uncertaintiesAreSet()) {
            setUncertainties(ts.getUncertainties());
        }
    }

    AbstractBinnedTimeSeries(double[] binEdges, double[] intensities) {
        setBinEdges(binEdges[0], binEdges);
        setIntensities(intensities);
    }

    AbstractBinnedTimeSeries(double tstart, double[] binEdges, double[] intensities) {
        setBinEdges(tstart, binEdges);
        setIntensities(intensities);
    }

    AbstractBinnedTimeSeries(double[] binEdges, double[] intensities, double[] uncertainties) {
        setBinEdges(binEdges[0], binEdges);
        setIntensities(intensities);
        setUncertainties(uncertainties);
    }

    AbstractBinnedTimeSeries(double tStart, double[] binEdges, double[] intensities, double[] uncertainties) {
        setBinEdges(tStart, binEdges);
        setIntensities(intensities);
        setUncertainties(uncertainties);
    }
    
    //  Setters
    void setBinEdges(double tStart, double[] binEdges) {
    	// binEdges are defined wrt tStart
    	binEdges = Utils.resetToZero(binEdges);
        this.binEdges = new double[binEdges.length];
        this.leftBinEdges = new double[binEdges.length/2];
        this.rightBinEdges = new double[binEdges.length/2];
        this.nBins = binEdges.length/2;
        this.nElements = this.nBins;
        for (int i=0; i < this.nBins; i++) {
            this.binEdges[2*i] = binEdges[2*i];
            this.binEdges[2*i+1] = binEdges[2*i+1];
            this.leftBinEdges[i] = binEdges[2*i];
            this.rightBinEdges[i] = binEdges[2*i+1];
        }
        this.tStart = tStart;
        this.duration = this.binEdges[this.binEdges.length-1] - this.binEdges[0];
        this.tStop = this.tStart + this.duration;
        this.tMid = (this.tStart + this.tStop)/2;
        logger.info("TimeSeries has "+this.nBins+" bins");
        logger.info("  TStart = "+this.tStart);
        logger.info("  TStop = "+this.tStop);
        logger.info("  Duration = "+this.duration);
        this.binCentres = new double[this.nBins];
        this.binWidths = new double[this.nBins];
        this.halfBinWidths = new double[this.nBins];
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        double sumOfBinWidths = 0;
        double sumOfSquaredBinWidths = 0;
        for (int i=0; i < this.nBins; i++) {
            this.binCentres[i] = (this.binEdges[2*i] + this.binEdges[2*i+1])/2;
            this.binWidths[i] = this.binEdges[2*i+1] - this.binEdges[2*i];
            this.halfBinWidths[i] = this.binWidths[i]/2.0;
            min = Math.min(min, binWidths[i]);
            max = Math.max(max, binWidths[i]);
            sumOfBinWidths += binWidths[i];
            sumOfSquaredBinWidths += binWidths[i] * binWidths[i];
        }
        this.times = Arrays.copyOf(this.binCentres, this.binCentres.length);
        this.minBinWidth = min;
        this.maxBinWidth = max;
    	this.sumOfBinWidths = sumOfBinWidths;
        this.sumOfSquaredBinWidths = sumOfSquaredBinWidths;
    	logger.info("  Sum of bin widths = "+this.sumOfBinWidths);
    	this.avgBinWidth = sumOfBinWidths/this.nBins;
        //  Check if bin width is constant, excluding the last bin
        double[] widths = getBinWidths();
        double[] w = new double[widths.length-1];
        for (int i=0; i < widths.length-1; i++) {
            w[i] = widths[i];
        }
        double var = Stats.getVariance(w);
        if (var < 1e-10 || Double.isNaN(var)) {
            this.binWidthIsConstant = true;
            logger.info("  Bin width is constant = "+this.binWidths[0]);
        }
        else {
            this.binWidthIsConstant = false;
            logger.warn("  Bin width is not constant");
            logger.info("  Min bin width = "+this.minBinWidth);
            logger.info("  Max bin width = "+this.maxBinWidth);
            logger.info("  Average bin width = "+this.avgBinWidth);
        }
        // Define gapEdges and sampling function
        this.gapEdges = new double[2*(this.nBins-1)];
        this.gapLengths = new double[this.nBins-1];
        DoubleArrayList samplingFuncValuesList = new DoubleArrayList();
        DoubleArrayList samplingFuncEdgesList = new DoubleArrayList();
        samplingFuncEdgesList.add(this.binEdges[0]);
        samplingFuncEdgesList.add(this.binEdges[1]);
        samplingFuncValuesList.add(1); // time series never starts with a gap, because if there is one, we take it out
        double minGap = Double.MAX_VALUE;
        double maxGap = -Double.MAX_VALUE;
        int nGaps = 0;
        double sumOfGaps = 0;
        for (int i=1; i < this.nBins; i++) {
            double gap = this.binEdges[2*i] - this.binEdges[2*i-1];
            if (gap > Math.ulp(2*this.binEdges[2*i])) {
                nGaps++;
                sumOfGaps += gap;
                samplingFuncEdgesList.add(this.binEdges[2*i-1]);
                samplingFuncEdgesList.add(this.binEdges[2*i]);
                samplingFuncValuesList.add(0);
            }
            samplingFuncEdgesList.add(this.binEdges[2*i]);
            samplingFuncEdgesList.add(this.binEdges[2*i+1]);
            samplingFuncValuesList.add(1);
            minGap = Math.min(minGap, gap);
            maxGap = Math.max(maxGap, gap);
            this.gapLengths[i-1] = gap;
            this.gapEdges[2*(i-1)] = this.binEdges[2*i-1];
            this.gapEdges[2*(i-1)+1] = this.binEdges[2*i];
        }
        if (maxGap > Math.ulp(2*this.binEdges[binEdges.length-1])) {
            this.thereAreGaps = true;
            this.nGaps = nGaps;
            this.sumOfGaps = sumOfGaps;
            this.meanGap = sumOfGaps/nGaps;
            this.maxGap = maxGap;
            this.minGap = minGap;
            logger.warn("There are "+nGaps+" gaps in timeline");
            logger.info("  Total gap time = "+sumOfGaps);
            logger.info("  Gap fraction wrt duration = "+(sumOfGaps/this.duration));
            logger.info("  Mean gap = "+meanGap);
            logger.info("  Max gap = "+maxGap);
        }
        else {
            this.thereAreGaps = false;
            this.nGaps = 0;
            this.sumOfGaps = 0;
            this.meanGap = 0;
            this.maxGap = 0;
            this.minGap = 0;
            logger.info("No gaps in timeline");
        }
        samplingFuncValuesList.trimToSize();
        samplingFuncEdgesList.trimToSize();
        this.samplingFunctionValues = samplingFuncValuesList.elements();
        this.samplingFunctionBinEdges = samplingFuncEdgesList.elements();
        this.nSamplingFunctionBins = (this.samplingFunctionValues).length;
        logger.info("Sampling function is defined");
        logger.info("  nZeros = "+this.nGaps);
        logger.info("  nOnes = "+this.nBins);
    }

    private void setStatsOnBins() {
        this.timeAtMinIntensity = this.binCentres[Utils.getIndex(this.minIntensity(), this.intensities)];
        this.timeAtMaxIntensity = this.binCentres[Utils.getIndex(this.maxIntensity(), this.intensities)];
        this.varianceInBinWidths = Descriptive.sampleVariance(this.nNonNaNs, this.sumOfBinWidths, this.sumOfSquaredBinWidths);
        this.meanDeviationInBinWidths = Descriptive.meanDeviation(new DoubleArrayList(this.binWidths), this.avgBinWidth);
    }

    //  Public methods
	
    //  About Bins
    public int nBins() { return this.nBins; }
    public double tStart() { return this.tStart; }
    public double tStop() { return this.tStop; }
    public double tMid() { return this.tMid; }
    public double duration() { return this.duration; }
    public double[] getBinCentres() { return Arrays.copyOf(this.binCentres, this.binCentres.length); }
    // Convenience method that returns binCentres
    public double[] getTimes() { return Arrays.copyOf(this.binCentres, this.binCentres.length); }
    public double[] getBinWidths() { return Arrays.copyOf(this.binWidths, this.binWidths.length); }
    public double[] getHalfBinWidths() { return Arrays.copyOf(this.halfBinWidths, this.halfBinWidths.length); }
    public double[] getBinEdges() { return Arrays.copyOf(this.binEdges, this.binEdges.length); }
    public double[] getLeftBinEdges() { return Arrays.copyOf(this.leftBinEdges, this.leftBinEdges.length); }
    public double[] getRightBinEdges() { return Arrays.copyOf(this.rightBinEdges, this.rightBinEdges.length); }
    public double binCentreAtMinIntensity() { return this.timeAtMinIntensity; }
    public double binCentreAtMaxIntensity() { return this.timeAtMaxIntensity; }
    public double minBinWidth() { return this.minBinWidth; }
    public double maxBinWidth() { return this.maxBinWidth; }
    public double avgBinWidth() { return this.avgBinWidth; }
    public double binWidth() throws TimeSeriesException {
        if (!this.binWidthIsConstant) {
            throw new TimeSeriesException("BinWidth is not constant. Use getBinWidths()");
        }
        return binWidths[0];
    }
    public double sumOfBinWidths() { return this.sumOfBinWidths; }
    public double varianceInBinWidths() { return this.varianceInBinWidths; }
    public double meanDeviationInBinWidths() { return this.meanDeviationInBinWidths; }
    public void setOntime(double ontime) {
        this.ontime = ontime;
    }
    public double ontime() { return this.sumOfBinWidths; }

    //  About Gaps
    public int nGaps() { return this.nGaps; }
    public double[] getGapEdges() { return Arrays.copyOf(this.gapEdges, this.gapEdges.length); }
    public double[] getGapLengths() { return Arrays.copyOf(this.gapLengths, this.gapLengths.length); }
    public double meanGap() { return this.meanGap; }
    public double minGap() { return this.minGap; }
    public double maxGap() { return this.maxGap; }
    public double sumOfGaps() { return this.sumOfGaps; }
    public int nSamplingFunctionBins() { return this.nSamplingFunctionBins; }
    public double[] getSamplingFunctionValues() { return Arrays.copyOf(this.samplingFunctionValues, this.samplingFunctionValues.length); }
    public double[] getSamplingFunctionBinEdges() { return Arrays.copyOf(this.samplingFunctionBinEdges, this.samplingFunctionBinEdges.length); }
        
    //  Boolean checkers
    public boolean binWidthIsConstant() { return this.binWidthIsConstant; }
    public boolean thereAreGaps() { return this.thereAreGaps; }
    
    //  Write as QDP
    // public void writeCountsAsQDP(String filename) throws IOException {
	// QDPTimeSeriesFileWriter.writeCounts(this, filename);
    // }
    // public void writeCountsAsQDP(double[] function, String filename) throws IOException, TimeSeriesFileFormatException {
	// QDPTimeSeriesFileWriter.writeCounts(this, function, filename);
    // }
    // public void writeCountsAndSamplingAsQDP(String filename) throws IOException, TimeSeriesFileFormatException {
    // 	QDPTimeSeriesFileWriter.writeCountsAndSampling(this, filename);
    // }
    // public void writeRatesAsQDP(String filename) throws IOException {
    // 	QDPTimeSeriesFileWriter.writeRates(this, filename);
    // }
    // public void writeRatesAsQDP(double[] function, String filename) throws IOException, TimeSeriesFileFormatException {
    // 	QDPTimeSeriesFileWriter.writeRates(this, function, filename);
    // }
    // public void writeRatesAndSamplingAsQDP(String filename) throws IOException, TimeSeriesFileFormatException {
    // 	QDPTimeSeriesFileWriter.writeRatesAndSampling(this, filename);
    // }

    // //  Write as Fits
    // public void writeCountsAsFits(String filename) throws IOException, FitsException {
    // 	FitsTimeSeriesFileWriter.writeCounts(this, filename);
    // }
    // public void writeCountsAsFits(double[] function, String filename) throws IOException, FitsException {
    // 	FitsTimeSeriesFileWriter.writeCounts(this, function, filename);
    // }
    // public void writeCountsAndSamplingAsFits(String filename) throws IOException, FitsException {
    // 	FitsTimeSeriesFileWriter.writeCountsAndSampling(this, filename);
    // }
    // public void writeRatesAsFits(String filename) throws IOException, FitsException {
    // 	FitsTimeSeriesFileWriter.writeRates(this, filename);
    // }
    // public void writeRatesAsFits(double[] function, String filename) throws IOException, FitsException {
    // 	FitsTimeSeriesFileWriter.writeRates(this, function, filename);
    // }
    // public void writeRatesAndSamplingAsFits(String filename) throws IOException, FitsException {
    // 	FitsTimeSeriesFileWriter.writeRatesAndSampling(this, filename);
    // }

    
    // //  Write as JS
    // public void writeCountsAsJS(String filename) throws IOException {
	// JSTimeSeriesFileWriter.writeCounts(this, filename);
    // }

    // public void writeCountsAsJS(double[] function, String filename) throws IOException, TimeSeriesFileFormatException {
    //  	JSTimeSeriesFileWriter.writeCounts(this, function, filename);
    // }
    // public void writeCountsAndSamplingAsJS(String filename) throws IOException, TimeSeriesFileFormatException {
    //     JSTimeSeriesFileWriter.writeCountsAndSampling(this, filename);
    // }
    // public void writeRatesAsJS(String filename) throws IOException {
    // 	JSTimeSeriesFileWriter.writeRates(this, filename);
    // }
    // public void writeRatesAsJS(double[] function, String filename) throws IOException {
    // 	JSTimeSeriesFileWriter.writeRates(this, function, filename);
    // }
    // public void writeRatesAndSamplingAsJS(String filename) throws IOException, TimeSeriesFileFormatException {
    //  	JSTimeSeriesFileWriter.writeRatesAndSampling(this, filename);
    // }
    

    // public void writeRatesAsPLT(String filename) throws IOException {
    // 	TimeSeriesWriter.writeRatesAsQDP(this, filename);
    // }
    
    // public void writeRatesAsXML(String filename) throws IOException {
    // 	TimeSeriesWriter.writeRatesAsQDP(this, filename);
    // }


}
