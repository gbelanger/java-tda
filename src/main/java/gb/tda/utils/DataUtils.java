package gb.tda.utils;

import java.util.Arrays;
import java.util.Date;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.stat.Descriptive;
import nom.tam.util.ArrayFuncs;
import org.apache.log4j.Logger;


/**
 * Class <code>DataUtils</code> 
 *
 * @author <a href="mailto:guilaume.belanger@esa.int">Guillaume Belanger</a>
 * 
 */
public final class DataUtils {

    private static Logger logger = Logger.getLogger(DataUtils.class);

	public static int[] getNRowsNCols(double[][] data) {
		int ncols = data.length;
		int nrows = data[0].length;
		return new int[] {nrows, ncols};
	}

    public static double[] shift(double[] data, double delta) {
		double[] shiftedData = new double[data.length];
		for (int i=0; i < data.length; i++) {
			shiftedData[i] = data[i] + delta;
		}
		return shiftedData;
    }

    public static double[] subtract(double[] data, double valueToSubtract) {
		double[] newData = new double[data.length];
		for (int i=0; i < data.length; i++)
			newData[i] = data[i] - valueToSubtract;
		return newData;
    }

    public static double[] subtract(double[] data, double[] valuesToSubtract) {
		double[] newData = new double[data.length];
		for (int i=0; i < data.length; i++)
			newData[i] = data[i] - valuesToSubtract[i];
		return newData;
    }

    public static double[] scale(double[] data, double scaling) {
		double[] newData = new double[data.length];
		for (int i=0; i < data.length; i++)
			newData[i] = data[i]*scaling;
		return newData;
    }

    public static double[] resetToZero(double[] data) {
	return resetToZero(data, 0);
    }

    public static double[] resetToZero(double[] data, double addedPositiveOffset) {
		int n = data.length;
		double[] zeroedData = new double[n];
		double zero = data[0];
		for (int i=0; i < n; i++) {
			zeroedData[i] = data[i] - zero + addedPositiveOffset;
		}
		return zeroedData;
    }

    public static double[] bootstrap(double[] data, int nevents) {
		int n = data.length;
		double[] bootData = new double[nevents];
		MersenneTwister64 randomEngine = new MersenneTwister64(new java.util.Date());
		Uniform uniform = new Uniform(0, (n-1), randomEngine);
		int idx = 0;
		for (int i=0; i < nevents; i++) {
			idx = uniform.nextInt();
			bootData[i] = data[idx];
		}
		Arrays.sort(bootData);
		return bootData;
    }

    public static double[] randomize(double[] data) {
		double[] randomData = new double[data.length];
		MersenneTwister64 randomEngine = new MersenneTwister64(new java.util.Date());
		Uniform uniform = new Uniform(0, (data.length-1), randomEngine);
	// 	int nbins = (new Double(Math.ceil(data.length/10D))).intValue() + 2;
	// 	FixedAxis axis = new FixedAxis(nbins, 0, (data.length));
	// 	Histogram1D histoOfIndexes = new Histogram1D(" "," ", axis);
		for (int i=0; i < randomData.length; i++) {
			int randomIndex = uniform.nextInt();
			randomData[i] = data[randomIndex];
			// 	    histoOfIndexes.fill(randomIndex);
		}

		//  Display the result
	// 	IAnalysisFactory af = IAnalysisFactory.create();
	// 	IPlotterFactory plotterF = af.createPlotterFactory();
	// 	IPlotter plotter = plotterF.create();
	// 	IPlotterRegion region = plotter.createRegion();
	// 	region.plot(histoOfIndexes);
	// 	plotter.show();

		return randomData;
    }

    public static double[] fillDataGaps(double[] data) {
		logger.info("Dropping leading NaNs forwards from first element");
		DoubleArrayList dataList = new DoubleArrayList();
		int k=0;
		while (Double.isNaN(data[k])) {
			k++;
		}
		// Fill list with rest of data array
		while (k < data.length) {
			dataList.add(data[k]);
			k++;
		}
		dataList.trimToSize();
		logger.info("Dropping trailing NaNs backwards from last element");
		k = dataList.size() - 1;
		int count = 0;
		while (Double.isNaN(dataList.get(k))) {
			count++;
			k--;
		}
		if (count > 0) {
			int first = dataList.size() - count;
			int last = dataList.size() - 1;
			dataList.removeFromTo(first, last);
		}
		dataList.trimToSize();
		logger.info("Filling gaps recursively");
		fillGapsRecursively(dataList);
		dataList.trimToSize();
		return dataList.elements();
    }

    private static void fillGapsRecursively(DoubleArrayList dataList) {

		// Make list of all the NaNs within the data
		IntArrayList indexListOfNaNs = getIndexListOfNaNs(dataList);
		if (indexListOfNaNs.size() > 0) {

			//  Get the avg fluctuation from bin to bin for the whole data set
			double[] avgAndVar = BasicStats.getAvgAndVarOfDiffsBetweenBins(dataList);

			//  Set up number generator
			MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
			Normal globalNoise = new Normal(avgAndVar[0]/2, Math.sqrt(avgAndVar[1]), engine);

			//  Go through the NaNs and fill
			int i=0;
			while (i < indexListOfNaNs.size()) {
				int idx = indexListOfNaNs.getQuick(i);
				double previousValue = dataList.get(idx-1);
				int idxOfNextNonNaN = idx+1;
				double nextValue = dataList.get(idxOfNextNonNaN);
				int gapSize = 1;
				while (Double.isNaN(nextValue)) {
					idxOfNextNonNaN++;
					nextValue = dataList.get(idxOfNextNonNaN);
					gapSize++;
				}
				if (gapSize == 1) {
					double basicFillValue = 0.5*(previousValue + nextValue);
					//  Add noise to basicFillValue

					/** This is not correct  **/

					double uncertainty = globalNoise.nextDouble();
					if (Math.random() < 0.5) {
						uncertainty *= -1;
					}
					double noisyFillValue = basicFillValue + uncertainty;
					dataList.set(idx, noisyFillValue);
					i++;
				}
				else {
					//logger.debug("Gap size = "+gapSize+": filling ...");

					//  Get next data block
					int referenceIdx = idxOfNextNonNaN;
					DoubleArrayList nextDataBlock = getNextDataBlock(dataList, referenceIdx, gapSize);
					int nextBlockSize = nextDataBlock.size();
					//logger.debug("Next data block size = "+nextBlockSize);

					//  Get previous data block
					referenceIdx = idx-1;
					DoubleArrayList previousDataBlock = getPreviousDataBlock(dataList, referenceIdx, gapSize);
					int previousBlockSize = previousDataBlock.size();
					//logger.debug("Previous data block size = "+previousBlockSize);

					//  Estimate avg statistical fluctuation from bin to bin in the two adjacent data blocks
					DoubleArrayList bothDataBlocks = previousDataBlock.copy();
					bothDataBlocks.addAllOf(nextDataBlock);
					bothDataBlocks.trimToSize();
					avgAndVar = BasicStats.getAvgAndVarOfDiffsBetweenBins(bothDataBlocks);

					//  Set up number generators
					Normal noise = new Normal(avgAndVar[0]/2, Math.sqrt(avgAndVar[1]), engine);
					Uniform uniform = new Uniform(engine);

					//  Fill the gap
					DoubleArrayList gap = (DoubleArrayList) dataList.partFromTo(idx, idx+gapSize-1);
					while (thereAreNaNs(gap)) {

						//  Get value randomly from next block
						int randomIdx = uniform.nextIntFromTo(0, nextBlockSize-1);
						double valueFromNextBlock = nextDataBlock.get(randomIdx);
						double weightForNext = (nextBlockSize-randomIdx)/nextBlockSize;

						// debugging: weightForNext = 0.5;
						int trials = 0;
						int maxTrials = 2*nextBlockSize;
						while (trials < maxTrials && Double.isNaN(valueFromNextBlock)) {
						/**  There is a problem with this: why nextInt() which gives 0 or 1 **/
							valueFromNextBlock = nextDataBlock.get(uniform.nextInt());
							trials++;
						}

						//  Get value randomly from previous block
						randomIdx = uniform.nextIntFromTo(0, previousBlockSize-1);
						double valueFromPreviousBlock = previousDataBlock.get(randomIdx);
						double weightForPrevious = (previousBlockSize-randomIdx)/previousBlockSize;
						//debugging: weightForPrevious = 0.5;
						trials = 0;
						maxTrials = 2*previousBlockSize;
						while (trials < maxTrials && Double.isNaN(valueFromPreviousBlock)) {
						/**  There is a problem with this: why nextInt() which gives 0 or 1 **/
							valueFromPreviousBlock = previousDataBlock.get(uniform.nextInt());
							trials++;
						}
						double basicFillValue = weightForNext*valueFromNextBlock +
							weightForPrevious*valueFromPreviousBlock;

						//  Add noise to basicFillValue
						double uncertainty = noise.nextDouble();
						if (bothDataBlocks.size() < 5) {
							uncertainty = globalNoise.nextDouble();
						}

						/**  There is something wrong with negative uncertainty  **/
						if (Math.random() < 0.5) {
							uncertainty *= -1;
						}
						double noisyFillValue = basicFillValue + uncertainty;
						int insertPointIdx = idx + uniform.nextIntFromTo(0, gapSize-1);
						dataList.set(insertPointIdx, noisyFillValue);
						i++;
						gap = (DoubleArrayList) dataList.partFromTo(idx, idx + gapSize-1);
					}
					fillGapsRecursively(dataList);
				}
			}
		}
    }

    private static IntArrayList getIndexListOfNaNs(DoubleArrayList dataList) {
		//  WARN: Input list must be without leading or trailing NaNs
		IntArrayList indexListOfNaNs = new IntArrayList();
		for (int i=0; i < dataList.size(); i++) {
			if (Double.isNaN(dataList.getQuick(i))) {
			indexListOfNaNs.add(i);
			}
		}
		indexListOfNaNs.trimToSize();
		return indexListOfNaNs;
    }

    private static boolean thereAreNaNs(DoubleArrayList dataList) {
		boolean thereAreNaNs = true;
		try {
			int i=0;
			while (!Double.isNaN(dataList.get(i))) {
			i++;
			}
		}
		catch (IndexOutOfBoundsException e) {
			thereAreNaNs = false;
		}
		return thereAreNaNs;
    }

    private static boolean allValuesAreNaN(DoubleArrayList dataList) {
		int i=0;
		int count=0;
		while (i < dataList.size()) {
			if (Double.isNaN(dataList.get(i))) {
			count++;
			}
			else i++;
		}
		if (count == dataList.size()) return true;
		else return false;
    }

    private static DoubleArrayList getNextDataBlock(DoubleArrayList dataList, int referenceIdx, int blockSize) {
		DoubleArrayList nextDataBlock = new DoubleArrayList();
		try {
			int from = referenceIdx;
			int to = referenceIdx + blockSize -1;
			//  Make sure we stay within the data
			to = Math.min(to, dataList.size()-1);
			nextDataBlock = (DoubleArrayList) dataList.partFromTo(from, to);
			while (allValuesAreNaN(nextDataBlock)) {
				from = to + 1;
				to = from + blockSize;
				nextDataBlock = (DoubleArrayList) dataList.partFromTo(from, to);
			}
		}
		catch (IndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException("All subsequent data are NaN");
		}
		return nextDataBlock;
    }

    private static DoubleArrayList getPreviousDataBlock(DoubleArrayList dataList, int referenceIdx, int blockSize) {
		DoubleArrayList previousDataBlock = new DoubleArrayList();
		try {
			int to = referenceIdx;
			int from = referenceIdx - blockSize +1;
			//  Make sure we stay within the data
			from = Math.max(from, 0);
			previousDataBlock = (DoubleArrayList) dataList.partFromTo(from, to);
			while (allValuesAreNaN(previousDataBlock)) {
				to = from - 1;
				from -= blockSize;
				previousDataBlock = (DoubleArrayList) dataList.partFromTo(from, to);
			}
		}
		catch (IndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException("All previous data are NaN");
		}
		return previousDataBlock;
    }

    public static int getClosestIndexInSortedData(double valueToFind, double[] data) {
		double min = data[0];
		double max = data[data.length-1];
		int k = 0;
		int index = 0;
		// valueToFind is less than smallest data value
		if (valueToFind < min) {
			logger.warn("Value to find ("+valueToFind+") is lesser than first element ("+min+")");
			logger.warn("Returning index = 0");
			index = 0;
		}
		// valueToFind is greater than largest data value
		else if (valueToFind > max) {
			logger.warn("Value to find ("+valueToFind+") is greater than last element ("+max+")");
			logger.warn("Returning index = data.length-1");
			index = data.length-1;
		}
		// valueToFind is within data value range
		else {
			while (valueToFind > data[k]) {
				k++;
			}
			if (valueToFind == data[k]) {
				index = k;
			}
			else {
				double distFromPrevious = valueToFind - data[k-1];
				double distToNext = data[k] - valueToFind;
				if (distFromPrevious < distToNext) {
					index = k-1;
				}
				else {
					index = k;
				}
			}
		}
		return index;
    }

    public static int getIndex(double valueToFind, double[] data) {
		int k=0;
		try {
		    while (data[k] != valueToFind) {
			k++;
		    }
		}
		catch (ArrayIndexOutOfBoundsException e) {
		    logger.warn("Value not found in the data. Returning -1");
		    k=-1;
		}
		return k;
    }

    public static int getIndex(String stringToFind, String[] stringData) {
		int k=0;
		try {
		    while (!stringData[k].equals(stringToFind)) {
				k++;
		    }
		}
		catch (ArrayIndexOutOfBoundsException e) {
		    logger.warn("Value not found in the data. Returning -1");
		    k=-1;
		}
		return k;
    }

    public static int[] getAllIndexes(double valueToFind, double precision, double[] data) {
		IntArrayList list = new IntArrayList();
		int k=0;
		while (k < data.length-1) {
			double diff = Math.abs(valueToFind - data[k]);
			if (diff <= precision) {
				list.add(k);
			}
			k++;
		}
		list.trimToSize();
		return list.elements();
    }	

    public static double[] getLocationHeightAndFWHMOfPeak(double[] x, double[] y) {
		int[] indexes = getIndexesOfPeakAndFWHMBounds(y);
		int indexOfLeftBoundOfFWHM = indexes[0];
		int indexOfPeak = indexes[1];
		int indexOfRightBoundOfFWHM = indexes[2];
		double locationOfPeak = x[indexOfPeak];
		double heightOfPeak = y[indexOfPeak];
		double fwhmOfPeak = x[indexOfRightBoundOfFWHM] - x[indexOfLeftBoundOfFWHM];
		return new double[] {locationOfPeak, heightOfPeak, fwhmOfPeak};
    }

    public static int[] getIndexesOfPeakAndFWHMBounds(double[] data) {
		//  The basic assumption is that there is a well-defined peak
		//  with a well-defined FWHM: at least twice as high as the second highest peak
		//
		//  Returns three indexes: left point of FWHM, peak, right point of FWHM
		double max = MinMax.getMax(data);
		int index = getIndex(max, data);
		double valueOnLeft = max;
		int k=1;
		while (valueOnLeft > (max/2)) {
			valueOnLeft = data[index-k];
			//logger.info("k = "+k+"	 valueOnLeft = "+valueOnLeft);
			k++;
		}
		k--;
		int leftIndex = index-k;
		double absoluteDiffOfCurrent = Math.abs((max/2)-data[leftIndex]);
		double absoluteDiffOfPrevious = Math.abs((max/2)-data[leftIndex+1]);
		if (absoluteDiffOfPrevious < absoluteDiffOfCurrent) {
			leftIndex++;
			valueOnLeft = data[leftIndex];
		}
		double valueOnRight = max;
		k=1;
		while (valueOnRight > (max/2)) {
			valueOnRight = data[index+k];
			//logger.info("k = "+k+"	 valueOnRight = "+valueOnRight);
			k++;
		}
		k--;
		int rightIndex = index+k;
		absoluteDiffOfCurrent = Math.abs((max/2)-data[rightIndex]);
		absoluteDiffOfPrevious = Math.abs((max/2)-data[rightIndex-1]);
		if (absoluteDiffOfPrevious < absoluteDiffOfCurrent) {
			rightIndex--;
			valueOnRight = data[rightIndex];
		}
		//logger.info("left index "+leftIndex+" (value="+data[leftIndex]+") peak index "+index+" (value="+data[index]+") right index "+rightIndex+" (value="+data[rightIndex]+")");
		return new int[] {leftIndex, index, rightIndex};
    }
    
    public static double findClosestValueSmallerThan(double valueToFind, double[] orderedData) {
		int binIndex = -(Arrays.binarySearch(orderedData, valueToFind) +1);
		double value = orderedData[binIndex];
		if (value == valueToFind) {
			return value;
		}
		else if (binIndex == 0) {
			return orderedData[binIndex];
		}
		else {
			return orderedData[binIndex-1];
		}
    }

    public static double findClosestValueLargerThan(double valueToFind, double[] orderedData) {
		int binIndex = -(Arrays.binarySearch(orderedData, valueToFind) +1);
		double value;
		try {
			value = orderedData[binIndex];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			value = orderedData[orderedData.length-1];
		}
		return value;
    }

    public static int[] getXYCoordsOfMaxValue(float[][] data) {		
		int[] maxXY = new int[2];
		float max = MinMax.getMax(data);
		int[] dims = ArrayFuncs.getDimensions(data);
		int xsize = dims[0];
		int ysize = dims[1];
		float pixValue = 0;
		int x=0, y=0;
		while (pixValue != max && y < ysize) {
			for (x=0; x < xsize; x++) {
				pixValue = data[y][x];
				if (pixValue == max) {
					maxXY[0] = x+1;
					maxXY[1] = y+1;
				}
			}
			y++;
		}
		return maxXY;
    }
	
    public static int[] getXYCoordsOfMaxValue(double[][] data) {		
		int[] maxXY = new int[2];
		double max = MinMax.getMax(data);
		int[] dims = ArrayFuncs.getDimensions(data);
		int xsize = dims[0];
		int ysize = dims[1];
		double pixValue = 0;
		int x=0, y=0;
		while (pixValue != max && y < ysize) {
			for (x=0; x < xsize; x++) {
				pixValue = data[y][x];
				if (pixValue == max) {
					maxXY[0] = x+1;
					maxXY[1] = y+1;
				}
			}
			y++;
		}
		return maxXY;
    }	

    public static double[] getSpacings(double[] data) {
		double[] spacings = new double[data.length-1];
		for (int i=0; i < data.length-1; i++) {
			spacings[i] = data[i+1] - data[i];
		}
    	return spacings;
    }

    public static double getMinSpacing(double[] data) {
	return MinMax.getNonZeroMin(getSpacings(data));
    }

    public static double getMeanSpacing(double[] data) {
	//   Get the average time between events  		
		return BasicStats.getMean(getSpacings(data));
    }

    public static double getRange(double[] data) {
		double range = MinMax.getMax(data) - MinMax.getMin(data);
		return range;
    }	

    public static double getHalfRangePos(double[] data) {
		double posHalfRange = 0;
		double mean = BasicStats.getMean(data);
		for (int i=0; i < data.length; i++) {
			if (!Double.isNaN(data[i]) && data[i] >= mean) {
				posHalfRange = Math.max(posHalfRange, data[i] - mean);
			}
		}
		return posHalfRange;
    }
	
    public static double getHalfRangeNeg(double[] data) {
		double negHalfRange = 0;
		double mean = BasicStats.getMean(data);
		for (int i=0; i < data.length; i++) {
			if (!Double.isNaN(data[i]) && data[i] <= mean) {
				negHalfRange = Math.max(negHalfRange, mean - data[i]);
			}
		}
		return negHalfRange;
    }

    public static int getNumOfGoodValues(double[] data) {
		int n = 0;
		for (int i=0; i < data.length; i++) {
			if (!Double.isNaN(data[i])) n++;
		}
		return n;
    }
	
    public static int getNumOfGoodValues(float[] data) {
		int n = 0;
		for (int i=0; i < data.length; i++) {
			if (!Double.isNaN(data[i])) n++;
		}
		return n;
    }
	
    public static int getNumOfGoodValues(int[] data) {
		int n = 0;
		for (int i=0; i < data.length; i++) {
			if (!Double.isNaN(data[i])) n++;
		}
		return n;
    }

}
