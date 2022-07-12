package gb.tda.timeseries;

/**
 This interface extends <code>ITimeSeries</code>
 with methods needed to work with bins and gaps

 @author G. Belanger
**/

public interface IBinnedTimeSeries extends ITimeSeries {

    // Bins
    public int nBins();
    public double[] getBinCentres();
    public double[] getBinWidths();
    public double[] getHalfBinWidths();
    public double[] getBinEdges();
    public double[] getLeftBinEdges();
    public double[] getRightBinEdges();
    public double minBinWidth();
    public double maxBinWidth();
    public double avgBinWidth();
    public double binWidth() throws TimeSeriesException;
    public double sumOfBinWidths();
    
    //  Gaps
    public int nGaps();
    public double[] getGapEdges();
    public double[] getGapLengths();
    public double meanGap();
    public double minGap();
    public double maxGap();
    public double sumOfGaps();
    public int nSamplingFunctionBins();
    public double[] getSamplingFunctionValues();
    public double[] getSamplingFunctionBinEdges();
    
    //  Boolean checkers
    public boolean binWidthIsConstant();
    public boolean thereAreGaps();

}
