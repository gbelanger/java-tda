package gb.tda.utils;

import java.text.DecimalFormat;
import java.util.Vector;
import hep.aida.ref.histogram.FixedAxis;
import hep.aida.ref.histogram.Histogram1D;
import hep.aida.ref.histogram.Histogram2D;
import hep.aida.ref.histogram.VariableAxis;
import nom.tam.util.ArrayFuncs;

public final class PrimitivesConverter {

    public static double[] days2sec(final double[] times) {
		double[] t = new double[times.length];
		for (int i=0; i < times.length; i++) {
		    t[i] = times[i]*86400.;
		}
		return t;
    }

    public static double[] sec2ms(final double[] times) {
		double[] t = new double[times.length];
		for (int i=0; i < times.length; i++) {
		    t[i] = times[i]*1000;
		}
		return t;
    }

    public static double[] lin2logSpace(final double[] data) {
		double[] logData = new double[data.length];
		for (int i=0; i < data.length; i++) {
		    logData[i] = Math.log10(data[i]);
		}
		return logData;
    }

    public static double[] vector2doubleArray(final Vector dataVector) {
		double[] data = new double[dataVector.size()];
		for (int i=0; i < dataVector.size(); i++) {
		    data[i] = ((Double) dataVector.elementAt(i)).doubleValue();
		}
		return data;
    }
    
    public static double[][] float2double(final float[][] floatData) {
		////  Determine the size of the floatData double array
		int[] dataDims = ArrayFuncs.getDimensions(floatData);
		//  dataDims[0] = number of col (xaxis length)
		//  dataDims[1] = number of rows (yaxis length)
		////  convert floats to doubles
		double[][] doubleData = new double[dataDims[0]][dataDims[1]];
		for (int row=0; row < dataDims[1]; row++) {
		    for (int col=0; col < dataDims[0]; col++) {
			doubleData[col][row] = Double.valueOf(floatData[col][row]);
		    }
		}
		return doubleData;	
    }
    
    public static double[] float2double(final float[] floatData) {
		double[] doubleData = new double[floatData.length];
		for (int i=0; i < floatData.length; i++) {
		    doubleData[i] = Double.valueOf(floatData[i]);
		}
		return doubleData;
    }

    public static double[] long2double(final long[] longData) {
		double[] doubleData = new double[longData.length];
		for (int i=0; i < longData.length; i++) {
		    doubleData[i] = Double.valueOf(longData[i]);
		}
		return doubleData;
    }

    public static double[] short2double(final short[] shortData) {
		double[] doubleData = new double[shortData.length];
		for (int i=0; i < shortData.length; i++) {
		    doubleData[i] = Double.valueOf(shortData[i]);
		}
		return doubleData;
    }

    public static int[] short2int(final short[] shortData) {
		int[] intData = new int[shortData.length];
		for (int i=0; i < shortData.length; i++) {
		    intData[i] = Integer.valueOf(shortData[i]);
		}
		return intData;
    }

    public static double[] int2double(final int[] intData) {
		double[] doubleData = new double[intData.length];
		for (int i=0; i < intData.length; i++) {
		    doubleData[i] = Double.valueOf(intData[i]);
		}
		return doubleData;
    }
    
    public static float[][] double2float(final double[][] doubleData) {
		int[] dataDims = ArrayFuncs.getDimensions(doubleData);
		//  dataDims[0] = number of col (xaxis length)
		//  dataDims[1] = number of rows (yaxis length)
		////  convert floats to doubles
		float[][] floatData = new float[dataDims[0]][dataDims[1]];
		for (int row=0; row < dataDims[1]; row++) {
		    for (int col=0; col < dataDims[0]; col++) {
			floatData[col][row] = Double.valueOf(doubleData[col][row]).floatValue();
		    }
		}
		return floatData;
    }
    
    public static float[] double2float(final double[] doubleData) {
		float[]  floatData = new float[doubleData.length];
		for (int i=0; i < doubleData.length; i++) {
		    floatData[i] = Double.valueOf(doubleData[i]).floatValue();
		}
		return floatData;
    }

    public static float[][] int2float(final int[][] intData) {
		int[] dataDims = ArrayFuncs.getDimensions(intData);
		//  dataDims[0] = number of col (xaxis length)
		//  dataDims[1] = number of rows (yaxis length)
		float[][] floatData = new float[dataDims[0]][dataDims[1]];
		for (int row=0; row < dataDims[1]; row++) {
		    for (int col=0; col < dataDims[0]; col++) {
			floatData[col][row] = Float.valueOf(intData[col][row]);
		    }
		}
		return floatData;
    }


}
