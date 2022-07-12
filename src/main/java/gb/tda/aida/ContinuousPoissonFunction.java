package gb.tda.aida;

import hep.aida.ref.function.AbstractIFunction;

import cern.jet.math.Arithmetic;
import cern.jet.stat.Gamma;
import cern.jet.random.ChiSquare;
import cern.jet.random.engine.MersenneTwister64;

public class ContinuousPoissonFunction extends AbstractIFunction {

    public static int dims = 1;
    public static int nPars = 2;
    
    public ContinuousPoissonFunction() {
        this("");
    }
    
    public ContinuousPoissonFunction(String title) {
        super(title, dims, nPars);
    }
    
    public ContinuousPoissonFunction(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {

	double lambda = p[0];
	double norm = p[1];
	double continuousPoisson = norm*Math.exp(-lambda)*Math.pow(lambda, v[0]) / Gamma.gamma(v[0]+1);
	return continuousPoisson;
    }
    
    // Here change the parameter names
    protected void init(String title) {
	parameterNames[0] = "mean";
	parameterNames[1] = "norm";
	super.init(title);
    }
}
