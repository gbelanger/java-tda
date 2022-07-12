package gb.tda.likelihood;

import gb.tda.tools.Converter;
import gb.tda.tools.MinMax;


public abstract class ThreeParameterLikelihood extends Likelihood {

    //  Abstract methods that must be implemented in children classes
    abstract double pdfValue(double par1Value, double par2Value, double par3Value, double x);
    abstract double getLogLikelihood(double par1Value, double par2Value, double par3Value, double data);

        //  Likelihood 
    //// double
    public double getLikelihood(double par1Value, double par2Value, double par3Value, double data) {
	return pdfValue(par1Value, par2Value, par3Value, data);
    }
    public double getLikelihood(double par1Value, double par2Value, double par3Value, double[] data) {
	double jointLikelihood = 1;
	for ( int i=0; i < data.length; i++ ) {
	    jointLikelihood *= getLikelihood(par1Value, par2Value, par3Value, data[i]);
	}
	return jointLikelihood;
    }
    //// int
    public double getLikelihood(double par1Value, double par2Value, double par3Value, int data) {
	return getLikelihood(par1Value, par2Value, par3Value, (double) data);
    }
    public double getLikelihood(double par1Value, double par2Value, double par3Value,  int[] data) {
	return getLikelihood(par1Value, par2Value, par3Value, Converter.int2double(data));
    }

    //  Likelihood functions 
    //// Likelihood
    public double[] getLikelihoodFunction(double par1FixedValue, double par2FixedValue, double[] par3Values, double data) {
	double[] likelihoodFunction = new double[par3Values.length];
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] = getLikelihood(par1FixedValue, par2FixedValue, par3Values[i], data);
	}
	double ml = MinMax.getMax(likelihoodFunction);
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] /= ml;
	}
	return likelihoodFunction;
    }
    public double[] getLikelihoodFunction(double par1FixedValue, double[] par2Values, double par3FixedValue, double data) {
	double[] likelihoodFunction = new double[par2Values.length];
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] = getLikelihood(par1FixedValue, par2Values[i], par3FixedValue, data);
	}
	double ml = MinMax.getMax(likelihoodFunction);
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] /= ml;
	}
	return likelihoodFunction;
    }
    public double[] getLikelihoodFunction(double[] par1Values, double par2FixedValue, double par3FixedValue, double data) {
	double[] likelihoodFunction = new double[par1Values.length];
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] = getLikelihood(par1Values[i], par2FixedValue, par3FixedValue, data);
	}
	double ml = MinMax.getMax(likelihoodFunction);
	for ( int i=0; i < likelihoodFunction.length; i++ ) {
	    likelihoodFunction[i] /= ml;
	}
	return likelihoodFunction;
    }

}
