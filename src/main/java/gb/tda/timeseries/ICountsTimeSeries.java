package gb.tda.timeseries;

/**
 This interface extends <code>IBinnedTimeSeries</code> 
 with methods needed to handle counts and equivalent rates

 @author G. Belanger
**/

public interface ICountsTimeSeries extends IBinnedTimeSeries {
    //  Counts
    public double[] getCounts();
    public double[] getMeanSubtractedCounts();
    public double meanCount();
    public double minCount();
    public double maxCount();
    public double errorOnMeanCount();
    public double varianceInCounts();
    public double meanDeviationInCounts();

    // Equivalent Rates
    public double[] getEquivalentRates();
    public double[] getUncertaintiesOnEquivalentRates();
    public double[] getWeightsOnEquivalentRates();
    public double meanEquivalentRate();
    public double minEquivalentRate();
    public double maxEquivalentRate();
    public double errorOnMeanEquivalentRate();
    public double weightedMeanEquivalentRate();
    public double errorOnWeightedMeanEquivalentRate();
    public double varianceInEquivalentRates();
    public double meanDeviationInEquivalentRates();
}
