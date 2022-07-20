package gb.tda.timeseries;

import java.io.IOException;
import java.util.Arrays;
import java.io.FileWriter;
import java.awt.geom.Point2D;

import cern.jet.stat.Descriptive;
import cern.colt.list.DoubleArrayList;
import nom.tam.fits.FitsException;
import org.apache.log4j.Logger;

/** 
 Abstract class <code>AbstractTimeSeries</code> implements <code>ITimeSeries</code>.

 The most basic time series is a set of time-stamped measurements.
 The quantity measured, which we refer to as intensity, can be anything, 
 and the time stamps can be arbitrarily arranged. 

 The notion of grouping measurements or binning is not needed.
 But intensities can have uncertainties.

 @author G. Belanger
**/

public abstract class AbstractTimeSeries implements ITimeSeries {

    private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractTimeSeries.class);

    // // Attributes
    // private String timeStart = "";
    // private String timeStop = "";
    // private String dateStart = "";
    // private String dateEnd = "";
    
    // times
    String timeUnit = "s";
    double[] times;
    double tStart = Double.NaN;
    double tStop = Double.NaN;
    double tMid = Double.NaN;
    double duration = Double.NaN;
    double timeAtMinIntensity = Double.NaN;
    double timeAtMaxIntensity = Double.NaN;
    double timeAtMinUncertainty = Double.NaN;
    double timeAtMaxUncertainty = Double.NaN;

    // intensities
    int nElements;
    double[] intensities;
    private double minIntensity = Double.NaN;
    private double maxIntensity = Double.NaN;
    private double meanIntensity = Double.NaN;
    private double errorOnMeanIntensity = Double.NaN;
    private double weightedMeanIntensity = Double.NaN;
    private double errorOnWeightedMeanIntensity = Double.NaN;
    private double sumOfIntensities = Double.NaN;
    private double sumOfSquaredIntensities = Double.NaN;
    private double varianceInIntensities = Double.NaN;
    private double meanDeviationInIntensities = Double.NaN;
    private double skewnessInIntensities = Double.NaN;
    private double skewnessStandardError = Double.NaN;
    private double kurtosisInIntensities = Double.NaN;
    private double kurtosisStandardError = Double.NaN;
    boolean thereAreNaNs = false;
    int nNaNs;
    int nNonNaNs;

    // uncertainties
    private double[] uncertainties;
    private double[] weights;
    private double minUncertainty = Double.NaN;
    private double maxUncertainty = Double.NaN;
    private double meanUncertainty = Double.NaN;
    private double sumOfUncertainties = Double.NaN;
    private double sumOfWeights = Double.NaN;
    private double sumOfSquaredUncertainties = Double.NaN;
    private double varianceInUncertainties = Double.NaN;
    private double errorOnMeanUncertainty = Double.NaN;
    private double meanDeviationInUncertainties = Double.NaN;
    boolean uncertaintiesAreSet = false;

    
    //  Constructors (package-private)
    AbstractTimeSeries() {}

    AbstractTimeSeries(ITimeSeries ts) {
        setTimes(ts.tStart(), ts.getTimes());
        setIntensities(ts.getIntensities());
        if (ts.uncertaintiesAreSet()) {
            setUncertainties(ts.getUncertainties());
        }
    }

    AbstractTimeSeries(double[] times, double[] intensities) {
        setTimes(times);
        setIntensities(intensities);
    }

    AbstractTimeSeries(double tstart, double[] times, double[] intensities) {
        setTimes(tstart, times);
        setIntensities(intensities);
    }

    AbstractTimeSeries(double[] times, double[] intensities, double[] uncertainties) {
        setTimes(times);
        setIntensities(intensities);
        setUncertainties(uncertainties);
    }

    AbstractTimeSeries(double tstart, double[] times, double[] intensities, double[] uncertainties) {
        setTimes(tstart, times);
        setIntensities(intensities);
        setUncertainties(uncertainties);
    }

    // info-printing
    void printTimesInfo() {
        logger.info("TimeSeries has "+this.nElements+" time values");
        logger.info("  TStart = "+this.tStart);
        logger.info("  TStop = "+this.tStop);
        logger.info("  Duration = "+this.duration);
    }
    void printIntensityInfo() {
        logger.info("Intensities are defined");
        logger.info("  Mean = "+this.meanIntensity);
        logger.info("  Min = "+this.minIntensity);
        logger.info("  Max = "+this.maxIntensity);
        logger.info("  Variance = "+this.varianceInIntensities);
    }
    void printUncertaintyInfo() {
        logger.info("Uncertainties are defined");
        logger.info("  Mean = "+this.meanUncertainty);
        logger.info("  Min = "+this.minUncertainty);
        logger.info("  Max = "+this.maxUncertainty);
        logger.info("  Variance = "+this.varianceInUncertainties);
        logger.info("  Weighted mean intensity = "+this.weightedMeanIntensity);
        logger.info("  Error on weighted mean = "+this.errorOnWeightedMeanIntensity);
    }
    
    //  Setters
    void setTimes(double[] times) {
        double tStart = times[0];
        setTimes(tStart, times);
    }

    void setTimes(double tStart, double[] times) {
        this.tStart = tStart;
        this.nElements = times.length;
    	this.times = Utils.resetToZero(times);
        this.duration = this.times[times.length-1];
        this.tStop = this.tStart + this.duration;
        this.tMid = (this.tStart + this.tStop)/2;
        printTimesInfo();
    }
    
    public void setIntensities(double[] intensities) {
        this.intensities = new double[this.nElements];
        double minIntensity = Double.MAX_VALUE;
        double maxIntensity = -Double.MAX_VALUE;
        double sumOfIntensities = 0;
        double sumOfSquaredIntensities = 0;
        int nNaNs = 0;
        int nNonNaNs = 0;
        for (int i=0; i < this.nElements; i++) {
            this.intensities[i] = intensities[i];
            if (Double.isNaN(this.intensities[i])) {
                //logger.warn("NaN encountered in Intensities: index "+i+". Excluding from calculations");
                nNaNs++;
                this.thereAreNaNs = true;
            }
            else {
                minIntensity = Math.min(minIntensity, this.intensities[i]);
                maxIntensity = Math.max(maxIntensity, this.intensities[i]);
                sumOfIntensities += this.intensities[i];
                sumOfSquaredIntensities += this.intensities[i] * this.intensities[i];
                nNonNaNs++;
            }
        }
        this.nNonNaNs = nNonNaNs;
    	this.nNaNs = nNaNs;
        this.minIntensity = minIntensity;
        this.maxIntensity = maxIntensity;
        this.sumOfIntensities = sumOfIntensities;
        this.sumOfSquaredIntensities = sumOfSquaredIntensities;
        setStatsOnIntensities();
        printIntensityInfo();
    }

    private void setStatsOnIntensities() {
        this.timeAtMinIntensity = this.times[Utils.getIndex(this.minIntensity, this.intensities)];
        this.timeAtMaxIntensity = this.times[Utils.getIndex(this.maxIntensity, this.intensities)];
        this.meanIntensity = this.sumOfIntensities / this.nNonNaNs;
        this.varianceInIntensities = Descriptive.sampleVariance(this.nNonNaNs, this.sumOfIntensities, this.sumOfSquaredIntensities);
        this.errorOnMeanIntensity = Math.sqrt(this.varianceInIntensities / this.nNonNaNs);
        this.meanDeviationInIntensities = Descriptive.meanDeviation(new DoubleArrayList(this.intensities), this.meanIntensity);
        this.skewnessInIntensities = Descriptive.sampleSkew(new DoubleArrayList(this.intensities), this.meanIntensity, this.varianceInIntensities);
        this.skewnessStandardError = Descriptive.sampleSkewStandardError(this.nElements);
        this.kurtosisInIntensities = Descriptive.sampleKurtosis(new DoubleArrayList(this.intensities), this.meanIntensity, this.varianceInIntensities);
        this.kurtosisStandardError = Descriptive.sampleKurtosisStandardError(this.nElements);
    }

    public void setUncertainties(double[] uncertainties) {
        this.uncertainties = new double[this.nElements];
        this.weights = new double[this.nElements];
        double minUncertainty = Double.MAX_VALUE;
        double maxUncertainty = -Double.MAX_VALUE;
        double meanUncertainty;
        double sumOfUncertainties = 0;
        double sumOfWeights = 0;
        double sumOfSquaredUncertainties = 0;  
        for (int i=0; i < this.nElements; i++) {
            this.uncertainties[i] = uncertainties[i];
            if (Double.isNaN(this.intensities[i])) {
                this.uncertainties[i] = Double.NaN;
            }
            else {
                if (Double.isNaN(uncertainties[i])) {
                    logger.warn("There is a NaN uncertainty whose corresponding intensity is not NaN. Applying uncertainty from previous element.");
                    this.uncertainties[i] = this.uncertainties[i-1];
                }
                minUncertainty = Math.min(minUncertainty, this.uncertainties[i]);
                maxUncertainty = Math.max(maxUncertainty, this.uncertainties[i]);
                sumOfUncertainties += this.uncertainties[i];
                sumOfSquaredUncertainties += this.uncertainties[i]*this.uncertainties[i];
                this.weights[i] = 1. / Math.pow(this.uncertainties[i], 2);
                sumOfWeights += this.weights[i];
            }
        }
        this.minUncertainty = minUncertainty;
        this.maxUncertainty = maxUncertainty;
        this.sumOfUncertainties = sumOfUncertainties;
        this.sumOfSquaredUncertainties = sumOfSquaredUncertainties;
        this.sumOfWeights = sumOfWeights;
        this.uncertaintiesAreSet = true;
        setStatsOnUncertainties();
        printUncertaintyInfo();
    }
    
    private void setStatsOnUncertainties() {
        this.weightedMeanIntensity = this.meanIntensity;	    
    	if (this.uncertaintiesAreSet) {
    	    this.weightedMeanIntensity = Descriptive.weightedMean(new DoubleArrayList(this.intensities), new DoubleArrayList(this.weights));
    	}
        this.errorOnWeightedMeanIntensity = Math.sqrt(1. / this.sumOfWeights);
        this.timeAtMinUncertainty = this.times[Utils.getIndex(this.minUncertainty, this.uncertainties)];
        this.timeAtMaxUncertainty = this.times[Utils.getIndex(this.maxUncertainty, this.uncertainties)];
        this.meanUncertainty = this.sumOfUncertainties/this.nNonNaNs;
        this.varianceInUncertainties = Descriptive.sampleVariance(this.nNonNaNs, this.sumOfUncertainties, this.sumOfSquaredUncertainties);
        this.errorOnMeanUncertainty = Math.sqrt(this.varianceInUncertainties/this.nNonNaNs);
        this.meanDeviationInUncertainties = Descriptive.meanDeviation(new DoubleArrayList(this.uncertainties), this.meanUncertainty);
    }

    //  Public methods

    //  About Times
    @Override public String timeUnit() { return new String(this.timeUnit); }
    @Override public void setTimeUnit(String timeUnit) { this.timeUnit = timeUnit; }
    @Override public int nElements() { return this.nElements; }
    @Override public double tStart() { return this.tStart; }
    @Override public double tStop() { return this.tStop; }
    public double tMid() { return this.tMid; }
    @Override public double duration() { return this.duration; }
    @Override public double[] getTimes() { return Arrays.copyOf(this.times, this.times.length); }
    public double timeAtMinIntensity() { return this.timeAtMinIntensity; }
    public double timeAtMaxIntensity() { return this.timeAtMaxIntensity; }
        
    //  About Intensities
    @Override public double[] getIntensities() { return Arrays.copyOf(this.intensities, this.intensities.length); }
    @Override public double sumOfIntensities() { return this.sumOfIntensities; }
    @Override public double minIntensity() { return this.minIntensity; }
    @Override public double maxIntensity() { return this.maxIntensity; }
    @Override public double meanIntensity() { return this.meanIntensity; }
    @Override public double errorOnMeanIntensity() { return this.errorOnMeanIntensity; }
    @Override public double weightedMeanIntensity() { return this.weightedMeanIntensity; }
    @Override public double errorOnWeightedMeanIntensity() { return this.errorOnWeightedMeanIntensity; }
    @Override public double varianceInIntensities() { return this.varianceInIntensities; }
    @Override public double meanDeviationInIntensities() { return this.meanDeviationInIntensities; }
    @Override public double skewnessInIntensities() { return this.skewnessInIntensities; }
    @Override public double skewnessStandardError() { return this.skewnessStandardError; }
    @Override public double kurtosisInIntensities() { return this.kurtosisInIntensities; }
    @Override public double kurtosisStandardError() { return this.kurtosisStandardError; }
    public double[] getMeanSubtractedIntensities() { 
        return subtractMean(this.intensities, this.meanIntensity);
    }
    double[] subtractMean(double[] data, double mean) {
        int n = data.length;
        double[] meanSubData = new double[n];
        for (int i=0; i < n; i++) {
            meanSubData[i] = data[i] - mean;
        }
        return meanSubData;
    }
    
    //  About uncertainties
    @Override public double[] getUncertainties() { 
        if (this.uncertaintiesAreSet) {
            return Arrays.copyOf(this.uncertainties, this.uncertainties.length);
        }
        else {
            logger.warn("Uncertainties are not defined. Returning null array");
            return null;
        }
    }
    @Override public double sumOfUncertainties() { return this.sumOfUncertainties; }
    @Override public double meanUncertainty() { return this.meanUncertainty; }
    @Override public double minUncertainty() { return this.minUncertainty; }
    @Override public double maxUncertainty() { return this.maxUncertainty; }
    @Override public double varianceInUncertainties() { return this.varianceInUncertainties; }
    @Override public double errorOnMeanUncertainty() { return this.errorOnMeanUncertainty; }
    @Override public double meanDeviationInUncertainties() { return this.meanDeviationInUncertainties; }

    //  Boolean checkers
    @Override public boolean thereAreNaNs() { return this.thereAreNaNs; }
    @Override public boolean uncertaintiesAreSet() { return this.uncertaintiesAreSet; }
    
    //  Write as QDP
     public void writeAsQDP(String filename) throws Exception {
         QDPTimeSeriesFileWriter.writeToFile(this, filename);
     }
    // public void writeAsQDP(double[] function, String filename) throws IOException, TimeSeriesFileFormatException {
    //     QDPTimeSeriesFileWriter.writeIntensities(this, function, filename);
    // }

    // //  Write as Fits
    // public void writeAsFits(String filename) throws IOException, FitsException {
    // 	FitsTimeSeriesFileWriter.writeIntensities(this, filename);
    // }
    // public void writeAsFits(double[] function, String filename) throws IOException, FitsException {
    // 	FitsTimeSeriesFileWriter.writeIntensities(this, function, filename);
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
