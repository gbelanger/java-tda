package gb.tda.timeseries;

import java.util.Arrays;

import cern.jet.stat.Descriptive;
import cern.colt.list.DoubleArrayList;
import org.apache.log4j.Logger;

public class RatesTimeSeries extends BinnedTimeSeries implements IRatesTimeSeries {

    private static org.apache.log4j.Logger logger = Logger.getLogger(RatesTimeSeries.class);

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

    RatesTimeSeries(IRatesTimeSeries ts) {
        super(ts);
        setEquivalentCounts();
    }

    RatesTimeSeries(double[] binEdges, double[] intensities) {
        super(binEdges, intensities);
        setEquivalentCounts();
    }

    RatesTimeSeries(double tstart, double[] binEdges, double[] intensities) {
        super(tstart, binEdges, intensities);
        setEquivalentCounts();
    }

    RatesTimeSeries(double[] binEdges, double[] intensities, double[] uncertainties) {
        super(binEdges, intensities, uncertainties);
        setEquivalentCounts();
    }

    RatesTimeSeries(double tstart, double[] binEdges, double[] intensities, double[] uncertainties) {
        super(tstart, binEdges, intensities, uncertainties);
        setEquivalentCounts();
    }

    // info-printing
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

    private void setStatsOnCounts() {
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

    // Equivalent Counts
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
    
}
