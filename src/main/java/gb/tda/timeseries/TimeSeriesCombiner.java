package gb.tda.timeseries;

import org.apache.log4j.Logger;

import java.util.Arrays;

import gb.tda.binner.BinningException;

public final class TimeSeriesCombiner {

    private static Logger logger  = Logger.getLogger(TimeSeriesCombiner.class);

    public void subtract(ITimeSeries ts, double constant) {
		logger.info("Subtracting a constant ("+constant+")");
		double[] newIntensities = new double[ts.nElements()];
		double[] oldIntensities = ts.getIntensities();
		for (int i=0; i < ts.nElements(); i++) {
			newIntensities[i] = oldIntensities[i] - constant;
		}
		ts.setIntensities(newIntensities);
    }


    public void subtract(IBinnedTimeSeries ts1, IBinnedTimeSeries ts2) throws BinningException  {
		if (ts1.nBins() != ts2.nBins()) {
			throw new BinningException("Time series have different number of bins.");
		}
		logger.info("Subtracting one IBinnedTimeSeries from another");
		//  Subtract intensities
		double[] intensities1  = ts1.getIntensities();
		double[] intensities2 = ts2.getIntensities();
		double[] subtracted = new double[ts1.nBins()];
		for (int i=0; i < ts1.nBins(); i++) {
			subtracted[i] = intensities1[i] - intensities2[i];
		}
		ts1.setIntensities(subtracted);
		//  Combine uncertainties
		if (ts1.uncertaintiesAreSet() && ts2.uncertaintiesAreSet()) {
			double[] uncertainties1 = ts1.getUncertainties();
			double[] uncertainties2 = ts1.getUncertainties();
			double[] newUncertainties = new double[ts1.nBins()];
			for (int i = 0; i < ts1.nBins(); i++) {
				newUncertainties[i] = Math.sqrt(Math.pow(uncertainties1[i], 2) + Math.pow(uncertainties2[i], 2));
			}
			ts1.setUncertainties(newUncertainties);
		}
		else {
			// If only uncertainties1 are set we don't need to do anything
			if (ts2.uncertaintiesAreSet()) {
				double[] newUncertainties = Arrays.copyOf(ts2.getUncertainties(), ts2.nBins());
				ts1.setUncertainties(newUncertainties);
			}
		}
    }

    public void add(ITimeSeries ts, double constant) {
		logger.info("Adding a constant ("+constant+")");
		double[] newIntensities = new double[ts.nElements()];
		double[] oldIntensities = ts.getIntensities();
		for (int i=0; i < ts.nElements(); i++) {
			newIntensities[i] = oldIntensities[i] + constant;
		}
		ts.setIntensities(newIntensities);
    }

    public void add(IBinnedTimeSeries ts1, IBinnedTimeSeries ts2) throws BinningException  {
		logger.info("Adding a TimeSeries");
		if (ts1.nBins() != ts2.nBins()) {
			throw new BinningException("Time series have different number of bins.");
		}
		logger.info("Subtracting one IBinnedTimeSeries from another");
		//  Subtract intensities
		double[] intensities1  = ts1.getIntensities();
		double[] intensities2 = ts2.getIntensities();
		double[] subtracted = new double[ts1.nBins()];
		for (int i=0; i < ts1.nBins(); i++) {
			subtracted[i] = intensities1[i] + intensities2[i];
		}
		ts1.setIntensities(subtracted);
		//  Combine uncertainties
		if (ts1.uncertaintiesAreSet() && ts2.uncertaintiesAreSet()) {
			double[] uncertainties1 = ts1.getUncertainties();
			double[] uncertainties2 = ts1.getUncertainties();
			double[] newUncertainties = new double[ts1.nBins()];
			for (int i = 0; i < ts1.nBins(); i++) {
				newUncertainties[i] = Math.sqrt(Math.pow(uncertainties1[i], 2) + Math.pow(uncertainties2[i], 2));
			}
			ts1.setUncertainties(newUncertainties);
		}
		else {
			// If only uncertainties1 are set we don't need to do anything
			if (ts2.uncertaintiesAreSet()) {
				double[] newUncertainties = Arrays.copyOf(ts2.getUncertainties(), ts2.nBins());
				ts1.setUncertainties(newUncertainties);
			}
		}
    }
   
    public void scale(ITimeSeries lc, double scalingFactor) {
		logger.info("Scaling TimeSeries by "+scalingFactor+"");
		double[] newIntensities = new double[lc.nElements()];
		double[] newUncertainties = new double[lc.nElements()];
		double[] intensities = lc.getIntensities();
		double[] uncertainties = lc.getUncertainties();
		for (int i=0; i < lc.nElements(); i++) {
		    newIntensities[i] = intensities[i]*scalingFactor;
			newUncertainties[i] = uncertainties[i]*scalingFactor;
		}
		lc.setIntensities(newIntensities);
		lc.setUncertainties(newUncertainties);
    }

    public double[][] combineRatesAndErrors(double[] rates1, double[] errors1, double[] rates2, double[] errors2) {
		int nCommonBins = rates1.length;
		double[] combinedRates = new double[nCommonBins];
		double[] combinedErrors = new double[nCommonBins];
		double weight1 = 0;
		double weight2 = 0;
		double sumOfWeights = 0;
		double weightedSum = 0;
		for (int i=0; i < nCommonBins; i++) {
		    weight1 = 1/Math.pow(errors1[i], 2);
		    weight2 = 1/Math.pow(errors2[i], 2);
		    sumOfWeights = weight1 + weight2;
		    weightedSum = rates1[i]*weight1 + rates2[i]*weight2;
		    combinedRates[i] = weightedSum/sumOfWeights;
		    combinedErrors[i] = 1/Math.sqrt(sumOfWeights);
		}
		return new double[][] {combinedRates, combinedErrors};
    }
}
