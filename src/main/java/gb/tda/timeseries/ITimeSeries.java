package gb.tda.timeseries;

/**
 Interface <code>ITimeSeries</code> is the top level interface for the time series package, 
 and defines the most basic attributes and methods that any time series must implement.

 @author G. Belanger
**/

public interface ITimeSeries {

    // Attributes
    int nElements();
    String timeUnit();
    void setTimeUnit(String timeUnit);
    double tStart();
    double tStop();
    double duration();

    // Times
    double[] getTimes();

    //  Intensities
    void setIntensities(double[] intensities);
    double[] getIntensities();
    double[] getMeanSubtractedIntensities();
    double sumOfIntensities();
    double minIntensity();
    double maxIntensity();
    double meanIntensity();
    double errorOnMeanIntensity();
    double weightedMeanIntensity();
    double errorOnWeightedMeanIntensity();
    double varianceInIntensities();
    double meanDeviationInIntensities();

    // Uncertainties
    void setUncertainties(double[] uncertainties);
    double[] getUncertainties();
    double sumOfUncertainties();
    double meanUncertainty();
    double minUncertainty();
    double maxUncertainty();
    double varianceInUncertainties();
    double errorOnMeanUncertainty();
    double meanDeviationInUncertainties();

    // Skewness and Kurtosis
    //// Both invariant under linear scaling
    //// skewness = third standardized moment = measure of asymmetry
    //// https://en.wikipedia.org/wiki/Skewness
    double skewnessInIntensities();
    double skewnessStandardError();
    //// kurtosis = fourth standardized moment = measure of tailedness
    //// https://en.wikipedia.org/wiki/Kurtosis
    double kurtosisInIntensities();
    double kurtosisStandardError();

    //  Boolean checkers
    boolean thereAreNaNs();
    boolean uncertaintiesAreSet();

}
