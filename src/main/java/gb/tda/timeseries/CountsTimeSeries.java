package gb.tda.timeseries;

import java.util.Arrays;

import cern.jet.stat.Descriptive;
import cern.colt.list.DoubleArrayList;
import org.apache.log4j.Logger;

public class CountsTimeSeries extends BinnedTimeSeries implements ICountsTimeSeries {

    private static org.apache.log4j.Logger logger = Logger.getLogger(CountsTimeSeries.class);

    // Equivalent rate related quantities only exist here
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


    CountsTimeSeries(ICountsTimeSeries ts) {
        super(ts);
        setEquivalentRates();
    }

    CountsTimeSeries(double[] binEdges, double[] intensities) {
        super(binEdges, intensities);
        setEquivalentRates();
    }

    CountsTimeSeries(double tstart, double[] binEdges, double[] intensities) {
        super(tstart, binEdges, intensities);
        setEquivalentRates();
    }

    CountsTimeSeries(double[] binEdges, double[] intensities, double[] uncertainties) {
        super(binEdges, intensities, uncertainties);
        setEquivalentRates();
    }

    CountsTimeSeries(double tstart, double[] binEdges, double[] intensities, double[] uncertainties) {
        super(tstart, binEdges, intensities, uncertainties);
        setEquivalentRates();
    }

    // info-printing
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

    private void setStatsOnRates() {
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

    // Equivalent Rates
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
    
}
