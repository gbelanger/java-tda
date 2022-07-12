package gb.tda.likelihood;

import gb.tda.binner.BinningException;
import gb.tda.tools.Converter;
import gb.tda.tools.MinMax;
import gb.tda.binner.BinningUtils;
import gb.tda.tools.DataUtils;


public abstract class TwoParameterLikelihood extends Likelihood {

    //  Abstract methods that must be implemented in children classes
    abstract double pdfValue(double parValue1, double parValue2, double x);
    abstract double getLogLikelihood(double par1Value, double par2Value, double data);

 
    //  Likelihood 
    //// double
    public double getLikelihood(double par1Value, double par2Value, double data) {
	return pdfValue(par1Value, par2Value, data);
    }
    public double getLikelihood(double par1Value, double par2Value, double[] data) {
	double jointLikelihood = 1;
	for ( int i=0; i < data.length; i++ ) {
	    jointLikelihood *= getLikelihood(par1Value, par2Value, data[i]);
	}
	return jointLikelihood;
    }
    //// int
    public double getLikelihood(double par1Value, double par2Value, int data) {
	return getLikelihood(par1Value, par2Value, (double) data);
    }
    public double getLikelihood(double par1Value, double par2Value,  int[] data) {
	return getLikelihood(par1Value, par2Value, Converter.int2double(data));
    }
   

    //  Log-Likelihood and Negative Log-Likelihood
    //// double
    public double getLogLikelihood(double par1Value, double par2Value,  double[] data) {
	double l = 0;
	for (int i = 0; i < data.length; i++) {
	    l += getLogLikelihood(par1Value, par2Value, data[i]);
	}
	return l;
    }
    public double getNegLogLikelihood(double par1Value, double par2Value, double data) {
	return -1d*getLogLikelihood(par1Value, par2Value, data);
    }
    public double getNegLogLikelihood(double par1Value, double par2Value,  double[] data) {
	double l = 0;
	for (int i = 0; i < data.length; i++) {
	    l += getNegLogLikelihood(par1Value, par2Value, data[i]);
	}
	return 1d*l;
    }
    //// int
    public double getLogLikelihood(double par1Value, double par2Value, int data) {
	return getLogLikelihood(par1Value, par2Value, (double) data);
    }
    public double getLogLikelihood(double par1Value, double par2Value, int[] data) {
	return getLogLikelihood(par1Value, par2Value, Converter.int2double(data));
    }
    public double getNegLogLikelihood(double par1Value, double par2Value, int data) {
	return -1d*getLogLikelihood(par1Value, par2Value, (double) data);
    }
    public double getNegLogLikelihood(double par1Value, double par2Value, int[] data) {
	return getNegLogLikelihood(par1Value, par2Value, Converter.int2double(data));
    }
    

    //  Log-Likelihood of model
    public double getLogLikelihoodOfModel(double[] modelPar1Values, double[] modelPar2Values, double[] data) throws BinningException {
	BinningUtils.checkArrayLengthsAreEqual(modelPar1Values, data);
	BinningUtils.checkArrayLengthsAreEqual(modelPar2Values, data);
	double logLikelihood = 0;
	for ( int i=0; i <  data.length; i++ ) {
	    logLikelihood += getLogLikelihood(modelPar1Values[i], modelPar2Values[i], data[i]);
	}
	return logLikelihood;
    }
    public double getNegLogLikelihoodOfModel(double[] modelPar1Values, double[] modelPar2Values, double[] data) throws BinningException {
	return -1d*getLogLikelihoodOfModel(modelPar1Values, modelPar2Values, data);
    }


    //  Likelihood functions 
    //// Likelihood
    public double[] getLikelihoodFunction(double par1FixedValue, double[] par2Values, double data) {
	double[] likelihoodFunction = new double[par2Values.length];
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] = getLikelihood(par1FixedValue, par2Values[i], data);
	}
	double ml = MinMax.getMax(likelihoodFunction);
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] /= ml;
	}
	return likelihoodFunction;
    }
    public double[] getLikelihoodFunction(double[] par1Values, double par2FixedValue, double data) {
	double[] likelihoodFunction = new double[par1Values.length];
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] = getLikelihood(par1Values[i], par2FixedValue, data);
	}
	double ml = MinMax.getMax(likelihoodFunction);
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] /= ml;
	}
	return likelihoodFunction;
    }
    //// Log-Likelihood and Negative Log-Likelihood
    public double[] getLogLikelihoodFunction(double par1FixedValue, double[] par2Values, double data) {
	double[] likelihoodFunction = new double[par2Values.length];
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] = getLogLikelihood(par1FixedValue, par2Values[i], data);
	}
	double ml = MinMax.getMax(likelihoodFunction);
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] /= ml;
	}
	return likelihoodFunction;
    }
    public double[] getLogLikelihoodFunction(double[] par1Values, double par2FixedValue, double data) {
	double[] likelihoodFunction = new double[par1Values.length];
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] = getLogLikelihood(par1Values[i], par2FixedValue, data);
	}
	double ml = MinMax.getMax(likelihoodFunction);
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] /= ml;
	}
	return likelihoodFunction;
    }
    public double[] getNegLogLikelihoodFunction(double par1FixedValue, double[] par2Values, double data) {
	double[] likelihoodFunction = getLogLikelihoodFunction(par1FixedValue, par2Values, data);
	return DataUtils.scale(likelihoodFunction, -1d);
    }
    public double[] getNegLogLikelihoodFunction(double[] par1Values, double par2FixedValue, double data) {
	double[] likelihoodFunction = getLogLikelihoodFunction(par1Values, par2FixedValue, data);
	return DataUtils.scale(likelihoodFunction, -1d);
    }
    
    //  Several data

    //// input is double[]
    public double[] getLikelihoodFunction(double par1FixedValue, double[] par2Values, double[] data) {
	double[] likelihoodFunction = new double[par2Values.length];
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] = getLikelihood(par1FixedValue, par2Values[i], data);
	}
	double ml = MinMax.getMax(likelihoodFunction);
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] /= ml;
	}
	return likelihoodFunction;
    }

    public double[] getLikelihoodFunction(double[] par1Values, double par2FixedValue, double[] data) {
	double[] likelihoodFunction = new double[par1Values.length];
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] = getLikelihood(par1Values[i], par2FixedValue, data);
	}
	double ml = MinMax.getMax(likelihoodFunction);
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] /= ml;
	}
	return likelihoodFunction;
    }

    ////  input is int[] (we simply convert it to double[])
    public double[] getLikelihoodFunction(double[] par1Values, double par2FixedValue, int[] data) {
	return getLikelihoodFunction(par1Values, par2FixedValue, Converter.int2double(data));
    }

    public double[] getLikelihoodFunction(double par1FixedValue, double[] par2Values, int[] data) {
	return getLikelihoodFunction(par1FixedValue, par2Values, Converter.int2double(data));
    }

    ////   log-likelihood
    public double[] getNegLogLikelihoodFunction(double par1FixedValue, double[] par2Values, double[] data) {
	double[] likelihoodFunction = new double[par2Values.length];
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] = -getLogLikelihood(par1FixedValue, par2Values[i], data);
	}
	double ml = MinMax.getMax(likelihoodFunction);
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] /= ml;
	}
	return likelihoodFunction;
    }

    public double[] getNegLogLikelihoodFunction(double[] par1Values, double par2FixedValue, double[] data) {
	double[] likelihoodFunction = new double[par1Values.length];
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] = -getLogLikelihood(par1Values[i], par2FixedValue, data);
	}
	double ml = MinMax.getMax(likelihoodFunction);
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] /= ml;
	}
	return likelihoodFunction;
    }

}
