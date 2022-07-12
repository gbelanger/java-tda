package gb.tda.timeseries;

import java.awt.geom.Point2D;

/**
 Interface <code>IAstroTimeSeries</code> extends both 
 <code>ICountsTimeSeries</code> and <code>IRatesTimeSeries</code>, and
 adds things that are specific to Astronomical time series.

 @author G. Belanger
**/

public interface IAstroTimeSeries extends ICountsTimeSeries, IRatesTimeSeries {

    //  Attributes
    public String telescope();
    public void setTelescope(String telescope);
    public String instrument();
    public void setInstrument(String instrument);
    public double mjdref();
    public void setMJDREF(double mjdref);
    public String targetName();
    public void setTargetName(String targetName);
    public double targetRA();
    public double targetDec();
    public void setTargetRaDec(double ra, double dec);
    public Point2D.Double targetRaDec();
    public double energyRangeMin();
    public double energyRangeMax();
    public void setEnergyRange(double emin, double emax);
    public Point2D.Double energyRange();
    public double relTimeError();
    public void setRelTimeError(double relTimeError);
    public double absTimeError();
    public void setAbsTimeError(double absTimeError);
    public double livetime(); // exposure time after deadtime correction
    public void setLivetime(double livetime);
    public double exposureOnTarget();
    public void setExposureOnTarget(double exposureOnTarget);
    public String dateStart();
    public String dateEnd();
    public String timeStart();
    public String timeStop();

    //  Boolean checkers
    public boolean isCountsTimeSeries();
    public boolean isRatesTimeSeries();
    public boolean telescopeIsSet();
    public boolean instrumentIsSet();
    public boolean mjdrefIsSet();
    public boolean targetNameIsSet();
    public boolean targetRaDecAreSet();
    public boolean energyRangeIsSet();
    public boolean dateRangeIsSet();
    public boolean timeRangeIsSet();
    public boolean relTimeErrorIsSet();
    public boolean absTimeErrorIsSet();

}
