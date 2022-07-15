package gb.tda.tools;

//import gb.codetda.periodogram.WindowFunctionException;
import org.apache.log4j.Logger;


public final class DataSmoother {

    private static Logger logger  = Logger.getLogger(DataSmoother.class);
    
    public static double[] detrend(double[] x, double[] y) {
		double[] mTimesX = getTrendValuesToSubtract(x, y);
		// De-trend: subtract m*x
		logger.info("Detrending: Subtracting m*x from original data");
		int n = x.length;
		double[] detrended = new double[n];
		for (int i=0; i < n; i++) {
		    detrended[i] = y[i] - mTimesX[i] + y[0];
		}
		return detrended;
    }

    public static double[] getTrendValuesToSubtract(double[] x, double[] y) {
		// Fit
		double[] fit = LeastSquaresFitter.fitLine(x, y);
		double m = fit[0];
		double err_m = fit[1];
		double b = fit[2];
		double err_b = fit[3];
		// Calculate detrending values
		int n = x.length;
		double[] valuesToSubtract = new double[n];
		for (int i=0; i < n; i++) {
		    valuesToSubtract[i] = m*x[i]; // + b;  //  there's a problem with the vertical offset
		}
		return valuesToSubtract;
    }

    public static double[] kalmanFilter(double[] measurements, double measRMS, double processRMS) {
		//  Using the notation of Welch & Bishop 2006, An Introduction to the Kalman Filter
    	//
		// xHat is the estimate of the state
		// xHat_apriori is its a priori estimate
		//
		// p is the evolving error variance in the estimation process
		// p_apriori is its a priori estimate
		//
		// k is the Kalman gain that weighs towards the measurements or towards the a priori estimate
		double[] z = measurements;
		int n = z.length;
		double[] xHat = new double[n];
		double[] xHat_apriori = new double[n];
		double[] p = new double[n];
		double[] p_apriori = new double[n];
		// a is the function that relates the state estimate to the previous
		// q is the process variance 
		// r is the measurement variance for which we use measErrors^2
		double a = 1d;
		double q = processRMS*processRMS;
		double r = measRMS*measRMS;
		//  Initialise
		xHat[0] = z[0];
		p[0] = 1;
		//  Run
		for (int i=1; i < n; i++) {
		    //  Time-Update equations
		    xHat_apriori[i] = a*xHat[i-1];
		    p_apriori[i] = a*a*p[i-1] + q;
		    //  Measurement-Update equations
		    double k = p_apriori[i] / (p_apriori[i] + r);
		    xHat[i] = xHat_apriori[i] + k * (z[i] - xHat_apriori[i]);
		    p[i] = (1 - k)*p_apriori[i];
		}
		return xHat;
    }

    
    public static double[] kalmanFilter(double[] measurements, double[] measErrors, double processRMS) {
		double[] z = measurements;
		double[] zErr = measErrors;
		int n = z.length;
		//  Set up the Kalman filtering
		double[] xHat = new double[n];
		double[] xHat_apriori = new double[n];
		double[] p = new double[n];
		double[] p_apriori = new double[n];
		double a = 1;
		double q = processRMS*processRMS;
		xHat[0] = z[0];
		p[0] = 1;
		//  Run the Kalman filtering
		for (int i=1; i < n; i++) {
		    //  Time-Update equations
		    //xHat_apriori[i] = a*xHat[i-1];
		    //p_apriori[i] = a*a*p[i-1] + q;
		    xHat_apriori[i] = xHat[i-1];
		    p_apriori[i] = p[i-1] + q;
		    //  Measurement-Update equations
		    double r = zErr[i]*zErr[i];
		    double k = p_apriori[i] / (p_apriori[i] + r);
		    xHat[i] = xHat_apriori[i] + k * (z[i] - xHat_apriori[i]);
		    p[i] = (1 - k)*p_apriori[i];
		}
		return xHat;
    }
    

    public static double[] smooth(double[] data, int nBins) {
		if (nBins%2 != 1) {
		    nBins += 1;
		    logger.warn("Using "+nBins+" bins instead of the specified "+(nBins-1)+". Number of bins must be odd");
		}
		double[] smoothData = new double[data.length];
		//  First point is not modified
		smoothData[0] = data[0];
		//  For the first bins, we increasing the size of the averaging window by 1
		//  as we progressively move away from the first bin, from the minimum of 3 bins, 
		//  up to the specified number of bins
		int i=1;
		while (i < (nBins/2)) {
		    int nUsed = 2*i+1;
		    double sum=0;
		    for (int j=0; j < nUsed; j++) {
				int idx = i - (nUsed/2) + j;
				if (!Double.isNaN(data[idx])) {
				    sum += data[idx];
				}
		    }
		    smoothData[i] = sum/nUsed;
		    i++;
		}
		//  For the central part of the data, we do the averaging using the specified number of bins
		while (i < (data.length - (nBins/2))) {
		    double sum=0;
		    for (int j=0; j < nBins; j++) {
				int idx = i - nBins/2 + j;
				if (!Double.isNaN(data[idx])) {
				    sum += data[idx];
				}
		    }
		    smoothData[i] = sum/nBins;
		    i++;
		}
		//  For the last bins, we do as we did for the first bins
		while (i < data.length-1) {
		    int nUsed = 2*(data.length-1-i)+1;
		    double sum=0;
		    for (int j=0; j < nUsed; j++) {
				if (!Double.isNaN(data[i - (nUsed/2)+j])) {
				    sum += data[i - (nUsed/2)+j];
				}
		    }
		    smoothData[i] = sum/nUsed;
		    i++;
		}
		//  Last point is not modified
		smoothData[data.length-1] = data[data.length-1];
		return smoothData;
    }
    
    
    public static Object[] smoothData(double[] time, double[] rate, double[] error, double smoothingWindowSize) {
		//  Mean overall rate for data set  
		double meanRate = BasicStats.getWMean(rate, error); 
		//  Array to return after calculation  
		double[] meanRates = new double[time.length];
		double[] meanErrors = new double[time.length];
		//  Initialise variables for simulated light curves  
		double halfWindowSize = smoothingWindowSize/2D;
		int eventWindowSize = (new Double(Math.round(smoothingWindowSize*meanRate))).intValue(); 
		int halfBlockSize = (new Double(Math.floor(eventWindowSize/2))).intValue();
		double[] eventBlock = new double[eventWindowSize];
		double[] firstBlock = new double[halfBlockSize];
		double[] lastBlock = new double[halfBlockSize];
		int k = 0;
		double deltaT = 0;
		double sumOfRates = rate[0];
		double weight = Math.pow(error[0], 2);
		double wSumOfRates = rate[0]*weight;
		double sumOfWeights = weight;
		//  Calculate the mean rates over the first half of the smoothing window 
		while (deltaT < halfWindowSize) {
		    deltaT += (time[k+1] - time[k]);
		    sumOfRates += rate[k+1];
		    weight = Math.pow(error[k], 2);
		    wSumOfRates += rate[k+1]*weight;
		    sumOfWeights += weight;
		    k++;
		}
		//  Set meanRates for each of the bins over the 
		//     first half window to the same average rate  
		//for (int i=0; i < k; i++) { meanRates[i] = sumOfRates/k; }
		for (int i=0; i < k; i++) meanRates[i] = wSumOfRates/sumOfWeights;
		//System.out.println(meanRates[0] +"	"+ sumOfRates/k);
		//  Calculate the mean rates for region between firstHalfBlock and lastHalfBlock 
		int nRateValues = 0;
		int m = k;
		k = 1;
		deltaT = 0;
		sumOfRates = 0;
		wSumOfRates = 0;
		sumOfWeights = 0;
		while (time[k] < (time[time.length-1] - halfWindowSize)) {
		    while (deltaT < smoothingWindowSize && k < time.length-2) {
				deltaT += (time[k+1] - time[k]);
				sumOfRates += rate[k];
				weight = Math.pow(error[k], 2);
				wSumOfRates += rate[k]*weight;
				sumOfWeights += weight;
				nRateValues++;
				k++;
		    }
		    meanRates[m] = wSumOfRates/sumOfWeights;
		    meanErrors[m] = 1/Math.sqrt(sumOfWeights);
		    m++;
		    deltaT = 0;
		    sumOfRates = 0;
		    wSumOfRates = 0;
		    sumOfWeights = 0;
		    nRateValues = 0;
		    k = k - nRateValues;
		}
		k = m;
		//  Calculate the average over the last half window 
		while (time[k] < time[time.length-1]) {
		    sumOfRates += rate[k];
		    weight = Math.pow(error[k], 2);
		    wSumOfRates += rate[k]*weight;
		    sumOfWeights += weight;
		    nRateValues++;
		    k++;
		}
		//  Set meanRates for each of the bins over 
		//   the last half window to the same average rate  
		for (int i=m; i < k; i++)  {
		    meanRates[i] = wSumOfRates/sumOfWeights; 
		    meanErrors[i] = 1/Math.sqrt(sumOfWeights);
		}
		Object[] meanRatesAndErrors = new Object[]{meanRates, meanErrors};
		return meanRatesAndErrors;
    }


    public static Object[] smoothData(double[] arrivalTimes, double smoothingWindowSize) {
		//  Mean overall rate for data set  
		double duration = arrivalTimes[arrivalTimes.length-1] - arrivalTimes[0];
		double meanRate = arrivalTimes.length/duration;
		//  Array to return after calculation  
		double[] meanRates = new double[arrivalTimes.length];
		double[] meanErrors = new double[arrivalTimes.length];
		//  Initialise variables for simulated light curves  
		double halfWindowSize = smoothingWindowSize/2d;
		int eventWindowSize = (new Double(Math.round(smoothingWindowSize*meanRate))).intValue(); 
		int halfBlockSize = (new Double(Math.floor(eventWindowSize/2))).intValue();
		double[] eventBlock = new double[eventWindowSize];
		double[] firstBlock = new double[halfBlockSize];
		double[] lastBlock = new double[halfBlockSize];
		int k = 0;
		double deltaT = 0;
		//  First half of event block 
		for (int i=0; i < halfBlockSize; i++) {
		    firstBlock[i] = arrivalTimes[k];
		    k++;
		}
		double timeSpanOfHalfBlock = DataUtils.getRange(firstBlock);
		double mean = halfBlockSize/timeSpanOfHalfBlock;
		double err = Math.sqrt(halfBlockSize)/timeSpanOfHalfBlock;
		for (int i=0; i < halfBlockSize; i++)  {
		    meanRates[i] = mean;
		    meanErrors[i] = err;
		}
		//  All complete event blocks between firstHalfBlock and lastHalfBlock  
		int m = halfBlockSize; 
		double timeSpanOfBlock = 0;
		while (k < (arrivalTimes.length - halfBlockSize)) {
		    for (int i=0; i < eventWindowSize; i++) {eventBlock[i] = arrivalTimes[k - halfBlockSize + i];}
		    timeSpanOfBlock = DataUtils.getRange(eventBlock);
		    meanRates[m] = eventWindowSize/timeSpanOfBlock;
		    meanErrors[m] = Math.sqrt(eventWindowSize)/timeSpanOfBlock;
		    m++;  k++;
		}
		//  Last half event block 
		for (int i=0; i < halfBlockSize; i++) { lastBlock[i] = arrivalTimes[k];  k++; }
		timeSpanOfHalfBlock = DataUtils.getRange(lastBlock);
		mean = halfBlockSize/timeSpanOfHalfBlock; 
		err = Math.sqrt(halfBlockSize)/timeSpanOfHalfBlock;
		for (int i=0; i < halfBlockSize; i++) {
		    meanRates[m + i] = mean;
		    meanErrors[m + i] = err;
		}
		Object[] meanRatesAndErrors = new Object[]{meanRates, meanErrors};
		return meanRatesAndErrors;
    }


    public static double[] getGaussWeights(double[] xDistance, double[] yDistance, double xSigmaOfPSF, double ySigmaOfPSF) {
		//  Note: Distances must be in the same units as sigma of PSF
		double[] weight = new double[xDistance.length];
		double norm = 1/(2*Math.PI*xSigmaOfPSF*ySigmaOfPSF);
		for (int i=0; i < weight.length; i++) {
		    weight[i] = norm*Math.exp(-0.5 * (Math.pow(xDistance[i]/xSigmaOfPSF, 2) + Math.pow(yDistance[i]/ySigmaOfPSF, 2)));
		}
		return weight;
    }


}
