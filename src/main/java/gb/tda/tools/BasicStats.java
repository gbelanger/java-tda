package gb.tda.tools;

import java.util.Arrays;
import java.util.Vector;

import cern.colt.list.DoubleArrayList;
import nom.tam.util.ArrayFuncs;

public final class BasicStats {

    public static double[] getAvgAndVarOfDiffsBetweenBins(DoubleArrayList dataList) {
        DoubleArrayList diffsList = new DoubleArrayList();
        for (int k = 0; k < dataList.size() - 1; k++) {
            double diff = Math.abs(dataList.getQuick(k + 1) - dataList.getQuick(k));
            if (!Double.isNaN(diff)) {
                diffsList.add(diff);
            }
        }
        diffsList.trimToSize();
        return BasicStats.getRunningAvgAndVar(diffsList.elements());
    }

    public static double[] getDiffsBetweenAdjacentBins(double[] data) {
        DoubleArrayList diffsList = new DoubleArrayList();
        for (int k = 1; k < data.length - 1; k++) {
            double diff = Math.abs(data[k + 1] - data[k]);
            if (!Double.isNaN(diff)) {
                diffsList.add(diff);
                System.out.println(data[k + 1] + "\t" + data[k] + "\t" + diff);
            }
        }
        diffsList.trimToSize();
        return diffsList.elements();
    }

    public static double getSum(double[] data) {
        double sum = 0;
        for (int i = 0; i < data.length; i++)
            if (!Double.isNaN(data[i])) sum += data[i];
        return sum;
    }

    public static double getSumOfSquares(double[] data) {
        double sumOfSquares = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Double.isNaN(data[i])) sumOfSquares += data[i] * data[i];
        }
        return sumOfSquares;
    }

    public static double getQuadSum(double[] data) {
        return Math.sqrt(getSumOfSquares(data));
    }

    public static double getMedian(double[] data) {
        double[] copy = Arrays.copyOf(data, data.length);
        Arrays.sort(copy);
        return copy[data.length / 2];
    }

    public static double getMean(double[] data) {
        return getSum(data) / data.length;
    }

    public static double getMean(Vector dataVector) {
        double sum = 0;
        double dataValue = 0;
        int n = 0;
        for (int i = 0; i < dataVector.size(); i++) {
            dataValue = ((Double) dataVector.elementAt(i)).doubleValue();
            if (!Double.isNaN(dataValue)) {
                sum += dataValue;
                n++;
            }
        }
        double mean = sum / n;
        return mean;
    }

    public static double getMean(double[][] data) {
        double[] flatData = (double[]) ArrayFuncs.flatten(data);
        double mean = getMean(flatData);
        return mean;
    }

    public static float getMean(float[] data) {
        float sum = 0;
        int n = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Float.isNaN(data[i])) {//&& data[i] != 0.0 ) {
                sum += data[i];
                n++;
            }
        }
        float mean = sum / n;
        return mean;
    }

    public static float getMean(float[][] data) {
        float[] flatData = (float[]) ArrayFuncs.flatten(data);
        float mean = getMean(flatData);
        return mean;
    }

    public static double getMean(int[] data) {
        double sum = 0;
        int n = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Double.isNaN(data[i])) { //&& data[i] != 0 ) {
                sum += data[i];
                n++;
            }
        }
        double mean = sum / n;
        return mean;
    }

    public static double getMean(int[][] data) {
        int[] flatData = (int[]) ArrayFuncs.flatten(data);
        double mean = getMean(flatData);
        return mean;
    }

    public static double getWMean(double[] data, double[] error) {
        double sumOfWeights = 0;
        double wsum = 0;
        double weight = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Double.isNaN(error[i]) && !Double.isNaN(data[i])) {
                if (error[i] == 0.0)
                    weight = 1d / (0.5 * 0.5);
                else
                    weight = 1d / Math.pow(error[i], 2);
                sumOfWeights += weight;
                wsum += weight * data[i];
            }
        }
        return wsum / sumOfWeights;
    }

    public static double[] getWMeanAndError(double[] data, double[] error) {
        double sumOfWeights = 0;
        double wsum = 0;
        double weight = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Double.isNaN(error[i]) && !Double.isNaN(data[i])) {
                if (error[i] == 0.0)
                    weight = 1d / (0.5 * 0.5);
                else
                    weight = 1d / Math.pow(error[i], 2);
                sumOfWeights += weight;
                wsum += weight * data[i];
            }
        }
        double wMean = wsum / sumOfWeights;
        double errOnWMean = 1 / Math.sqrt(sumOfWeights);
        return new double[]{wMean, errOnWMean};
    }

    public static double[] getWMeanAndError_weights(double[] data, double[] weight) {
        double sumOfWeights = 0;
        double wsum = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Double.isNaN(weight[i]) && !Double.isNaN(data[i])) {
                sumOfWeights += weight[i];
                wsum += weight[i] * data[i];
            }
        }
        double wMean = wsum / sumOfWeights;
        double errOnWMean = 1 / Math.sqrt(sumOfWeights);
        return new double[]{wMean, errOnWMean};
    }

    public static double getWMean_weights(double[] data, double[] weight) {
        double sumOfWeights = 0;
        double wsum = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Double.isNaN(weight[i]) && !Double.isNaN(data[i])) {
                wsum += weight[i] * data[i];
                sumOfWeights += weight[i];
            }
        }
        return wsum / sumOfWeights;
    }

    public static double getWMean(double[][] data, double[][] error) {
        double[] flatData = (double[]) ArrayFuncs.flatten(data);
        double[] flatError = (double[]) ArrayFuncs.flatten(error);
        double wmean = getWMean(flatData, flatError);
        return wmean;
    }


    public static double getWMean(float[] data, float[] error) {
        double sumOfWeights = 0;
        double wsum = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Float.isNaN(error[i]) && !Float.isNaN(data[i])) { //&& data[i] != 0.0 ) {
                double weight = 1 / Math.pow(error[i], 2);
                sumOfWeights += weight;
                wsum += weight * data[i];
            }
        }
        double wmean = wsum / sumOfWeights;
        return wmean;
    }

    public static double getWMean(float[][] data, float[][] error) {
        float[] flatData = (float[]) ArrayFuncs.flatten(data);
        float[] flatError = (float[]) ArrayFuncs.flatten(error);
        double wmean = getWMean(flatData, flatError);
        return wmean;
    }

    public static double getWMean(int[] data, int[] error) {
        double sumOfWeights = 0;
        double wsum = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Double.isNaN(error[i]) && !Double.isNaN(data[i])) { //&& data[i] != 0.0 ) {
                double weight = 1 / Math.pow(error[i], 2);
                sumOfWeights += weight;
                wsum += weight * data[i];
            }
        }
        double wmean = wsum / sumOfWeights;
        return wmean;
    }

    public static double getWMean(int[][] data, int[][] error) {
        int[] flatData = (int[]) ArrayFuncs.flatten(data);
        int[] flatError = (int[]) ArrayFuncs.flatten(error);
        double wmean = getWMean(flatData, flatError);
        return wmean;
    }

    public static double getVariance(double[] data) {
        double mean = getMean(data);
        double sum = 0;
        int n = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Double.isNaN(data[i])) { //&& data[i] != 0.0 ) {
                sum += Math.pow(data[i] - mean, 2);
                n++;
            }
        }
        double variance = sum / (n - 1);
        return variance;
    }

    public static double getVariance(Vector dataVector) {
        double mean = getMean(dataVector);
        double sum = 0;
        double dataValue = 0;
        int n = 0;
        for (int i = 0; i < dataVector.size(); i++) {
            dataValue = ((Double) dataVector.elementAt(i)).doubleValue();
            if (!Double.isNaN(dataValue)) { //&& dataValue != 0.0 ) {
                sum += Math.pow(dataValue - mean, 2);
                n++;
            }
        }
        double variance = sum / (n - 1);
        return variance;
    }

    public static double getVariance(double[][] data) {
        double[] flatData = (double[]) ArrayFuncs.flatten(data);
        double variance = getVariance(flatData);
        return variance;
    }

    public static double getVariance(float[] data) {
        double mean = getMean(data);
        double sum = 0;
        int n = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Float.isNaN(data[i])) { //&& data[i] != 0.0 ) {
                sum += Math.pow(data[i] - mean, 2);
                n++;
            }
        }
        double variance = sum / (n - 1);
        return variance;
    }

    public static double getVariance(float[][] data) {
        float[] flatData = (float[]) ArrayFuncs.flatten(data);
        double variance = getVariance(flatData);
        return variance;
    }

    public static double getVariance(int[] data) {
        double mean = getMean(data);
        double sum = 0;
        int n = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Double.isNaN(data[i])) { //&& data[i] != 0.0 ) {
                sum += Math.pow(data[i] - mean, 2);
                n++;
            }
        }
        double variance = sum / (n - 1);
        return variance;
    }

    public static double getVariance(int[][] data) {
        int[] flatData = (int[]) ArrayFuncs.flatten(data);
        return getVariance(flatData);
    }

    public static double getWVariance(double[] data, double[] error) {
        double wmean = getWMean(data, error);
        double wsum = 0;
        double sumOfWeights = 0, sumOfSqrdWeights = 0;
        for (int i = 0; i < data.length; i++) {
            double weight = 0;
            if (!Double.isNaN(error[i]) && !Double.isNaN(data[i]) && data[i] != 0.0) {
                weight = 1 / Math.pow(error[i], 2);
                wsum += weight * Math.pow(data[i] - wmean, 2);
                sumOfWeights += weight;
                sumOfSqrdWeights += Math.pow(weight, 2);
            }
        }
        double neff = Math.pow(sumOfWeights, 2) / sumOfSqrdWeights;
        double wvariance = wsum / sumOfWeights * neff / (neff - 1);
        return wvariance;
    }

    public static double getWVariance(float[] data, float[] error) {
        double wmean = getWMean(data, error);
        double wsum = 0;
        double sumOfWeights = 0, sumOfSqrdWeights = 0;
        for (int i = 0; i < data.length; i++) {
            double weight = 0;
            if (!Double.isNaN(error[i]) && !Double.isNaN(data[i]) && data[i] != 0.0) {
                weight = 1 / Math.pow(error[i], 2);
                wsum += weight * Math.pow(data[i] - wmean, 2);
                sumOfWeights += weight;
                sumOfSqrdWeights += Math.pow(weight, 2);
            }
        }
        double neff = Math.pow(sumOfWeights, 2) / sumOfSqrdWeights;
        double wvariance = wsum / sumOfWeights * neff / (neff - 1);
        return wvariance;
    }

    public static double getWVariance(int[] data, int[] error) {
        double wmean = getWMean(data, error);
        double wsum = 0;
        double sumOfWeights = 0, sumOfSqrdWeights = 0;
        for (int i = 0; i < data.length; i++) {
            double weight = 0;
            if (!Double.isNaN(error[i]) && !Double.isNaN(data[i]) && data[i] != 0.0) {
                weight = 1 / Math.pow(error[i], 2);
                wsum += weight * Math.pow(data[i] - wmean, 2);
                sumOfWeights += weight;
                sumOfSqrdWeights += Math.pow(weight, 2);
            }
        }
        double neff = Math.pow(sumOfWeights, 2) / sumOfSqrdWeights;
        double wvariance = wsum / sumOfWeights * neff / (neff - 1);
        return wvariance;
    }

    public static double[] getRunningAvgAndVar(float[] data) {
        return getRunningAvgAndVar(Converter.float2double(data));
    }

    public static double[] getRunningAvgAndVar(double[] data) {
        double firstValue = data[0];
        if (Double.isNaN(new Double(data[0]))) {
            firstValue = 0;
        }
        double sum = firstValue;
        int n = 1;
        double ave = sum / n;
        double var = Math.pow((firstValue - ave), 2);
        double runAve = ave;
        double runVar = 0;
        if (data.length > 1) {
            for (int i = 1; i < data.length; i++) {
                if (!Double.isNaN(new Double(data[i])) && data[i] != 0.0) {
                    runAve = ave + (data[i] - ave) / (n + 1);
                    runVar = var + (data[i] - runAve) * (data[i] - ave);
                    sum += data[i];
                    n++;
                    ave = sum / n;
                    var += Math.pow((data[i] - ave), 2);
                }
            }
            runVar /= (n - 1);
        }
        return (new double[]{runAve, runVar});
    }

    public static double[] getKurtosisAndSkewness(double[] data) {
        //  Computationally efficient algorithm for Kurtosis and Skewness is from
        //  http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Higher-order_statistics
        int n = 0;
        double mean = 0;
        double m2 = 0;
        double m3 = 0;
        double m4 = 0;
        for (int i = 0; i < data.length; i++) {
            int n1 = n;
            n++;
            double delta = data[i] - mean;
            double deltaOverN = delta / n;
            double deltaOverN2 = deltaOverN * deltaOverN;
            double term1 = delta * deltaOverN * n1;
            mean += deltaOverN;
            m4 += term1 * deltaOverN2 * (n * n - 3 * n + 3) + 6 * deltaOverN2 * m2 - 4 * deltaOverN * m3;
            m3 += term1 * deltaOverN * (n - 2) - 3 * deltaOverN * m2;
            m2 += term1;
        }
        double kurtosis = (n * m4) / (m2 * m2) - 3;
        double m2Cubed = m2 * m2 * m2;
        double skewness = m3 * Math.sqrt(n / m2Cubed);
        return new double[]{kurtosis, skewness};
    }

    public static double getKurtosis(double[] data) {
        return (getKurtosisAndSkewness(data))[0];
    }

    public static double getSkewness(double[] data) {
        return (getKurtosisAndSkewness(data))[1];
    }

    public static double getCovariance(double[] x, double[] y) {
        double meanX = getMean(x);
        double meanY = getMean(y);
        double sum = 0;
        int n = 0;
        int nDataPoints = Math.min(x.length, y.length);
        for (int i = 0; i < nDataPoints; i++) {
            if (!Double.isNaN(x[i]) && !Double.isNaN(y[i])) {
                sum += (x[i] - meanX) * (y[i] - meanY);
                n++;
            }
        }
        double covariance = sum / (n - 1);
        return covariance;
    }

    public static double[] getCorrelationCoefficient(double[] x, double[] y) {
        // From Glen Cowan's book on Statistical Data Analysis
        double varX = getVariance(x);
        double varY = getVariance(y);
        double sigX = Math.sqrt(varX);
        double sigY = Math.sqrt(varY);
        double covariance = getCovariance(x, y);
        double correlationCoeff = covariance / (sigX * sigY);

        int sampleSize = x.length;
        double z = 0.5 * Math.log((1 + correlationCoeff) / (1 - correlationCoeff));
        double zVar = 1.0 / (sampleSize - 3);
        double zSigma = Math.sqrt(zVar);
        double zLow = z - zSigma;
        double zHi = z + zSigma;

        double rLow = Math.tanh(zLow);
        double rHi = Math.tanh(zHi);
        double rSigma = (rHi - rLow) / 2;
        double uncertainty = rSigma;
        return new double[]{correlationCoeff, uncertainty};
    }

    public static double[] getAutocorrelationFunction(double[] data) {
        double[] rhos = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                int index = j + i;
                if (index >= data.length) {
                    index -= data.length;
                }
                rhos[i] += data[j] * data[index];
            }
        }
        return rhos;
    }

    public static double getErrOnMean(double[] data) {
        int n = DataUtils.getNumOfGoodValues(data);
        double var = getVariance(data);
        double varOfMean = var / n;
        double errOnMean = Math.sqrt(varOfMean);
        return errOnMean;
    }

    public static double getErrOnMean(double[][] data) {
        double[] flatData = (double[]) ArrayFuncs.flatten(data);
        double errOnMean = getErrOnMean(flatData);
        return errOnMean;
    }

    public static double getErrOnMean(Vector data) {
        double n = (new Double(data.size())).doubleValue();
        double var = getVariance(data);
        double varOfMean = var / n;
        double errOnMean = Math.sqrt(varOfMean);
        return errOnMean;
    }

    public static double getErrOnMean(float[] data) {
        int n = DataUtils.getNumOfGoodValues(data);
        double var = getVariance(data);
        double varOfMean = var / n;
        double errOnMean = Math.sqrt(varOfMean);
        return errOnMean;
    }

    public static double getErrOnMean(float[][] data) {
        float[] flatData = (float[]) ArrayFuncs.flatten(data);
        double errOnMean = getErrOnMean(flatData);
        return errOnMean;
    }

    public static double getErrOnMean(int[] data) {
        int n = DataUtils.getNumOfGoodValues(data);
        double var = getVariance(data);
        double varOfMean = var / n;
        double errOnMean = Math.sqrt(varOfMean);
        return errOnMean;
    }

    public static double getErrOnMean(int[][] data) {
        int[] flatData = (int[]) ArrayFuncs.flatten(data);
        double errOnMean = getErrOnMean(flatData);
        return errOnMean;
    }

    public static double getErrOnWMean(double[] error) {
        double sumOfWeights = 0;
        for (int i = 0; i < error.length; i++) {
            if (!Double.isNaN(error[i]) && error[i] != 0.0) {
                double weight = 1 / Math.pow(error[i], 2);
                sumOfWeights += weight;
            }
        }
        double errOnWMean = 1 / Math.sqrt(sumOfWeights);
        return errOnWMean;
    }

    public static double getErrOnWMean(double[][] error) {
        double[] flatError = (double[]) ArrayFuncs.flatten(error);
        return getErrOnWMean(flatError);
    }

    public static double getErrOnWMean(float[] error) {
        return getErrOnWMean(Converter.float2double(error));
    }

    public static double getErrOnWMean(float[][] error) {
        float[] flatError = (float[]) ArrayFuncs.flatten(error);
        return getErrOnWMean(flatError);
    }

    public static double getErrOnWMean(int[] error) {
        return getErrOnWMean(Converter.int2double(error));
        // 	int n = data.length;
        // 	double var = getVariance(data);
        // 	double neff = getNeffective(data, error);
        // 	double varOfWMean = var/neff;
        // 	double errOnWMean = Math.sqrt(varOfWMean);
        //      return errOnWMean;
    }

    public static double getErrOnWMean(int[][] data, int[][] error) {
        int[] flatError = (int[]) ArrayFuncs.flatten(error);
        return getErrOnWMean(flatError);
    }

    public static double getTotalSignif(double[] data, double[] error) {
        double[] wMeanAndErr = getWMeanAndError(data, error);
        return wMeanAndErr[0] / wMeanAndErr[1];
    }

    public static double getTotalSignif(float[] data, float[] error) {
        return getTotalSignif(Converter.float2double(data), Converter.float2double(error));
    }

    public static double getNeffective(double[] data, double[] error) {
        double wmean = getWMean(data, error);
        double wsum = 0;
        double sumOfWeights = 0, sumOfSqrdWeights = 0;
        for (int i = 0; i < data.length; i++) {
            double weight = 1 / Math.pow(error[i], 2);
            wsum += weight * Math.pow(data[i] - wmean, 2);
            sumOfWeights += weight;
            sumOfSqrdWeights += Math.pow(weight, 2);
        }
        double neff = Math.pow(sumOfWeights, 2) / sumOfSqrdWeights;
        return neff;
    }

    public static double getNeffective(float[] data, float[] error) {
        double wmean = getWMean(data, error);
        double wsum = 0;
        double sumOfWeights = 0, sumOfSqrdWeights = 0;
        for (int i = 0; i < data.length; i++) {
            double weight = 1 / Math.pow(error[i], 2);
            wsum += weight * Math.pow(data[i] - wmean, 2);
            sumOfWeights += weight;
            sumOfSqrdWeights += Math.pow(weight, 2);
        }
        double neff = Math.pow(sumOfWeights, 2) / sumOfSqrdWeights;
        return neff;
    }

    public static double getNeffective(int[] data, int[] error) {
        double wmean = getWMean(data, error);
        double wsum = 0;
        double sumOfWeights = 0, sumOfSqrdWeights = 0;
        for (int i = 0; i < data.length; i++) {
            double weight = 1 / Math.pow(error[i], 2);
            wsum += weight * Math.pow(data[i] - wmean, 2);
            sumOfWeights += weight;
            sumOfSqrdWeights += Math.pow(weight, 2);
        }
        double neff = Math.pow(sumOfWeights, 2) / sumOfSqrdWeights;
        return neff;
    }

}
