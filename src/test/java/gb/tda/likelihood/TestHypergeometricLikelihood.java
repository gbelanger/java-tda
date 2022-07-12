package gb.esac.test;

import gb.esac.likelihood.HypergeometricLikelihood;

public class TestHypergeometricLikelihood {

    public static void main(String[] args) throws Exception {
	double bigN = 500;
	double s = 50;
	double n = 100;

	HypergeometricLikelihood hyperL = new HypergeometricLikelihood();
	double[] xValues = hyperL.getXValues(0, 20);
	double[] likeFunc = hyperL.getLikelihoodFunction(bigN,xValues,n,s);
	String filename = "testHypergeometricLikelihood.qdp";
	hyperL.drawFunction(xValues, likeFunc, filename);
    }
}
