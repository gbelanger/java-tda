package gb.tda.timeseries;

import java.util.Arrays;
import java.awt.geom.Point2D;
import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.apache.log4j.Logger;

/**
 
 The class <code>AbstractAstroTimeSeries</code> extends <code>AbstractBinnedTimeSeries</code> 
 and implements <code>IAstroTimeSeries</code> 
 
 @author G. Belanger

**/

public abstract class AbstractAstroTimeSeries extends AbstractBinnedTimeSeries implements IAstroTimeSeries {

    private static Logger logger = Logger.getLogger(AbstractAstroTimeSeries.class);

    // Default time unit
    String timeUnit = "s";

    // Time series type
    private boolean isCountsTimeSeries = false;
    private boolean isRatesTimeSeries = false;
    
    // Attributes
    private String telescope = "";
    private boolean telescopeIsSet = false;
    private String instrument = "";
    private boolean instrumentIsSet = false;
    private double mjdref = Double.NaN;
    private boolean mjdrefIsSet = false;
    private double targetRA = Double.NaN;
    private double targetDec = Double.NaN;
    private Point2D.Double targetRaDec;
    private boolean targetRaDecAreSet = false;
    private String targetName = "";
    private boolean targetNameIsSet = false;
    private double energyRangeMin = Double.NaN;
    private double energyRangeMax = Double.NaN;
    private Point2D.Double energyRange;    
    private boolean energyRangeIsSet = false;
    private String dateStart = "";
    private String dateEnd = "";
    private boolean dateRangeIsSet = false;
    private String timeStart = "";
    private String timeStop = "";
    private boolean timeRangeIsSet = false;
    private double relTimeError = Double.NaN;
    private double absTimeError = Double.NaN;
    private boolean relTimeErrorIsSet = false;
    private boolean absTimeErrorIsSet = false;

    private double livetime = Double.NaN;
    private double exposureOnTarget = Double.NaN;

    public double getExposureOnTarget() {
        return exposureOnTarget;
    }

    // Equivalent rate related quantities
    private double[] equivalentRates;
    private double[] uncertaintiesOnEquivalentRates;
    private double[] weightsOnEquivalentRates;
    private double errorOnWeightedMeanEquivalentRate = Double.NaN;
    private double sumOfWeightsOnEquivalentRates = Double.NaN;
    private double minEquivalentRate = Double.NaN;
    private double maxEquivalentRate = Double.NaN;
    private double meanEquivalentRate = Double.NaN;
    private double weightedMeanEquivalentRate = Double.NaN;
    private double sumOfEquivalentRates = Double.NaN;
    private double sumOfSquaredEquivalentRates = Double.NaN;
    private double varianceInEquivalentRates = Double.NaN;
    private double errorOnMeanEquivalentRate = Double.NaN;
    private double meanDeviationInEquivalentRates = Double.NaN;
    
    // Equivalent counts related quantities only exist here
    private double[] equivalentCounts;
    private double[] uncertaintiesOnEquivalentCounts;
    private double[] weightsOnEquivalentCounts;
    private double minEquivalentCount = Double.NaN;
    private double maxEquivalentCount = Double.NaN;
    private double meanEquivalentCount = Double.NaN;
    private double errorOnMeanEquivalentCount = Double.NaN;
    private double weightedMeanEquivalentCount = Double.NaN;
    private double errorOnWeightedMeanEquivalentCount = Double.NaN;
    private double sumOfEquivalentCounts = Double.NaN;
    private double sumOfSquaredEquivalentCounts = Double.NaN;
    private double varianceInEquivalentCounts = Double.NaN;
    private double meanDeviationInEquivalentCounts = Double.NaN;
    private double sumOfWeightsOnEquivalentCounts = Double.NaN;
    
    //  Constructors
    AbstractAstroTimeSeries() {}
    
    AbstractAstroTimeSeries(IAstroTimeSeries astroTimeSeries) {
        super(astroTimeSeries);
        if (astroTimeSeries.isCountsTimeSeries()) {
            this.isCountsTimeSeries = true;
            setEquivalentRates();
        }
        else {
            this.isRatesTimeSeries = true;
            setEquivalentCounts();
        }
        init(astroTimeSeries);
     }

    AbstractAstroTimeSeries(ICountsTimeSeries countsTimeSeries) {
        super(countsTimeSeries);
        this.isCountsTimeSeries = true;
        setEquivalentRates();
        // Set default values for livetime and exposureOnTarget
        this.livetime = this.ontime();
        this.exposureOnTarget = this.ontime();
    }

    AbstractAstroTimeSeries(IRatesTimeSeries ratesTimeSeries) {
        super(ratesTimeSeries);
        this.isRatesTimeSeries = true;
        setEquivalentCounts();
        // Set default values for livetime and exposureOnTarget
        this.livetime = this.ontime();
        this.exposureOnTarget = this.ontime();
    }

    // Private methods
     private void init(IAstroTimeSeries astroTimeSeries) {
        if (astroTimeSeries.telescopeIsSet()) {
            setTelescope(astroTimeSeries.telescope());
        }
        if (astroTimeSeries.instrumentIsSet()) {
            setInstrument(astroTimeSeries.instrument());
        }
        if (astroTimeSeries.mjdrefIsSet()) {
            setMJDREF(astroTimeSeries.mjdref());
        }
        if (astroTimeSeries.targetRaDecAreSet()) {
            setTargetRaDec(astroTimeSeries.targetRA(), astroTimeSeries.targetDec());
        }
        if (astroTimeSeries.targetNameIsSet()) {
            setTargetName(astroTimeSeries.targetName());
        }
        if (astroTimeSeries.energyRangeIsSet()) {
            setEnergyRange(astroTimeSeries.energyRangeMin(), astroTimeSeries.energyRangeMax());
        }
        if (astroTimeSeries.dateRangeIsSet()) {
            setDateStartEnd(astroTimeSeries.dateStart(), astroTimeSeries.dateEnd());
        }
        if (astroTimeSeries.timeRangeIsSet()) {
            setTimeStartStop(astroTimeSeries.timeStart(), astroTimeSeries.timeStop());
        }
        if (astroTimeSeries.relTimeErrorIsSet()) {
            setRelTimeError(astroTimeSeries.relTimeError());
        }
        if (astroTimeSeries.absTimeErrorIsSet()) {
            setAbsTimeError(astroTimeSeries.absTimeError());
        }
     }

    // Equivalent rates
    void printEquivalentRatesInfo() {
        logger.info("Equivalent rates are defined");
        logger.info("  Mean = "+this.meanEquivalentRate());
        logger.info("  Min = "+this.minEquivalentRate());
        logger.info("  Max = "+this.maxEquivalentRate());
        logger.info("  Variance = "+this.varianceInEquivalentRates());
    }

    void setEquivalentRates() {
        this.equivalentRates = new double[this.nBins];
        this.uncertaintiesOnEquivalentRates = new double[this.nBins];
        this.weightsOnEquivalentRates = new double[this.nBins];
        double minRate = Double.MAX_VALUE;
        double maxRate = -Double.MAX_VALUE;
        double sumOfRates = 0;
        double sumOfSquaredRates = 0;
        double sumOfWeights = 0;
        double[] counts = this.getCounts();
        double[] binWidths = this.getBinWidths();
        double[] uncertainties = new double[this.nBins];
        if (this.uncertaintiesAreSet()) {
            uncertainties = this.getUncertainties();
        }
        for (int i = 0; i < this.nBins; i++) {
            double rate = Double.NaN;
            double uncertainty = Double.NaN;
            double weight = Double.NaN;
            if (!Double.isNaN(counts[i])) {
                rate = counts[i] / binWidths[i];
                minRate = Math.min(minRate, rate);
                maxRate = Math.max(maxRate, rate);
                sumOfRates += rate;
                sumOfSquaredRates += rate * rate;
                if (this.uncertaintiesAreSet()) {
                    uncertainty = uncertainties[i];
                }
                else {
                    uncertainty = Math.sqrt(counts[i]);
                }
                uncertainty /= binWidths[i];
                weight = 1. / Math.pow(uncertainty, 2);
                sumOfWeights += weight;
            }
            this.equivalentRates[i] = rate;
            this.uncertaintiesOnEquivalentRates[i] = uncertainty;
            this.weightsOnEquivalentRates[i] = weight;
        }
        this.minEquivalentRate = minRate;
        this.maxEquivalentRate = maxRate;
        this.sumOfEquivalentRates = sumOfRates;
        this.sumOfSquaredEquivalentRates = sumOfSquaredRates;
        this.sumOfWeightsOnEquivalentRates = sumOfWeights;
        setStatsOnRates();
    }

    void setStatsOnRates() {
        this.meanEquivalentRate = this.sumOfEquivalentRates / this.nNonNaNs;
        this.weightedMeanEquivalentRate = this.meanEquivalentRate;	    
    	if (this.uncertaintiesAreSet) {
    	    this.weightedMeanEquivalentRate = Descriptive.weightedMean(new DoubleArrayList(this.equivalentRates), new DoubleArrayList(this.weightsOnEquivalentRates));
    	}
        this.errorOnWeightedMeanEquivalentRate = Math.sqrt(1. / this.sumOfWeightsOnEquivalentRates);
        this.varianceInEquivalentRates = Descriptive.sampleVariance(this.nNonNaNs, this.sumOfEquivalentRates, this.sumOfSquaredEquivalentRates);
        this.errorOnMeanEquivalentRate = Math.sqrt(this.varianceInEquivalentRates / this.nNonNaNs);
        this.meanDeviationInEquivalentRates = Descriptive.meanDeviation(new DoubleArrayList(this.equivalentRates), this.meanEquivalentRate);
        printEquivalentRatesInfo();
    }

    // Equivalent counts
    void printEquivalentCountsInfo() {
        logger.info("Equivalent counts are defined");
        logger.info("  Mean = "+this.meanEquivalentCount());
        logger.info("  Min = "+this.minEquivalentCount());
        logger.info("  Max = "+this.maxEquivalentCount());
        logger.info("  Variance = "+this.varianceInEquivalentCounts());
    }

    void setEquivalentCounts() {
        this.equivalentCounts = new double[this.nBins];
        this.uncertaintiesOnEquivalentCounts = new double[this.nBins];
        this.weightsOnEquivalentCounts = new double[this.nBins];
        double minCount = Double.MAX_VALUE;
        double maxCount = -Double.MAX_VALUE;
        double sumOfCounts = 0;
        double sumOfSquaredCounts = 0;
        double sumOfWeights = 0;
        double[] rates = this.getRates();
        double[] binWidths = this.getBinWidths();
        double[] uncertainties = new double[this.nBins];
        if (this.uncertaintiesAreSet()) {
            uncertainties = this.getUncertainties();
        }
        for (int i = 0; i < this.nBins; i++) {
            double count = Double.NaN;
            double uncertainty = Double.NaN;
            double weight = Double.NaN;
            if (!Double.isNaN(rates[i])) {
                count = rates[i] * binWidths[i];
                minCount = Math.min(minCount, count);
                maxCount = Math.max(maxCount, count);
                sumOfCounts += count;
                sumOfSquaredCounts += count * count;
                if (this.uncertaintiesAreSet()) {
                    uncertainty = Math.sqrt(uncertainties[i] * binWidths[i]);
                }
                else {
                    uncertainty = Math.sqrt(count);
                }
                weight = 1. / Math.pow(uncertainty, 2);
                sumOfWeights += weight;
            }
            this.equivalentCounts[i] = count;
            this.uncertaintiesOnEquivalentCounts[i] = uncertainty;
            this.weightsOnEquivalentCounts[i] = weight;
        }
        this.minEquivalentCount = minCount;
        this.maxEquivalentCount = maxCount;
        this.sumOfEquivalentCounts = sumOfCounts;
        this.sumOfSquaredEquivalentCounts = sumOfSquaredCounts;
        this.sumOfWeightsOnEquivalentCounts = sumOfWeights;
        setStatsOnCounts();
    }

    void setStatsOnCounts() {
        this.meanEquivalentCount = this.sumOfEquivalentCounts/this.nNonNaNs;
        this.weightedMeanEquivalentCount = this.meanEquivalentCount;	    
    	if (this.uncertaintiesAreSet) {
    	    this.weightedMeanEquivalentCount = Descriptive.weightedMean(new DoubleArrayList(this.equivalentCounts), new DoubleArrayList(this.weightsOnEquivalentCounts));
    	}
        this.errorOnWeightedMeanEquivalentCount = Math.sqrt(1 / this.sumOfWeightsOnEquivalentCounts);
        this.varianceInEquivalentCounts = Descriptive.sampleVariance(this.nNonNaNs, this.sumOfEquivalentCounts, this.sumOfSquaredEquivalentCounts);
        this.errorOnMeanEquivalentCount = Math.sqrt(this.varianceInEquivalentCounts/this.nNonNaNs);
        this.meanDeviationInEquivalentCounts = Descriptive.meanDeviation(new DoubleArrayList(this.equivalentCounts), this.meanEquivalentCount);
        printEquivalentCountsInfo();
    }

    //  Public methods

    //  Counts 
    //// Convenience methods that map counts to intensities defined in AbstractTimeSeries
    @Override public double[] getCounts() { return this.getIntensities(); }
    @Override public double[] getMeanSubtractedCounts() { return this.getMeanSubtractedIntensities(); }
    @Override public double meanCount() { return this.meanIntensity(); }
    @Override public double minCount() { return this.minIntensity(); }
    @Override public double maxCount() { return this.maxIntensity(); }
    @Override public double errorOnMeanCount() { return this.errorOnMeanIntensity(); }
    @Override public double varianceInCounts() { return this.varianceInIntensities(); }
    @Override public double meanDeviationInCounts() { return this.meanDeviationInIntensities(); }

    //// Equivalent Rates
    @Override public double[] getEquivalentRates() { 
        return Arrays.copyOf(this.equivalentRates, this.equivalentRates.length); 
    }
    @Override public double[] getUncertaintiesOnEquivalentRates() { 
        return Arrays.copyOf(this.uncertaintiesOnEquivalentRates, this.uncertaintiesOnEquivalentRates.length);
    }
    @Override public double[] getWeightsOnEquivalentRates() { 
        return Arrays.copyOf(this.weightsOnEquivalentRates, this.weightsOnEquivalentRates.length);
    }
    public double[] getMeanSubtractedEquivalentRates() { 
        return this.subtractMean(this.equivalentRates, this.meanEquivalentRate);
    }
    @Override public double meanEquivalentRate() { return this.meanEquivalentRate; }
    @Override public double minEquivalentRate() { return this.minEquivalentRate; }
    @Override public double maxEquivalentRate() { return this.maxEquivalentRate; }
    @Override public double errorOnMeanEquivalentRate() { return this.errorOnMeanEquivalentRate ; }
    @Override public double weightedMeanEquivalentRate() { return this.weightedMeanEquivalentRate ; }
    @Override public double errorOnWeightedMeanEquivalentRate() { return this.errorOnWeightedMeanEquivalentRate ; }
    @Override public double varianceInEquivalentRates() { return this.varianceInEquivalentRates ; }
    @Override public double meanDeviationInEquivalentRates() { return this.meanDeviationInEquivalentRates ; }

    //  Rates 
    //// Convenience methods that map rates to intensities defined in AbstractTimeSeries
    public double[] getRates() { return this.getIntensities(); }
    public double[] getMeanSubtractedRates() { return this.getMeanSubtractedIntensities(); }
    public double meanRate() { return this.meanIntensity(); }
    public double minRate() { return this.minIntensity(); }
    public double maxRate() { return this.maxIntensity(); }
    public double errorOnMeanRate() { return this.errorOnMeanIntensity(); }
    public double varianceInRates() { return this.varianceInIntensities(); }
    public double meanDeviationInRates() { return this.meanDeviationInIntensities(); }
    public double weightedMeanRate() { return this.weightedMeanIntensity() ; }
    public double errorOnWeightedMeanRate() { return this.errorOnWeightedMeanIntensity() ; }

    //// Equivalent Counts
    public double[] getEquivalentCounts() { 
        return Arrays.copyOf(this.equivalentCounts, this.equivalentCounts.length); 
    }
    public double[] getUncertaintiesOnEquivalentCounts() { 
        return Arrays.copyOf(this.uncertaintiesOnEquivalentCounts, this.uncertaintiesOnEquivalentCounts.length);
    }
    public double[] getWeightsOnEquivalentCounts() { 
        return Arrays.copyOf(this.uncertaintiesOnEquivalentCounts, this.uncertaintiesOnEquivalentCounts.length);
    }
    public double[] getMeanSubtractedEquivalentCounts() { 
        return this.subtractMean(this.equivalentCounts, this.meanEquivalentCount);
    }
    public double minEquivalentCount() { return this.minEquivalentCount; }
    public double maxEquivalentCount() { return this.maxEquivalentCount; }
    public double meanEquivalentCount() { return this.meanEquivalentCount; }
    public double errorOnMeanEquivalentCount() { return this.errorOnMeanEquivalentCount ; }
    public double weightedMeanEquivalentCount() { return this.weightedMeanEquivalentCount; }
    public double errorOnWeightedMeanEquivalentCount() { return this.errorOnWeightedMeanEquivalentCount ; }
    public double varianceInEquivalentCounts() { return this.varianceInEquivalentCounts ; }
    public double meanDeviationInEquivalentCounts() { return this.meanDeviationInEquivalentCounts ; }


    ////  Telescope
    @Override public void setTelescope(String telescope) {
        this.telescope = telescope;
    	this.telescopeIsSet = true;
    }
    @Override public String telescope() {
    	if ( !this.telescopeIsSet ) {
    	    logger.warn("Telescope is not defined: Returning empty string");
    	}
    	return new String(this.telescope);
    }

    ////  Instrument
    @Override public void setInstrument(String instrument) {
        this.instrument = instrument;
    	this.instrumentIsSet = true;
    }
    @Override public String instrument() {
    	if ( !this.instrumentIsSet ) {
    	    logger.warn("Instrument is not defined: Returning empty string");
    	}
    	return new String(this.instrument);	    
    }

    ////  Mjdref
    @Override public void setMJDREF(double mjdref) {
        this.mjdref = mjdref;
    	this.mjdrefIsSet = true;
    }
    @Override public double mjdref() {
    	if ( !this.mjdrefIsSet ) {
    	    logger.warn("MJD ref is not defined: Returning Double.NaN");
    	}
    	return this.mjdref;
    }

    //// Target Name
    @Override public void setTargetName(String targetName) {
        this.targetName = targetName;
    	this.targetNameIsSet = true;
    }
    @Override public String targetName() {
    	if ( !this.targetNameIsSet ) {
    	    logger.warn("Target name is not defined: Returning empty string");
    	}
    	return new String(this.targetName);
    }

    ////  Target RA, Dec
    @Override public void setTargetRaDec(double ra, double dec) {
    	this.targetRA = ra;
    	this.targetDec = dec;
    	this.targetRaDec = new Point2D.Double(ra, dec);
    	this.targetRaDecAreSet = true;
    }
    public void setTargetRaDec(Point2D.Double raDec) {
    	setTargetRaDec(raDec.getX(), raDec.getY());
    }
    public void setTargetRaDec(double[] raDec) {
    	setTargetRaDec(raDec[0], raDec[1]);
    }
    @Override public double targetRA() {
    	if ( !this.targetRaDecAreSet ) {
    	    logger.warn("Target RA, Dec are not defined: Returning Double.NaN");
    	}
    	return this.targetRA;
    }
    @Override public double targetDec() {
    	if ( !this.targetRaDecAreSet ) {
    	    logger.warn("Target RA, Dec are not defined: Returning Double.NaN");
    	}
    	return this.targetDec;
    }
    @Override public Point2D.Double targetRaDec() {
    	if ( !this.targetRaDecAreSet ) {
    	    logger.warn("Target RA, Dec are not defined: Returning null object");
    	}
    	return this.targetRaDec;
    }

    ////  Energy range
    @Override public void setEnergyRange(double eMin, double eMax) {
    	this.energyRangeMin = eMin;
    	this.energyRangeMax = eMax;
    	this.energyRange = new Point2D.Double(eMin, eMax);
    	this.energyRangeIsSet = true;
    }
    public void setEnergyRange(Point2D.Double eMinMax) {
        setEnergyRange(eMinMax.getX(), eMinMax.getY());
    }
    public void setEnergyRange(double[] eMinMax) {
    	setEnergyRange(eMinMax[0], eMinMax[1]);
    }
    @Override public double energyRangeMin() {
    	if ( !this.energyRangeIsSet ) {
    	    logger.warn("Energy range is not defined: Returning Double.NaN");
    	}
    	return this.energyRangeMin;
    }
    @Override public double energyRangeMax() {
    	if ( !this.energyRangeIsSet ) {
    	    logger.warn("Energy range is not defined: Returning Double.NaN");
    	}
    	return this.energyRangeMax;
    }
    @Override public Point2D.Double energyRange() {
    	if ( !this.energyRangeIsSet ) {
    	    logger.warn("Energy range is not defined: Returning Double.NaN");
    	}
    	return (Point2D.Double)energyRange.clone();
    }

    ////  Dates and times of observations
    public void setDateStartEnd(String dateStart, String dateEnd) {
    	this.dateStart = new String(dateStart);
    	this.dateEnd = new String(dateEnd);
    	this.dateRangeIsSet = true;
    }

    public void setTimeStartStop(String timeStart, String timeStop) {
    	this.timeStart = new String(timeStart);
    	this.timeStop = new String(timeStop);
    	this.timeRangeIsSet = true;
    }

    ////  DATE-OBS and DATE-END
    public String dateStart() {
    	if ( !this.dateRangeIsSet ) {
    	    logger.warn("DATE-OBS not defined: Returning empty string");
    	}
    	return this.dateStart;
    }
    public String dateEnd() {
    	if ( !this.dateRangeIsSet ) {
    	    logger.warn("DATE-END not defined: Returning empty string");
    	}
    	return this.dateEnd;
    }
    public String[] dateStartEnd() {
    	if ( !this.dateRangeIsSet ) {
    	    logger.warn("DATE-OBS and DATE-END not defined: Returning empty strings");
    	}
    	return new String[] {new String(this.dateStart), new String(this.dateEnd)};
    }

    ////  TIME-OBS and TIME-END
    public String timeStart() {
    	if ( !this.timeRangeIsSet ) {
    	    logger.warn("TIME-OBS not defined: Returning empty string");
    	}
    	return this.timeStart;
    }
    public String timeStop() {
    	if ( !this.timeRangeIsSet ) {
    	    logger.warn("TIME-END not defined: Returning empty string");
    	}
    	return this.timeStop;
    }
    public String[] timeStartStop() {
    	if ( !this.timeRangeIsSet ) {
    	    logger.warn("TIME-OBS and TIME-END not defined: Returning empty strings");
    	}
    	return new String[] {new String(this.timeStart), new String(this.timeStop)};
    }

    ////  Time error
    // setters
    @Override public void setRelTimeError(double relTimeError) {
    	this.relTimeError = relTimeError;
    	this.relTimeErrorIsSet = true;
    }
    @Override public void setAbsTimeError(double absTimeError) {
    	this.absTimeError = absTimeError;
    	this.absTimeErrorIsSet = true;
    }
    public void setTimeErrors(double relTimeError, double absTimeError) {
    	this.relTimeError = relTimeError;
    	this.relTimeErrorIsSet = true;
    	this.absTimeError = absTimeError;
    	this.absTimeErrorIsSet = true;
    }

    // getters
    @Override public double relTimeError() {
        if ( !this.relTimeErrorIsSet ) {
            logger.warn("Relative time error is not set: Returning Double.NaN");
        }
        return this.relTimeError;
    }
    @Override public double absTimeError() {
        if ( !this.absTimeErrorIsSet ) {
            logger.warn("Absolute time error is not set: Returning Double.NaN");
        }
        return this.absTimeError;
    }
    public double[] getTimeErrors() {
        double relTimeError = relTimeError();
        double absTimeError = absTimeError();
        return new double[] {relTimeError, absTimeError};
    }

    @Override
    public void setLivetime(double livetime) {
        this.livetime = livetime;
    }
    @Override
    public double livetime() {
        return this.livetime;
    }

    @Override
    public void setExposureOnTarget(double exposureOnTarget) {
        this.exposureOnTarget = exposureOnTarget;
    }

    @Override
    public double exposureOnTarget() {
        return this.exposureOnTarget;
    }

    //  Boolean checkers

    @Override public boolean isCountsTimeSeries() {
        return this.isCountsTimeSeries;
    }
    @Override public boolean isRatesTimeSeries() {
        return this.isRatesTimeSeries;
    }

    @Override public boolean telescopeIsSet() {
        return this.telescopeIsSet;
    }
    @Override public boolean instrumentIsSet() {
        return this.instrumentIsSet;
    }
    @Override public boolean mjdrefIsSet() {
        return this.mjdrefIsSet;
    }
    @Override public boolean targetNameIsSet() {
        return this.targetNameIsSet;
    }
    @Override public boolean targetRaDecAreSet() {
        return this.targetRaDecAreSet;
    }

    @Override public boolean energyRangeIsSet() {
        return this.energyRangeIsSet;
    }
    @Override public boolean dateRangeIsSet() {
        return this.dateRangeIsSet;
    }
    @Override public boolean timeRangeIsSet() {
        return this.timeRangeIsSet;
    }
    @Override public boolean relTimeErrorIsSet() {
        return this.relTimeErrorIsSet;
    }
    @Override public boolean absTimeErrorIsSet() {
        return this.absTimeErrorIsSet;
    }


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

    // // //  Write as Fits
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

    
    // // //  Write as JS
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
