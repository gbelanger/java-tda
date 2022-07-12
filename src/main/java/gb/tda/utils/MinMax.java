package gb.tda.utils;

import java.util.Vector;
import nom.tam.util.ArrayFuncs;

/**
 *
 * @version  2021 April
 * @author   Guillaume Belanger (ESAC, Spain)
 *
 * Last modified:
 *  2021 April: replaced deprecated methods; updated formatting.
 *  2015 April : added getMinMax(double[] data)
 *
 **/

public final class MinMax {

    public static double[] getMinMax(double[] data) {
		double max = -Double.MAX_VALUE;
		double min = Double.MAX_VALUE;
		for (int i=0; i < data.length; i++) {
			if (!Double.isNaN(data[i])) {
			max = Math.max(max, data[i]);
			min = Math.min(min, data[i]);
			}
		}
		if (min == Double.MAX_VALUE) min = Double.NaN;
		if (max == -Double.MAX_VALUE) max = Double.NaN;
		return new double[] {min, max};
    }

    public static double getMax(double[] data) {
	double max = -Double.MAX_VALUE;
		for (int i=0; i < data.length; i++) {
			if (!Double.isNaN(data[i])) max = Math.max(max, data[i]);
		}
		if (max == -Double.MAX_VALUE) max = Double.NaN;
		return max;
    }
	
    public static float getMax(float[] data) {
		float max = -Float.MAX_VALUE;
		for (int i=0; i < data.length; i++)
			if (!Float.isNaN(data[i])) max = Math.max(max, data[i]);
		if (max == -Float.MAX_VALUE) max = Float.NaN;
		return max;
    }

    public static int getMax(int[] data) {
		int max = -Integer.MAX_VALUE;
		for (int i=0; i < data.length; i++)
			max = Math.max(max, data[i]);
		return max;
    }
    
    public static short getMax(short[] data) {
		int dataInt[] = PrimitivesConverter.short2int(data);
		int max = -Integer.MAX_VALUE;
		for (int i=0; i < dataInt.length; i++)
			max = Math.max(max, dataInt[i]);
		return (new Integer(max)).shortValue();
    }
	
    public static double getMax(double[][] data) {
		double[] flatData =  (double[]) ArrayFuncs.flatten(data);
		double max = -Double.MAX_VALUE;
		for (int i=0; i < flatData.length; i++)
			if (!Double.isNaN(flatData[i])) max = Math.max(max, flatData[i]);
		return max;
    }
	
    public static float getMax(float[][] data) {
		float[] flatData =  (float[]) ArrayFuncs.flatten(data);
		float max = -Float.MAX_VALUE;
		for (int i=0; i < flatData.length; i++)
			if (!Float.isNaN(flatData[i])) max = Math.max(max, flatData[i]);
		return max;
    }
	
    public static double getMax(Vector<Double> dataVector) {
		double max = -Double.MAX_VALUE;
		double dataValue = 0;
		for (int i=0; i < dataVector.size(); i++) {
			dataValue = (dataVector.elementAt(i)).doubleValue();
			if (!Double.isNaN(dataValue)) max = Math.max(max, dataValue);
		}
		return max;
    }
	
	
    public static double getMin(double[] data) {
		double min = Double.MAX_VALUE;
		for (int i=0; i < data.length; i++) {
			if (!Double.isNaN(data[i])) {
			min = Math.min(min, data[i]);
			}
		}
		return min;
    }
	
    public static float getMin(float[] data) {
		float min = Float.MAX_VALUE;
		for (int i=0; i < data.length; i++)
			if (!Float.isNaN(data[i])) min = Math.min(min, data[i]);
		return min;
    }
	
    public static short getMin(short[] data) {
		int dataInt[] = PrimitivesConverter.short2int(data);
		int min = Integer.MAX_VALUE;
		for (int i=0; i < dataInt.length; i++)
			min = Math.min(min, dataInt[i]);
		return (short) min;
    }
	
    public static double getMin(int[] data) {
		double min = Integer.MAX_VALUE;
		for (int i=0; i < data.length; i++)
			min = (int) Math.min(min, data[i]);
		return min;
    }
	
    public static double getMin(Vector<Double> dataVector) {
		double min = Double.MAX_VALUE;
		double dataValue = 0;
		for (int i=0; i < dataVector.size(); i++) {
			dataValue = (dataVector.elementAt(i)).doubleValue();
			if (!Double.isNaN(dataValue)) min = Math.min(min, dataValue);
		}
		return min;
    }
	
    public static float getNonZeroMin(float[] data) {
		float min = Float.MAX_VALUE;
		for (int i=0; i < data.length; i++)
			if (!Float.isNaN(data[i]) && data[i] != 0.0) min = Math.min(min, data[i]);
		return min;
    }
	
    public static double getNonZeroMin(double[] data) {
		double min = Float.MAX_VALUE;
		for (int i=0; i < data.length; i++)
			if (!Double.isNaN(data[i]) && data[i] != 0.0) min = Math.min(min, data[i]);
		return min;
    }
	
    public static double getMin(double[][] data) {
		double[] flatData =  (double[]) ArrayFuncs.flatten(data);
		double min = Double.MAX_VALUE;
		for (int i=0; i < flatData.length; i++)
			if (!Double.isNaN(flatData[i])) min = Math.min(min, flatData[i]);
		return min;
    }
	
    public static float getMin(float[][] data) {
		float[] flatData =  (float[]) ArrayFuncs.flatten(data);
		float min = Float.MAX_VALUE;
		for (int i=0; i < flatData.length; i++)
			if (!Float.isNaN(flatData[i])) min = Math.min(min, flatData[i]);
		return min;
    }


}
