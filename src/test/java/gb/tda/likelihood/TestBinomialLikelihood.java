package gb.esac.test;

import gb.esac.likelihood.BinomialLikelihood;

public class TestBinomialLikelihood {

    public static void main(String[] args) throws Exception {
	double n = 10;
	double k =  3;
	BinomialLikelihood binL = new BinomialLikelihood();
	binL.setDeltaX(0.01);
	double[] xValues = binL.getXValues(0.05,0.7);
	double[] likeFunc = binL.getNegLogLikelihoodFunction(n, xValues, k);
	double n2 = 100;
	double k2 = 30;
	double[] likeFunc2 = binL.getNegLogLikelihoodFunction(n2, xValues, k2);	
	String filename = "testBinomialLikelihood.qdp";
	binL.drawTwoFunctions(xValues, likeFunc, likeFunc2, filename);
    }
}
