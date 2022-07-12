package gb.esac.test;

import gb.esac.likelihood.BinomialLikelihood;

public class TestBinomialLikelihood {

    double n = 10;
    double k =  3;
    double[] valuesOfP = BinomialLikelihood.getXValues(0,1);
    double[] xValues = BinomialLikelihood.getXValues(0,30);
    double[] likeFunc = BinomialLikelihood.getLikelihoodFunction(n, valuesOfP, k);

}
