package gb.tda.likelihood;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister64;
import java.util.Date;

public class NormalLikelihood extends TwoParameterLikelihood {

    public double pdfValue(double mu, double sigma, double xValue) {
	MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
	Normal normal = new Normal(mu, sigma, engine);
	return normal.pdf(xValue);
    }

    public double getLogLikelihood(double mu, double sigma, double x) {
	double s2 = sigma*sigma;
	return -0.5*(Math.log(2*Math.PI) + Math.log(s2) + Math.pow((x-mu)/sigma, 2));
    }    

}
