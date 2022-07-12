package gb.tda.timeseries;

/**
 This interface extends <code>IBinnedTimeSeries</code> 
 with methods needed to handle rates and equivalent counts

 @author G. Belanger
**/

public interface IRatesTimeSeries extends IBinnedTimeSeries {
    //  Rates
    public double[] getRates();
    public double[] getMeanSubtractedRates();
    public double meanRate();
    public double minRate();
    public double maxRate();
    public double errorOnMeanRate();
    public double weightedMeanRate();
    public double errorOnWeightedMeanRate();
    public double varianceInRates();
    public double meanDeviationInRates();

    // Equivalent Counts
    public double[] getEquivalentCounts();
    public double[] getMeanSubtractedEquivalentCounts();
    public double[] getUncertaintiesOnEquivalentCounts();
    public double[] getWeightsOnEquivalentCounts();
    public double minEquivalentCount();
    public double maxEquivalentCount();
    public double meanEquivalentCount();
    public double errorOnMeanEquivalentCount();
    public double weightedMeanEquivalentCount();
    public double errorOnWeightedMeanEquivalentCount();
    public double varianceInEquivalentCounts();
    public double meanDeviationInEquivalentCounts();
}
