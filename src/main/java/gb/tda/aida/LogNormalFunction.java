package gb.tda.aida;

import hep.aida.ref.function.AbstractIFunction;

import cern.jet.stat.Gamma;
import cern.jet.random.ChiSquare;
import cern.jet.random.engine.MersenneTwister64;

public class LogNormalFunction extends AbstractIFunction {

    public static int dims = 1;
    public static int nPars = 3;
    
    public LogNormalFunction() {
        this("");
    }
    
    public LogNormalFunction(String title) {
        super(title, dims, nPars);
    }
    
    public LogNormalFunction(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {

	double mean = p[0];
	double sigma = p[1];
	double norm = p[2];
	double frontTerm = norm/(v[0]*sigma*Math.sqrt(2*Math.PI));
	double logNorm = frontTerm*Math.exp(-(Math.log(v[0])-mean)*(Math.log(v[0])-mean))/(2*sigma*sigma);
	return logNorm;
    }
    
    // Here change the parameter names
    protected void init(String title) {
	parameterNames[0] = "mean";
	parameterNames[1] = "sigma";
	parameterNames[2] = "norm";
	super.init(title);
    }
}
