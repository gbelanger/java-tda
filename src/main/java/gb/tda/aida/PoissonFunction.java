package gb.tda.aida;

import hep.aida.ref.function.AbstractIFunction;

import cern.jet.math.Arithmetic;
import cern.jet.stat.Gamma;
import cern.jet.random.ChiSquare;
import cern.jet.random.engine.MersenneTwister64;

public class PoissonFunction extends AbstractIFunction {

    public static int dims = 1;
    public static int nPars = 2;
    
    public PoissonFunction() {
        this("");
    }
    
    public PoissonFunction(String title) {
        super(title, dims, nPars);
    }
    
    public PoissonFunction(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {
	double lambda = p[0];
	double norm = p[1];
	double poisson = norm*Math.exp(-lambda)*Math.pow(lambda, v[0]) / Arithmetic.factorial((int) v[0]);
	return poisson;
    }
    
    // Define parameter names
    protected void init(String title) {
	parameterNames[0] = "expect";
	parameterNames[1] = "norm";
	super.init(title);
    }
}
