package gb.tda.likelihood;

import cern.jet.random.Binomial;
import cern.jet.stat.Gamma;
import cern.jet.random.engine.MersenneTwister64;
import java.util.Date;

public class BinomialLikelihood extends TwoParameterLikelihood {

    //  Continuous analogue of Binomial p.d.f. where factorial is replaced by Gamma function
    double pdfValue(double n, double p, double x) {
	return Gamma.gamma(n+1)/Gamma.gamma(x+1)*Gamma.gamma(n-x+1) * Math.pow(p,x)*Math.pow(1-p,n-x);
    }
    
    // //  Discrete (true) Binomial distribution function
    // double pdfValue(double n, double p, double xValue) {
    // 	MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
    // 	Binomial binomial = new Binomial((int)n, p, engine);
    // 	return binomial.pdf((int)xValue);
    // }

    public double getLogLikelihood(double n, double p, double x) {
	return x*Math.log(p) + (n-x)*Math.log(1-p);
    }    

}
