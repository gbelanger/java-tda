package gb.tda.timeseries;

import org.apache.log4j.Logger;

import gb.tda.binner.DensityBin;
import gb.tda.binner.Bin;
import gb.tda.binner.BinningException;
import gb.tda.binner.BinningUtils;
import gb.tda.binner.Rebinner;
import gb.tda.binner.Resampler;
import gb.tda.binner.IntensityBinResampler;
import gb.tda.utils.MinMax;


public final class TimeSeriesResampler {

    //  IMPORTANT:
    //  There is a problem when the start time is not 0
    //  Must fix this!!!

    private static Logger logger  = Logger.getLogger(TimeSeriesResampler.class);

    public static BinnedTimeSeries rebinToMinSignif(IBinnedTimeSeries ts, double minSignif) {
		logger.info("Rebinning time series to minSignif = "+minSignif);
		double[][] binEdgesIntensitiesUncertainties = Rebinner.rebinToMinSignif(ts.getIntensities(), ts.getUncertainties(), ts.getBinEdges(), minSignif);
		double[] newBinEdges = binEdgesIntensitiesUncertainties[0];
		double[] rebinnedIntensities = binEdgesIntensitiesUncertainties[1];
		double[] rebinnedUncertainties = binEdgesIntensitiesUncertainties[2];
		return new BinnedTimeSeries(ts.tStart(), newBinEdges, rebinnedIntensities, rebinnedUncertainties);
    }

    public static BinnedTimeSeries resampleToClosestPowerOfTwo(IBinnedTimeSeries ts) throws BinningException {
		int nLCBins = ts.nBins();
		double n = Math.log(nLCBins) / Math.log(2);
		double diff = n - Math.floor(n);
		BinnedTimeSeries bts;
		if (diff == 0) { 
		    if (ts.binWidthIsConstant()) {
				logger.info("Number of bins already is a power of 2: Returning copy of TimeSeries");
				bts = new BinnedTimeSeries(ts);
		    }
		    else {
				logger.info("Resampling TimeSeries to closest power of 2");
				int nBins = (int) Math.pow(2, n);
				bts = resample(ts, nBins);
		    }
		}
		else {
		    logger.info("Resampling TimeSeries to closest power of 2");
		    double powerOfTwo = Math.ceil(n);
		    powerOfTwo = Math.floor(n);
		    int nBins = (int) Math.pow(2, powerOfTwo);
		    bts = resample(ts, nBins);
		}
		return bts;
    }
    
    public static BinnedTimeSeries resample(IBinnedTimeSeries ts, int nNewBins) throws BinningException {
		logger.info("Resampling TimeSeries to nNewBins = "+nNewBins);
		double newBinWidth = ts.duration() / nNewBins;
		return resample(ts, newBinWidth);
    }

    public static BinnedTimeSeries resample(IBinnedTimeSeries ts, double newBinWidth) throws BinningException {
		logger.info("Resampling TimeSeries to newBinWidth = "+newBinWidth);
		double[] newBinEdges = BinningUtils.getBinEdges(0, ts.duration(), newBinWidth);
		return resample(ts, newBinEdges);
    }

    public static BinnedTimeSeries resample(IBinnedTimeSeries ts, double[] newBinEdges) throws BinningException {
		logger.info("Resampling TimeSeries using defined binEdges");

		////  This is the old version of the code using Resampler.java
		//double[][] ratesAndErrors =
		// Resampler.resample(ts.getIntensities(), ts.getUncertainties(), ts.getBinEdges(), newBinEdges);
		//double[] rebinnedIntensities = ratesAndErrors[0];
		//double[] rebinnedUncertainties = ratesAndErrors[1];
		////  Up to here

		//  This is the new version of the code using BinResampler.java
		//  Construct Bin[] for the new bins
		int nnewBins = newBinEdges.length / 2;
		Bin[] newBins = new Bin[nnewBins];
		for (int i = 0; i < nnewBins; i++) {
		    double leftEdge = newBinEdges[2*i];
		    double rightEdge = newBinEdges[2*i+1];
		    newBins[i] = new Bin(leftEdge, rightEdge);
		}
		
		//  Construct IntensityBin[] for the old bins
		int nOldBins = ts.nBins();
		double[] leftEdges = ts.getLeftBinEdges();
		double[] rightEdges = ts.getRightBinEdges();
		double[] intensities = ts.getIntensities();
		double[] uncertainties = ts.getUncertainties();
		DensityBin[] oldIntensityBins = new DensityBin[nOldBins];
		for (int i = 0; i < nOldBins; i++) {
		    oldIntensityBins[i] = new DensityBin(leftEdges[i],rightEdges[i],intensities[i],uncertainties[i]);
		}
		
		// Resample and define return values
		DensityBin[] newIntensityBins = IntensityBinResampler.resample(oldIntensityBins, newBins);
		double[] rebinnedIntensities = new double[nnewBins];
		double[] rebinnedUncertainties = new double[nnewBins];
		for (int i = 0; i < nnewBins; i++) {
		    rebinnedIntensities[i] = newIntensityBins[i].getValue();
		    rebinnedUncertainties[i] = newIntensityBins[i].getError();
		}		
		return new BinnedTimeSeries(ts.tStart(), newBinEdges, rebinnedIntensities, rebinnedUncertainties);
    }


    public static double[] getRateFromTo(IBinnedTimeSeries ts, double t1, double t2) {
		//  Get data from TimeSeries
		double[] edges = ts.getBinEdges();
		double[] oldBinEdges = Utils.shift(edges, ts.tStart());
		int nOldBins = ts.nBins();
		double tstop = oldBinEdges[oldBinEdges.length - 1];
		double[] intensities = ts.getIntensities();
		double[] uncertainties = ts.getUncertainties();
		//   Initialize variables
		double counts = 0;
		double errorCounts = 0;
		double exposure = 0;
		double effNewBinTime = 0;
		//   Determine where we are in the TimeSeries
		//  There's a bug here somewhere with the identification of the bin index. No time to look into it now.
		//System.out.println(t1);
		int binEdgeIndex = Utils.getClosestIndexInSortedData(t1, oldBinEdges);   
		int k = (int) Math.floor(binEdgeIndex / 2);  // k is the index of the old bins
		double leftEdge = oldBinEdges[2*k];
		double rightEdge = oldBinEdges[2*k+1];
		boolean binEdgeIndexIsEven = binEdgeIndex % 2 == 0;
		if (binEdgeIndexIsEven) {	    
		    leftEdge = t1;
		}
		//   Sum the counts of the old bins while within the new bin
		double rebinnedIntensity = 0;
		double rebinnedUncertainty = 0;
		while (k < nOldBins-1 && rightEdge <= t2) {
		    exposure = (rightEdge - leftEdge);
		    effNewBinTime += exposure;
		    counts += exposure * intensities[k];
		    errorCounts += Math.pow(exposure * uncertainties[k], 2);
		    //   Move to the next old bin and define its edges
		    k++; 
		    if (k < nOldBins) {
			leftEdge = oldBinEdges[2*k];
			rightEdge = oldBinEdges[2*k+1];
		    }
		}
		//   At this point, the next old bin is not fully contained within the new bin
		//   If there is a gap in the old bins, and therefore, the new bin ends before or at the start 
		//   of the next old bin, write out the final rate for the new bin and reset counts to 0
		if (t2 <= leftEdge) {
		    rebinnedIntensity = counts / effNewBinTime;
		    rebinnedUncertainty = Math.sqrt(errorCounts) / effNewBinTime;
		    //logger.debug("effNewBinTime="+effNewBinTime);
		    //logger.debug("r="+rebinnedIntensities[0][i]+"		 e="+rebinnedIntensities[1][i]);
		    effNewBinTime = 0;
		    exposure = 0;
		    counts = 0;
		    errorCounts = 0;
		}
		//   If the new bin ends inside the next old bin, add the counts corresponding to the 
		//   fraction of the old bin, and write out the final rate for the new bin. 
		//   Here we reset the counts to the other fraction of the old bin. 
		else {
		    if (k == nOldBins-1) {
				t2 = Math.min(t2, tstop);
				//   Add last bit of counts from the first part of the old bin
				exposure = (t2 - leftEdge);
				counts += intensities[k]*exposure;
				errorCounts += Math.pow(uncertainties[k] * exposure, 2);
				effNewBinTime += exposure;
				rebinnedIntensity = counts/effNewBinTime;
				rebinnedUncertainty = Math.sqrt(errorCounts)/effNewBinTime;
				//logger.debug("effNewBinTime="+effNewBinTime);
				//logger.debug("r="+rebinnedIntensities[0][i]+"		 e="+rebinnedIntensities[1][i]);
		    }
		    else {
				//   Add last bit of counts from the first part of the old bin
				exposure = (t2 - leftEdge);
				counts += intensities[k]*exposure;
				errorCounts += Math.pow(uncertainties[k] * exposure, 2);
				effNewBinTime += exposure;
				rebinnedIntensity = counts/effNewBinTime;
				rebinnedUncertainty = Math.sqrt(errorCounts)/effNewBinTime;
				//logger.debug("effNewBinTime="+effNewBinTime);
				//logger.debug("r="+rebinnedIntensities[0][i]+"		 e="+rebinnedIntensities[1][i]);
				//   Reset to take into account the second piece of the old bin
				exposure = t2 - leftEdge;
				counts = exposure * intensities[k];
				errorCounts = Math.pow(uncertainties[k] * exposure, 2);
				effNewBinTime = exposure;
				//   Move to the next old bin and define its edges
				k++;
				if (k < nOldBins) {
					leftEdge = oldBinEdges[2*k];
					rightEdge = oldBinEdges[2*k+1];
				}
		    }
		}
		return new double[] {rebinnedIntensity, rebinnedUncertainty};
    }

}