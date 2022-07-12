package gb.tda.likelihood;

import cern.jet.random.Binomial;
import cern.jet.stat.Gamma;
import cern.jet.random.engine.MersenneTwister64;
import java.util.Date;

public class HypergeometricLikelihood extends ThreeParameterLikelihood {

    //  Continuous analogue of Hypergeometric p.d.f. where factorial is replaced by Gamma function: n! = Gamma(n+1)
    @Override
    double pdfValue(double bigN, double s, double n, double x) {
	return combin(s,x) * combin(bigN-s,n-x) / combin(bigN,n);
    }

    private double combin(double a, double b) {
	System.out.println(a+" and "+b);
	return Gamma.gamma(a+1)/Gamma.gamma(b+1)*Gamma.gamma(a-b+1);
    }
    
    // //  Discrete (true) Hypergeometric distribution function
    // double pdfValue(int N, int s, int  n, double xValue) {
    // 	MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
    // 	Hypergeomtric hyper = new Hypergeometric(N,s,n,engine);
    // 	return hyper.pdf((int)xValue);
    // }

    //  This needs to be defined
    @Override
    public double getLogLikelihood(double bigN, double s, double n, double x) {
	return 0; 
    }    

}
