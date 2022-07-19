package gb.tda.likelihood;

import gb.tda.utils.PrimitivesConverter;
import gb.tda.utils.MinMax;

public abstract class OneParameterLikelihood extends Likelihood {

    /**  Abstract methods that must be implemented in children classes  **/
    abstract double pdfValue(double parameterValue, double x);
    abstract double getMLE(double[] data);
    abstract double getLogLikelihood(double parameterValue, double data);
    abstract double getLogLikelihoodOfModel(double[] model, double[] data) throws IllegalArgumentException;

    /**  Likelihood for the value of the parameter given the data  **/
    public double getLikelihood(double parameterValue, double data) {
		return pdfValue(parameterValue, data);
    }

    public double getLikelihood(double parameterValue, double[] data) {
		double jointLikelihood = 1;
		for (int i=0; i < data.length; i++) {
			jointLikelihood *= getLikelihood(parameterValue, data[i]);
		}
		return jointLikelihood;
    }

    public double getLikelihood(double parameterValue, int data) {
		return getLikelihood(parameterValue, (double) data);
    }

    public double getLikelihood(double parameterValue, int[] data) {
		return getLikelihood(parameterValue, PrimitivesConverter.int2double(data));
    }

    /**  Likelihood function  **/
    public double[] getLikelihoodFunction(double[] parameterValues, double data) {
		double[] likelihoodFunction = new double[parameterValues.length];
		for (int i=0; i < likelihoodFunction.length; i++) {
			likelihoodFunction[i] = getLikelihood(parameterValues[i], data);
		}
		double ml = MinMax.getMax(likelihoodFunction);
		for (int i=0; i < likelihoodFunction.length; i++) {
			likelihoodFunction[i] /= ml;
		}
		return likelihoodFunction;
    }

    public double[] getLikelihoodFunction(double[] parameterValues, int data) {
		return getLikelihoodFunction(parameterValues, (double) data);
    }

    public double[] getLikelihoodFunction(double minParameterValue, double maxParameterValue, double data) {
		double[] parameterValues = getXValues(minParameterValue, maxParameterValue);
		return getLikelihoodFunction(parameterValues, data);
    }

    public double[] getLikelihoodFunction(double minParameterValue, double maxParameterValue, int data) {
		return getLikelihoodFunction(minParameterValue, maxParameterValue, (double) data);
    }


    public double[] getLikelihoodFunction(double[] parameterValues, double[] data) {
		double[] likelihoodFunction = new double[parameterValues.length];
		for (int i=0; i < likelihoodFunction.length; i++) {
			likelihoodFunction[i] = getLikelihood(parameterValues[i], data);
		}
		double ml = MinMax.getMax(likelihoodFunction);
		for (int i=0; i < likelihoodFunction.length; i++) {
			likelihoodFunction[i] /= ml;
		}
		return likelihoodFunction;
    }

    public double[] getLikelihoodFunction(double[] parameterValues, int[] data) {
		return getLikelihoodFunction(parameterValues, PrimitivesConverter.int2double(data));
    }

    public double[] getLikelihoodFunction(double minParameterValue, double maxParameterValue, double[] data) {
		double[] parameterValues = getXValues(minParameterValue, maxParameterValue);
		return getLikelihoodFunction(parameterValues, data);
    }

    public double[] getLikelihoodFunction(double minParameterValue, double maxParameterValue, int[] data) {
		return getLikelihoodFunction(minParameterValue, maxParameterValue, PrimitivesConverter.int2double(data));
    }

    /**  Log-Likelihood  **/
    public double getLogLikelihood(double parameterValue, double[] data) {
		double l = 0;
		for (int i=0; i < data.length; i++) {
			l += getLogLikelihood(parameterValue, data[i]);
		}
		return l;
    }

    public double getNegLogLikelihood(double parameterValue, double data) {
		return -1*getLogLikelihood(parameterValue, data);
    }

}
